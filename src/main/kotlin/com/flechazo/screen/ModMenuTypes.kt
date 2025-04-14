package com.flechazo.screen

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.MenuType
import com.flechazo.LootBags
import com.flechazo.screen.custom.BagOpenerMenu
import com.flechazo.screen.custom.BagStorageMenu
import com.flechazo.screen.custom.LootRecyclerMenu

object ModMenuTypes {
    val BAG_STORAGE: MenuType<BagStorageMenu> = Registry.register(
        BuiltInRegistries.MENU,
        ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "bag_storage"),
        MenuType({ containerId, inv ->
            BagStorageMenu(containerId, inv, inv.player.level(), FriendlyByteBuf(null))
        }, FeatureFlagSet.of())
    )

    val BAG_OPENER: MenuType<BagOpenerMenu> = Registry.register(
        BuiltInRegistries.MENU,
        ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "bag_opener"),
        MenuType({ containerId, inv ->
            BagOpenerMenu(containerId, inv, inv.player.level(), FriendlyByteBuf(null))
        }, FeatureFlagSet.of())
    )

    val LOOT_RECYCLER: MenuType<LootRecyclerMenu> = Registry.register(
        BuiltInRegistries.MENU,
        ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "loot_recycler"),
        MenuType({ containerId, inv ->
            LootRecyclerMenu(containerId, inv, inv.player.level(), FriendlyByteBuf(null))
        }, FeatureFlagSet.of())
    )

    fun register() {
    }
}