package net.environmentz.block.entity;

import net.environmentz.block.CopperHeater;
import net.environmentz.block.screen.CopperHeaterScreenHandler;
import net.environmentz.init.BlockInit;
import net.environmentz.init.EffectInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CopperHeaterEntity extends LockableContainerBlockEntity implements SidedInventory {

    protected static final int FUEL_SLOT_INDEX = 0;
    private static final int[] SLOTS = new int[]{FUEL_SLOT_INDEX};

    public static final int BURN_TIME_PROPERTY_INDEX = 0;
    public static final int FUEL_TIME_PROPERTY_INDEX = 1;
    public static final int PROPERTY_COUNT = 2;

    private static final int COMFORT_CHECK_INTERVAL = 200;
    private static final int COMFORT_THRESHOLD = 3;
    private static final int COMFORT_EFFECT_DURATION = 2400;

    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    int burnTime;
    int fuelTime;

    private final HashMap<UUID, Integer> playerComfortMap = new HashMap<>();

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
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
        public int size() {
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
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        this.burnTime = nbt.getShort("BurnTime");
        this.fuelTime = this.getFuelTime(this.inventory.get(FUEL_SLOT_INDEX));
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putShort("BurnTime", (short) this.burnTime);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("block.environmentz.copper_heater");
    }

    public static void tick(World world, BlockPos pos, BlockState state, CopperHeaterEntity blockEntity) {
        boolean wasBurning = blockEntity.isBurning();
        boolean dirty = false;

        if (blockEntity.isBurning()) {
            blockEntity.burnTime--;
        }

        if (!blockEntity.isBurning()) {
            ItemStack fuelStack = blockEntity.inventory.get(FUEL_SLOT_INDEX);
            if (!fuelStack.isEmpty()) {
                int fuelTime = blockEntity.getFuelTime(fuelStack);
                if (fuelTime > 0) {
                    blockEntity.burnTime = fuelTime;
                    blockEntity.fuelTime = fuelTime;

                    Item item = fuelStack.getItem();
                    fuelStack.decrement(1);
                    if (fuelStack.isEmpty()) {
                        Item remainder = item.getRecipeRemainder();
                        blockEntity.inventory.set(FUEL_SLOT_INDEX, remainder == null ? ItemStack.EMPTY : new ItemStack(remainder));
                    }
                    dirty = true;
                }
            }
        }

        if (blockEntity.isBurning() && world.getTime() % COMFORT_CHECK_INTERVAL == 0) {
            blockEntity.warmNearbyPlayers(world, pos);
        }

        if (wasBurning != blockEntity.isBurning()) {
            dirty = true;
            state = state.with(CopperHeater.LIT, blockEntity.isBurning());
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }

        if (dirty) {
            markDirty(world, pos, state);
        }
    }

    private void warmNearbyPlayers(World world, BlockPos pos) {
        List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, new Box(pos).expand(8.0D, 4.0D, 8.0D), EntityPredicates.EXCEPT_SPECTATOR);

        if (!players.isEmpty()) {
            List<UUID> presentUuids = new ArrayList<>();

            for (PlayerEntity player : players) {

                BlockHitResult hitResult = world.raycast(new RaycastContext(new Vec3d(player.getX(), player.getY() + player.getHeight() / 2f, player.getZ()), new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f),
                        RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
                if (!hitResult.getBlockPos().equals(pos)) {
                    continue;
                }

                UUID uuid = player.getUuid();
                int comfort = this.playerComfortMap.getOrDefault(uuid, 0) + 1;

                if (comfort > COMFORT_THRESHOLD) {
                    player.addStatusEffect(new StatusEffectInstance(EffectInit.COMFORT, COMFORT_EFFECT_DURATION, 0, false, false, true));
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

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(fuel.getItem(), 0);
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(stack.getItem());
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return stack.isOf(Items.WATER_BUCKET) || stack.isOf(Items.BUCKET);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CopperHeaterScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot != FUEL_SLOT_INDEX) {
            return false;
        }
        ItemStack currentFuel = this.inventory.get(FUEL_SLOT_INDEX);
        return canUseAsFuel(stack) || stack.isOf(Items.BUCKET) && !currentFuel.isOf(Items.BUCKET);
    }
}
