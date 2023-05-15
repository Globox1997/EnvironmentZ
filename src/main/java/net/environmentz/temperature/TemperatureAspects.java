package net.environmentz.temperature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.dehydration.access.ThirstManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.TagInit;
import net.environmentz.mixin.access.EntityAccessor;
import net.environmentz.network.EnvironmentServerPacket;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class TemperatureAspects {

    private static final boolean isDehydrationLoaded = FabricLoader.getInstance().isModLoaded("dehydration");

    private static final EntityAttributeModifier FREEZING_DEBUFF = new EntityAttributeModifier(UUID.fromString("92a0771a-744c-4bb9-b813-4c1048c80bb2"), "Freezing debuff", -0.3,
            EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private static final EntityAttributeModifier OVERHEATING_DEBUFF = new EntityAttributeModifier(UUID.fromString("852ecc8c-8f4b-4c98-912b-a79a1e4e0abe"), "Overheating debuff", -0.4,
            EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private static final EntityAttributeModifier COLD_DEBUFF = new EntityAttributeModifier(UUID.fromString("08898afc-5de5-4152-947f-98a5fc1cedb8"), "Cold debuff", -0.15,
            EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private static final EntityAttributeModifier HOT_DEBUFF = new EntityAttributeModifier(UUID.fromString("a5491cad-174c-4e41-a481-87ce0df45998"), "Hot debuff", -0.2,
            EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private static final EntityAttributeModifier GENERAL_DEBUFF = new EntityAttributeModifier(UUID.fromString("13552c59-835e-4cc8-b09b-306bbe4ee786"), "General debuff", -0.15,
            EntityAttributeModifier.Operation.MULTIPLY_BASE);

    /*
     * b16b275c-4bd4-400c-9c91-e1e6e8f86edb b154bd6d-8b9a-4f43-92d8-64d837a05f87 3b1997c0-8902-40e7-a96c-be40777e4a52 a8dad3e1-7d16-4e37-9e7b-213dee8c53f6 2e930d17-7080-4859-bda3-e8671cd2a023
     * 115b965d-8210-4965-8966-d7860e04d056 347bca75-d9a3-4ce1-88d8-4f28fc9b02dc 1627d656-7b8d-46bb-80d5-0cb28a759024 7c8137f1-c385-4ce7-90be-12ba5ef72caa
     */

    public static void tickPlayerEnvironment(TemperatureManager temperatureManager, PlayerEntity playerEntity, int environmentTickCount) {
        int calculatingTemperature = 0;
        int thermometerCalculatingTemperature = 0;
        float biomeTemperature = playerEntity.world.getBiome(playerEntity.getBlockPos()).value().getTemperature();
        boolean isDay = playerEntity.world.isDay();
        playerWetness(playerEntity, temperatureManager);
        int playerWetness = temperatureManager.getPlayerWetIntensityValue();
        boolean isSoaked = playerWetness >= Temperatures.getBodyWetness(1);
        boolean isInShadow = !playerEntity.world.isSkyVisible(playerEntity.getBlockPos());

        Identifier dimensionIdentifier = playerEntity.world.getRegistryKey().getValue();
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
        if (isInShadow) {
            int shadowTemperature = Temperatures.getDimensionShadowTemperatures(dimensionIdentifier, environmentCode);
            calculatingTemperature += shadowTemperature;
            thermometerCalculatingTemperature += shadowTemperature;

            if (ConfigInit.CONFIG.printInConsole) {
                debugString += " Shadow: " + shadowTemperature;
            }
        }
        // Sweat
        if (isDehydrationLoaded && environmentCode > 2) {
            if (ConfigInit.CONFIG.exhaustion_instead_dehydration) {
                if (playerEntity.getHungerManager().getFoodLevel() > 6) {
                    playerEntity.addExhaustion(ConfigInit.CONFIG.overheating_exhaustion);
                    int sweatTemperature = Temperatures.getDimensionSweatTemperatures(dimensionIdentifier, environmentCode - 3);
                    calculatingTemperature += sweatTemperature;

                    if (ConfigInit.CONFIG.printInConsole) {
                        debugString += " Sweat: " + sweatTemperature;
                    }
                }
            } else {
                if (((ThirstManagerAccess) playerEntity).getThirstManager().getThirstLevel() > 6) {
                    ((ThirstManagerAccess) playerEntity).getThirstManager().addDehydration(ConfigInit.CONFIG.overheating_exhaustion);
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
        HashMap<Integer, Integer> maxCountBlockMap = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> maxCountFluidMap = new HashMap<Integer, Integer>();

        for (int i = 0; i <= (ConfigInit.CONFIG.heat_block_radius * 2); i++) { // height
            int height = i;
            if (i > ConfigInit.CONFIG.heat_block_radius) {
                height = -(i - ConfigInit.CONFIG.heat_block_radius);
            }
            for (int u = -ConfigInit.CONFIG.heat_block_radius; u <= ConfigInit.CONFIG.heat_block_radius; u++) { // x
                for (int o = -ConfigInit.CONFIG.heat_block_radius; o <= ConfigInit.CONFIG.heat_block_radius; o++) {// z

                    // BlockPos pos = playerEntity.getBlockPos().add(i, u, o);
                    BlockPos pos = playerEntity.getBlockPos().add(u, height, o);
                    BlockState state = playerEntity.world.getBlockState(pos);
                    if (state.isAir()) {
                        continue;
                    }
                    int rawId = Registry.BLOCK.getRawId(state.getBlock());
                    if (Temperatures.hasBlockTemperature(rawId)) {
                        maxCountBlockMap.put(rawId, maxCountBlockMap.get(rawId) == null ? 1 : maxCountBlockMap.get(rawId) + 1);
                        if (maxCountBlockMap.get(rawId) != null && maxCountBlockMap.get(rawId) > Temperatures.getBlockTemperature(rawId, -1)) {
                            continue;
                        }
                        if (Temperatures.hasBlockProperty(rawId) && state.contains(Temperatures.getBlockProperty(rawId)) && !state.get(Temperatures.getBlockProperty(rawId))) {
                            continue;
                        }
                        int distance = (int) Math.sqrt(playerEntity.getBlockPos().getSquaredDistance(pos));
                        int blockTemperature = Temperatures.getBlockTemperature(rawId, distance);
                        calculatingTemperature += blockTemperature;
                        thermometerCalculatingTemperature += blockTemperature;
                        if (ConfigInit.CONFIG.printInConsole) {
                            debugString += " Block: " + blockTemperature + " : " + state.getBlock().getName().getString();
                        }
                    } else if (!state.getFluidState().isEmpty()) {
                        rawId = Registry.FLUID.getRawId(state.getFluidState().getFluid());
                        if (Temperatures.hasFluidTemperature(rawId)) {
                            maxCountFluidMap.put(rawId, maxCountFluidMap.get(rawId) == null ? 1 : maxCountFluidMap.get(rawId) + 1);
                            if (maxCountFluidMap.get(rawId) != null && maxCountFluidMap.get(rawId) > Temperatures.getFluidTemperature(rawId, -1))
                                continue;
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
            playerTemperature += Temperatures.getAcclimatization(5) * 2;
        } else if (environmentCode > 2 && playerTemperature < Temperatures.getBodyTemperatures(2)) {
            playerTemperature += Temperatures.getAcclimatization(1) * 2;
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
                if (entitySpeedAttributeInstance.hasModifier(COLD_DEBUFF))
                    entitySpeedAttributeInstance.removeModifier(COLD_DEBUFF);

                if (entityStrengthAttributeInstance.hasModifier(HOT_DEBUFF))
                    entityStrengthAttributeInstance.removeModifier(HOT_DEBUFF);

            } else if (playerTemperature <= Temperatures.getBodyTemperatures(2)) {
                if (playerTemperature <= Temperatures.getBodyTemperatures(1)) {
                    if (!entitySpeedAttributeInstance.hasModifier(FREEZING_DEBUFF)) {
                        entitySpeedAttributeInstance.addTemporaryModifier(FREEZING_DEBUFF);
                        if (!entityAttackSpeedAttributeInstance.hasModifier(GENERAL_DEBUFF))
                            entityAttackSpeedAttributeInstance.addTemporaryModifier(GENERAL_DEBUFF);
                    }
                    if (entitySpeedAttributeInstance.hasModifier(COLD_DEBUFF))
                        entitySpeedAttributeInstance.removeModifier(COLD_DEBUFF);
                } else {
                    if (!entitySpeedAttributeInstance.hasModifier(COLD_DEBUFF)) {
                        entitySpeedAttributeInstance.addTemporaryModifier(COLD_DEBUFF);
                    }
                    if (entitySpeedAttributeInstance.hasModifier(FREEZING_DEBUFF)) {
                        entitySpeedAttributeInstance.removeModifier(FREEZING_DEBUFF);
                        if (entityAttackSpeedAttributeInstance.hasModifier(GENERAL_DEBUFF))
                            entityAttackSpeedAttributeInstance.removeModifier(GENERAL_DEBUFF);
                    }
                }
            } else if (playerTemperature >= Temperatures.getBodyTemperatures(4)) {
                if (playerTemperature >= Temperatures.getBodyTemperatures(5)) {
                    if (!entityStrengthAttributeInstance.hasModifier(OVERHEATING_DEBUFF)) {
                        entityStrengthAttributeInstance.addTemporaryModifier(OVERHEATING_DEBUFF);
                        if (!entityAttackSpeedAttributeInstance.hasModifier(GENERAL_DEBUFF))
                            entityAttackSpeedAttributeInstance.addTemporaryModifier(GENERAL_DEBUFF);
                    }
                    if (entityStrengthAttributeInstance.hasModifier(HOT_DEBUFF))
                        entityStrengthAttributeInstance.removeModifier(HOT_DEBUFF);
                } else {
                    if (!entityStrengthAttributeInstance.hasModifier(HOT_DEBUFF))
                        entityStrengthAttributeInstance.addTemporaryModifier(HOT_DEBUFF);
                    if (entityStrengthAttributeInstance.hasModifier(OVERHEATING_DEBUFF)) {
                        entityStrengthAttributeInstance.removeModifier(OVERHEATING_DEBUFF);
                        if (entityAttackSpeedAttributeInstance.hasModifier(GENERAL_DEBUFF))
                            entityAttackSpeedAttributeInstance.removeModifier(GENERAL_DEBUFF);
                    }
                }
            }

            if (ConfigInit.CONFIG.printInConsole) {
                debugString += " Cold Debuff: " + entitySpeedAttributeInstance.hasModifier(COLD_DEBUFF);
                debugString += " Hot Debuff: " + entitySpeedAttributeInstance.hasModifier(HOT_DEBUFF);
            }
        }

        // Debug Print
        if (ConfigInit.CONFIG.printInConsole) {
            System.out.println("Total: " + calculatingTemperature + " New Player Tmp: " + playerTemperature + debugString + " : " + environmentCode);
        }

        // Damage or exhaustion
        if (playerTemperature <= Temperatures.getBodyTemperatures(0)) {
            playerEntity.damage(FREEZING_DAMAGE, 1.0F);
        } else if (playerTemperature >= Temperatures.getBodyTemperatures(6)) {
            if (isDehydrationLoaded && !ConfigInit.CONFIG.exhaustion_instead_dehydration) {
                ((ThirstManagerAccess) playerEntity).getThirstManager().addDehydration(ConfigInit.CONFIG.overheating_exhaustion);
            } else {
                playerEntity.addExhaustion(ConfigInit.CONFIG.overheating_exhaustion);
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
                    if ((stack.hasNbt() && stack.getNbt().contains("environmentz")) || stack.isIn(TagInit.WARM_ARMOR)) {
                        returnValue += Temperatures.getDimensionInsulatedArmorTemperatures(dimensionIdentifier, environmentCode);
                    } else {
                        returnValue += Temperatures.getDimensionArmorTemperatures(dimensionIdentifier, environmentCode);
                    }
                }
                if (!stack.isIn(TagInit.WARM_ARMOR) && stack.hasNbt() && stack.getNbt().contains("iced")) {
                    returnValue += Temperatures.getDimensionIcedArmorTemperatures(dimensionIdentifier, environmentCode);
                    int iced = stack.getNbt().getInt("iced") - 1;
                    stack.getNbt().putInt("iced", iced);
                    if (iced <= 0) {
                        stack.getNbt().remove("iced");
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
        stacks.add(playerEntity.getStackInHand(Hand.MAIN_HAND));
        stacks.add(playerEntity.getStackInHand(Hand.OFF_HAND));
        playerEntity.getItemsEquipped().forEach((stack) -> {
            stacks.add(stack);
        });

        for (int i = 0; i < stacks.size(); i++) {
            if (stacks.get(i).isEmpty()) {
                continue;
            }
            int itemId = Registry.ITEM.getRawId(stacks.get(i).getItem());
            if (Temperatures.hasItemTemperature(itemId)) {
                if (Temperatures.getItemValue(itemId, -1) != 0) {
                    if (stacks.get(i).isDamageable() && !stacks.get(i).isIn(TagInit.ARMOR_ITEMS)) {
                        if (stacks.get(i).getMaxDamage() - stacks.get(i).getDamage() > 1) {
                            if (!playerEntity.isCreative()) {
                                int damage = Temperatures.getItemValue(itemId, -1);
                                if (stacks.get(i).getMaxDamage() - stacks.get(i).getDamage() - damage <= 0) {
                                    stacks.get(i).setDamage(0);
                                } else {
                                    stacks.get(i).damage(damage, playerEntity, (p) -> p.sendToolBreakStatus(p.getActiveHand()));
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
            Identifier identifier = Registry.STATUS_EFFECT.getId(iterator.next().getEffectType());
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

    private static final DamageSource FREEZING_DAMAGE = new DamageSource("freezing") {

        @Override
        public boolean bypassesArmor() {
            return true;
        }

        @Override
        public boolean isUnblockable() {
            return true;
        }

        @Override
        public float getExhaustion() {
            return 0.1F;
        }
    };

}
