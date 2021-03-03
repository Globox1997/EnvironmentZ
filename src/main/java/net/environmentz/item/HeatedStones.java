package net.environmentz.item;

import net.environmentz.init.EffectInit;
import net.environmentz.init.ItemInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
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
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            if (stack.getMaxDamage() - stack.getDamage() > 1) {
                if (playerEntity.age % 40 == 0 && !world.isClient) {
                    playerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.WARMING, 101, 0, false, false));
                    if (!playerEntity.isCreative()) {
                        stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(p.getActiveHand()));
                    }
                }
            } else {
                playerEntity.inventory.setStack(slot, new ItemStack(ItemInit.HEATING_STONES));
            }
        }
    }

}
