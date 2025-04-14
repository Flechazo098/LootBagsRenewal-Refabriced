package com.flechazo

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import com.flechazo.datagen.*

object DataGenerators : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()

        // 添加数据生成器
        pack.addProvider { output, registriesFuture ->
            ModBlockLootTableProvider(output, registriesFuture)
        }
        pack.addProvider { output, registriesFuture ->
            ModRecipeProvider(output, registriesFuture)
        }
        pack.addProvider { output, registriesFuture ->
            ModBlockTagsProvider(output, registriesFuture)
        }
        pack.addProvider { output, registriesFuture ->
            ModItemTagsProvider(output, registriesFuture)
        }
        pack.addProvider { output, _ ->
            ModModelsProvider(output)
        }
        pack.addProvider { output, registriesFuture ->
            ModDynamicRegistryProvider(output, registriesFuture)
        }
    }
}