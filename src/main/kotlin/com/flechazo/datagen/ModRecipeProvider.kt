package com.flechazo.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import com.flechazo.block.ModBlocks
import com.flechazo.util.LootBagType
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricRecipeProvider(output, registriesFuture) {

    override fun buildRecipes(recipeOutput: RecipeOutput) {
        // 功能性方块的配方
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LOOT_RECYCLER)
            .pattern("AAA")
            .pattern("ABA")
            .pattern("ACA")
            .define('A', Blocks.STONE)
            .define('B', Items.CHEST)
            .define('C', Items.IRON_INGOT)
            .unlockedBy("has_chest", has(Items.CHEST))
            .save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BAG_OPENER)
            .pattern("ACA")
            .pattern("ABA")
            .pattern("AAA")
            .define('A', Blocks.STONE)
            .define('B', Items.CHEST)
            .define('C', Items.IRON_INGOT)
            .unlockedBy("has_chest", has(Items.CHEST))
            .save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BAG_STORAGE)
            .pattern("AAA")
            .pattern("CBC")
            .pattern("AAA")
            .define('A', Blocks.STONE)
            .define('B', Items.CHEST)
            .define('C', Items.IRON_INGOT)
            .unlockedBy("has_chest", has(Items.CHEST))
            .save(recipeOutput)

        // 战利品袋之间的转换配方
        for (type in LootBagType.entries) {
            for (otherType in LootBagType.entries) {
                // 跳过稀有度大于等于目标类型的情况
                if (otherType.rarity >= type.rarity) {
                    continue
                }
                // 跳过创造模式专属的袋子
                if (type.creativeOnly || otherType.creativeOnly) {
                    continue
                }
                val amount = type.amountFactorEquivalentTo(otherType).toInt()
                // 跳过数量大于4的情况，防止客户端-服务器通信出错
                if (amount <= 4) {
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, type.asItem())
                        .requires(otherType.asItem(), amount)
                        .unlockedBy("has_${otherType.itemId}", has(otherType.asItem()))
                        .save(recipeOutput, "${type.itemId}_from_${otherType.itemId}")
                }
            }
        }
    }
}