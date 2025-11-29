package top.srcres258.renewal.lootbags.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class ModItemTagsProvider(
    output: FabricDataOutput,
    registries: CompletableFuture<HolderLookup.Provider>
) : FabricTagProvider.ItemTagProvider(output, registries) {
    override fun addTags(provider: HolderLookup.Provider) {}
}