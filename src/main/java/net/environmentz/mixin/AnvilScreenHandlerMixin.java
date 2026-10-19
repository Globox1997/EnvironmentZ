package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.WorldEvents;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

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

    public AnvilScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    protected void canTakeOutputMixin(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> info) {
        if (this.environmentzResult != NO_RESULT) {
            info.setReturnValue(present);
        }
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    protected void onTakeOutputMixin(PlayerEntity player, ItemStack stack, CallbackInfo info) {
        if (this.environmentzResult == NO_RESULT) {
            return;
        }
        int result = this.environmentzResult;
        this.environmentzResult = NO_RESULT;
        this.input.setStack(0, ItemStack.EMPTY);
        if (result == REMOVED_INSULATION_RESULT) {
            player.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0f, 1.0f);
        } else {
            ItemStack resourceStack = this.input.getStack(1);
            resourceStack.decrement(1);
            this.input.setStack(1, resourceStack);
            this.context.run((world, pos) -> world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0));
        }
        info.cancel();
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void updateResultMixin(CallbackInfo info) {
        this.environmentzResult = NO_RESULT;
        ItemStack armorStack = this.input.getStack(0);
        ItemStack resourceStack = this.input.getStack(1);

        if (!armorStack.isIn(TagInit.ARMOR_ITEMS) || armorStack.isIn(TagInit.WARM_ARMOR)) {
            return;
        }
        boolean isInsulated = Boolean.TRUE.equals(armorStack.get(ItemInit.INSULATED));
        ItemStack resultStack = armorStack.copy();
        if (resourceStack.isIn(TagInit.ICE_ITEMS)) {
            resultStack.set(ItemInit.ICED, ItemInit.COOLING_HEATING_VALUE);
            this.environmentzResult = ICED_RESULT;
        } else if (!isInsulated && resourceStack.isIn(TagInit.INSOLATING_ITEM)) {
            resultStack.set(ItemInit.INSULATED, true);
            this.environmentzResult = INSULATED_RESULT;
        } else if (isInsulated && resourceStack.getItem() instanceof ShearsItem) {
            resultStack.remove(ItemInit.INSULATED);
            this.environmentzResult = REMOVED_INSULATION_RESULT;
        } else {
            return;
        }
        this.output.setStack(0, resultStack);
        info.cancel();
    }
}
