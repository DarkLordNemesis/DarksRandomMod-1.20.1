package net.DarkLordNemesis.DarksRandomMod.datagen;

import net.DarkLordNemesis.DarksRandomMod.DarksRandomMod;
import net.DarkLordNemesis.DarksRandomMod.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {


    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DarksRandomMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.SAPPHIRE_BLOCK);
        blockWithItem(ModBlocks.RAW_SAPPHIRE_BLOCK);

        blockWithItem(ModBlocks.SOUND_BLOCK);

        blockWithItem(ModBlocks.MULTIBLOCK_PART_BLOCK);


        simpleBlockWithItem(ModBlocks.MACHINE_BLOCK.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/machine_block"))
        );

        simpleBlockWithItem(ModBlocks.ADVANCED_MACHINE_BLOCK.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/advanced_machine_block"))
        );

        simpleBlockWithItem(ModBlocks.GENERATOR_BLOCK.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/generator_block"))
        );




    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));

    }

}
