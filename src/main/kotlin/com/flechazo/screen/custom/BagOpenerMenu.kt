package com.flechazo.screen.custom

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import com.flechazo.block.ModBlocks
import com.flechazo.block.entity.custom.BagOpenerBlockEntity
import com.flechazo.item.ModItems
import com.flechazo.screen.ModMenuTypes
import com.flechazo.util.addPlayerHotbarSlots
import com.flechazo.util.addPlayerInventorySlots
import com.flechazo.util.quickMoveStack
import net.minecraft.core.BlockPos

// THIS YOU HAVE TO DEFINE!
private const val BE_INVENTORY_SLOT_COUNT = BagOpenerBlockEntity.SLOTS_COUNT

class BagOpenerMenu(
    containerId: Int,
    inv: Inventory,
    private val level: Level,
    val blockEntity: BagOpenerBlockEntity,
    val data: ContainerData
) : AbstractContainerMenu(ModMenuTypes.BAG_OPENER, containerId) {
    // 创建一个包装 BlockEntity 的 Container
    private val inputContainer = object : SimpleContainer(BagOpenerBlockEntity.INPUT_SLOTS_COUNT) {
        override fun setChanged() {
            super.setChanged()
            blockEntity.setChanged()
        }

        override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean {
            // 只允许放入战利品袋
            return ModItems.LOOT_BAGS.any { it == stack.item }
        }
    }

    private val outputContainer = object : SimpleContainer(BagOpenerBlockEntity.OUTPUT_SLOTS_COUNT) {
        override fun setChanged() {
            super.setChanged()
            blockEntity.setChanged()
        }

        override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean {
            // 输出槽不允许放入物品
            return false
        }
    }

    constructor(
        containerId: Int,
        inv: Inventory,
        level: Level,
        extraData: FriendlyByteBuf
    ) : this(
        containerId,
        inv,
        level,
        // 这里可能是问题所在，需要安全地读取BlockPos
        try {
            level.getBlockEntity(extraData.readBlockPos()) as BagOpenerBlockEntity
        } catch (e: Exception) {
            // 如果读取失败，创建一个临时的BlockEntity
            BagOpenerBlockEntity(BlockPos.ZERO, ModBlocks.BAG_OPENER.defaultBlockState())
        },
        SimpleContainerData(BagOpenerBlockEntity.ContainerDataType.entries.size)
    )

    init {
        // 从方块实体同步物品到容器
        syncBlockEntityToContainers()

        addPlayerInventorySlots(inv)
        addPlayerHotbarSlots(inv)

        addBlockEntityInputSlots()
        addBlockEntityOutputSlots()

        addDataSlots(data)
    }


    private fun syncBlockEntityToContainers() {
        // 同步输入槽
        for (i in 0 until BagOpenerBlockEntity.INPUT_SLOTS_COUNT) {
            val stack = blockEntity.getItem(i)
            inputContainer.setItem(i, stack)
        }

        // 同步输出槽
        for (i in 0 until BagOpenerBlockEntity.OUTPUT_SLOTS_COUNT) {
            val stack = blockEntity.getItem(i + BagOpenerBlockEntity.INPUT_SLOTS_COUNT)
            outputContainer.setItem(i, stack)
        }
        broadcastChanges()
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack =
        quickMoveStack(this, player, index, BE_INVENTORY_SLOT_COUNT, ::moveItemStackTo).also {
            broadcastChanges()
        }

    override fun stillValid(player: Player): Boolean =
        stillValid(ContainerLevelAccess.create(level, blockEntity.blockPos), player, ModBlocks.BAG_OPENER)

    private fun addPlayerInventorySlots(inv: Inventory) {
        addPlayerInventorySlots(inv, 8, 102, ::addSlot)
    }

    private fun addPlayerHotbarSlots(inv: Inventory) {
        addPlayerHotbarSlots(inv, 8, 159, ::addSlot)
    }

    private fun addBlockEntityInputSlots() {
        val left = 8
        val top = 19
        for (i in 0 ..< 9) {
            addSlot(object : Slot(inputContainer, i, left + i * 18, top) {
                override fun mayPlace(stack: ItemStack): Boolean {
                    return inputContainer.canPlaceItem(i, stack)
                }

                override fun setChanged() {
                    super.setChanged()
                    // 将容器中的物品同步回方块实体
                    blockEntity.setItem(i, inputContainer.getItem(i))
                }
            })
        }
    }

    private fun addBlockEntityOutputSlots() {
        val left = 8
        val top = 43
        for (i in 0 ..< 3) {
            for (j in 0..< 9) {
                val containerIndex = j + i * 9
                val blockEntityIndex = containerIndex + BagOpenerBlockEntity.INPUT_SLOTS_COUNT
                addSlot(object : Slot(outputContainer, containerIndex, left + j * 18, top + i * 18) {
                    override fun mayPlace(stack: ItemStack): Boolean = false // 输出槽不允许放入物品

                    override fun setChanged() {
                        super.setChanged()
                        // 将容器中的物品同步回方块实体
                        blockEntity.setItem(blockEntityIndex, outputContainer.getItem(containerIndex))
                    }
                })
            }
        }
    }

    val isCrafting: Boolean
        get() = data.get(BagOpenerBlockEntity.ContainerDataType.PROGRESS.ordinal) > 0

    val scaledProgress: Int
        get() {
            val progress = data.get(BagOpenerBlockEntity.ContainerDataType.PROGRESS.ordinal)
            val maxProgress = data.get(BagOpenerBlockEntity.ContainerDataType.MAX_PROGRESS.ordinal)
            val progressBarPixelSize = 162

            return if (progress > 0 && maxProgress > 0) progress * progressBarPixelSize / maxProgress else 0
        }
}