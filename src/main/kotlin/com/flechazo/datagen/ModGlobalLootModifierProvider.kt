//package com.flechazo.datagen
//
//import net.minecraft.core.HolderLookup
//import net.minecraft.data.PackOutput
//import net.neoforged.neoforge.common.data.GlobalLootModifierProvider
//import com.flechazo.LootBags
//import java.util.concurrent.CompletableFuture
//
//class ModGlobalLootModifierProvider(
//    output: PackOutput,
//    registries: CompletableFuture<HolderLookup.Provider>
//) : GlobalLootModifierProvider(output, registries, LootBags.MOD_ID) {
//    override fun start() {}
//}