package dev.backrooms.world;

import dev.backrooms.Backrooms;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Level0Teleport {
    public static final ResourceKey<Level> LEVEL_0 = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(Backrooms.MOD_ID, "level_0"));
    private static final String COOLDOWN_KEY = "backrooms_noclip_after";
    private static final int SEARCH_RADIUS = 32;

    private Level0Teleport() {}

    /** Called only on the server thread. The client cannot choose a dimension or position. */
    public static void tryEnter(ServerPlayer player) {
        if (!player.isAlive() || player.isSleeping() || player.isPassenger()
                || !player.level().dimension().equals(Level.OVERWORLD)) {
            return;
        }
        long now = player.serverLevel().getGameTime();
        if (now < player.getPersistentData().getLong(COOLDOWN_KEY)) {
            return;
        }
        player.getPersistentData().putLong(COOLDOWN_KEY, now + 40);
        ServerLevel destination = player.server.getLevel(LEVEL_0);
        if (destination == null) {
            player.displayClientMessage(Component.translatable("message.backrooms.level_unavailable"), true);
            return;
        }
        BlockPos safe = findSafePosition(destination, player);
        if (safe == null) {
            player.displayClientMessage(Component.translatable("message.backrooms.no_safe_position"), true);
            return;
        }
        player.teleportTo(destination, safe.getX() + 0.5D, safe.getY(), safe.getZ() + 0.5D,
                player.getYRot(), player.getXRot());
        player.setDeltaMovement(Vec3.ZERO);
        player.resetFallDistance();
    }

    private static BlockPos findSafePosition(ServerLevel level, ServerPlayer player) {
        BlockPos center = new BlockPos(4, Level0Layout.FLOOR_Y + 1, 4);
        // Bounded nearest-first search respects player edits and never destroys blocks to make space.
        for (int radius = 0; radius <= SEARCH_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }
                    BlockPos pos = center.offset(dx, 0, dz);
                    if (!level.getWorldBorder().isWithinBounds(pos)) {
                        continue;
                    }
                    level.getChunkAt(pos);
                    if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)
                            || !level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) {
                        continue;
                    }
                    // Check standing height even if the player requested no-clip while crouching.
                    double halfWidth = player.getBbWidth() / 2.0D;
                    AABB box = new AABB(pos.getX() + 0.5D - halfWidth, pos.getY(), pos.getZ() + 0.5D - halfWidth,
                            pos.getX() + 0.5D + halfWidth, pos.getY() + 1.8D, pos.getZ() + 0.5D + halfWidth);
                    if (level.getWorldBorder().isWithinBounds(box) && level.noCollision(player, box)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }
}
