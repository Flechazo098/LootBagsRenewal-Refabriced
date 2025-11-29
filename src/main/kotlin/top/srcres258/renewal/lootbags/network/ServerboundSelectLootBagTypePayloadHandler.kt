package top.srcres258.renewal.lootbags.network

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import top.srcres258.renewal.lootbags.util.LootBagType
import top.srcres258.renewal.lootbags.network.custom.ServerboundSelectLootBagTypePayload
import top.srcres258.renewal.lootbags.screen.custom.BagStorageMenu

object ServerboundSelectLootBagTypePayloadHandler {
    fun receive(payload: ServerboundSelectLootBagTypePayload, context: ServerPlayNetworking.Context) {
        val player = context.player()
        val lootBagTypeIndex = payload.lootBagTypeOrdinal % LootBagType.entries.size

        player.server.execute {
            val menu = player.containerMenu
            if (menu is BagStorageMenu) {
                menu.targetBagType = LootBagType.entries[lootBagTypeIndex]
            }
        }
    }
}
