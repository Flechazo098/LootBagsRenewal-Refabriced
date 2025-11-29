package top.srcres258.renewal.lootbags.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates
import top.srcres258.renewal.lootbags.block.ModBlocks
import top.srcres258.renewal.lootbags.item.ModItems

class ModModelProvider(
    output: FabricDataOutput
) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockModelGenerators: BlockModelGenerators) {
        blockModelGenerators.family(ModBlocks.LOOT_RECYCLER)
        blockModelGenerators.family(ModBlocks.BAG_OPENER)
        blockModelGenerators.family(ModBlocks.BAG_STORAGE)
    }
    override fun generateItemModels(itemModelGenerator: ItemModelGenerators) {
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