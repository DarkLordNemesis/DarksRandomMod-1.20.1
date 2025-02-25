
package net.DarkLordNemesis.DarksRandomMod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ScanStructureCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("scanstructure")
                        .then(Commands.argument("width", IntegerArgumentType.integer(1))
                                .then(Commands.argument("depth", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("height", IntegerArgumentType.integer(1))
                                                .executes(context -> scanStructure(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "width"),
                                                        IntegerArgumentType.getInteger(context, "depth"),
                                                        IntegerArgumentType.getInteger(context, "height")
                                                ))))
                        ));
    }

    private static int scanStructure(CommandSourceStack source, int width, int depth, int height) {
        Level level = source.getLevel();
        // Convert Vec3 to BlockPos
        BlockPos startPos = BlockPos.containing(source.getPosition());

        Block[][][] structure = new Block[height][width][depth];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    BlockPos pos = startPos.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    structure[y][x][z] = state.getBlock();
                }
            }
        }

        // Output the structure to the console in the desired format
        System.out.println("Scanned Structure:");
        for (int y = 0; y < height; y++) {
            System.out.println("{");
            for (int x = 0; x < width; x++) {
                System.out.print("{");
                for (int z = 0; z < depth; z++) {
                    Block block = structure[y][x][z];
                    String blockName = formatBlock(block);
                    System.out.print(blockName);
                    if (z < depth - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println("},");
            }
            System.out.println("},");
        }

        // Save the structure to a file in the desired format
        File folder = new File("structures");
        if (!folder.exists()) {
            folder.mkdirs(); // Create the folder if it doesn't exist
        }

        File file = new File(folder, "structure.txt");
        try (FileWriter writer = new FileWriter(file)) {
            for (int y = 0; y < height; y++) {
                writer.write("{\n");
                for (int x = 0; x < width; x++) {
                    writer.write("{");
                    for (int z = 0; z < depth; z++) {
                        Block block = structure[y][x][z];
                        String blockName = formatBlock(block);
                        writer.write(blockName);
                        if (z < depth - 1) {
                            writer.write(", ");
                        }
                    }
                    writer.write("},\n");
                }
                writer.write("},\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use a Supplier for the success message
        source.sendSuccess(() -> Component.literal("Structure scanned and saved to structures/structure.txt!"), true);
        return 1;
    }

    // Helper method to format a block into the desired format
    private static String formatBlock(Block block) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        if (blockId.getNamespace().equals("minecraft")) {
            return "Blocks." + blockId.getPath().toUpperCase();
        } else if (blockId.getNamespace().equals("darks_random_mod")) {
            return "ModBlocks." + blockId.getPath().toUpperCase() + ".get()";
        } else {
            return "Blocks.AIR"; // Default to air if the block is not recognized
        }
    }
}