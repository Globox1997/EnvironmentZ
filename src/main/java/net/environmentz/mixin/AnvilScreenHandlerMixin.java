package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.LevelEvent;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {

    @Unique
    private static final int NO_RESULT = 0;
    @Unique
    private static final int ICED_RESULT = 1;
    @Unique
    private static final int INSULATED_RESULT = 2;
    @Unique
    private static final int REMOVED_INSULATION_RESULT = 3;

    @Unique
    private int environmentzResult = NO_RESULT;

    public AnvilScreenHandlerMixin(MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    protected void canTakeOutputMixin(Player player, boolean present, CallbackInfoReturnable<Boolean> info) {
        if (this.environmentzResult != NO_RESULT) {
            info.setReturnValue(present);
        }
    }

    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    protected void onTakeOutputMixin(Player player, ItemStack stack, CallbackInfo info) {
        if (this.environmentzResult == NO_RESULT) {
            return;
        }
        int result = this.environmentzResult;
        this.environmentzResult = NO_RESULT;
        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (result == REMOVED_INSULATION_RESULT) {
            player.playSound(SoundEvents.SHEEP_SHEAR, 1.0f, 1.0f);
        } else {
            ItemStack resourceStack = this.inputSlots.getItem(1);
            resourceStack.shrink(1);
            this.inputSlots.setItem(1, resourceStack);
            this.access.execute((world, pos) -> world.levelEvent(LevelEvent.SOUND_ANVIL_USED, pos, 0));
        }
        info.cancel();
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void updateResultMixin(CallbackInfo info) {
        this.environmentzResult = NO_RESULT;
        ItemStack armorStack = this.inputSlots.getItem(0);
        ItemStack resourceStack = this.inputSlots.getItem(1);

        if (!armorStack.is(TagInit.ARMOR_ITEMS) || armorStack.is(TagInit.WARM_ARMOR)) {
            return;
        }
        boolean isInsulated = Boolean.TRUE.equals(armorStack.get(ItemInit.INSULATED));
        ItemStack resultStack = armorStack.copy();
        if (resourceStack.is(TagInit.ICE_ITEMS)) {
            resultStack.set(ItemInit.ICED, ItemInit.COOLING_HEATING_VALUE);
            this.environmentzResult = ICED_RESULT;
        } else if (!isInsulated && resourceStack.is(TagInit.INSOLATING_ITEM)) {
            resultStack.set(ItemInit.INSULATED, true);
            this.environmentzResult = INSULATED_RESULT;
        } else if (isInsulated && resourceStack.getItem() instanceof ShearsItem) {
            resultStack.remove(ItemInit.INSULATED);
            this.environmentzResult = REMOVED_INSULATION_RESULT;
        } else {
            return;
        }
        this.resultSlots.setItem(0, resultStack);
        info.cancel();
    }
}
