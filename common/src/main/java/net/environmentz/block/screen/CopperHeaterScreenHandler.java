package net.environmentz.block.screen;

import net.environmentz.block.entity.CopperHeaterEntity;
import net.environmentz.init.BlockInit;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CopperHeaterScreenHandler extends AbstractContainerMenu {

    private static final int FUEL_SLOT_INDEX = 0;
    private static final int PLAYER_INVENTORY_START = 1;
    private static final int PLAYER_INVENTORY_END = 28;
    private static final int PLAYER_HOTBAR_END = 37;

    private final Container inventory;
    private final ContainerData propertyDelegate;

    public CopperHeaterScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(1), new SimpleContainerData(CopperHeaterEntity.PROPERTY_COUNT));
    }

    public CopperHeaterScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
        super(BlockInit.COPPER_HEATER_SCREEN_HANDLER_TYPE, syncId);
        checkContainerSize(inventory, 1);
        checkContainerDataCount(propertyDelegate, CopperHeaterEntity.PROPERTY_COUNT);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        this.addSlot(new Slot(inventory, FUEL_SLOT_INDEX, 80, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CopperHeaterScreenHandler.this.inventory.canPlaceItem(FUEL_SLOT_INDEX, stack);
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

        this.addDataSlots(propertyDelegate);
        this.inventory.startOpen(playerInventory.player);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.inventory.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack result = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(slot);
        if (clickedSlot != null && clickedSlot.hasItem()) {
            ItemStack stackInSlot = clickedSlot.getItem();
            result = stackInSlot.copy();

            if (slot == FUEL_SLOT_INDEX) {
                if (!this.moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.inventory.canPlaceItem(FUEL_SLOT_INDEX, stackInSlot)) {
                if (!this.moveItemStackTo(stackInSlot, FUEL_SLOT_INDEX, FUEL_SLOT_INDEX + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot < PLAYER_INVENTORY_END) {
                if (!this.moveItemStackTo(stackInSlot, PLAYER_INVENTORY_END, PLAYER_HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot < PLAYER_HOTBAR_END && !this.moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                clickedSlot.setByPlayer(ItemStack.EMPTY);
            } else {
                clickedSlot.setChanged();
            }

            if (stackInSlot.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            clickedSlot.onTake(player, stackInSlot);
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
        return Mth.clamp((float) this.propertyDelegate.get(CopperHeaterEntity.BURN_TIME_PROPERTY_INDEX) / fuelTime, 0.0F, 1.0F);
    }
}