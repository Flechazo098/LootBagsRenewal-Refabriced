package com.flechazo.network.custom

import com.flechazo.LootBags
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

data class ServerboundSelectLootBagTypePayload(val lootBagTypeOrdinal: Int) : CustomPacketPayload {
    companion object {
        val ID = ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "select_loot_bag_type")
        val TYPE = CustomPacketPayload.Type<ServerboundSelectLootBagTypePayload>(ID)

        val CODEC: StreamCodec<RegistryFriendlyByteBuf, ServerboundSelectLootBagTypePayload> =
            StreamCodec.of(
                { buf, payload -> buf.writeVarInt(payload.lootBagTypeOrdinal) },
                { buf -> ServerboundSelectLootBagTypePayload(buf.readVarInt()) }
            )
    }

    override fun type() = TYPE
}