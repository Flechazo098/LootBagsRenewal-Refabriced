package top.srcres258.renewal.lootbags.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import top.srcres258.renewal.lootbags.block.ModBlocks
import top.srcres258.renewal.lootbags.util.LootBagType
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(
    output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricRecipeProvider(output, registriesFuture) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
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

        for (type in LootBagType.entries) {
            for (otherType in LootBagType.entries) {
                if (otherType.rarity >= type.rarity) continue
                if (type.creativeOnly || otherType.creativeOnly) continue
                val amount = type.amountFactorEquivalentTo(otherType).toInt()
                if (amount <= 4) {
                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, type.asItem())
                        .requires(otherType, amount)
                        .unlockedBy("has_${otherType.itemId}", has(otherType.asItem()))
                        .save(recipeOutput, "${type.itemId}_from_${otherType.itemId}")
                }
            }
        }
    }
}