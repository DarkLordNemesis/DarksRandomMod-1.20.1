package net.DarkLordNemesis.DarksRandomMod.block.custom;

import net.DarkLordNemesis.DarksRandomMod.block.ModBlocks;
import net.DarkLordNemesis.DarksRandomMod.block.entity.MultiblockPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MachineBlock extends Block {
    public MachineBlock(Properties pProperties) {
        super(pProperties);
    }

    private static Block[][][] MULTIBLOCK_STRUCTURE;

    public static Block[][][] getMultiblockStructure() {
        if (MULTIBLOCK_STRUCTURE == null) {
            MULTIBLOCK_STRUCTURE = new Block[][][]{
                    {
                            {ModBlocks.MACHINE_BLOCK.get(), ModBlocks.MULTIBLOCK_PART_BLOCK.get(), ModBlocks.MULTIBLOCK_PART_BLOCK.get()},
                            {ModBlocks.MULTIBLOCK_PART_BLOCK.get(), Blocks.DIAMOND_BLOCK, ModBlocks.MULTIBLOCK_PART_BLOCK.get()},
                            {ModBlocks.MULTIBLOCK_PART_BLOCK.get(), ModBlocks.MULTIBLOCK_PART_BLOCK.get(), ModBlocks.MULTIBLOCK_PART_BLOCK.get()}
                    },
                    {
                            {null, Blocks.OBSIDIAN, null},
                            {Blocks.OBSIDIAN, ModBlocks.MULTIBLOCK_PART_BLOCK.get(), Blocks.OBSIDIAN},
                            {null, Blocks.OBSIDIAN, null}
                    },
                    {
                            {null, null, null},
                            {null, Blocks.EMERALD_BLOCK, null},
                            {null, null, null}
                    }
            };
        }
        return MULTIBLOCK_STRUCTURE;
    }


    public InteractionResult use(BlockState pState, Level level, BlockPos pos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        pPlayer.sendSystemMessage(Component.literal("click registered"));

        if (!level.isClientSide) {
            if (isMultiblockFormed(level, pos)) {
                // Multiblock ist korrekt, führe Aktion aus
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
                if (player != null) {
                    player.sendSystemMessage(Component.literal("Multiblock erfolgreich gebaut!"));
                }
                assignMaster(level, pos);
            } else {
                buildMultiblockAsFarAsPossible(level, pos, pPlayer);
            }
        }
        return InteractionResult.SUCCESS;
    }

    public void assignMaster(Level level, BlockPos pos) {
        if (isMultiblockFormed(level, pos)) {
            // Assign this block as the master to all parts
            for (int y = 0; y < getMultiblockStructure().length; y++) {
                for (int x = 0; x < getMultiblockStructure()[y].length; x++) {
                    for (int z = 0; z < getMultiblockStructure()[y][x].length; z++) {
                        BlockPos partPos = pos.offset(x, y, z);
                        BlockEntity blockEntity = level.getBlockEntity(partPos);
                        if (blockEntity instanceof MultiblockPartBlockEntity partEntity) {
                            partEntity.setMasterPos(pos);
                        }
                    }
                }
            }
        }
    }

    public void removeMaster(Level level, BlockPos pos) {
        for (int y = 0; y < getMultiblockStructure().length; y++) {
            for (int x = 0; x < getMultiblockStructure()[y].length; x++) {
                for (int z = 0; z < getMultiblockStructure()[y][x].length; z++) {
                    BlockPos partPos = pos.offset(x, y, z);
                    BlockEntity blockEntity = level.getBlockEntity(partPos);
                    if (blockEntity instanceof MultiblockPartBlockEntity partEntity) {
                        partEntity.removeMasterPos();
                    }
                }
            }
        }
    }


    public boolean isMultiblockFormed(Level level, BlockPos anchorPos) {
        System.out.println("Ankerpunkt: " + anchorPos);

        for (int y = 0; y < getMultiblockStructure().length; y++) { // Y = Höhe
            for (int x = 0; x < getMultiblockStructure()[y].length; x++) { // X-Achse
                for (int z = 0; z < getMultiblockStructure()[y][x].length; z++) { // Z-Achse
                    BlockPos pos = anchorPos.offset(x, y, z); // Richtig: y als Höhe nehmen
                    System.out.println("Überprüfe Block bei: " + pos);
                    Block expectedBlock = getMultiblockStructure()[y][x][z];
                    if (expectedBlock == null) {
                        continue; // Ignoriere leere Blöcke
                    }

                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() != expectedBlock) {
                        System.out.println("Falscher Block bei: " + pos + " (Erwartet: " + expectedBlock + ", Gefunden: " + state.getBlock() + ")");
                        Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
                        if (player != null) {
                            player.sendSystemMessage(Component.literal("Multiblock invalid"));
                        }
                        removeMaster(level, anchorPos);
                        return false;

                    }
                }
            }
        }
        return true;
    }

    public void buildMultiblockAsFarAsPossible(Level level, BlockPos anchorPos, Player player) {
        int blocksPlaced = 0;
        int blocksSkipped = 0;

        for (int y = 0; y < getMultiblockStructure().length; y++) {
            for (int x = 0; x < getMultiblockStructure()[y].length; x++) {
                for (int z = 0; z < getMultiblockStructure()[y][x].length; z++) {
                    BlockPos pos = anchorPos.offset(x, y, z);
                    Block expectedBlock = getMultiblockStructure()[y][x][z];

                    if (expectedBlock != null) {
                        // Check if the player has the required item and the area is clear
                        if (hasItem(player, expectedBlock.asItem()) && isAreaClear(level, pos)) {
                            // Place the block
                            level.setBlock(pos, expectedBlock.defaultBlockState(), 3);
                            consumeItem(player, expectedBlock.asItem(), 1);
                            blocksPlaced++;
                        } else {
                            blocksSkipped++;
                        }
                    }
                }
            }
        }

        // Notify the player
        if (blocksPlaced > 0) {
            player.sendSystemMessage(Component.literal("Placed " + blocksPlaced + " blocks."));
        }
        if (blocksSkipped > 0) {
            player.sendSystemMessage(Component.literal("Skipped " + blocksSkipped + " blocks due to missing items or obstructions."));
        }
    }

    private boolean hasItem(Player player, Item item) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == item && stack.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    private void consumeItem(Player player, Item item, int count) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == item) {
                int toRemove = Math.min(stack.getCount(), count);
                stack.shrink(toRemove);
                count -= toRemove;

                if (count <= 0) {
                    break;
                }
            }
        }
    }

    private boolean isAreaClear(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        // Allow replacing air or replaceable blocks (e.g., grass, flowers)
        return state.isAir() || state.canBeReplaced();
    }
}
