package com.jamiedev.bygone.core.registry;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.function.Supplier;

public class BGCompostables {
    public static final Supplier<Object2FloatMap<Item>> COMPOSTABLES = Suppliers.memoize(() -> new Object2FloatArrayMap<>(Map.ofEntries(
            Map.entry(BGBlocks.ALPHA_MOSS_BLOCK.get().asItem(), 0.3f),
            Map.entry(BGBlocks.ALPHA_MOSS_CARPET.get().asItem(), 0.3f),
            Map.entry(BGBlocks.ANCIENT_LEAVES.get().asItem(), 0.3f),
            Map.entry(BGBlocks.ANCIENT_ROOTS.get().asItem(), 0.3f),
            Map.entry(BGBlocks.ANCIENT_SAPLING.get().asItem(), 0.3f),
            Map.entry(BGBlocks.ANCIENT_VINE.get().asItem(), 0.3f),
            Map.entry(BGBlocks.BELLADONNA.get().asItem(), 0.3f),
            Map.entry(BGBlocks.BIG_WHIRLIWEED.get().asItem(), 0.3f),
            Map.entry(BGBlocks.COLEUS.get().asItem(), 0.3f),
            Map.entry(BGBlocks.CREOSOTE_SPROUTS.get().asItem(), 0.3f),
            Map.entry(BGBlocks.MONTSECHIA.get().asItem(), 0.3f),
            Map.entry(BGBlocks.ORANGE_FUNGI_VINES.get().asItem(), 0.3f),
            Map.entry(BGBlocks.PINK_FUNGI_VINES.get().asItem(), 0.3f),
            Map.entry(BGBlocks.PURPLE_FUNGI_VINES.get().asItem(), 0.3f),
            Map.entry(BGBlocks.RAFFLESIA.get().asItem(), 0.3f),
            Map.entry(BGBlocks.LIME_SAGARIA.get().asItem(), 0.3f),
            Map.entry(BGBlocks.PINK_SAGARIA.get().asItem(), 0.3f),
            Map.entry(BGBlocks.PURPLE_SAGARIA.get().asItem(), 0.3f),
            Map.entry(BGBlocks.YELLOW_SAGARIA.get().asItem(), 0.3f),
            Map.entry(BGBlocks.SHELF_FUNGUS.get().asItem(), 0.3f),
            Map.entry(BGBlocks.SHELF_MOLD.get().asItem(), 0.3f),
            Map.entry(BGBlocks.SHELF_ROOTS.get().asItem(), 0.3f),
            Map.entry(BGBlocks.SHELF_SPROUTS.get().asItem(), 0.3f),
            Map.entry(BGBlocks.WHIRLIWEED.get().asItem(), 0.3f),
            Map.entry(BGItems.AMARANTH_GRAIN.get(), 0.3f),
            Map.entry(BGItems.AMARANTH_SEEDS.get(), 0.3f))));
}
