package dev.backrooms.network;

import dev.backrooms.Backrooms;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class BackroomsNetwork {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Backrooms.MOD_ID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private BackroomsNetwork() {}

    public static void register() {
        CHANNEL.messageBuilder(NoClipPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(NoClipPacket::encode)
                .decoder(NoClipPacket::decode)
                .consumerNetworkThread(NoClipPacket::handle)
                .add();
    }

    public static void requestNoClip() {
        CHANNEL.sendToServer(new NoClipPacket());
    }
}
