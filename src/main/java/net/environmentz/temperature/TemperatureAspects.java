package net.environmentz.temperature;

import net.dehydration.access.ThirstManagerAccess;
import net.environmentz.EnvironmentzMain;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.environmentz.mixin.access.EntityAccessor;
import net.environmentz.network.EnvironmentServerPacket;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TemperatureAspects {

    private static final boolean isDehydrationLoaded = FabricLoader.getInstance().isModLoaded("dehydration");

    private static final Identifier FREEZING = EnvironmentzMain.identifierOf("freezing_debuff");
    private static final Identifier OVERHEATING = EnvironmentzMain.identifierOf("overheating_debuff");
    private static final Identifier COLD = EnvironmentzMain.identifierOf("cold_debuff");
    private static final Identifier HOT = EnvironmentzMain.identifierOf("hot_debuff");
    private static final Identifier GENERAL = EnvironmentzMain.identifierOf("general_debuff");

    private static final EntityAttributeModifier FREEZING_DEBUFF = new EntityAttributeModifier(FREEZING, -0.25,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final EntityAttributeModifier OVERHEATING_DEBUFF = new EntityAttributeModifier(OVERHEATING, -0.3,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final EntityAttributeModifier COLD_DEBUFF = new EntityAttributeModifier(COLD, -0.08,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final EntityAttributeModifier HOT_DEBUFF = new EntityAttributeModifier(HOT, -0.12,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    private static final EntityAttributeModifier GENERAL_DEBUFF = new EntityAttributeModifier(GENERAL, -0.2,
            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    public static final RegistryKey<DamageType> FREEZING_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("environmentz", "freezing"));

    public static void tickPlayerEnvironment(TemperatureManager temperatureManager, PlayerEntity playerEntity, int environmentTickCount) {
        int calculatingTemperature = 0;
        int thermometerCalculatingTemperature = 0;
        float biomeTemperature = playerEntity.getWorld().getBiome(playerEntity.getBlockPos()).value().getTemperature();
        boolean isDay = playerEntity.getWorld().isDay();
        playerWetness(playerEntity, temperatureManager);
        int playerWetness = temperatureManager.getPlayerWetIntensityValue();
        boolean isSoaked = playerWetness >= Temperatures.getBodyWetness(1);
        boolean isSkyNotVisible = !playerEntity.getWorld().isSkyVisible(playerEntity.getBlockPos().up());

        Identifier dimensionIdentifier = playerEntity.getWorld().getRegistryKey().getValue();
        if (Temperatures.shouldUseOverworldTemperatures(dimensionIdentifier)) {
            dimensionIdentifier = Temperatures.OVERWORLD;
        }
        String debugString = "";

        // environmentCode 0: very_cold, 1: cold, 2: normal, 3: hot, 4: very_hot
        int environmentCode = 2;
        if (biomeTemperature < Temperatures.getBiomeTemperatures(1)) {
            environmentCode = 1;
            if (biomeTemperature < Temperatures.getBiomeTemperatures(0)) {
                environmentCode = 0;
            }
        } else if (biomeTemperature > Temperatures.getBiomeTemperatures(2)) {
            environmentCode = 3;
            if (biomeTemperature > Temperatures.getBiomeTemperatures(3)) {
                environmentCode = 4;
            }
        }

        // Standard/Day/Night
        if (Temperatures.shouldUseStandardTemperatures(dimensionIdentifier)) {
            int standardTemperature = Temperatures.getDimensionStandardTemperatures(dimensionIdentifier, environmentCode);
            calculatingTemperature += standardTemperature;
            thermometerCalculatingTemperature += standardTemperature;

            if (ConfigInit.CONFIG.printInConsole) {
                debugString += " Standard: " + standardTemperature;
            }
        } else {
            if (isDay) {
                int dayTemperature = Temperatures.getDimensionDayTemperatures(dimensionIdentifier, environmentCode);
                calculatingTemperature += dayTemperature;
                thermometerCalculatingTemperature += dayTemperature;

                if (ConfigInit.CONFIG.printInConsole) {
                    debugString += " Day: " + dayTemperature;
                }
            } else {
                int nightTemperature = Temperatures.getDimensionNightTemperatures(dimensionIdentifier, environmentCode);
                calculatingTemperature += nightTemperature;
                thermometerCalculatingTemperature += nightTemperature;

                if (ConfigInit.CONFIG.printInConsole) {
                    debugString += " Night: " + nightTemperature;
                }
            }
        }
        // Wetness
        if (playerWetness > 0) {
            if (isSoaked) {
                int soakedTemperature = Temperatures.getDimensionSoakedTemperatures(dimensionIdentifier, environmentCode);
                calculatingTemperature += soakedTemperature;

                if (ConfigInit.CONFIG.printInConsole) {
                    debugString += " Soaked: " + soakedTemperature;
                }
            } else {
                int wettTemperature = Temperatures.getDimensionWettTemperatures(dimensionIdentifier, environmentCode);
                calculatingTemperature += wettTemperature;

                if (ConfigInit.CONFIG.printInConsole) {
                    debugString += " Wett: " + wettTemperature;
                }
            }
        }
        // Shadow
        if (isSkyNotVisible) {
            int shadowTemperature = Temperatures.getDimensionShadowTemperatures(dimensionIdentifier, environmentCode);
            calculatingTemperature += shadowTemperature;
            thermometerCalculatingTemperature += shadowTemperature;

            if (ConfigInit.CONFIG.printInConsole) {
                debugString += " Shadow: " + shadowTemperature;
            }
        }
        // Sweat
        if (isDehydrationLoaded && environmentCode > 2) {
            if (ConfigInit.CONFIG.exhaustionInsteadDehydration) {
                if (playerEntity.getHungerManager().getFoodLevel() > 6) {
                    playerEntity.addExhaustion(ConfigInit.CONFIG.overheatingExhaustion);
                    int sweatTemperature = Temperatures.getDimensionSweatTemperatures(dimensionIdentifier, environmentCode - 3);
                    calculatingTemperature += sweatTemperature;

                    if (ConfigInit.CONFIG.printInConsole) {
                        debugString += " Sweat: " + sweatTemperature;
                    }
                }
            } else {
                if (((ThirstManagerAccess) playerEntity).getThirstManager().getThirstLevel() > 6) {
                    ((ThirstManagerAccess) playerEntity).getThirstManager().addDehydration(ConfigInit.CONFIG.overheatingExhaustion);
                    int sweatTemperature = Temperatures.getDimensionSweatTemperatures(dimensionIdentifier, environmentCode - 3);
                    calculatingTemperature += sweatTemperature;

                    if (ConfigInit.CONFIG.printInConsole) {
                        debugString += " Sweat: " + sweatTemperature;
                    }
                }
            }
        }
        // Armor
        int armorTemperature = playerArmorTemperature(playerEntity, dimensionIdentifier, environmentCode);
        calculatingTemperature += armorTemperature;
        if (ConfigInit.CONFIG.printInConsole) {
            debugString += " Armor: " + armorTemperature;
        }

        // Height
        int heightTemperature = playerHeight(playerEntity, dimensionIdentifier);
        calculatingTemperature += heightTemperature;
        thermometerCalculatingTemperature += heightTemperature;
        if (ConfigInit.CONFIG.printInConsole) {
            debugString += " Height: " + heightTemperature;
        }

        // Block Temperature
        HashMap<Integer, Integer> maxCountBlockMap = new HashMap<>();
        HashMap<Integer, Integer> maxCountFluidMap = new HashMap<>();

        boolean isEnclosed = false;
        int blockHeatSum = 0;
        if (isSkyNotVisible) {
            int skyAccessCount = 0;
            int enclosedRadius = Temperatures.getEnclosedRadius();
            BlockPos playerPos = playerEntity.getBlockPos();

            BlockPos[] checkPositions = {playerPos.add(enclosedRadius, 1, 0), playerPos.add(-enclosedRadius, 1, 0),
                    playerPos.add(0, 1, enclosedRadius), playerPos.add(0, 1, -enclosedRadius)};

            for (BlockPos pos : checkPositions) {
                if (playerEntity.getWorld().isSkyVisible(pos)) {
                    skyAccessCount++;
                }
            }

            isEnclosed = skyAccessCount == 0;
        }

        for (int i = 0; i <= (ConfigInit.CONFIG.heatBlockRadius * 2); i++) { // height
            int height = i;
            if (i > ConfigInit.CONFIG.heatBlockRadius) {
                height = -(i - ConfigInit.CONFIG.heatBlockRadius);
            }

            int radius = ConfigInit.CONFIG.heatBlockRadius + 2;
            int x, z, dx, dz;
            x = z = dx = 0;
            dz = -1;
            int t = radius;
            int maxI = t * t;
            for (int k = 0; k < maxI; k++) {
                if ((-radius / 2 <= x) && (x <= radius / 2) && (-radius / 2 <= z) && (z <= radius / 2)) {
                    BlockPos pos = playerEntity.getBlockPos().add(x, height, z);
                    BlockState state = playerEntity.getWorld().getBlockState(pos);
                    if (!state.isAir()) {
                        int rawId = Registries.BLOCK.getRawId(state.getBlock());
                        if (Temperatures.hasBlockTemperature(rawId)) {
                            boolean shouldContinue = false;
                            if (Temperatures.hasBlockProperty(rawId) && state.contains(Temperatures.getBlockProperty(rawId)) && !state.get(Temperatures.getBlockProperty(rawId))) {
                                shouldContinue = true;
                            }
                            if (!shouldContinue) {
                                BlockHitResult hitResult = playerEntity.getWorld()
                                        .raycast(new RaycastContext(new Vec3d(playerEntity.getX(), playerEntity.getY() + playerEntity.getHeight() / 2f, playerEntity.getZ()),
                                                new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,
                                                playerEntity));
                                if (hitResult.getType() != HitResult.Type.MISS && !hitResult.getBlockPos().equals(pos)) {
                                    shouldContinue = true;
                                }
                                if (!shouldContinue) {
                                    maxCountBlockMap.put(rawId, maxCountBlockMap.get(rawId) == null ? 1 : maxCountBlockMap.get(rawId) + 1);

                                    if (maxCountBlockMap.get(rawId) != null && maxCountBlockMap.get(rawId) > Temperatures.getBlockTemperature(rawId, -1)) {
                                        shouldContinue = true;
                                    }
                                    if (!shouldContinue) {
                                        int distance = (int) Math.sqrt(playerEntity.getBlockPos().getSquaredDistance(pos));
                                        int blockTemperature = Temperatures.getBlockTemperature(rawId, distance);
                                        calculatingTemperature += blockTemperature;
                                        thermometerCalculatingTemperature += blockTemperature;
                                        blockHeatSum += blockTemperature;
                                        if (ConfigInit.CONFIG.printInConsole) {
                                            debugString += " Block: " + blockTemperature + " : " + state.getBlock().getName().getString();
                                        }
                                    }
                                }
                            }
                        } else if (!state.getFluidState().isEmpty()) {
                            rawId = Registries.FLUID.getRawId(state.getFluidState().getFluid());
                            if (Temperatures.hasFluidTemperature(rawId)) {
                                maxCountFluidMap.put(rawId, maxCountFluidMap.get(rawId) == null ? 1 : maxCountFluidMap.get(rawId) + 1);
                                boolean shouldContinue = false;
                                if (maxCountFluidMap.get(rawId) != null && maxCountFluidMap.get(rawId) > Temperatures.getFluidTemperature(rawId, -1)) {
                                    shouldContinue = true;
                                }
                                if (!shouldContinue) {
                                    int distance = (int) Math.sqrt(playerEntity.getBlockPos().getSquaredDistance(pos));
                                    int fluidTemperature = Temperatures.getFluidTemperature(rawId, distance);
                                    calculatingTemperature += fluidTemperature;
                                    thermometerCalculatingTemperature += fluidTemperature;
                                    if (ConfigInit.CONFIG.printInConsole) {
                                        debugString += " Fluid: " + fluidTemperature + " : " + state.getBlock().getName().getString();
                                    }
                                }
                            }
                        }
                    }

                }
                if ((x == z) || ((x < 0) && (x == -z)) || ((x > 0) && (x == 1 - z))) {
                    t = dx;
                    dx = -dz;
                    dz = t;
                }
                x += dx;
                z += dz;
            }
        }
        if (isEnclosed && blockHeatSum > 0) {
            int roomBonus = (int) (blockHeatSum * Temperatures.getRoomHeatFactor());
            calculatingTemperature += roomBonus;
            thermometerCalculatingTemperature += roomBonus;

            if (ConfigInit.CONFIG.printInConsole) {
                debugString += " RoomHeat: " + roomBonus;
            }
        }

        // Item
        int itemTemperature = itemTemperature(playerEntity, temperatureManager);
        calculatingTemperature += itemTemperature;
        if (ConfigInit.CONFIG.printInConsole) {
            debugString += " Item: " + itemTemperature;
        }

        // Effect
        int effectTemperature = effectTemperature(playerEntity, temperatureManager);
        calculatingTemperature += effectTemperature;
        if (ConfigInit.CONFIG.printInConsole) {
            debugString += " Effect: " + effectTemperature;
        }

        // Acclimatization
        int playerTemperature = temperatureManager.getPlayerTemperature();
        int acclimatization = 0;
        if (Temperatures.getDimensionAcclimatization(dimensionIdentifier) != 1997) {
            acclimatization = Temperatures.getDimensionAcclimatization(dimensionIdentifier);
        } else {
            if (environmentCode == 1) {
                if (playerTemperature < Temperatures.getAcclimatization(6)) {
                    acclimatization += Temperatures.getAcclimatization(7);
                }
            } else if (environmentCode == 2) {
                if (playerTemperature < Temperatures.getAcclimatization(4)) {
                    acclimatization += Temperatures.getAcclimatization(5);
                } else if (playerTemperature > Temperatures.getAcclimatization(0)) {
                    acclimatization += Temperatures.getAcclimatization(1);
                }
            } else if (environmentCode == 3) {
                if (playerTemperature > Temperatures.getAcclimatization(2)) {
                    acclimatization += Temperatures.getAcclimatization(3);
                }
            }

        }
        calculatingTemperature += acclimatization;
        if (ConfigInit.CONFIG.printInConsole) {
            debugString += " Acclimatization: " + acclimatization;
        }

        // Protection and resistance
        int temperatureDifference = Math.abs(calculatingTemperature);
        if (environmentCode < 2) {
            if (calculatingTemperature < 0) {
                int coldResistance = temperatureManager.getPlayerColdResistance();
                if (coldResistance > 0) {
                    if (coldResistance >= temperatureDifference) {
                        calculatingTemperature = 0;
                        temperatureManager.setPlayerColdResistance(coldResistance - temperatureDifference);
                    } else {
                        calculatingTemperature += temperatureDifference;
                        temperatureManager.setPlayerColdResistance(0);
                    }
                }
                if (calculatingTemperature < 0) {
                    temperatureDifference = Math.abs(calculatingTemperature);
                    int coldProtection = temperatureManager.getPlayerColdProtectionAmount();
                    if (coldProtection > 0) {
                        if (coldProtection >= temperatureDifference) {
                            calculatingTemperature = 0;
                            temperatureManager.setPlayerColdProtectionAmount(coldProtection - temperatureDifference);
                        } else {
                            calculatingTemperature += temperatureDifference;
                            temperatureManager.setPlayerColdProtectionAmount(0);
                        }
                    }
                }
            }
        } else if (environmentCode > 2) {
            if (calculatingTemperature > 0) {
                int heatResistance = temperatureManager.getPlayerHeatResistance();
                if (heatResistance > 0) {
                    if (heatResistance >= temperatureDifference) {
                        calculatingTemperature = 0;
                        temperatureManager.setPlayerHeatResistance(heatResistance - temperatureDifference);
                    } else {
                        calculatingTemperature += temperatureDifference;
                        temperatureManager.setPlayerHeatResistance(0);
                    }
                }
                if (calculatingTemperature < 0) {
                    temperatureDifference = Math.abs(calculatingTemperature);
                    int heatProtection = temperatureManager.getPlayerHeatProtectionAmount();
                    if (heatProtection > 0) {
                        if (heatProtection >= temperatureDifference) {
                            calculatingTemperature = 0;
                            temperatureManager.setPlayerHeatProtectionAmount(heatProtection - temperatureDifference);
                        } else {
                            calculatingTemperature -= temperatureDifference;
                            temperatureManager.setPlayerHeatProtectionAmount(0);
                        }
                    }
                }
            }
        }

        // Thermometer Sync
        EnvironmentServerPacket.writeS2CThermometerPacket((ServerPlayerEntity) playerEntity, thermometerCalculatingTemperature);

        playerTemperature = temperatureManager.getPlayerTemperature() + calculatingTemperature;

        // Cutoff
        // if (environmentCode < 2 && playerTemperature > Temperatures.getBodyTemperatures(4)) {
        // playerTemperature = Temperatures.getBodyTemperatures(4);
        // } else if (environmentCode > 2 && playerTemperature < Temperatures.getBodyTemperatures(2)) {
        // playerTemperature = Temperatures.getBodyTemperatures(2);
        // } else if (environmentCode < 2 && playerTemperature < Temperatures.getBodyTemperatures(0)) {
        // playerTemperature = Temperatures.getBodyTemperatures(0);
        // } else if (environmentCode > 2 && playerTemperature > Temperatures.getBodyTemperatures(6)) {
        // playerTemperature = Temperatures.getBodyTemperatures(6);
        // }

        // New cutoff/strong acclimatization
        if (environmentCode < 2 && playerTemperature > Temperatures.getBodyTemperatures(4)) {
            playerTemperature += Temperatures.getAcclimatization(1) * 2;
        } else if (environmentCode > 2 && playerTemperature < Temperatures.getBodyTemperatures(2)) {
            playerTemperature += Temperatures.getAcclimatization(5) * 2;
        } else if (playerTemperature < Temperatures.getBodyTemperatures(0)) {
            playerTemperature = Temperatures.getBodyTemperatures(0);
        } else if (playerTemperature > Temperatures.getBodyTemperatures(6)) {
            playerTemperature = Temperatures.getBodyTemperatures(6);
        }

        if (!temperatureManager.isColdEnvAffected() && playerTemperature < 0) {
            if (playerTemperature < 0) {
                EnvironmentServerPacket.writeS2CTemperaturePacket((ServerPlayerEntity) playerEntity, playerTemperature, playerWetness);
            }
            playerTemperature = 0;
        }
        if (!temperatureManager.isHotEnvAffected() && playerTemperature > 0) {
            if (playerTemperature > 0) {
                EnvironmentServerPacket.writeS2CTemperaturePacket((ServerPlayerEntity) playerEntity, playerTemperature, playerWetness);
            }
            playerTemperature = 0;
        }

        // Debuffs
        if (playerTemperature != 0 && playerTemperature % 2 == 0) {
            EntityAttributeInstance entitySpeedAttributeInstance = playerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            EntityAttributeInstance entityStrengthAttributeInstance = playerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            EntityAttributeInstance entityAttackSpeedAttributeInstance = playerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
            if (playerTemperature > Temperatures.getBodyTemperatures(2) && playerTemperature < Temperatures.getBodyTemperatures(4)) {
                if (entitySpeedAttributeInstance.hasModifier(COLD))
                    entitySpeedAttributeInstance.removeModifier(COLD_DEBUFF);

                if (entityStrengthAttributeInstance.hasModifier(HOT))
                    entityStrengthAttributeInstance.removeModifier(HOT_DEBUFF);

            } else if (playerTemperature <= Temperatures.getBodyTemperatures(2)) {
                if (playerTemperature <= Temperatures.getBodyTemperatures(1)) {
                    if (!entitySpeedAttributeInstance.hasModifier(FREEZING)) {
                        entitySpeedAttributeInstance.addTemporaryModifier(FREEZING_DEBUFF);
                        if (!entityAttackSpeedAttributeInstance.hasModifier(GENERAL))
                            entityAttackSpeedAttributeInstance.addTemporaryModifier(GENERAL_DEBUFF);
                    }
                    if (entitySpeedAttributeInstance.hasModifier(COLD))
                        entitySpeedAttributeInstance.removeModifier(COLD_DEBUFF);
                } else {
                    if (!entitySpeedAttributeInstance.hasModifier(COLD)) {
                        entitySpeedAttributeInstance.addTemporaryModifier(COLD_DEBUFF);
                    }
                    if (entitySpeedAttributeInstance.hasModifier(FREEZING)) {
                        entitySpeedAttributeInstance.removeModifier(FREEZING_DEBUFF);
                        if (entityAttackSpeedAttributeInstance.hasModifier(GENERAL))
                            entityAttackSpeedAttributeInstance.removeModifier(GENERAL_DEBUFF);
                    }
                }
            } else if (playerTemperature >= Temperatures.getBodyTemperatures(4)) {
                if (playerTemperature >= Temperatures.getBodyTemperatures(5)) {
                    if (!entityStrengthAttributeInstance.hasModifier(OVERHEATING)) {
                        entityStrengthAttributeInstance.addTemporaryModifier(OVERHEATING_DEBUFF);
                        if (!entityAttackSpeedAttributeInstance.hasModifier(GENERAL))
                            entityAttackSpeedAttributeInstance.addTemporaryModifier(GENERAL_DEBUFF);
                    }
                    if (entityStrengthAttributeInstance.hasModifier(HOT))
                        entityStrengthAttributeInstance.removeModifier(HOT_DEBUFF);
                } else {
                    if (!entityStrengthAttributeInstance.hasModifier(HOT))
                        entityStrengthAttributeInstance.addTemporaryModifier(HOT_DEBUFF);
                    if (entityStrengthAttributeInstance.hasModifier(OVERHEATING)) {
                        entityStrengthAttributeInstance.removeModifier(OVERHEATING_DEBUFF);
                        if (entityAttackSpeedAttributeInstance.hasModifier(GENERAL))
                            entityAttackSpeedAttributeInstance.removeModifier(GENERAL_DEBUFF);
                    }
                }
            }

            if (ConfigInit.CONFIG.printInConsole) {
                debugString += " Cold Debuff: " + entitySpeedAttributeInstance.hasModifier(COLD);
                debugString += " Hot Debuff: " + entitySpeedAttributeInstance.hasModifier(HOT);
            }
        }

        // Debug Print
        if (ConfigInit.CONFIG.printInConsole) {
            System.out.println("Total: " + calculatingTemperature + " New Player Tmp: " + playerTemperature + debugString + " : " + environmentCode);
        }

        // Damage or exhaustion
        if (playerTemperature <= Temperatures.getBodyTemperatures(0)) {
            playerEntity.damage(createDamageSource(playerEntity), 1.0F);
        } else if (playerTemperature >= Temperatures.getBodyTemperatures(6)) {
            if (isDehydrationLoaded && !ConfigInit.CONFIG.exhaustionInsteadDehydration) {
                ((ThirstManagerAccess) playerEntity).getThirstManager().addDehydration(ConfigInit.CONFIG.overheatingExhaustion);
            } else {
                playerEntity.addExhaustion(ConfigInit.CONFIG.overheatingExhaustion);
            }
        }
        // Set temp and send packet
        if ((playerTemperature != 0 && playerTemperature % 2 == 0) || (playerWetness != 0 && playerWetness % 2 == 0)) {
            EnvironmentServerPacket.writeS2CTemperaturePacket((ServerPlayerEntity) playerEntity, playerTemperature, playerWetness);
        }
        temperatureManager.setPlayerTemperature(playerTemperature);
    }

    private static int playerArmorTemperature(PlayerEntity playerEntity, Identifier dimensionIdentifier, int environmentCode) {
        int returnValue = 0;
        for (int i = 0; i < playerEntity.getInventory().armor.size(); i++) {
            ItemStack stack = playerEntity.getInventory().armor.get(i);
            if (!stack.isEmpty()) {
                if (!stack.isIn(TagInit.NON_AFFECTING_ARMOR)) {
                    if ((stack.get(ItemInit.INSULATED) != null && stack.get(ItemInit.INSULATED)) || stack.isIn(TagInit.WARM_ARMOR)) {
                        returnValue += Temperatures.getDimensionInsulatedArmorTemperatures(dimensionIdentifier, environmentCode);
                    } else {
                        returnValue += Temperatures.getDimensionArmorTemperatures(dimensionIdentifier, environmentCode);
                    }
                }
                if (!stack.isIn(TagInit.WARM_ARMOR) && stack.get(ItemInit.ICED) != null) {
                    returnValue += Temperatures.getDimensionIcedArmorTemperatures(dimensionIdentifier, environmentCode);
                    int iced = stack.get(ItemInit.ICED) - 1;
                    stack.set(ItemInit.ICED, iced);
                    if (iced <= 0) {
                        stack.remove(ItemInit.ICED);
                    }
                }
            }
        }
        return returnValue;
    }

    private static void playerWetness(PlayerEntity playerEntity, TemperatureManager temperatureManager) {
        int waterIntensityValue = temperatureManager.getPlayerWetIntensityValue();
        if (playerEntity.isTouchingWaterOrRain()) {
            if (waterIntensityValue < Temperatures.getBodyWetness(0)) {
                if (playerEntity.isTouchingWater()) {
                    waterIntensityValue += Temperatures.getBodyWetness(2);
                } else if (((EntityAccessor) playerEntity).callIsBeingRainedOn()) {
                    waterIntensityValue += Temperatures.getBodyWetness(3);
                }
                temperatureManager.setPlayerWetIntensityValue(waterIntensityValue <= Temperatures.getBodyWetness(0) ? waterIntensityValue : Temperatures.getBodyWetness(0));
            }
        } else if (waterIntensityValue > 0) {
            waterIntensityValue += Temperatures.getBodyWetness(4);
            temperatureManager.setPlayerWetIntensityValue(waterIntensityValue > 0 ? waterIntensityValue : 0);
        }
    }

    private static int playerHeight(PlayerEntity playerEntity, Identifier dimensionIdentifier) {
        int returnValue = 0;
        int playerPositionHeight = playerEntity.getBlockY();
        if (playerPositionHeight < Temperatures.getDimensionHeightTemperatures(dimensionIdentifier, 5)) {
            if (playerPositionHeight < Temperatures.getDimensionHeightTemperatures(dimensionIdentifier, 4)) {
                returnValue += Temperatures.getDimensionHeightTemperatures(dimensionIdentifier, 0);
            } else {
                returnValue += Temperatures.getDimensionHeightTemperatures(dimensionIdentifier, 1);
            }
        } else if (playerPositionHeight > Temperatures.getDimensionHeightTemperatures(dimensionIdentifier, 6)) {
            if (playerPositionHeight > Temperatures.getDimensionHeightTemperatures(dimensionIdentifier, 7)) {
                returnValue += Temperatures.getDimensionHeightTemperatures(dimensionIdentifier, 3);
            } else {
                returnValue += Temperatures.getDimensionHeightTemperatures(dimensionIdentifier, 2);
            }
        }
        return returnValue;
    }

    private static int itemTemperature(PlayerEntity playerEntity, TemperatureManager temperatureManager) {
        int returnValue = 0;
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        playerEntity.getEquippedItems().forEach(stacks::add);

        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }
            int itemId = Registries.ITEM.getRawId(stack.getItem());
            if (Temperatures.hasItemTemperature(itemId)) {
                if (Temperatures.getItemValue(itemId, -1) != 0) {
                    if (stack.isDamageable() && !stack.isIn(TagInit.ARMOR_ITEMS)) {
                        if (stack.getMaxDamage() - stack.getDamage() > 1) {
                            if (!playerEntity.isCreative()) {
                                int damage = Temperatures.getItemValue(itemId, -1);
                                if (stack.getMaxDamage() - stack.getDamage() - damage <= 0) {
                                    stack.setDamage(0);
                                } else {
                                    stack.damage(damage, playerEntity, LivingEntity.getSlotForHand(playerEntity.getActiveHand()));
                                }
                            }
                        } else
                            continue;
                    }
                }
                returnValue += Temperatures.getItemValue(itemId, 0);
                if (Temperatures.getItemValue(itemId, 1) != 0 && Temperatures.getBodyProtection(0) > temperatureManager.getPlayerHeatProtectionAmount()) {
                    int heatProtectionAddition = Temperatures.getItemValue(itemId, 1) + temperatureManager.getPlayerHeatProtectionAmount();
                    if (heatProtectionAddition > Temperatures.getBodyProtection(0)) {
                        heatProtectionAddition = Temperatures.getBodyProtection(0);
                    }
                    temperatureManager.setPlayerHeatProtectionAmount(heatProtectionAddition);
                }
                if (Temperatures.getItemValue(itemId, 2) != 0 && Temperatures.getBodyProtection(1) > temperatureManager.getPlayerColdProtectionAmount()) {
                    int heatProtectionAddition = Temperatures.getItemValue(itemId, 2) + temperatureManager.getPlayerColdProtectionAmount();
                    if (heatProtectionAddition > Temperatures.getBodyProtection(1)) {
                        heatProtectionAddition = Temperatures.getBodyProtection(1);
                    }
                    temperatureManager.setPlayerColdProtectionAmount(heatProtectionAddition);
                }
            }
        }
        return returnValue;
    }

    private static int effectTemperature(PlayerEntity playerEntity, TemperatureManager temperatureManager) {
        int returnValue = 0;
        Iterator<StatusEffectInstance> iterator = playerEntity.getStatusEffects().iterator();
        if (iterator.hasNext()) {
            Identifier identifier = Registries.STATUS_EFFECT.getId(iterator.next().getEffectType().value());
            if (Temperatures.hasEffectTemperature(identifier)) {
                int effectValue = Temperatures.getEffectValue(identifier, 0);
                if ((temperatureManager.getPlayerTemperature() < Temperatures.getBodyTemperatures(3) && effectValue > 0)
                        || (temperatureManager.getPlayerTemperature() > Temperatures.getBodyTemperatures(3) && effectValue < 0)) {
                    returnValue += effectValue;
                }
                if (Temperatures.getEffectValue(identifier, 1) != 0 && Temperatures.getBodyProtection(0) > temperatureManager.getPlayerHeatProtectionAmount()) {
                    int heatProtectionAddition = Temperatures.getEffectValue(identifier, 1) + temperatureManager.getPlayerHeatProtectionAmount();
                    if (heatProtectionAddition > Temperatures.getBodyProtection(0)) {
                        heatProtectionAddition = Temperatures.getBodyProtection(0);
                    }
                    temperatureManager.setPlayerHeatProtectionAmount(heatProtectionAddition);
                }
                if (Temperatures.getEffectValue(identifier, 2) != 0 && Temperatures.getBodyProtection(1) > temperatureManager.getPlayerColdProtectionAmount()) {
                    int heatProtectionAddition = Temperatures.getEffectValue(identifier, 2) + temperatureManager.getPlayerColdProtectionAmount();
                    if (heatProtectionAddition > Temperatures.getBodyProtection(1)) {
                        heatProtectionAddition = Temperatures.getBodyProtection(1);
                    }
                    temperatureManager.setPlayerColdProtectionAmount(heatProtectionAddition);
                }
            }
        }
        return returnValue;
    }

    private static DamageSource createDamageSource(Entity entity) {
        return entity.getDamageSources().create(FREEZING_DAMAGE);
    }

}
