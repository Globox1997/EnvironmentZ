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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public class TemperatureAspects {

    private static final AttributeModifier FREEZING_DEBUFF = new AttributeModifier(EnvironmentzMain.identifierOf("freezing_debuff"), -0.25,
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final AttributeModifier OVERHEATING_DEBUFF = new AttributeModifier(EnvironmentzMain.identifierOf("overheating_debuff"), -0.3,
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final AttributeModifier COLD_DEBUFF = new AttributeModifier(EnvironmentzMain.identifierOf("cold_debuff"), -0.08,
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final AttributeModifier HOT_DEBUFF = new AttributeModifier(EnvironmentzMain.identifierOf("hot_debuff"), -0.12,
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final AttributeModifier GENERAL_DEBUFF = new AttributeModifier(EnvironmentzMain.identifierOf("general_debuff"), -0.2,
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    public static final ResourceKey<DamageType> FREEZING_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, EnvironmentzMain.identifierOf("freezing"));

    @Nullable
    private static HeatScanArea heatScanArea;

    public static void tickPlayerEnvironment(TemperatureManager temperatureManager, ServerPlayer player) {
        Level world = player.level();
        BlockPos playerPos = player.blockPosition();
        Breakdown breakdown = new Breakdown();

        float biomeTemperature = world.getBiome(playerPos).value().getBaseTemperature();
        EnvironmentCode environmentCode = EnvironmentCode.fromBiomeTemperature(biomeTemperature, Temperatures.getBiomeTemperature());
        DimensionTemperatures dimension = Temperatures.getDimension(world.dimension().location());

        updateWetness(player, temperatureManager);
        int wetness = temperatureManager.getPlayerWetIntensityValue();
        boolean isSkyNotVisible = !world.canSeeSky(playerPos.above());

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
            breakdown.add("Sweat", sweatTemperature(player, temperatureManager, dimension, environmentCode), false);
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
        int playerTemperature = applyBodyLimits(oldTemperature, oldTemperature + temperatureChange, environmentCode);
        int bodyLimit = playerTemperature - (oldTemperature + temperatureChange);
        if ((!temperatureManager.isColdEnvAffected() && playerTemperature < 0) || (!temperatureManager.isHotEnvAffected() && playerTemperature > 0)) {
            playerTemperature = 0;
        }

        updateDebuffs(player, playerTemperature);

        if (breakdown.isDebug()) {
            EnvironmentzMain.LOGGER.info("Total: {} Body Limit: {} New Player Tmp: {}{} Cold Debuff: {} Hot Debuff: {} : {}", temperatureChange, bodyLimit, playerTemperature,
                    breakdown.getDebugText(),
                    hasModifier(player, Attributes.MOVEMENT_SPEED, COLD_DEBUFF), hasModifier(player, Attributes.ATTACK_DAMAGE, HOT_DEBUFF), environmentCode);
        }

        BodyTemperature bodyTemperature = Temperatures.getBodyTemperature();
        if (playerTemperature <= bodyTemperature.maxVeryCold()) {
            player.hurt(player.damageSources().source(FREEZING_DAMAGE), 1.0F);
        } else if (playerTemperature >= bodyTemperature.maxVeryHot()) {
            if (DehydrationCompat.IS_LOADED && !ConfigInit.CONFIG.exhaustionInsteadDehydration) {
                DehydrationCompat.addDehydration(player, ConfigInit.CONFIG.overheatingExhaustion);
            } else {
                player.causeFoodExhaustion(ConfigInit.CONFIG.overheatingExhaustion);
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

    public static int applyBodyLimits(int oldTemperature, int temperature, EnvironmentCode environmentCode) {
        BodyTemperature bodyTemperature = Temperatures.getBodyTemperature();
        int min = environmentCode.isHot() ? Math.min(oldTemperature, bodyTemperature.minCold() + 1) : bodyTemperature.maxVeryCold();
        int max = environmentCode.isCold() ? Math.max(oldTemperature, bodyTemperature.minHot() - 1) : bodyTemperature.maxVeryHot();
        return Mth.clamp(temperature, Math.max(min, bodyTemperature.maxVeryCold()), Math.min(max, bodyTemperature.maxVeryHot()));
    }

    private static int absorb(int temperature, int buffer, IntConsumer bufferSetter) {
        if (buffer <= 0 || temperature == 0) {
            return temperature;
        }
        int absorbed = Math.min(buffer, Math.abs(temperature));
        bufferSetter.accept(buffer - absorbed);
        return temperature - Integer.signum(temperature) * absorbed;
    }

    private static void updateDebuffs(Player player, int temperature) {
        BodyTemperature bodyTemperature = Temperatures.getBodyTemperature();
        boolean freezing = temperature <= bodyTemperature.maxCold();
        boolean cold = !freezing && temperature <= bodyTemperature.minCold();
        boolean overheating = temperature >= bodyTemperature.maxHot();
        boolean hot = !overheating && temperature >= bodyTemperature.minHot();

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        setModifier(speed, FREEZING_DEBUFF, freezing);
        setModifier(speed, COLD_DEBUFF, cold);
        setModifier(attackDamage, OVERHEATING_DEBUFF, overheating);
        setModifier(attackDamage, HOT_DEBUFF, hot);
        setModifier(attackSpeed, GENERAL_DEBUFF, freezing || overheating);
    }

    private static void setModifier(@Nullable AttributeInstance instance, AttributeModifier modifier, boolean active) {
        if (instance == null) {
            return;
        }
        boolean present = instance.hasModifier(modifier.id());
        if (active && !present) {
            instance.addTransientModifier(modifier);
        } else if (!active && present) {
            instance.removeModifier(modifier.id());
        }
    }

    private static boolean hasModifier(Player player, Holder<Attribute> attribute, AttributeModifier modifier) {
        AttributeInstance instance = player.getAttribute(attribute);
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

    private static int sweatTemperature(Player player, TemperatureManager temperatureManager, DimensionTemperatures dimension, EnvironmentCode environmentCode) {
        if (temperatureManager.getPlayerTemperature() <= Temperatures.getBodyTemperature().normal()) {
            return 0;
        }
        float exhaustion = ConfigInit.CONFIG.overheatingExhaustion;
        if (ConfigInit.CONFIG.exhaustionInsteadDehydration) {
            if (player.getFoodData().getFoodLevel() <= 6) {
                return 0;
            }
            player.causeFoodExhaustion(exhaustion);
        } else {
            if (DehydrationCompat.getThirstLevel(player) <= 6) {
                return 0;
            }
            DehydrationCompat.addDehydration(player, exhaustion);
        }
        return dimension.sweat().get(environmentCode);
    }

    private static int armorTemperature(Player player, DimensionTemperatures dimension, EnvironmentCode environmentCode) {
        float temperature = 0.0F;
        for (ItemStack stack : player.getInventory().armor) {
            if (stack.isEmpty()) {
                continue;
            }
            boolean isWarmArmor = stack.is(TagInit.WARM_ARMOR);
            if (!stack.is(TagInit.NON_AFFECTING_ARMOR)) {
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

    private static void updateWetness(Player player, TemperatureManager temperatureManager) {
        BodyWetness bodyWetness = Temperatures.getBodyWetness();
        int wetness = temperatureManager.getPlayerWetIntensityValue();
        if (player.isInWaterOrRain()) {
            if (wetness < bodyWetness.max()) {
                if (player.isInWater()) {
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

    private static void addBlockTemperatures(Player player, Breakdown breakdown, boolean isSkyNotVisible) {
        Level world = player.level();
        BlockPos playerPos = player.blockPosition();
        Map<Block, Integer> blockCounts = new HashMap<>();
        Map<Fluid, Integer> fluidCounts = new HashMap<>();
        HeatScanArea scanArea = getHeatScanArea(ConfigInit.CONFIG.heatBlockRadius);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int blockHeatSum = 0;

        for (int i = 0; i < scanArea.offsets().length; i++) {
            pos.setWithOffset(playerPos, scanArea.offsets()[i]);
            blockHeatSum += addBlockTemperature(world, player, pos, scanArea.distances()[i], blockCounts, fluidCounts, breakdown);
        }

        if (isSkyNotVisible && blockHeatSum > 0 && isEnclosed(world, playerPos)) {
            breakdown.add("RoomHeat", (int) (blockHeatSum * Temperatures.getRoomHeat().factor()), true);
        }
    }

    private static HeatScanArea getHeatScanArea(int radius) {
        HeatScanArea area = heatScanArea;
        if (area == null || area.radius() != radius) {
            area = HeatScanArea.create(radius);
            heatScanArea = area;
        }
        return area;
    }

    private static int addBlockTemperature(Level world, Player player, BlockPos pos, int distance, Map<Block, Integer> blockCounts, Map<Fluid, Integer> fluidCounts,
                                           Breakdown breakdown) {
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) {
            return 0;
        }
        BlockTemperature blockTemperature = Temperatures.getBlockTemperature(state.getBlock());
        if (blockTemperature != null) {
            BooleanProperty property = blockTemperature.property();
            if (property != null && state.hasProperty(property) && !state.getValue(property)) {
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
            Fluid fluid = state.getFluidState().getType();
            BlockTemperature fluidTemperature = Temperatures.getFluidTemperature(fluid);
            if (fluidTemperature != null && fluidCounts.merge(fluid, 1, Integer::sum) <= fluidTemperature.maxCount()) {
                breakdown.add(breakdown.isDebug() ? "Fluid " + state.getBlock().getName().getString() : "Fluid", fluidTemperature.getTemperature(distance), true);
            }
        }
        return 0;
    }

    private static boolean isObstructed(Level world, Player player, BlockPos pos) {
        Vec3 start = new Vec3(player.getX(), player.getY() + player.getBbHeight() / 2f, player.getZ());
        BlockHitResult hitResult = world.clip(new ClipContext(start, Vec3.atCenterOf(pos), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return hitResult.getType() != HitResult.Type.MISS && !hitResult.getBlockPos().equals(pos);
    }

    private static boolean isEnclosed(Level world, BlockPos playerPos) {
        int enclosedRadius = Temperatures.getRoomHeat().enclosedRadius();
        BlockPos[] checkPositions = {playerPos.offset(enclosedRadius, 1, 0), playerPos.offset(-enclosedRadius, 1, 0), playerPos.offset(0, 1, enclosedRadius), playerPos.offset(0, 1, -enclosedRadius)};
        for (BlockPos pos : checkPositions) {
            if (world.canSeeSky(pos)) {
                return false;
            }
        }
        return true;
    }

    private static int itemTemperature(Player player, TemperatureManager temperatureManager) {
        int temperature = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            ItemTemperature itemTemperature = Temperatures.getItemTemperature(stack.getItem());
            if (itemTemperature == null) {
                continue;
            }
            if (itemTemperature.damage() != 0 && stack.isDamageableItem() && !stack.is(TagInit.ARMOR_ITEMS)) {
                int remainingDurability = stack.getMaxDamage() - stack.getDamageValue();
                if (remainingDurability <= 1) {
                    continue;
                }
                if (!player.isCreative()) {
                    if (remainingDurability - itemTemperature.damage() <= 0) {
                        stack.setDamageValue(stack.getMaxDamage() - 1);
                    } else {
                        stack.hurtAndBreak(itemTemperature.damage(), player, slot);
                    }
                }
            }
            temperature += itemTemperature.temperature();
            addProtection(temperatureManager, itemTemperature.heatProtection(), itemTemperature.coldProtection());
        }
        return temperature;
    }

    private static int effectTemperature(Player player, TemperatureManager temperatureManager) {
        int temperature = 0;
        int normal = Temperatures.getBodyTemperature().normal();
        int playerTemperature = temperatureManager.getPlayerTemperature();
        for (MobEffectInstance statusEffectInstance : player.getActiveEffects()) {
            EffectTemperature effectTemperature = Temperatures.getEffectTemperature(statusEffectInstance.getEffect().value());
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

    private record HeatScanArea(int radius, Vec3i[] offsets, int[] distances) {

        private static HeatScanArea create(int radius) {
            int range = Math.max(radius, 0);
            List<Vec3i> offsets = new ArrayList<>();
            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        if (distance(x * x + y * y + z * z) <= range) {
                            offsets.add(new Vec3i(x, y, z));
                        }
                    }
                }
            }
            offsets.sort(Comparator.comparingInt(HeatScanArea::squaredLength));
            int[] distances = new int[offsets.size()];
            for (int i = 0; i < distances.length; i++) {
                distances[i] = distance(squaredLength(offsets.get(i)));
            }
            return new HeatScanArea(radius, offsets.toArray(new Vec3i[0]), distances);
        }

        private static int squaredLength(Vec3i offset) {
            return offset.getX() * offset.getX() + offset.getY() * offset.getY() + offset.getZ() * offset.getZ();
        }

        private static int distance(int squaredLength) {
            return (int) Math.sqrt(squaredLength);
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
