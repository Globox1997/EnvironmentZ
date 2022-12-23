package net.environmentz.item;

import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.init.ConfigInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class IcePack extends Item {

    public IcePack(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity playerEntity && playerEntity.age % 20 == 0) {
            if (!ConfigInit.CONFIG.hand_only_comforting_items || (playerEntity.getMainHandStack().equals(stack) || playerEntity.getOffHandStack().equals(stack)))
                if (stack.getMaxDamage() - stack.getDamage() > 1) {
                    int coldProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerColdProtectionAmount();
                    int heatProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerHeatProtectionAmount();
                    if (heatProtectionAmount < ConfigInit.CONFIG.max_heat_protection_amount)
                        ((PlayerEnvAccess) playerEntity).setPlayerHeatProtectionAmount(heatProtectionAmount + ConfigInit.CONFIG.heat_protection_amount_addition);
                    if (coldProtectionAmount > 0)
                        ((PlayerEnvAccess) playerEntity).setPlayerColdProtectionAmount(coldProtectionAmount - 1);
                    if (!playerEntity.isCreative())
                        stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(p.getActiveHand()));
                }
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

}
