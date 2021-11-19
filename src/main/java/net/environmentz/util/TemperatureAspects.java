package net.environmentz.util;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.thirst.ThirstManager;
import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.effect.ColdEffect;
import net.environmentz.effect.OverheatingEffect;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.EffectInit;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public class TemperatureAspects {

    public static int coldnessTimer;
    public static int dehydrationTimer;
    public static int acclimatizeTimer;
    private static int dryingTimer;

    public static void coldEnvironment(PlayerEntity playerEntity) {
        if (((PlayerEnvAccess) playerEntity).isColdEnvAffected()) {
            int warmClothingModifier = ColdEffect.warmClothingModifier(playerEntity);
            if (!playerEntity.hasStatusEffect(EffectInit.WARMING) && warmClothingModifier != ConfigInit.CONFIG.warm_armor_tick_modifier * 4 && !ColdEffect.isWarmBlockNearBy(playerEntity)) {
                coldnessTimer++;
                if (playerEntity.hasStatusEffect(EffectInit.WET) && dryingTimer % ConfigInit.CONFIG.wet_bonus_malus == 0) {
                    coldnessTimer += 1;
                }
                if (coldnessTimer >= (ConfigInit.CONFIG.cold_tick_interval + warmClothingModifier)) {
                    int coldDamageEffectTime = ConfigInit.CONFIG.cold_damage_effect_time;
                    if (playerEntity.world.getLevelProperties().isRaining()) {
                        coldDamageEffectTime += ConfigInit.CONFIG.cold_tick_snowing_bonus;
                    }
                    playerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.COLDNESS, coldDamageEffectTime, 0, false, false, true));
                    coldnessTimer = 0;
                }
            } else {
                if (coldnessTimer > 0) {
                    coldnessTimer = 0;
                }
            }
        }
    }

    public static void hotEnvironment(PlayerEntity playerEntity) {
        if (((PlayerEnvAccess) playerEntity).isHotEnvAffected()) {
            if (!playerEntity.hasStatusEffect(EffectInit.COOLING) && OverheatingEffect.wearsArmor(playerEntity) && !playerEntity.isTouchingWaterOrRain()
                    && playerEntity.world.isSkyVisible(playerEntity.getBlockPos()) && playerEntity.world.isDay()) {
                dehydrationTimer++;
                if (FabricLoader.getInstance().isModLoaded("dehydration")) {
                    if (dehydrationTimer % ConfigInit.CONFIG.overheating_dehydration_timer == 0) {
                        ThirstManager thirstManager = ((ThirstManagerAccess) playerEntity).getThirstManager(playerEntity);
                        thirstManager.addDehydration(ConfigInit.CONFIG.overheating_dehydration_thirst);
                    }
                }
                if (playerEntity.hasStatusEffect(EffectInit.WET) && dehydrationTimer % ConfigInit.CONFIG.wet_bonus_malus == 0) {
                    dehydrationTimer -= 1;
                }
                if (dehydrationTimer >= (ConfigInit.CONFIG.overheating_tick_interval)) {
                    if (FabricLoader.getInstance().isModLoaded("dehydration")) {
                        ThirstManager thirstManager = ((ThirstManagerAccess) playerEntity).getThirstManager(playerEntity);
                        if (thirstManager.getThirstLevel() < 14) {
                            playerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.OVERHEATING, ConfigInit.CONFIG.overheating_damage_effect_time, 0, false, false, true));
                        }
                    } else {
                        playerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.OVERHEATING, ConfigInit.CONFIG.overheating_damage_effect_time, 0, false, false, true));
                    }
                    dehydrationTimer = 0;
                }
            } else if (dehydrationTimer > 0) {
                dehydrationTimer = 0;
            }

        }
    }

    public static void acclimatize(PlayerEntity playerEntity) {
        if (playerEntity.hasStatusEffect(EffectInit.COLDNESS)) {
            if (ColdEffect.isWarmBlockNearBy(playerEntity) || playerEntity.world.getBiome(playerEntity.getBlockPos()).getTemperature() >= ConfigInit.CONFIG.acclimatize_biome_temp
                    || playerEntity.hasStatusEffect(EffectInit.WARMING)) {
                acclimatizeTimer++;
                if (acclimatizeTimer >= ConfigInit.CONFIG.heating_up_interval) {
                    int coldDuration = playerEntity.getStatusEffect(EffectInit.COLDNESS).getDuration();
                    playerEntity.removeStatusEffect(EffectInit.COLDNESS);
                    if (coldDuration > ConfigInit.CONFIG.heating_up_cold_tick_decrease) {
                        playerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.COLDNESS, coldDuration - ConfigInit.CONFIG.heating_up_cold_tick_decrease, 0, false, false, true));
                    }
                    acclimatizeTimer = 0;
                }
            } else if (acclimatizeTimer > 0) {
                acclimatizeTimer = 0;
            }
        } else if (playerEntity.hasStatusEffect(EffectInit.OVERHEATING)) {
            if (playerEntity.world.isNight() || playerEntity.world.getBiome(playerEntity.getBlockPos()).getTemperature() <= ConfigInit.CONFIG.acclimatize_biome_temp
                    || playerEntity.isTouchingWaterOrRain() || playerEntity.hasStatusEffect(EffectInit.COOLING)) {
                acclimatizeTimer++;
                if (FabricLoader.getInstance().isModLoaded("dehydration")) {
                    ThirstManager thirstManager = ((ThirstManagerAccess) playerEntity).getThirstManager(playerEntity);
                    if (thirstManager.getThirstLevel() > 17) {
                        if (acclimatizeTimer >= ConfigInit.CONFIG.cooling_down_interval) {
                            int coldDuration = playerEntity.getStatusEffect(EffectInit.OVERHEATING).getDuration();
                            playerEntity.removeStatusEffect(EffectInit.OVERHEATING);
                            if (coldDuration > ConfigInit.CONFIG.cooling_down_tick_decrease) {
                                playerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.OVERHEATING, coldDuration - ConfigInit.CONFIG.cooling_down_tick_decrease, 0, false, false, true));
                                thirstManager.addDehydration(ConfigInit.CONFIG.overheating_dehydration_thirst);
                            }
                            acclimatizeTimer = 0;
                        }
                    }
                } else {
                    if (acclimatizeTimer >= ConfigInit.CONFIG.cooling_down_interval) {
                        int coldDuration = playerEntity.getStatusEffect(EffectInit.OVERHEATING).getDuration();
                        playerEntity.removeStatusEffect(EffectInit.OVERHEATING);
                        if (coldDuration > ConfigInit.CONFIG.cooling_down_tick_decrease) {
                            playerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.OVERHEATING, coldDuration - ConfigInit.CONFIG.cooling_down_tick_decrease, 0, false, false, true));
                        }
                        acclimatizeTimer = 0;
                    }
                }
            }
        }
    }

    public static void dryOrWett(PlayerEntity playerEntity) {
        dryingTimer++;
        if (dryingTimer >= 5 && playerEntity.isTouchingWaterOrRain()) {
            playerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.WET, ConfigInit.CONFIG.wet_effect_time, 0, false, false, true));
            dryingTimer = 0;
        }
    }

}
