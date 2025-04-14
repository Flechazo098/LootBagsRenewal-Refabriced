package com.flechazo.event

import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import com.flechazo.LootBags
import com.flechazo.util.LootBagType

object ModEvents {
    fun register() {
        LootTableEvents.MODIFY.register { key, tableBuilder, source, _ ->
            // 只处理实体掉落表
            if (source.isBuiltin && key.location().path.startsWith("entities/")) {
                for (bagType in LootBagType.entries) {
                    if (!bagType.droppable) {
                        continue
                    }

                    // 创建新的战利品池
                    val pool = LootPool.Builder()
                        .setRolls(UniformGenerator.between(
                            bagType.dropAmountRange.first.toFloat(),
                            bagType.dropAmountRange.last.toFloat()
                        ))
                        .add(LootItem.lootTableItem(bagType.asItem()))
                        .`when`(LootItemRandomChanceCondition.randomChance(bagType.dropChance.toFloat()))
                        .build()

                    // 添加到战利品表
                    tableBuilder.pool(pool)

                    LootBags.LOGGER.debug(
                        "Added loot bag pool of type {} to entity loot table {}",
                        bagType,
                        key.location()
                    )
                }
            }
        }
    }
}