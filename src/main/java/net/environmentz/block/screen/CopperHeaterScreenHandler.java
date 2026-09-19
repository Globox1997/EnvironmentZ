package net.environmentz.block.screen;

import net.environmentz.block.entity.CopperHeaterEntity;
import net.environmentz.init.BlockInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;

public class CopperHeaterScreenHandler extends ScreenHandler {

    private static final int FUEL_SLOT_INDEX = 0;
    private static final int PLAYER_INVENTORY_START = 1;
    private static final int PLAYER_INVENTORY_END = 28;
    private static final int PLAYER_HOTBAR_END = 37;

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public CopperHeaterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1), new ArrayPropertyDelegate(CopperHeaterEntity.PROPERTY_COUNT));
    }

    public CopperHeaterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(BlockInit.COPPER_HEATER_SCREEN_HANDLER_TYPE, syncId);
        checkSize(inventory, 1);
        checkDataCount(propertyDelegate, CopperHeaterEntity.PROPERTY_COUNT);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        this.addSlot(new Slot(inventory, FUEL_SLOT_INDEX, 80, 53) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return CopperHeaterScreenHandler.this.inventory.isValid(FUEL_SLOT_INDEX, stack);
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        this.addProperties(propertyDelegate);
        this.inventory.onOpen(playerInventory.player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack result = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(slot);
        if (clickedSlot != null && clickedSlot.hasStack()) {
            ItemStack stackInSlot = clickedSlot.getStack();
            result = stackInSlot.copy();

            if (slot == FUEL_SLOT_INDEX) {
                if (!this.insertItem(stackInSlot, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.inventory.isValid(FUEL_SLOT_INDEX, stackInSlot)) {
                if (!this.insertItem(stackInSlot, FUEL_SLOT_INDEX, FUEL_SLOT_INDEX + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot < PLAYER_INVENTORY_END) {
                if (!this.insertItem(stackInSlot, PLAYER_INVENTORY_END, PLAYER_HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot < PLAYER_HOTBAR_END && !this.insertItem(stackInSlot, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                clickedSlot.setStack(ItemStack.EMPTY);
            } else {
                clickedSlot.markDirty();
            }

            if (stackInSlot.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            clickedSlot.onTakeItem(player, stackInSlot);
        }

        return result;
    }

    public boolean isBurning() {
        return this.propertyDelegate.get(CopperHeaterEntity.BURN_TIME_PROPERTY_INDEX) > 0;
    }

    public float getFuelProgress() {
        int fuelTime = this.propertyDelegate.get(CopperHeaterEntity.FUEL_TIME_PROPERTY_INDEX);
        if (fuelTime == 0) {
            fuelTime = 200;
        }
        return MathHelper.clamp((float) this.propertyDelegate.get(CopperHeaterEntity.BURN_TIME_PROPERTY_INDEX) / fuelTime, 0.0F, 1.0F);
    }
}