package com.flechazo.item

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import com.flechazo.LootBags
import com.flechazo.item.custom.LootBagItem
import com.flechazo.util.LootBagType

object ModItems {
    val COMMON_LOOT_BAG = registerLootBagItem(LootBagType.COMMON)
    val UNCOMMON_LOOT_BAG = registerLootBagItem(LootBagType.UNCOMMON)
    val RARE_LOOT_BAG = registerLootBagItem(LootBagType.RARE)
    val EPIC_LOOT_BAG = registerLootBagItem(LootBagType.EPIC)
    val LEGENDARY_LOOT_BAG = registerLootBagItem(LootBagType.LEGENDARY)
    val PATIENT_LOOT_BAG = registerLootBagItem(LootBagType.PATIENT)
    val ARTIFICIAL_LOOT_BAG = registerLootBagItem(LootBagType.ARTIFICIAL)
    val BACON_LOOT_BAG = registerLootBagItem(LootBagType.BACON)

    val LOOT_BAGS: List<Item> = listOf(
        COMMON_LOOT_BAG,
        UNCOMMON_LOOT_BAG,
        RARE_LOOT_BAG,
        EPIC_LOOT_BAG,
        LEGENDARY_LOOT_BAG,
        PATIENT_LOOT_BAG,
        ARTIFICIAL_LOOT_BAG,
        BACON_LOOT_BAG
    )

    private fun registerLootBagItem(type: LootBagType): Item {
        return registerItem(type.itemId, LootBagItem(type))
    }

    private fun registerItem(name: String, item: Item): Item {
        return Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, name),
            item
        )
    }

    fun registerModItems() {
        LootBags.LOGGER.info("Registering ModItems for ${LootBags.MOD_ID}")
    }
}