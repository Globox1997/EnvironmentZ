package net.environmentz.mixin;

import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {

    private boolean removedInsulation = false;

    public SmithingScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void updateResultMixin(CallbackInfo info) {
        ItemStack itemStack = this.input.getStack(0);
        ItemStack itemStack2 = this.input.getStack(1);

        if (itemStack.isIn(TagInit.ARMOR_ITEMS) && !itemStack.isIn(TagInit.WARM_ARMOR)) {
            if (itemStack2.isIn(TagInit.ICE_ITEMS)) {
                ItemStack itemStack3 = itemStack.copy();
                NbtCompound tag = itemStack3.getOrCreateNbt();
                tag.putInt("iced", ItemInit.COOLING_HEATING_VALUE);
                itemStack3.setNbt(tag);
                this.output.setStack(0, itemStack3);
                info.cancel();
            } else {
                this.removedInsulation = false;
                if (!itemStack.getNbt().contains("environmentz")) {
                    if (itemStack2.isIn(TagInit.INSOLATING_ITEM)) {
                        ItemStack itemStack3 = itemStack.copy();
                        NbtCompound tag = itemStack3.getOrCreateNbt();
                        tag.putString("environmentz", "fur_insolated");
                        itemStack3.setNbt(tag);
                        this.output.setStack(0, itemStack3);
                        info.cancel();
                    }
                } else {
                    if (itemStack2.getItem() instanceof ShearsItem) {
                        ItemStack itemStack3 = itemStack.copy();
                        NbtCompound tag = itemStack3.getNbt();
                        tag.remove("environmentz");
                        itemStack3.setNbt(tag);
                        this.output.setStack(0, itemStack3);
                        this.removedInsulation = true;
                        info.cancel();
                    }
                }
            }
        }
    }

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    protected void canTakeOutputMixin(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> info) {
        ItemStack itemStack = this.input.getStack(0);
        ItemStack itemStack2 = this.input.getStack(1);
        if (itemStack.isIn(TagInit.ARMOR_ITEMS) && ((itemStack2.isIn(TagInit.INSOLATING_ITEM) && !itemStack.getNbt().contains("environmentz"))
                || (itemStack2.getItem() instanceof ShearsItem && itemStack.getNbt().contains("environmentz")) || (itemStack2.isIn(TagInit.ICE_ITEMS)))) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "decrementStack", at = @At("HEAD"), cancellable = true)
    private void decrementStackMixin(int slot, CallbackInfo info) {
        if (slot == 1) {
            if (this.input.getStack(slot).getItem() instanceof ShearsItem && this.removedInsulation) {
                info.cancel();
            }
        }
    }

}
