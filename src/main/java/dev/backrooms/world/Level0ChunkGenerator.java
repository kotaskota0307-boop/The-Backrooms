package dev.backrooms.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.backrooms.registry.ModContent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class Level0ChunkGenerator extends ChunkGenerator {
    public static final Codec<Level0ChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(Level0ChunkGenerator::getBiomeSource)
    ).apply(instance, Level0ChunkGenerator::new));

    public Level0ChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState random,
                                                       StructureManager structures, ChunkAccess chunk) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Heightmap surface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        Heightmap floor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        for (int localX = 0; localX < 16; localX++) {
            int x = chunk.getPos().getMinBlockX() + localX;
            for (int localZ = 0; localZ < 16; localZ++) {
                int z = chunk.getPos().getMinBlockZ() + localZ;
                for (int y = Level0Layout.FLOOR_Y; y <= Level0Layout.CEILING_Y; y++) {
                    BlockState state = stateAt(x, y, z);
                    chunk.setBlockState(pos.set(x, y, z), state, false);
                    surface.update(localX, y, localZ, state);
                    floor.update(localX, y, localZ, state);
                }
            }
        }
        // Like vanilla flat generation, only this supplied chunk is modified, synchronously.
        return CompletableFuture.completedFuture(chunk);
    }

    private static BlockState stateAt(int x, int y, int z) {
        if (y == Level0Layout.FLOOR_Y) {
            return ModContent.MOIST_CARPET.get().defaultBlockState();
        }
        if (y == Level0Layout.CEILING_Y) {
            return (Level0Layout.isLight(x, z) ? ModContent.FLUORESCENT_LIGHT : ModContent.CEILING_TILE)
                    .get().defaultBlockState();
        }
        if (y > Level0Layout.FLOOR_Y && y < Level0Layout.CEILING_Y && Level0Layout.isWall(x, z)) {
            return ModContent.YELLOW_WALLPAPER.get().defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return Level0Layout.CEILING_Y + 1;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState random) {
        BlockState[] states = new BlockState[getGenDepth()];
        for (int y = 0; y < states.length; y++) {
            states[y] = stateAt(x, y + getMinY(), z);
        }
        return new NoiseColumn(getMinY(), states);
    }

    @Override
    public int getGenDepth() { return Level0Layout.HEIGHT; }

    @Override
    public int getMinY() { return Level0Layout.FLOOR_Y; }

    @Override
    public int getSeaLevel() { return Level0Layout.FLOOR_Y; }

    @Override
    public int getSpawnHeight(LevelHeightAccessor level) { return Level0Layout.FLOOR_Y + 1; }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState random, BiomeManager biomes,
                             StructureManager structures, ChunkAccess chunk, GenerationStep.Carving step) {}

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState random, ChunkAccess chunk) {}

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {}

    @Override
    public void applyBiomeDecoration(net.minecraft.world.level.WorldGenLevel level, ChunkAccess chunk,
                                     StructureManager structures) {
        // Never decorate the enclosed rooms with surface vegetation or structures.
    }

    @Override
    public void addDebugScreenInfo(List<String> lines, RandomState random, BlockPos pos) {
        lines.add("Backrooms Level 0 / room " + Math.floorDiv(pos.getX(), 8) + ", " + Math.floorDiv(pos.getZ(), 8));
    }
}
