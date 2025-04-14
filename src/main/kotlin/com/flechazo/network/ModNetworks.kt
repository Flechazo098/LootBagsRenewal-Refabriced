package com.flechazo.network

import com.flechazo.network.custom.ServerboundSelectLootBagTypePayload
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object ModNetworks {
    fun registerPayloadHandlers() {
        // 注册数据包类型
        PayloadTypeRegistry.playC2S().register(
            ServerboundSelectLootBagTypePayload.TYPE,
            ServerboundSelectLootBagTypePayload.CODEC
        )

        // 注册服务器端处理器
        ServerPlayNetworking.registerGlobalReceiver(
            ServerboundSelectLootBagTypePayload.TYPE
        ) { payload, context ->
            ServerboundSelectLootBagTypePayloadHandler.handleData(payload, context)
        }
    }
}