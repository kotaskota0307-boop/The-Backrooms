package dev.backrooms.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.backrooms.Backrooms;
import dev.backrooms.network.BackroomsNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Backrooms.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientKeys {
    private static final KeyMapping NO_CLIP = new KeyMapping("key.backrooms.noclip", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.backrooms");

    private ClientKeys() {}

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(NO_CLIP);
    }

    @Mod.EventBusSubscriber(modid = Backrooms.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class InputEvents {
        private InputEvents() {}

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            boolean pressed = false;
            while (NO_CLIP.consumeClick()) {
                pressed = true;
            }
            if (pressed && minecraft.player != null && minecraft.screen == null && !minecraft.isPaused()) {
                BackroomsNetwork.requestNoClip();
            }
        }
    }
}
