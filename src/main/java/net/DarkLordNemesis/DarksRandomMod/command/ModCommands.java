package net.DarkLordNemesis.DarksRandomMod.command;

import net.DarkLordNemesis.DarksRandomMod.command.ScanStructureCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "darks_random_mod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ScanStructureCommand.register(event.getDispatcher());
    }
}