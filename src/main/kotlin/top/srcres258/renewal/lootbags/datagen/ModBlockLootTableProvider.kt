package top.srcres258.renewal.lootbags.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import top.srcres258.renewal.lootbags.block.ModBlocks
import top.srcres258.renewal.lootbags.component.ModDataComponents
import java.util.concurrent.CompletableFuture

class ModBlockLootTableProvider(
    output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>
) : FabricBlockLootTableProvider(output, registryLookup) {
    override fun generate() {
        dropSelf(ModBlocks.BAG_OPENER)
        add(
            ModBlocks.BAG_STORAGE,
            LootTable.lootTable().withPool(
                applyExplosionDecay(
                    ModBlocks.BAG_STORAGE,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1F))
                        .add(
                            LootItem.lootTableItem(ModBlocks.BAG_STORAGE).apply(
                                CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                    .include(ModDataComponents.BAG_STORAGE)
                            )
                        )
                )
            )
        )
        add(
            ModBlocks.LOOT_RECYCLER,
            LootTable.lootTable().withPool(
                applyExplosionDecay(
                    ModBlocks.LOOT_RECYCLER,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1F))
                        .add(
                            LootItem.lootTableItem(ModBlocks.LOOT_RECYCLER).apply(
                                CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                    .include(ModDataComponents.BAG_STORAGE)
                            )
                        )
                )
            )
        )
    }
}