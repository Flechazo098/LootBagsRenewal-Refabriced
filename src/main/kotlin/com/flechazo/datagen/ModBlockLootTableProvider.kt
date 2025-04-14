package com.flechazo.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.core.HolderLookup
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import com.flechazo.block.ModBlocks
import com.flechazo.component.ModDataComponents
import java.util.concurrent.CompletableFuture

/**
 * 方块战利品表提供器
 */
class ModBlockLootTableProvider(
    dataOutput: FabricDataOutput,
    registryLookupFuture: CompletableFuture<HolderLookup.Provider>
) : FabricBlockLootTableProvider(dataOutput, registryLookupFuture) {

    override fun generate() {
        // 简单掉落自身的方块
        dropSelf(ModBlocks.BAG_OPENER)

        // 需要保存组件数据的方块
        add(ModBlocks.BAG_STORAGE) { block ->
            LootTable.lootTable()
                .withPool(applyExplosionDecay(
                    block,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1F))
                        .add(
                            LootItem.lootTableItem(block)
                                .apply(
                                    CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                        .include(ModDataComponents.BAG_STORAGE)
                                )
                        )
                ))
        }

        add(ModBlocks.LOOT_RECYCLER) { block ->
            LootTable.lootTable()
                .withPool(applyExplosionDecay(
                    block,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1F))
                        .add(
                            LootItem.lootTableItem(block)
                                .apply(
                                    CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                        .include(ModDataComponents.BAG_STORAGE)
                                )
                        )
                ))
        }
    }
}