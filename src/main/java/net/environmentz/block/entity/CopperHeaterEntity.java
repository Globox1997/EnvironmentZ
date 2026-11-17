package net.environmentz.block.entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.environmentz.block.CopperHeater;
import net.environmentz.block.screen.CopperHeaterScreenHandler;
import net.environmentz.init.BlockInit;
import net.environmentz.init.EffectInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CopperHeaterEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    protected static final int FUEL_SLOT_INDEX = 0;
    private static final int[] SLOTS = new int[]{FUEL_SLOT_INDEX};

    public static final int BURN_TIME_PROPERTY_INDEX = 0;
    public static final int FUEL_TIME_PROPERTY_INDEX = 1;
    public static final int PROPERTY_COUNT = 2;

    private static final int COMFORT_CHECK_INTERVAL = 200;
    private static final int COMFORT_THRESHOLD = 3;
    private static final int COMFORT_EFFECT_DURATION = 2400;

    protected NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    int burnTime;
    int fuelTime;

    private final HashMap<UUID, Integer> playerComfortMap = new HashMap<>();

    protected final ContainerData propertyDelegate = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case BURN_TIME_PROPERTY_INDEX -> CopperHeaterEntity.this.burnTime;
                case FUEL_TIME_PROPERTY_INDEX -> CopperHeaterEntity.this.fuelTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case BURN_TIME_PROPERTY_INDEX -> CopperHeaterEntity.this.burnTime = value;
                case FUEL_TIME_PROPERTY_INDEX -> CopperHeaterEntity.this.fuelTime = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return PROPERTY_COUNT;
        }
    };

    public CopperHeaterEntity(BlockPos pos, BlockState state) {
        super(BlockInit.HEATER, pos, state);
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    protected void loadAdditional(ValueInput nbt) {
        super.loadAdditional(nbt);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.inventory);
        this.burnTime = nbt.getShortOr("BurnTime", (short) 0);
        this.fuelTime = nbt.getIntOr("FuelTime", this.burnTime);
    }

    @Override
    protected void saveAdditional(ValueOutput nbt) {
        super.saveAdditional(nbt);
        nbt.putShort("BurnTime", (short) this.burnTime);
        nbt.putInt("FuelTime", this.fuelTime);
        ContainerHelper.saveAllItems(nbt, this.inventory);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.environmentz.copper_heater");
    }

    public static void tick(Level world, BlockPos pos, BlockState state, CopperHeaterEntity blockEntity) {
        boolean wasBurning = blockEntity.isBurning();
        boolean dirty = false;

        if (blockEntity.isBurning()) {
            blockEntity.burnTime--;
        }

        if (!blockEntity.isBurning()) {
            ItemStack fuelStack = blockEntity.inventory.get(FUEL_SLOT_INDEX);
            if (!fuelStack.isEmpty()) {
                int fuelTime = blockEntity.getFuelTime((ServerLevel) world, fuelStack);
                if (fuelTime > 0) {
                    blockEntity.burnTime = fuelTime;
                    blockEntity.fuelTime = fuelTime;

                    Item item = fuelStack.getItem();
                    fuelStack.shrink(1);
                    if (fuelStack.isEmpty()) {
                        ItemStackTemplate remainder = item.getCraftingRemainder();
                        blockEntity.inventory.set(FUEL_SLOT_INDEX, remainder == null ? ItemStack.EMPTY : remainder.create());
                    }
                    dirty = true;
                }
            }
        }

        if (blockEntity.isBurning() && world.getGameTime() % COMFORT_CHECK_INTERVAL == 0) {
            blockEntity.warmNearbyPlayers(world, pos, state);
        }

        if (wasBurning != blockEntity.isBurning()) {
            dirty = true;
            state = state.setValue(CopperHeater.LIT, blockEntity.isBurning());
            world.setBlock(pos, state, Block.UPDATE_ALL);
        }

        if (dirty) {
            setChanged(world, pos, state);
        }
    }

    private void warmNearbyPlayers(Level world, BlockPos pos, BlockState state) {
        double range = switch (state.getValue(CopperHeater.OXIDATION)) {
            case 0 -> 8.0f;
            case 1 -> 6.0f;
            case 2 -> 4.0f;
            default -> 2.0f;
        };

        List<Player> players = world.getEntitiesOfClass(Player.class, new AABB(pos).inflate(range, range / 2.0f, range), EntitySelector.NO_SPECTATORS);

        if (!players.isEmpty()) {
            List<UUID> presentUuids = new ArrayList<>();

            for (Player player : players) {

                BlockHitResult hitResult = world.clip(new ClipContext(new Vec3(player.getX(), player.getY() + player.getBbHeight() / 2f, player.getZ()), new Vec3(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                if (!hitResult.getBlockPos().equals(pos)) {
                    continue;
                }

                UUID uuid = player.getUUID();
                int comfort = this.playerComfortMap.getOrDefault(uuid, 0) + 1;

                if (comfort > COMFORT_THRESHOLD) {
                    player.addEffect(new MobEffectInstance(EffectInit.COMFORT, COMFORT_EFFECT_DURATION, 0, false, false, true));
                    comfort = 0;
                }
                this.playerComfortMap.put(uuid, comfort);
                presentUuids.add(uuid);
            }

            this.playerComfortMap.keySet().retainAll(presentUuids);
        } else {
            this.playerComfortMap.clear();
        }
    }

    protected int getFuelTime(ServerLevel world, ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        return ResolvableInt.getFromItem(fuel, DataComponents.COOKING_FUEL, CookingFuel::burnTime, this.getLootContext(world), 0);
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return stack.has(DataComponents.COOKING_FUEL);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return stack.is(Items.WATER_BUCKET) || stack.is(Items.BUCKET);
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new CopperHeaterScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot != FUEL_SLOT_INDEX) {
            return false;
        }
        return canUseAsFuel(stack);
    }
}
