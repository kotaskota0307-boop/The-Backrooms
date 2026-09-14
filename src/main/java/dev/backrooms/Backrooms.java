package dev.backrooms;

import dev.backrooms.network.BackroomsNetwork;
import dev.backrooms.registry.ModContent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Backrooms.MOD_ID)
public final class Backrooms {
    public static final String MOD_ID = "backrooms";

    public Backrooms() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModContent.register(bus);
        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(BackroomsNetwork::register);
    }
}
