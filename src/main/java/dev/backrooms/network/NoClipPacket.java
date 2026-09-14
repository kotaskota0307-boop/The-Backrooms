package dev.backrooms.network;

import dev.backrooms.world.Level0Teleport;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Empty intent-only C2S packet: all authorization and location selection are server-owned. */
public record NoClipPacket() {
    public static void encode(NoClipPacket message, FriendlyByteBuf buffer) {}

    public static NoClipPacket decode(FriendlyByteBuf buffer) {
        if (buffer.readableBytes() != 0) {
            throw new IllegalArgumentException("No-clip packet must have an empty payload");
        }
        return new NoClipPacket();
    }

    public static void handle(NoClipPacket message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                Level0Teleport.tryEnter(sender);
            }
        });
        context.setPacketHandled(true);
    }
}
