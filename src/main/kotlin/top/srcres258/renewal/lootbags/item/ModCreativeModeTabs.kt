package top.srcres258.renewal.lootbags.item

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import top.srcres258.renewal.lootbags.LootBags
import top.srcres258.renewal.lootbags.block.ModBlocks

object ModCreativeModeTabs {

    val LOOT_BAGS_TAB: CreativeModeTab = FabricItemGroup.builder()
        .icon { ItemStack(ModItems.COMMON_LOOT_BAG) }
        .title(Component.translatable("creativetab.lootbags.loot_bags_tab"))
        .displayItems { displayContext, entries ->
            entries.accept(ModItems.COMMON_LOOT_BAG)
            entries.accept(ModItems.UNCOMMON_LOOT_BAG)
            entries.accept(ModItems.RARE_LOOT_BAG)
            entries.accept(ModItems.EPIC_LOOT_BAG)
            entries.accept(ModItems.LEGENDARY_LOOT_BAG)
            entries.accept(ModItems.PATIENT_LOOT_BAG)
            entries.accept(ModItems.ARTIFICIAL_LOOT_BAG)
            entries.accept(ModItems.BACON_LOOT_BAG)

            entries.accept(ModBlocks.LOOT_RECYCLER)
            entries.accept(ModBlocks.BAG_OPENER)
            entries.accept(ModBlocks.BAG_STORAGE)
        }
        .build()

    fun register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            LootBags.id("loot_bags_tab"), LOOT_BAGS_TAB)
    }
}