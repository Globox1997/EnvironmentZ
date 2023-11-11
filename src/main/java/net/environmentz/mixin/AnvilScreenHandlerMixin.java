package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.WorldEvents;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    private boolean removedInsulation = false;

    public AnvilScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
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

    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    protected void onTakeOutputMixin(PlayerEntity player, ItemStack stack, CallbackInfo info) {
        if (this.input.getStack(1).getItem() instanceof ShearsItem && this.removedInsulation) {
            this.input.setStack(0, ItemStack.EMPTY);
            player.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
            info.cancel();
        } else if (this.input.getStack(1).isIn(TagInit.ICE_ITEMS) || this.input.getStack(1).isIn(TagInit.INSOLATING_ITEM)) {
            this.input.setStack(0, ItemStack.EMPTY);
            this.input.setStack(1, ItemStack.EMPTY);
            this.context.run((world, pos) -> {
                world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
            });
            info.cancel();
        }
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
                if (itemStack.hasNbt() && !itemStack.getNbt().contains("environmentz")) {
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
}
