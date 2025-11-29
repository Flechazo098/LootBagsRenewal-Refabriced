package top.srcres258.renewal.lootbags.block.entity

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.core.Direction
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import top.srcres258.renewal.lootbags.LootBags
import top.srcres258.renewal.lootbags.block.ModBlocks
import top.srcres258.renewal.lootbags.block.entity.custom.BagOpenerBlockEntity
import top.srcres258.renewal.lootbags.block.entity.custom.BagStorageBlockEntity
import top.srcres258.renewal.lootbags.block.entity.custom.LootRecyclerBlockEntity

object ModBlockEntities {
    lateinit var BAG_STORAGE: BlockEntityType<BagStorageBlockEntity>
    lateinit var BAG_OPENER: BlockEntityType<BagOpenerBlockEntity>
    lateinit var LOOT_RECYCLER: BlockEntityType<LootRecyclerBlockEntity>

    fun register() {
        BAG_STORAGE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            LootBags.id("bag_storage"),
            BlockEntityType.Builder.of(
                ::BagStorageBlockEntity,
                ModBlocks.BAG_STORAGE
            ).build(null)
        )

        BAG_OPENER = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            LootBags.id("bag_opener"),
            BlockEntityType.Builder.of(
                ::BagOpenerBlockEntity,
                ModBlocks.BAG_OPENER
            ).build(null)
        )

        LOOT_RECYCLER = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            LootBags.id("loot_recycler"),
            BlockEntityType.Builder.of(
                ::LootRecyclerBlockEntity,
                ModBlocks.LOOT_RECYCLER
            ).build(null)
        )

        ItemStorage.SIDED.registerForBlockEntity({ be, side ->
            val inv = InventoryStorage.of(be.itemHandler, side)
            if (side == Direction.UP) inv.getSlot(BagStorageBlockEntity.INPUT_SLOT) else inv.getSlot(BagStorageBlockEntity.OUTPUT_SLOT)
        }, BAG_STORAGE)

        ItemStorage.SIDED.registerForBlockEntity({ be, side ->
            if (side == Direction.UP) InventoryStorage.of(be.inputInventory, side)
            else InventoryStorage.of(be.outputInventory, side)
        }, BAG_OPENER)

        ItemStorage.SIDED.registerForBlockEntity({ be, side ->
            val inv = InventoryStorage.of(be.itemHandler, side)
            if (side == Direction.UP) inv.getSlot(LootRecyclerBlockEntity.INPUT_SLOT) else inv.getSlot(LootRecyclerBlockEntity.OUTPUT_SLOT)
        }, LOOT_RECYCLER)
    }
}
