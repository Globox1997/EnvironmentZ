package net.environmentz.item;

import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.init.ConfigInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.world.World;

public class HeatingStones extends Item {

    public HeatingStones(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity playerEntity && playerEntity.age % 20 == 0) {
            if (!ConfigInit.CONFIG.hand_only_comforting_items || (playerEntity.getMainHandStack().equals(stack) || playerEntity.getOffHandStack().equals(stack)))
                if (stack.getMaxDamage() - stack.getDamage() > 1) {
                    int coldProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerColdProtectionAmount();
                    int heatProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerHeatProtectionAmount();
                    if (coldProtectionAmount < ConfigInit.CONFIG.max_cold_protection_amount)
                        ((PlayerEnvAccess) playerEntity).setPlayerColdProtectionAmount(coldProtectionAmount + ConfigInit.CONFIG.cold_protection_amount_addition);
                    if (heatProtectionAmount > 0)
                        ((PlayerEnvAccess) playerEntity).setPlayerHeatProtectionAmount(heatProtectionAmount - 1);
                    if (!playerEntity.isCreative())
                        stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(p.getActiveHand()));
                }
        }
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClient && player.currentScreenHandler instanceof StonecutterScreenHandler)
            stack.setDamage(stack.getMaxDamage() - 1);

        super.onCraft(stack, world, player);
    }

}
