package net.DarkLordNemesis.DarksRandomMod.block.entity;

import net.DarkLordNemesis.DarksRandomMod.screen.GeneratorBlockMenu;
import net.DarkLordNemesis.DarksRandomMod.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlockEntity extends BlockEntity implements MenuProvider {

    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage(10000, 0, 1000);
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) { // 1 slot for fuel
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int burnTime;
    private int maxBurnTime;



    public GeneratorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GENERATOR_BLOCK_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> GeneratorBlockEntity.this.energyStorage.getEnergyStored();
                    case 1 -> GeneratorBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> GeneratorBlockEntity.this.energyStorage.setEnergy(pValue);
                    case 1 -> {} // Max energy is constant, no need to set it
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
        lazyItemHandler.invalidate();
    }



    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("burnTime", burnTime);
        tag.putInt("burnTimeTotal", maxBurnTime);
        tag.putInt("energy", energyStorage.getEnergyStored()); // Save energy

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        burnTime = tag.getInt("burnTime");
        maxBurnTime = tag.getInt("burnTimeTotal");
        energyStorage.setEnergy(tag.getInt("energy")); // Load energy
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            if (burnTime > 0) {
                burnTime--;
                energyStorage.increaseEnergy(50); // Generate energy while burning
                setChanged();

                if (burnTime <= 0 && this.energyStorage.getEnergyStored() < this.energyStorage.getMaxEnergyStored()) {
                    consumeFuel();
                }
            } else if (this.energyStorage.getEnergyStored() < this.energyStorage.getMaxEnergyStored()){
                consumeFuel(); // Try to consume fuel if not burning
            }

            distributeEnergy();
        }
    }


    private void consumeFuel() {
        if (canBurn(this.itemHandler.getStackInSlot(0))) {
            this.burnTime = this.maxBurnTime = getBurnTime(this.itemHandler.getStackInSlot(0));
            this.itemHandler.getStackInSlot(0).shrink(1);
            setChanged();

        }
    }

    public int getBurnTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
    }

    public boolean canBurn(@NotNull ItemStack stack) {
        return getBurnTime(stack) > 0;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    private void distributeEnergy() {
        for (Direction direction : Direction.values()) {
            BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
            if (be != null) {
                be.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(handler -> {
                    int energyToGive = Math.min(energyStorage.getEnergyStored(), 100); // Transfer up to 100 FE
                    int accepted = handler.receiveEnergy(energyToGive, false);
                    energyStorage.decreaseEnergy(accepted);
                });
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.darks_random_mod.generator_block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new GeneratorBlockMenu(pContainerId, pPlayerInventory, this, this.data);
    }
}