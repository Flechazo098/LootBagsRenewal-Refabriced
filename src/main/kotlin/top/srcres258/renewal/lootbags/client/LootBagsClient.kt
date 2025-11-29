package top.srcres258.renewal.lootbags.client

import net.fabricmc.api.ClientModInitializer
import top.srcres258.renewal.lootbags.screen.ModMenuTypes

class LootBagsClient : ClientModInitializer {

    override fun onInitializeClient() {
        ModMenuTypes.registerScreen()
    }
}
