package com.flechazo.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.world.item.Item
import com.flechazo.LootBags
import net.minecraft.core.HolderLookup
import net.minecraft.tags.TagKey
import java.util.concurrent.CompletableFuture

class ModItemTagsProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricTagProvider.ItemTagProvider(output, registriesFuture) {

    override fun addTags(wrapperLookup: HolderLookup.Provider) {
        // 在这里添加物品标签
        // 例如：
        // getOrCreateTagBuilder(ItemTags.PLANKS)
        //     .add(ModItems.XXX)
    }

    override fun tag(tag: TagKey<Item>): FabricTagBuilder {
        return getOrCreateTagBuilder(tag)
    }

    override fun getName(): String {
        return "${LootBags.MOD_ID} Item Tags"
    }
}