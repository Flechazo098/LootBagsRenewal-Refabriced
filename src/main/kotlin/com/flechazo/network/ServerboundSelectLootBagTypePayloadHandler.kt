package com.flechazo.network

import com.flechazo.network.custom.ServerboundSelectLootBagTypePayload
import com.flechazo.screen.custom.BagStorageMenu
import com.flechazo.util.LootBagType
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object ServerboundSelectLootBagTypePayloadHandler {
    fun handleData(payload: ServerboundSelectLootBagTypePayload, context: ServerPlayNetworking.Context) {
        val lootBagTypeIndex = payload.lootBagTypeOrdinal % LootBagType.entries.size
        val menu = context.player().containerMenu
        if (menu is BagStorageMenu) {
            menu.targetBagType = LootBagType.entries[lootBagTypeIndex]
        }
    }
}