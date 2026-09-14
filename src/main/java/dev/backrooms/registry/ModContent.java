package dev.backrooms.registry;

import com.mojang.serialization.Codec;
import dev.backrooms.Backrooms;
import dev.backrooms.block.FluorescentLightBlock;
import dev.backrooms.item.AlmondWaterItem;
import dev.backrooms.world.Level0ChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModContent {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Backrooms.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Backrooms.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Backrooms.MOD_ID);
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, Backrooms.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Backrooms.MOD_ID);

    public static final RegistryObject<SoundEvent> FLUORESCENT_HUM = sound("fluorescent_hum");
    public static final RegistryObject<SoundEvent> CARPET_STEP = sound("carpet_step");
    private static final SoundType CARPET_SOUNDS = new ForgeSoundType(1.0F, 0.8F,
            () -> SoundEvents.WOOL_BREAK, CARPET_STEP, () -> SoundEvents.WOOL_PLACE,
            () -> SoundEvents.WOOL_HIT, CARPET_STEP);

    public static final RegistryObject<Block> YELLOW_WALLPAPER = block("yellow_wallpaper",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(1.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> CEILING_TILE = block("ceiling_tile",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).strength(1.0F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> MOIST_CARPET = block("moist_carpet",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.6F).sound(CARPET_SOUNDS)));
    public static final RegistryObject<Block> FLUORESCENT_LIGHT = block("fluorescent_light", FluorescentLightBlock::new);
    public static final RegistryObject<Item> ALMOND_WATER = ITEMS.register("almond_water",
            () -> new AlmondWaterItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Codec<? extends ChunkGenerator>> LEVEL_0_GENERATOR =
            GENERATORS.register("level_0", () -> Level0ChunkGenerator.CODEC);
    public static final RegistryObject<CreativeModeTab> BACKROOMS_TAB = TABS.register("backrooms", () ->
            CreativeModeTab.builder().title(Component.translatable("itemGroup.backrooms"))
                    .icon(() -> YELLOW_WALLPAPER.get().asItem().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(YELLOW_WALLPAPER.get());
                        output.accept(CEILING_TILE.get());
                        output.accept(MOIST_CARPET.get());
                        output.accept(FLUORESCENT_LIGHT.get());
                        output.accept(ALMOND_WATER.get());
                    }).build());

    private ModContent() {}

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Backrooms.MOD_ID, name)));
    }

    private static RegistryObject<Block> block(String name, Supplier<Block> factory) {
        RegistryObject<Block> block = BLOCKS.register(name, factory);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        SOUNDS.register(bus);
        GENERATORS.register(bus);
        TABS.register(bus);
    }
}
