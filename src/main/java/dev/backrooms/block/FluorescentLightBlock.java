package dev.backrooms.block;

import dev.backrooms.registry.ModContent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;

public final class FluorescentLightBlock extends Block {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public FluorescentLightBlock() {
        super(Properties.of().mapColor(MapColor.QUARTZ).strength(0.4F).sound(SoundType.GLASS)
                .randomTicks().lightLevel(state -> state.getValue(LIT) ? 14 : 0));
        registerDefaultState(stateDefinition.any().setValue(LIT, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT) && random.nextInt(80) == 0) {
            level.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL);
            // Scheduled ticks restore light promptly, rather than waiting for another random tick.
            level.scheduleTick(pos, this, 2 + random.nextInt(7));
        } else if (!state.getValue(LIT)) {
            level.scheduleTick(pos, this, 2);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT) && random.nextInt(100) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    ModContent.FLUORESCENT_HUM.get(), SoundSource.BLOCKS, 0.12F, 1.0F, false);
        }
    }
}
