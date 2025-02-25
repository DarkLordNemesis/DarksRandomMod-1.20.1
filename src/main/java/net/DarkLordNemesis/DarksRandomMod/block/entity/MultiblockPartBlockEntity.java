package net.DarkLordNemesis.DarksRandomMod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockPartBlockEntity extends BlockEntity {
    private BlockPos masterPos;

    public MultiblockPartBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MULTIBLOCK_PART.get(), pos, state); // Register ModBlockEntities (see step 3)
    }

    public BlockPos getMasterPos() {
        return masterPos;
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
        setChanged(); // Save changes to disk
    }

    public void removeMasterPos() {
        this.masterPos = null;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("MasterX") && tag.contains("MasterY") && tag.contains("MasterZ")) {
            masterPos = new BlockPos(
                    tag.getInt("MasterX"),
                    tag.getInt("MasterY"),
                    tag.getInt("MasterZ")
            );
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (masterPos != null) {
            tag.putInt("MasterX", masterPos.getX());
            tag.putInt("MasterY", masterPos.getY());
            tag.putInt("MasterZ", masterPos.getZ());
        }
    }
}