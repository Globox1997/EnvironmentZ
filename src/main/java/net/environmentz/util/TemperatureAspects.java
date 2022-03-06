package net.environmentz.util;

import java.util.List;
import java.util.UUID;

import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.thirst.ThirstManager;
import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.TagInit;
import net.environmentz.mixin.access.EntityAccessor;
import net.environmentz.network.EnvironmentServerPacket;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

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

    // Called one time in a second
    // 0.8 is normal temperature
    public static void tickPlayerEnvironment(PlayerEntity playerEntity, int timer) {
        int playerTemperature = ((PlayerEnvAccess) playerEntity).getPlayerTemperature();
        float biomeTemperature = playerEntity.world.getBiome(playerEntity.getBlockPos()).value().getTemperature();

        // System.out.println("Temperature: " + playerTemperature + " :: " + biomeTemperature + " :: " + ((PlayerEnvAccess) playerEntity).getPlayerColdProtectionAmount() + " :: "
        // + ((PlayerEnvAccess) playerEntity).getPlayerWetIntensityValue());

        // Cold environment
        if (biomeTemperature <= ConfigInit.CONFIG.biome_cold_temp) {
            if (((PlayerEnvAccess) playerEntity).isColdEnvAffected()) {
                int protectiveArmorValue = wearingProtectiveArmorValue(playerEntity);
                if (protectiveArmorValue != 5) {
                    int coldProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerColdProtectionAmount();
                    if (coldProtectionAmount <= 0) {
                        if (timer % protectiveArmorValue == 0) {
                            if (playerTemperature > -120)
                                playerTemperature = playerTemperature - (((PlayerEnvAccess) playerEntity).getPlayerWetIntensityValue() > 0 ? 2 : 1);

                            if (biomeTemperature <= ConfigInit.CONFIG.biome_freeze_temp)
                                if (playerTemperature > -240)
                                    playerTemperature = playerTemperature - 1;
                        }
                    } else
                        ((PlayerEnvAccess) playerEntity).setPlayerColdProtectionAmount(coldProtectionAmount - (biomeTemperature <= ConfigInit.CONFIG.biome_freeze_temp ? 2 : 1));
                }
            }
            // Hot environment
        } else if (playerEntity.world.getBiome(playerEntity.getBlockPos()).value().getTemperature() >= ConfigInit.CONFIG.biome_hot_temp) {
            if (((PlayerEnvAccess) playerEntity).isHotEnvAffected()) {
                int armorPartsValue = wearsArmorPartsValue(playerEntity);
                if (armorPartsValue > 0 && playerEntity.world.isSkyVisible(playerEntity.getBlockPos()) && playerEntity.world.isDay()) {
                    int hotProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerHeatProtectionAmount();
                    if (hotProtectionAmount <= 0) {
                        if (playerTemperature < 120)
                            playerTemperature = playerTemperature + (((PlayerEnvAccess) playerEntity).getPlayerWetIntensityValue() > 0 ? 1 : 2) + armorPartsValue;
                        if (biomeTemperature >= ConfigInit.CONFIG.biome_overheat_temp) {
                            if (playerTemperature < 240)
                                playerTemperature = playerTemperature + 1;
                            if (isDehydrationLoaded)
                                ((ThirstManagerAccess) playerEntity).getThirstManager(playerEntity).addDehydration(ConfigInit.CONFIG.overheating_exhaustion);
                        }
                        if (isDehydrationLoaded) {
                            ThirstManager thirstManager = ((ThirstManagerAccess) playerEntity).getThirstManager(playerEntity);
                            if (thirstManager.getThirstLevel() > 8)
                                playerTemperature = playerTemperature - 1;
                        }
                    } else
                        ((PlayerEnvAccess) playerEntity).setPlayerHeatProtectionAmount(hotProtectionAmount - (biomeTemperature >= ConfigInit.CONFIG.biome_overheat_temp ? 2 : 1));
                }
            }
            // Neutral environment
        } else {
            if (playerTemperature < 0)
                playerTemperature = playerTemperature + (playerEntity.isSprinting() ? 2 : 1);
            else if (playerTemperature > 0)
                playerTemperature = playerTemperature - 1;
        }
        // Wet calculation
        if (((PlayerEnvAccess) playerEntity).getPlayerWetIntensityValue() > 0) {
            int wetRemovalValue = 1;
            if (((PlayerEnvAccess) playerEntity).getPlayerColdProtectionAmount() > 0)
                wetRemovalValue += 2;
            ((PlayerEnvAccess) playerEntity).setPlayerWetIntensityValue(((PlayerEnvAccess) playerEntity).getPlayerWetIntensityValue() - wetRemovalValue);
        }
        // Wetness set
        if (playerEntity.isTouchingWaterOrRain()) {
            if (playerEntity.isTouchingWater())
                ((PlayerEnvAccess) playerEntity).setPlayerWetIntensityValue(120);
            else if (((EntityAccessor) playerEntity).callIsBeingRainedOn()) {
                int waterIntensityValue = ((PlayerEnvAccess) playerEntity).getPlayerWetIntensityValue();
                if (waterIntensityValue < 120)
                    ((PlayerEnvAccess) playerEntity).setPlayerWetIntensityValue(waterIntensityValue + 6);
            }

        }
        // Attributes on temperature
        if (playerTemperature != 0 && playerTemperature % 10 == 0) {
            EntityAttributeInstance entitySpeedAttributeInstance = playerEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            EntityAttributeInstance entityStrengthAttributeInstance = playerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            EntityAttributeInstance entityAttackSpeedAttributeInstance = playerEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
            if (playerTemperature > -30 && playerTemperature < 30) {
                if (entitySpeedAttributeInstance.hasModifier(COLD_DEBUFF))
                    entitySpeedAttributeInstance.removeModifier(COLD_DEBUFF);

                if (entityStrengthAttributeInstance.hasModifier(HOT_DEBUFF))
                    entityStrengthAttributeInstance.removeModifier(HOT_DEBUFF);

            } else if (playerTemperature <= -120) {
                int coldProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerColdProtectionAmount();
                if (playerTemperature <= -220) {
                    if (!entitySpeedAttributeInstance.hasModifier(FREEZING_DEBUFF) && coldProtectionAmount <= 0) {
                        entitySpeedAttributeInstance.addTemporaryModifier(FREEZING_DEBUFF);
                        if (!entityAttackSpeedAttributeInstance.hasModifier(GENERAL_DEBUFF))
                            entityAttackSpeedAttributeInstance.addTemporaryModifier(GENERAL_DEBUFF);
                    }
                    if (entitySpeedAttributeInstance.hasModifier(COLD_DEBUFF))
                        entitySpeedAttributeInstance.removeModifier(COLD_DEBUFF);
                } else {
                    if (!entitySpeedAttributeInstance.hasModifier(COLD_DEBUFF) && coldProtectionAmount <= 0) {
                        entitySpeedAttributeInstance.addTemporaryModifier(COLD_DEBUFF);
                    }
                    if (entitySpeedAttributeInstance.hasModifier(FREEZING_DEBUFF)) {
                        entitySpeedAttributeInstance.removeModifier(FREEZING_DEBUFF);
                        if (entityAttackSpeedAttributeInstance.hasModifier(GENERAL_DEBUFF))
                            entityAttackSpeedAttributeInstance.removeModifier(GENERAL_DEBUFF);
                    }
                }

            } else if (playerTemperature >= 120) {
                int hotProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerHeatProtectionAmount();
                if (playerTemperature >= 220) {
                    if (!entityStrengthAttributeInstance.hasModifier(OVERHEATING_DEBUFF) && hotProtectionAmount <= 0) {
                        entityStrengthAttributeInstance.addTemporaryModifier(OVERHEATING_DEBUFF);
                        if (!entityAttackSpeedAttributeInstance.hasModifier(GENERAL_DEBUFF))
                            entityAttackSpeedAttributeInstance.addTemporaryModifier(GENERAL_DEBUFF);
                    }
                    if (entityStrengthAttributeInstance.hasModifier(HOT_DEBUFF))
                        entityStrengthAttributeInstance.removeModifier(HOT_DEBUFF);
                } else {
                    if (!entityStrengthAttributeInstance.hasModifier(HOT_DEBUFF) && hotProtectionAmount <= 0)
                        entityStrengthAttributeInstance.addTemporaryModifier(HOT_DEBUFF);
                    if (entityStrengthAttributeInstance.hasModifier(OVERHEATING_DEBUFF)) {
                        entityStrengthAttributeInstance.removeModifier(OVERHEATING_DEBUFF);
                        if (entityAttackSpeedAttributeInstance.hasModifier(GENERAL_DEBUFF))
                            entityAttackSpeedAttributeInstance.removeModifier(GENERAL_DEBUFF);
                    }
                }

            }
        }

        if (playerTemperature <= -240)
            playerEntity.damage(FREEZING_DAMAGE, 1.0F);
        else if (playerTemperature >= 240) {
            if (isDehydrationLoaded)
                ((ThirstManagerAccess) playerEntity).getThirstManager(playerEntity).addDehydration(ConfigInit.CONFIG.overheating_exhaustion);
            else
                playerEntity.addExhaustion(ConfigInit.CONFIG.overheating_exhaustion);
        }
        // Set temp and send packet
        ((PlayerEnvAccess) playerEntity).setPlayerTemperature(playerTemperature);
        if ((playerTemperature != 0 && playerTemperature % 2 == 0)
                || (((PlayerEnvAccess) playerEntity).getPlayerWetIntensityValue() != 0 && ((PlayerEnvAccess) playerEntity).getPlayerWetIntensityValue() % 2 == 0))
            EnvironmentServerPacket.writeS2CTemperaturePacket((ServerPlayerEntity) playerEntity, playerTemperature, ((PlayerEnvAccess) playerEntity).getPlayerWetIntensityValue());

    }

    public static int wearsArmorPartsValue(PlayerEntity playerEntity) {
        ItemStack headStack = playerEntity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestStack = playerEntity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legStack = playerEntity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feetStack = playerEntity.getEquippedStack(EquipmentSlot.FEET);

        int returnValue = 0;
        if (!headStack.isEmpty() || !headStack.isIn(TagInit.ALLOWED_ARMOR)) {
            returnValue++;
        }
        if (!chestStack.isEmpty() || !chestStack.isIn(TagInit.ALLOWED_ARMOR)) {
            returnValue++;
        }
        if (!legStack.isEmpty() || !legStack.isIn(TagInit.ALLOWED_ARMOR)) {
            returnValue++;
        }
        if (!feetStack.isEmpty() || !feetStack.isIn(TagInit.ALLOWED_ARMOR)) {
            returnValue++;
        }
        return returnValue;

    }

    private static int wearingProtectiveArmorValue(PlayerEntity playerEntity) {
        boolean allowAllArmor = ConfigInit.CONFIG.allow_all_armor;

        ItemStack headStack = playerEntity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestStack = playerEntity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legStack = playerEntity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feetStack = playerEntity.getEquippedStack(EquipmentSlot.FEET);

        int returnValue = 1;
        if (headStack.isIn(TagInit.WARM_ARMOR) || (allowAllArmor && !headStack.isEmpty()) || (headStack.hasNbt() && headStack.getNbt().contains("environmentz"))) {
            returnValue++;
        }
        if (chestStack.isIn(TagInit.WARM_ARMOR) || (allowAllArmor && !chestStack.isEmpty()) || (chestStack.hasNbt() && chestStack.getNbt().contains("environmentz"))) {
            returnValue++;
        }
        if (legStack.isIn(TagInit.WARM_ARMOR) || (allowAllArmor && !legStack.isEmpty()) || (legStack.hasNbt() && legStack.getNbt().contains("environmentz"))) {
            returnValue++;
        }
        if (feetStack.isIn(TagInit.WARM_ARMOR) || (allowAllArmor && !feetStack.isEmpty()) || (feetStack.hasNbt() && feetStack.getNbt().contains("environmentz"))) {
            returnValue++;
        }
        return returnValue;
    }

    public static void heatPlayerWithBlock(World world, BlockPos pos) {
        if (world.getTime() % 20 == 0) {
            List<PlayerEntity> list = world.getPlayers(TargetPredicate.createAttackable().setBaseMaxDistance(ConfigInit.CONFIG.heating_up_block_range), null,
                    new Box(pos).expand(ConfigInit.CONFIG.heating_up_block_range, ConfigInit.CONFIG.heating_up_block_range - 1, ConfigInit.CONFIG.heating_up_block_range));
            if (!list.isEmpty())
                for (int i = 0; i < list.size(); i++) {
                    int coldProtectionAmount = ((PlayerEnvAccess) list.get(i)).getPlayerColdProtectionAmount();
                    if (coldProtectionAmount < ConfigInit.CONFIG.max_cold_protection_amount)
                        ((PlayerEnvAccess) list.get(i)).setPlayerColdProtectionAmount(coldProtectionAmount + ConfigInit.CONFIG.cold_protection_amount_addition + 1);
                    int playerTemperature = ((PlayerEnvAccess) list.get(i)).getPlayerTemperature();
                    if (playerTemperature < 0)
                        ((PlayerEnvAccess) list.get(i)).setPlayerTemperature(playerTemperature + 1);
                }
        }
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
