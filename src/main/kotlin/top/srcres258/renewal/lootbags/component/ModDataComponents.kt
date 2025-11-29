package top.srcres258.renewal.lootbags.component

import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import top.srcres258.renewal.lootbags.LootBags
import top.srcres258.renewal.lootbags.util.BagStorageRecord


object ModDataComponents {
    lateinit var BAG_STORAGE : DataComponentType<BagStorageRecord>

    fun register() {
        BAG_STORAGE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            LootBags.id("bag_storage"),
            DataComponentType.builder<BagStorageRecord>()
                .persistent(BagStorageRecord.CODEC)
                .networkSynchronized(BagStorageRecord.STREAM_CODEC)
                .build()
        )
    }
}