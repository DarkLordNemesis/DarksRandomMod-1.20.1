// MultiblockPartBlock.java
package net.DarkLordNemesis.DarksRandomMod.block.custom;

import net.DarkLordNemesis.DarksRandomMod.block.entity.MultiblockPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;


public class MultiblockPartBlock extends BaseEntityBlock {
    public MultiblockPartBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MultiblockPartBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos pos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MultiblockPartBlockEntity partEntity) {
                BlockPos masterPos = partEntity.getMasterPos();
                pPlayer.sendSystemMessage(Component.literal(masterPos.toString()));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        // Ensure this is server-side
        if (!level.isClientSide) {
            // Check if the neighbor is NOT a MultiblockPartBlock
            if (!(neighborBlock instanceof MultiblockPartBlock)) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof MultiblockPartBlockEntity partEntity) {
                    BlockPos masterPos = partEntity.getMasterPos();
                    if (masterPos != null) {
                        BlockState masterState = level.getBlockState(masterPos);
                        if (masterState.getBlock() instanceof MachineBlock machine) {
                            // Check if the multiblock is still valid
                            if (!machine.isMultiblockFormed(level, masterPos)) {
                            }
                        }
                    }
                }
            }
        }
        super.neighborChanged(state, level, pos, neighborBlock, pNeighborPos, pMovedByPiston);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // Notify the master when this block is broken
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MultiblockPartBlockEntity partEntity) {
                BlockPos masterPos = partEntity.getMasterPos();
                if (masterPos != null) {
                    BlockState masterState = level.getBlockState(masterPos);
                    if (masterState.getBlock() instanceof MachineBlock machine) {
                        machine.isMultiblockFormed(level, masterPos);
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}