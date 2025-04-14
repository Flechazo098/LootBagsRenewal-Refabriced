package com.flechazo.component

import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import com.flechazo.LootBags
import com.flechazo.util.BagStorageRecord

object ModDataComponents {
    val BAG_STORAGE: DataComponentType<BagStorageRecord> = run {
        val builder = DataComponentType.builder<BagStorageRecord>()
            .persistent(BagStorageRecord.CODEC)
            .networkSynchronized(BagStorageRecord.STREAM_CODEC)

        Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "bag_storage"),
            builder.build()
        )
    }

    fun register() {
        LootBags.LOGGER.info("注册 ${LootBags.MOD_ID} 的数据组件")
    }
}