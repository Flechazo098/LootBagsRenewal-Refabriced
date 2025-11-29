package top.srcres258.renewal.lootbags.screen

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.entity.player.Inventory
import top.srcres258.renewal.lootbags.LootBags
import top.srcres258.renewal.lootbags.screen.custom.BagOpenerMenu
import top.srcres258.renewal.lootbags.screen.custom.BagOpenerScreen
import top.srcres258.renewal.lootbags.screen.custom.BagStorageMenu
import top.srcres258.renewal.lootbags.screen.custom.BagStorageScreen
import top.srcres258.renewal.lootbags.screen.custom.LootRecyclerMenu
import top.srcres258.renewal.lootbags.screen.custom.LootRecyclerScreen

object ModMenuTypes {
    lateinit var BAG_STORAGE: MenuType<BagStorageMenu>
    lateinit var BAG_OPENER: MenuType<BagOpenerMenu>
    lateinit var LOOT_RECYCLER: MenuType<LootRecyclerMenu>

    fun registerMenu() {
        BAG_STORAGE = Registry.register(
            BuiltInRegistries.MENU,
            LootBags.id("bag_storage"),
            ExtendedScreenHandlerType(
                { id: Int, inv: Inventory, pos: BlockPos -> BagStorageMenu(id, inv, inv.player.level(), pos) },
                BlockPos.STREAM_CODEC
            )
        )
        BAG_OPENER = Registry.register(
            BuiltInRegistries.MENU,
            LootBags.id("bag_opener"),
            ExtendedScreenHandlerType(
                { id: Int, inv: Inventory, pos: BlockPos -> BagOpenerMenu(id, inv, inv.player.level(), pos) },
                BlockPos.STREAM_CODEC
            )
        )
        LOOT_RECYCLER = Registry.register(
            BuiltInRegistries.MENU,
            LootBags.id("loot_recycler"),
            ExtendedScreenHandlerType(
                { id: Int, inv: Inventory, pos: BlockPos -> LootRecyclerMenu(id, inv, inv.player.level(), pos) },
                BlockPos.STREAM_CODEC
            )
        )
    }

    fun registerScreen() {
        MenuScreens.register(BAG_STORAGE, ::BagStorageScreen)
        MenuScreens.register(BAG_OPENER, ::BagOpenerScreen)
        MenuScreens.register(LOOT_RECYCLER, ::LootRecyclerScreen)
    }
}
