package com.flechazo.screen.custom

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.level.Level
import com.flechazo.block.ModBlocks
import com.flechazo.block.entity.custom.LootRecyclerBlockEntity
import com.flechazo.screen.ModMenuTypes
import com.flechazo.util.LootBagType
import net.minecraft.core.BlockPos

class LootRecyclerMenu(
    containerId: Int,
    inv: Inventory,
    level: Level,
    private val blockEntity: LootRecyclerBlockEntity,
    val data: ContainerData
) : LootBagContainerMenu(
    ModMenuTypes.LOOT_RECYCLER,
    containerId,
    inv,
    level,
    data,
    blockEntity.itemHandler,
    blockEntity.itemHandler,
    44, 15,
    116, 15,
    8, 50,
    8, 107
) {
    constructor(
        containerId: Int,
        inv: Inventory,
        level: Level,
        extraData: FriendlyByteBuf
    ) : this(containerId, inv, level,
        try {
            level.getBlockEntity(extraData.readBlockPos()) as LootRecyclerBlockEntity
        } catch (e: Exception) {
            LootRecyclerBlockEntity(BlockPos.ZERO, ModBlocks.LOOT_RECYCLER.defaultBlockState())
        },
        SimpleContainerData(LootRecyclerBlockEntity.ContainerDataType.entries.size))

    val storedBagAmount: Int
        get() = data.get(LootRecyclerBlockEntity.ContainerDataType.STORED_BAG_AMOUNT.ordinal)

    override val targetBagType: LootBagType = LootBagType.COMMON

    override val targetBagAmount: Int
        get() = (storedBagAmount.toFloat() * LootBagType.COMMON.amountFactorEquivalentTo(targetBagType)).toInt()

    override fun stillValid(player: Player): Boolean =
        stillValid(ContainerLevelAccess.create(level, blockEntity.blockPos), player, ModBlocks.LOOT_RECYCLER)
}