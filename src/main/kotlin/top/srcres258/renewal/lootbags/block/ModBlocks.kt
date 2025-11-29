package top.srcres258.renewal.lootbags.block

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import top.srcres258.renewal.lootbags.LootBags
import top.srcres258.renewal.lootbags.block.custom.BagOpenerBlock
import top.srcres258.renewal.lootbags.block.custom.BagStorageBlock
import top.srcres258.renewal.lootbags.block.custom.LootRecyclerBlock

object ModBlocks {
    lateinit var LOOT_RECYCLER: Block
    lateinit var BAG_OPENER: Block
    lateinit var BAG_STORAGE: Block

    fun register() {
        LOOT_RECYCLER = registerBlock("loot_recycler") { LootRecyclerBlock() }
        BAG_OPENER = registerBlock("bag_opener") { BagOpenerBlock() }
        BAG_STORAGE = registerBlock("bag_storage") { BagStorageBlock() }
    }

    private inline fun <reified T : Block> registerBlock(name: String, crossinline factory: () -> T): T {
        val block = factory()
        val registeredBlock = Registry.register(BuiltInRegistries.BLOCK, LootBags.id(name), block)
        Registry.register(BuiltInRegistries.ITEM, LootBags.id(name), BlockItem(registeredBlock, Item.Properties()))
        return registeredBlock
    }
}