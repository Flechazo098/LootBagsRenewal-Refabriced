package top.srcres258.renewal.lootbags.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import top.srcres258.renewal.lootbags.block.ModBlocks
import java.util.concurrent.CompletableFuture

class ModBlockTagsProvider(
    output: FabricDataOutput,
    registries: CompletableFuture<HolderLookup.Provider>
) : FabricTagProvider.BlockTagProvider(output, registries) {
    override fun addTags(provider: HolderLookup.Provider) {
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.LOOT_RECYCLER)
            .add(ModBlocks.BAG_OPENER)
            .add(ModBlocks.BAG_STORAGE)
    }
}