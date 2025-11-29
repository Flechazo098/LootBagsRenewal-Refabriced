package top.srcres258.renewal.lootbags

import com.mojang.logging.LogUtils
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import top.srcres258.renewal.lootbags.block.ModBlocks
import top.srcres258.renewal.lootbags.block.entity.ModBlockEntities
import top.srcres258.renewal.lootbags.component.ModDataComponents
import top.srcres258.renewal.lootbags.event.ModEvents
import top.srcres258.renewal.lootbags.item.ModCreativeModeTabs
import top.srcres258.renewal.lootbags.item.ModItems
import top.srcres258.renewal.lootbags.network.ModNetworks
import top.srcres258.renewal.lootbags.screen.ModMenuTypes

class LootBags : ModInitializer {
    companion object {
        const val MOD_ID = "lootbags"

        val LOGGER: Logger = LogUtils.getLogger()

        fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }

    override fun onInitialize() {
        ModNetworks.register()
        ModDataComponents.register()
        ModItems.register()
        ModBlocks.register()
        ModMenuTypes.registerMenu()
        ModBlockEntities.register()
        ModCreativeModeTabs.register()
        ModEvents.register()
    }
}
