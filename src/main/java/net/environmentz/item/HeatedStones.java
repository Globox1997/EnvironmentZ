package net.environmentz.item;

import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.ItemInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class HeatedStones extends Item {

    public HeatedStones(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            if (stack.getMaxDamage() - stack.getDamage() > 1) {
                if (playerEntity.age % 20 == 0) {
                    int coldProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerColdProtectionAmount();
                    int heatProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerHeatProtectionAmount();
                    if (coldProtectionAmount < ConfigInit.CONFIG.max_cold_protection_amount)
                        ((PlayerEnvAccess) playerEntity).setPlayerColdProtectionAmount(coldProtectionAmount + ConfigInit.CONFIG.cold_protection_amount_addition);
                    if (heatProtectionAmount > 0)
                        ((PlayerEnvAccess) playerEntity).setPlayerHeatProtectionAmount(heatProtectionAmount - 1);
                    if (!playerEntity.isCreative()) {
                        stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(p.getActiveHand()));
                    }
                }
            } else {
                playerEntity.getInventory().setStack(slot, new ItemStack(ItemInit.HEATING_STONES));
            }
        }
    }

}
