package com.flechazo.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import com.flechazo.block.ModBlocks
import com.flechazo.item.ModItems
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates

class ModModelsProvider(
    output: FabricDataOutput
) : FabricModelProvider(output) {

    override fun generateBlockStateModels(blockStateModelGenerator: BlockModelGenerators) {
        // 注册方块状态和模型
        blockStateModelGenerator.createTrivialCube(ModBlocks.LOOT_RECYCLER)
        blockStateModelGenerator.createTrivialCube(ModBlocks.BAG_OPENER)
        blockStateModelGenerator.createTrivialCube(ModBlocks.BAG_STORAGE)
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerators) {
        // 注册物品模型
        itemModelGenerator.generateFlatItem(ModItems.COMMON_LOOT_BAG, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.UNCOMMON_LOOT_BAG, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.RARE_LOOT_BAG, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.EPIC_LOOT_BAG, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.LEGENDARY_LOOT_BAG, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.PATIENT_LOOT_BAG, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.ARTIFICIAL_LOOT_BAG, ModelTemplates.FLAT_ITEM)
        itemModelGenerator.generateFlatItem(ModItems.BACON_LOOT_BAG, ModelTemplates.FLAT_ITEM)
    }
}