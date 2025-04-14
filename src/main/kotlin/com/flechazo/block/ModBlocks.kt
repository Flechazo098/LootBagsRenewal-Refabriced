package com.flechazo.block

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import com.flechazo.LootBags
import com.flechazo.block.custom.BagOpenerBlock
import com.flechazo.block.custom.BagStorageBlock
import com.flechazo.block.custom.LootRecyclerBlock

object ModBlocks {
    // 存储所有注册的方块，用于战利品表生成
    val BLOCKS = mutableListOf<Block>()

    val LOOT_RECYCLER = registerBlock("loot_recycler", LootRecyclerBlock())
    val BAG_OPENER = registerBlock("bag_opener", BagOpenerBlock())
    val BAG_STORAGE = registerBlock("bag_storage", BagStorageBlock())

    private fun registerBlock(name: String, block: Block): Block {
        val registeredBlock = Registry.register(
            BuiltInRegistries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, name),
            block
        )

        registerBlockItem(name, registeredBlock)
        BLOCKS.add(registeredBlock) // 添加到方块列表中

        return registeredBlock
    }

    private fun registerBlockItem(name: String, block: Block) {
        Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, name),
            BlockItem(block, Item.Properties())
        )
    }

    fun registerModBlocks() {
        LootBags.LOGGER.info("Registering ModBlocks for ${LootBags.MOD_ID}")
    }
}