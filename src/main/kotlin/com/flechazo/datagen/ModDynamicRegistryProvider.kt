package com.flechazo.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import com.flechazo.LootBags
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class ModDynamicRegistryProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricDynamicRegistryProvider(output, registriesFuture) {

    override fun configure(registries: HolderLookup.Provider, entries: Entries) {
    }

    override fun getName(): String = "${LootBags.MOD_ID} Dynamic Registries"
}