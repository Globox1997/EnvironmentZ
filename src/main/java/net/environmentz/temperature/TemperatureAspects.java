package net.environmentz.temperature;

import net.environmentz.EnvironmentzMain;
import net.environmentz.api.TemperatureEvents;
import net.environmentz.compat.DehydrationCompat;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.environmentz.mixin.access.EntityAccessor;
import net.environmentz.network.EnvironmentServerPacket;
import net.environmentz.temperature.data.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

public class TemperatureAspects {

    private static final EntityAttributeModifier FREEZING_DEBUFF = new EntityAttributeModifier(EnvironmentzMain.identifierOf("freezing_debuff"), -0.25,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final EntityAttributeModifier OVERHEATING_DEBUFF = new EntityAttributeModifier(EnvironmentzMain.identifierOf("overheating_debuff"), -0.3,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final EntityAttributeModifier COLD_DEBUFF = new EntityAttributeModifier(EnvironmentzMain.identifierOf("cold_debuff"), -0.08,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final EntityAttributeModifier HOT_DEBUFF = new EntityAttributeModifier(EnvironmentzMain.identifierOf("hot_debuff"), -0.12,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final EntityAttributeModifier GENERAL_DEBUFF = new EntityAttributeModifier(EnvironmentzMain.identifierOf("general_debuff"), -0.2,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    public static final RegistryKey<DamageType> FREEZING_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, EnvironmentzMain.identifierOf("freezing"));

    public static void tickPlayerEnvironment(TemperatureManager temperatureManager, ServerPlayerEntity player) {
        World world = player.getWorld();
        BlockPos playerPos = player.getBlockPos();
        Breakdown breakdown = new Breakdown();

        float biomeTemperature = world.getBiome(playerPos).value().getTemperature();
        EnvironmentCode environmentCode = EnvironmentCode.fromBiomeTemperature(biomeTemperature, Temperatures.getBiomeTemperature());
        DimensionTemperatures dimension = Temperatures.getDimension(world.getRegistryKey().getValue());

        updateWetness(player, temperatureManager);
        int wetness = temperatureManager.getPlayerWetIntensityValue();
        boolean isSkyNotVisible = !world.isSkyVisible(playerPos.up());

        if (dimension.usesStandardTemperatures()) {
            breakdown.add("Standard", dimension.standard().get(environmentCode), true);
        } else if (world.isDay()) {
            breakdown.add("Day", dimension.day().get(environmentCode), true);
        } else {
            breakdown.add("Night", dimension.night().get(environmentCode), true);
        }
        if (wetness > 0) {
            if (wetness >= Temperatures.getBodyWetness().soaked()) {
                breakdown.add("Soaked", dimension.soaked().get(environmentCode), false);
            } else {
                breakdown.add("Wett", dimension.wett().get(environmentCode), false);
            }
        }
        if (isSkyNotVisible) {
            breakdown.add("Shadow", dimension.shadow().get(environmentCode), true);
        }
        if (DehydrationCompat.IS_LOADED && environmentCode.isHot()) {
            breakdown.add("Sweat", sweatTemperature(player, dimension, environmentCode), false);
        }
        breakdown.add("Armor", armorTemperature(player, dimension, environmentCode), false);
        breakdown.add("Height", dimension.height().getTemperature(player.getBlockY()), true);
        addBlockTemperatures(player, breakdown, isSkyNotVisible);
        breakdown.add("Item", itemTemperature(player, temperatureManager), false);
        breakdown.add("Effect", effectTemperature(player, temperatureManager), false);
        breakdown.add("Acclimatization", acclimatization(dimension, environmentCode, temperatureManager.getPlayerTemperature()), false);

        int temperatureChange = TemperatureEvents.MODIFY_TEMPERATURE.invoker().modifyTemperature(player, environmentCode, breakdown.getTemperature());
        temperatureChange = applyResistanceAndProtection(temperatureManager, environmentCode, temperatureChange);

        temperatureManager.setThermometerTemperature(breakdown.getThermometerTemperature());
        EnvironmentServerPacket.syncThermometer(player, false);

        int oldTemperature = temperatureManager.getPlayerTemperature();
        int playerTemperature = applyBodyLimits(oldTemperature + temperatureChange, environmentCode);
        if ((!temperatureManager.isColdEnvAffected() && playerTemperature < 0) || (!temperatureManager.isHotEnvAffected() && playerTemperature > 0)) {
            playerTemperature = 0;
        }

        updateDebuffs(player, playerTemperature);

        if (breakdown.isDebug()) {
            EnvironmentzMain.LOGGER.info("Total: {} New Player Tmp: {}{} Cold Debuff: {} Hot Debuff: {} : {}", temperatureChange, playerTemperature, breakdown.getDebugText(),
                    hasModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, COLD_DEBUFF), hasModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, HOT_DEBUFF), environmentCode);
        }

        BodyTemperature bodyTemperature = Temperatures.getBodyTemperature();
        if (playerTemperature <= bodyTemperature.maxVeryCold()) {
            player.damage(player.getDamageSources().create(FREEZING_DAMAGE), 1.0F);
        } else if (playerTemperature >= bodyTemperature.maxVeryHot()) {
            if (DehydrationCompat.IS_LOADED && !ConfigInit.CONFIG.exhaustionInsteadDehydration) {
                DehydrationCompat.addDehydration(player, ConfigInit.CONFIG.overheatingExhaustion);
            } else {
                player.addExhaustion(ConfigInit.CONFIG.overheatingExhaustion);
            }
        }

        temperatureManager.setPlayerTemperature(playerTemperature);
        EnvironmentServerPacket.syncTemperature(player, false);
        if (oldTemperature != playerTemperature) {
            TemperatureEvents.TEMPERATURE_CHANGED.invoker().onTemperatureChanged(player, oldTemperature, playerTemperature);
        }
    }

    public static int applyResistanceAndProtection(TemperatureManager temperatureManager, EnvironmentCode environmentCode, int temperature) {
        if (environmentCode.isCold() && temperature < 0) {
            temperature = absorb(temperature, temperatureManager.getPlayerColdResistance(), temperatureManager::setPlayerColdResistance);
            temperature = absorb(temperature, temperatureManager.getPlayerColdProtectionAmount(), temperatureManager::setPlayerColdProtectionAmount);
        } else if (environmentCode.isHot() && temperature > 0) {
            temperature = absorb(temperature, temperatureManager.getPlayerHeatResistance(), temperatureManager::setPlayerHeatResistance);
            temperature = absorb(temperature, temperatureManager.getPlayerHeatProtectionAmount(), temperatureManager::setPlayerHeatProtectionAmount);
        }
        return temperature;
    }

    public static int applyBodyLimits(int temperature, EnvironmentCode environmentCode) {
        BodyTemperature bodyTemperature = Temperatures.getBodyTemperature();
        Acclimatization acclimatization = Temperatures.getAcclimatization();
        if (environmentCode.isCold() && temperature > bodyTemperature.minHot()) {
            return temperature + acclimatization.hotBody() * 2;
        }
        if (environmentCode.isHot() && temperature < bodyTemperature.minCold()) {
            return temperature + acclimatization.coldBody() * 2;
        }
        return MathHelper.clamp(temperature, bodyTemperature.maxVeryCold(), bodyTemperature.maxVeryHot());
    }

    private static int absorb(int temperature, int buffer, IntConsumer bufferSetter) {
        if (buffer <= 0 || temperature == 0) {
            return temperature;
        }
        int absorbed = Math.min(buffer, Math.abs(temperature));
        bufferSetter.accept(buffer - absorbed);
        return temperature - Integer.signum(temperature) * absorbed;
    }

    private static void updateDebuffs(PlayerEntity player, int temperature) {
        BodyTemperature bodyTemperature = Temperatures.getBodyTemperature();
        boolean freezing = temperature <= bodyTemperature.maxCold();
        boolean cold = !freezing && temperature <= bodyTemperature.minCold();
        boolean overheating = temperature >= bodyTemperature.maxHot();
        boolean hot = !overheating && temperature >= bodyTemperature.minHot();

        EntityAttributeInstance speed = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        EntityAttributeInstance attackDamage = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        EntityAttributeInstance attackSpeed = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
        setModifier(speed, FREEZING_DEBUFF, freezing);
        setModifier(speed, COLD_DEBUFF, cold);
        setModifier(attackDamage, OVERHEATING_DEBUFF, overheating);
        setModifier(attackDamage, HOT_DEBUFF, hot);
        setModifier(attackSpeed, GENERAL_DEBUFF, freezing || overheating);
    }

    private static void setModifier(@Nullable EntityAttributeInstance instance, EntityAttributeModifier modifier, boolean active) {
        if (instance == null) {
            return;
        }
        boolean present = instance.hasModifier(modifier.id());
        if (active && !present) {
            instance.addTemporaryModifier(modifier);
        } else if (!active && present) {
            instance.removeModifier(modifier.id());
        }
    }

    private static boolean hasModifier(PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        return instance != null && instance.hasModifier(modifier.id());
    }

    private static int acclimatization(DimensionTemperatures dimension, EnvironmentCode environmentCode, int playerTemperature) {
        if (dimension.acclimatization().isPresent()) {
            return dimension.acclimatization().get();
        }
        Acclimatization acclimatization = Temperatures.getAcclimatization();
        return switch (environmentCode) {
            case COLD -> playerTemperature < acclimatization.veryColdBodyTemperature() ? acclimatization.veryColdBody() : 0;
            case NORMAL -> {
                if (playerTemperature < acclimatization.coldBodyTemperature()) {
                    yield acclimatization.coldBody();
                }
                yield playerTemperature > acclimatization.hotBodyTemperature() ? acclimatization.hotBody() : 0;
            }
            case HOT -> playerTemperature > acclimatization.veryHotBodyTemperature() ? acclimatization.veryHotBody() : 0;
            default -> 0;
        };
    }

    private static int sweatTemperature(PlayerEntity player, DimensionTemperatures dimension, EnvironmentCode environmentCode) {
        float exhaustion = ConfigInit.CONFIG.overheatingExhaustion;
        if (ConfigInit.CONFIG.exhaustionInsteadDehydration) {
            if (player.getHungerManager().getFoodLevel() <= 6) {
                return 0;
            }
            player.addExhaustion(exhaustion);
        } else {
            if (DehydrationCompat.getThirstLevel(player) <= 6) {
                return 0;
            }
            DehydrationCompat.addDehydration(player, exhaustion);
        }
        return dimension.sweat().get(environmentCode);
    }

    private static int armorTemperature(PlayerEntity player, DimensionTemperatures dimension, EnvironmentCode environmentCode) {
        float temperature = 0.0F;
        for (ItemStack stack : player.getInventory().armor) {
            if (stack.isEmpty()) {
                continue;
            }
            boolean isWarmArmor = stack.isIn(TagInit.WARM_ARMOR);
            if (!stack.isIn(TagInit.NON_AFFECTING_ARMOR)) {
                boolean isInsulated = isWarmArmor || Boolean.TRUE.equals(stack.get(ItemInit.INSULATED));
                temperature += isInsulated ? dimension.insulatedArmor().get(environmentCode) : dimension.armor().get(environmentCode);
            }
            Integer iced = stack.get(ItemInit.ICED);
            if (!isWarmArmor && iced != null) {
                temperature += dimension.icedArmor().get(environmentCode);
                if (iced - 1 <= 0) {
                    stack.remove(ItemInit.ICED);
                } else {
                    stack.set(ItemInit.ICED, iced - 1);
                }
            }
        }
        return Math.round(temperature);
    }

    private static void updateWetness(PlayerEntity player, TemperatureManager temperatureManager) {
        BodyWetness bodyWetness = Temperatures.getBodyWetness();
        int wetness = temperatureManager.getPlayerWetIntensityValue();
        if (player.isTouchingWaterOrRain()) {
            if (wetness < bodyWetness.max()) {
                if (player.isTouchingWater()) {
                    wetness += bodyWetness.water();
                } else if (((EntityAccessor) player).callIsBeingRainedOn()) {
                    wetness += bodyWetness.rain();
                }
                temperatureManager.setPlayerWetIntensityValue(Math.min(wetness, bodyWetness.max()));
            }
        } else if (wetness > 0) {
            temperatureManager.setPlayerWetIntensityValue(Math.max(wetness + bodyWetness.dry(), 0));
        }
    }

    private static void addBlockTemperatures(PlayerEntity player, Breakdown breakdown, boolean isSkyNotVisible) {
        World world = player.getWorld();
        BlockPos playerPos = player.getBlockPos();
        Map<Block, Integer> blockCounts = new HashMap<>();
        Map<Fluid, Integer> fluidCounts = new HashMap<>();
        int heatBlockRadius = ConfigInit.CONFIG.heatBlockRadius;
        int radius = heatBlockRadius + 2;
        int blockHeatSum = 0;

        for (int i = 0; i <= heatBlockRadius * 2; i++) {
            int height = i > heatBlockRadius ? -(i - heatBlockRadius) : i;
            int x = 0;
            int z = 0;
            int dx = 0;
            int dz = -1;
            for (int k = 0; k < radius * radius; k++) {
                if (-radius / 2 <= x && x <= radius / 2 && -radius / 2 <= z && z <= radius / 2) {
                    blockHeatSum += addBlockTemperature(world, player, playerPos, playerPos.add(x, height, z), blockCounts, fluidCounts, breakdown);
                }
                if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                    int turn = dx;
                    dx = -dz;
                    dz = turn;
                }
                x += dx;
                z += dz;
            }
        }

        if (isSkyNotVisible && blockHeatSum > 0 && isEnclosed(world, playerPos)) {
            breakdown.add("RoomHeat", (int) (blockHeatSum * Temperatures.getRoomHeat().factor()), true);
        }
    }

    private static int addBlockTemperature(World world, PlayerEntity player, BlockPos playerPos, BlockPos pos, Map<Block, Integer> blockCounts, Map<Fluid, Integer> fluidCounts,
                                           Breakdown breakdown) {
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) {
            return 0;
        }
        int distance = (int) Math.sqrt(playerPos.getSquaredDistance(pos));
        BlockTemperature blockTemperature = Temperatures.getBlockTemperature(state.getBlock());
        if (blockTemperature != null) {
            BooleanProperty property = blockTemperature.property();
            if (property != null && state.contains(property) && !state.get(property)) {
                return 0;
            }
            if (isObstructed(world, player, pos)) {
                return 0;
            }
            if (blockCounts.merge(state.getBlock(), 1, Integer::sum) > blockTemperature.maxCount()) {
                return 0;
            }
            int temperature = blockTemperature.getTemperature(distance);
            breakdown.add(breakdown.isDebug() ? "Block " + state.getBlock().getName().getString() : "Block", temperature, true);
            return temperature;
        }
        if (!state.getFluidState().isEmpty()) {
            Fluid fluid = state.getFluidState().getFluid();
            BlockTemperature fluidTemperature = Temperatures.getFluidTemperature(fluid);
            if (fluidTemperature != null && fluidCounts.merge(fluid, 1, Integer::sum) <= fluidTemperature.maxCount()) {
                breakdown.add(breakdown.isDebug() ? "Fluid " + state.getBlock().getName().getString() : "Fluid", fluidTemperature.getTemperature(distance), true);
            }
        }
        return 0;
    }

    private static boolean isObstructed(World world, PlayerEntity player, BlockPos pos) {
        Vec3d start = new Vec3d(player.getX(), player.getY() + player.getHeight() / 2f, player.getZ());
        BlockHitResult hitResult = world.raycast(new RaycastContext(start, Vec3d.ofCenter(pos), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
        return hitResult.getType() != HitResult.Type.MISS && !hitResult.getBlockPos().equals(pos);
    }

    private static boolean isEnclosed(World world, BlockPos playerPos) {
        int enclosedRadius = Temperatures.getRoomHeat().enclosedRadius();
        BlockPos[] checkPositions = {playerPos.add(enclosedRadius, 1, 0), playerPos.add(-enclosedRadius, 1, 0), playerPos.add(0, 1, enclosedRadius), playerPos.add(0, 1, -enclosedRadius)};
        for (BlockPos pos : checkPositions) {
            if (world.isSkyVisible(pos)) {
                return false;
            }
        }
        return true;
    }

    private static int itemTemperature(PlayerEntity player, TemperatureManager temperatureManager) {
        int temperature = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            ItemTemperature itemTemperature = Temperatures.getItemTemperature(stack.getItem());
            if (itemTemperature == null) {
                continue;
            }
            if (itemTemperature.damage() != 0 && stack.isDamageable() && !stack.isIn(TagInit.ARMOR_ITEMS)) {
                int remainingDurability = stack.getMaxDamage() - stack.getDamage();
                if (remainingDurability <= 1) {
                    continue;
                }
                if (!player.isCreative()) {
                    if (remainingDurability - itemTemperature.damage() <= 0) {
                        stack.setDamage(0);
                    } else {
                        stack.damage(itemTemperature.damage(), player, slot);
                    }
                }
            }
            temperature += itemTemperature.temperature();
            addProtection(temperatureManager, itemTemperature.heatProtection(), itemTemperature.coldProtection());
        }
        return temperature;
    }

    private static int effectTemperature(PlayerEntity player, TemperatureManager temperatureManager) {
        int temperature = 0;
        int normal = Temperatures.getBodyTemperature().normal();
        int playerTemperature = temperatureManager.getPlayerTemperature();
        for (StatusEffectInstance statusEffectInstance : player.getStatusEffects()) {
            EffectTemperature effectTemperature = Temperatures.getEffectTemperature(statusEffectInstance.getEffectType().value());
            if (effectTemperature == null) {
                continue;
            }
            int value = effectTemperature.temperature();
            if ((playerTemperature < normal && value > 0) || (playerTemperature > normal && value < 0)) {
                temperature += value;
            }
            addProtection(temperatureManager, effectTemperature.heatProtection(), effectTemperature.coldProtection());
        }
        return temperature;
    }

    private static void addProtection(TemperatureManager temperatureManager, int heatProtection, int coldProtection) {
        BodyProtection limits = Temperatures.getBodyProtection();
        int currentHeatProtection = temperatureManager.getPlayerHeatProtectionAmount();
        if (heatProtection != 0 && currentHeatProtection < limits.maxHeat()) {
            temperatureManager.setPlayerHeatProtectionAmount(Math.min(currentHeatProtection + heatProtection, limits.maxHeat()));
        }
        int currentColdProtection = temperatureManager.getPlayerColdProtectionAmount();
        if (coldProtection != 0 && currentColdProtection < limits.maxCold()) {
            temperatureManager.setPlayerColdProtectionAmount(Math.min(currentColdProtection + coldProtection, limits.maxCold()));
        }
    }

    private static final class Breakdown {

        @Nullable
        private final StringBuilder debugText = ConfigInit.CONFIG.printInConsole ? new StringBuilder() : null;
        private int temperature;
        private int thermometerTemperature;

        private void add(String source, int value, boolean affectsThermometer) {
            this.temperature += value;
            if (affectsThermometer) {
                this.thermometerTemperature += value;
            }
            if (this.debugText != null) {
                this.debugText.append(' ').append(source).append(": ").append(value);
            }
        }

        private boolean isDebug() {
            return this.debugText != null;
        }

        private String getDebugText() {
            return this.debugText != null ? this.debugText.toString() : "";
        }

        private int getTemperature() {
            return this.temperature;
        }

        private int getThermometerTemperature() {
            return this.thermometerTemperature;
        }
    }
}
