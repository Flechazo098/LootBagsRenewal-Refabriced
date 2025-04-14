package com.flechazo

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.client.gui.screens.MenuScreens
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import com.flechazo.block.ModBlocks
import com.flechazo.block.entity.ModBlockEntities
import com.flechazo.component.ModDataComponents
import com.flechazo.item.ModCreativeModeTabs
import com.flechazo.item.ModItems
import com.flechazo.network.ModNetworks
import com.flechazo.screen.ModMenuTypes
import com.flechazo.screen.custom.BagOpenerScreen
import com.flechazo.screen.custom.BagStorageScreen
import com.flechazo.screen.custom.LootRecyclerScreen

/**
 * 主模组类。实现 ModInitializer 接口。
 */
object LootBags : ModInitializer {
	const val MOD_ID = "lootbags"

	val LOGGER: Logger = LogManager.getLogger(MOD_ID)

	override fun onInitialize() {
		LOGGER.info("$MOD_ID is loading...")
		ModNetworks.registerPayloadHandlers()

		// 注册物品、方块等
		ModItems.registerModItems()
		ModBlocks.registerModBlocks()
		ModBlockEntities.registerModBlockEntities()
		ModMenuTypes.register()
		ModCreativeModeTabs.register()
		ModDataComponents.register()
	}
}

/**
 * 客户端初始化类
 */
object LootBagsClient : ClientModInitializer {
	override fun onInitializeClient() {
		// 注册屏幕
		MenuScreens.register(ModMenuTypes.BAG_STORAGE, ::BagStorageScreen)
		MenuScreens.register(ModMenuTypes.BAG_OPENER, ::BagOpenerScreen)
		MenuScreens.register(ModMenuTypes.LOOT_RECYCLER, ::LootRecyclerScreen)
	}
}