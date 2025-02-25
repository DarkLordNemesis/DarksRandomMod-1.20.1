package net.DarkLordNemesis.DarksRandomMod.block.entity;

import net.DarkLordNemesis.DarksRandomMod.DarksRandomMod;
import net.DarkLordNemesis.DarksRandomMod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DarksRandomMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<AdvancedMachineBlockEntity>> ADVANCED_MACHINE_BLOCK_BE =
            BLOCK_ENTITIES.register("advanced_machine_block_be", () ->
                    BlockEntityType.Builder.of(AdvancedMachineBlockEntity::new,
                            ModBlocks.ADVANCED_MACHINE_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<GeneratorBlockEntity>> GENERATOR_BLOCK_BE =
            BLOCK_ENTITIES.register("generator_block_be", () ->
                    BlockEntityType.Builder.of(GeneratorBlockEntity::new,
                            ModBlocks.GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<MultiblockPartBlockEntity>> MULTIBLOCK_PART =
            BLOCK_ENTITIES.register("multiblock_part",
                    () -> BlockEntityType.Builder.of(
                            MultiblockPartBlockEntity::new,
                            ModBlocks.MULTIBLOCK_PART_BLOCK.get() // Register this block in ModBlocks
                    ).build(null)
            );


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
