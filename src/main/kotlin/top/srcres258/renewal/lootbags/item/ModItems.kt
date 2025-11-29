package top.srcres258.renewal.lootbags.item

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import top.srcres258.renewal.lootbags.LootBags
import top.srcres258.renewal.lootbags.item.custom.LootBagItem
import top.srcres258.renewal.lootbags.util.LootBagType

object ModItems {
    lateinit var COMMON_LOOT_BAG: Item
    lateinit var UNCOMMON_LOOT_BAG: Item
    lateinit var RARE_LOOT_BAG: Item
    lateinit var EPIC_LOOT_BAG: Item
    lateinit var LEGENDARY_LOOT_BAG: Item
    lateinit var PATIENT_LOOT_BAG: Item
    lateinit var ARTIFICIAL_LOOT_BAG: Item
    lateinit var BACON_LOOT_BAG: Item

    val LOOT_BAGS: List<Item>
        get() = listOf(
            COMMON_LOOT_BAG,
            UNCOMMON_LOOT_BAG,
            RARE_LOOT_BAG,
            EPIC_LOOT_BAG,
            LEGENDARY_LOOT_BAG,
            PATIENT_LOOT_BAG,
            ARTIFICIAL_LOOT_BAG,
            BACON_LOOT_BAG
        )

    fun register() {
        COMMON_LOOT_BAG = registerLootBagItem(LootBagType.COMMON)
        UNCOMMON_LOOT_BAG = registerLootBagItem(LootBagType.UNCOMMON)
        RARE_LOOT_BAG = registerLootBagItem(LootBagType.RARE)
        EPIC_LOOT_BAG = registerLootBagItem(LootBagType.EPIC)
        LEGENDARY_LOOT_BAG = registerLootBagItem(LootBagType.LEGENDARY)
        PATIENT_LOOT_BAG = registerLootBagItem(LootBagType.PATIENT)
        ARTIFICIAL_LOOT_BAG = registerLootBagItem(LootBagType.ARTIFICIAL)
        BACON_LOOT_BAG = registerLootBagItem(LootBagType.BACON)
    }

    private fun registerLootBagItem(type: LootBagType): Item {
        val item = LootBagItem(type, Item.Properties())
        return Registry.register(BuiltInRegistries.ITEM, LootBags.id(type.itemId), item)
    }
}