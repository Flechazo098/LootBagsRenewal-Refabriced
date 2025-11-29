package top.srcres258.renewal.lootbags.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class LootBagsDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()
        pack.addProvider(::ModBlockLootTableProvider)
        pack.addProvider(::ModModelProvider)
        pack.addProvider { output, registries -> ModBlockTagsProvider(output, registries) }
        pack.addProvider { output, registries -> ModItemTagsProvider(output, registries) }
        pack.addProvider(::ModRecipeProvider)
    }
}