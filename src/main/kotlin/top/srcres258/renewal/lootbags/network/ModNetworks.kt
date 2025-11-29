package top.srcres258.renewal.lootbags.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import top.srcres258.renewal.lootbags.network.custom.ServerboundSelectLootBagTypePayload

object ModNetworks {
    fun register() {
        PayloadTypeRegistry.playS2C().register(
            ServerboundSelectLootBagTypePayload.TYPE,
            ServerboundSelectLootBagTypePayload.STREAM_CODEC
        )

        PayloadTypeRegistry.playC2S().register(
            ServerboundSelectLootBagTypePayload.TYPE,
            ServerboundSelectLootBagTypePayload.STREAM_CODEC
        )

        ServerPlayNetworking.registerGlobalReceiver(
            ServerboundSelectLootBagTypePayload.TYPE,
            ServerboundSelectLootBagTypePayloadHandler::receive
        )
    }

    fun sendSelectedBagType(ordinal: Int) {
        val payload = ServerboundSelectLootBagTypePayload(ordinal)
        ClientPlayNetworking.send(payload)
    }

}
