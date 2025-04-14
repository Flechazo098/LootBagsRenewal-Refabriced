package com.flechazo.block.entity

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntityType
import com.flechazo.LootBags
import com.flechazo.block.ModBlocks
import com.flechazo.block.entity.custom.BagOpenerBlockEntity
import com.flechazo.block.entity.custom.BagStorageBlockEntity
import com.flechazo.block.entity.custom.LootRecyclerBlockEntity

object ModBlockEntities {
    val BAG_STORAGE: BlockEntityType<BagStorageBlockEntity> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "bag_storage"),
        BlockEntityType.Builder.of(
            ::BagStorageBlockEntity,
            ModBlocks.BAG_STORAGE
        ).build(null)
    )

    val BAG_OPENER: BlockEntityType<BagOpenerBlockEntity> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "bag_opener"),
        BlockEntityType.Builder.of(
            ::BagOpenerBlockEntity,
            ModBlocks.BAG_OPENER
        ).build(null)
    )

    val LOOT_RECYCLER: BlockEntityType<LootRecyclerBlockEntity> = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "loot_recycler"),
        BlockEntityType.Builder.of(
            ::LootRecyclerBlockEntity,
            ModBlocks.LOOT_RECYCLER
        ).build(null)
    )

    fun registerModBlockEntities() {
        LootBags.LOGGER.info("Registering ModBlockEntities for ${LootBags.MOD_ID}")
    }
}