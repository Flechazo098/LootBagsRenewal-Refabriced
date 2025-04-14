package com.flechazo.item

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import com.flechazo.LootBags
import com.flechazo.block.ModBlocks

object ModCreativeModeTabs {
    val LOOT_BAGS_TAB_KEY: ResourceKey<CreativeModeTab> = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB,
        ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "loot_bags_tab")
    )

    val LOOT_BAGS_TAB: CreativeModeTab = FabricItemGroup.builder()
        .icon {ItemStack(ModItems.COMMON_LOOT_BAG) }
        .title(Component.translatable("creativetab.lootbags.loot_bags_tab"))
        .displayItems { _, output ->
            output.accept(ModItems.COMMON_LOOT_BAG)
            output.accept(ModItems.UNCOMMON_LOOT_BAG)
            output.accept(ModItems.RARE_LOOT_BAG)
            output.accept(ModItems.EPIC_LOOT_BAG)
            output.accept(ModItems.LEGENDARY_LOOT_BAG)
            output.accept(ModItems.PATIENT_LOOT_BAG)
            output.accept(ModItems.ARTIFICIAL_LOOT_BAG)
            output.accept(ModItems.BACON_LOOT_BAG)

            output.accept(ModBlocks.LOOT_RECYCLER)
            output.accept(ModBlocks.BAG_OPENER)
            output.accept(ModBlocks.BAG_STORAGE)
        }
        .build()

    fun register() {
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            LOOT_BAGS_TAB_KEY,
            LOOT_BAGS_TAB
        )
    }
}