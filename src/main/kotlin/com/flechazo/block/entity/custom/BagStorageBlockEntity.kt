package com.flechazo.block.entity.custom

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import com.flechazo.block.entity.ModBlockEntities
import com.flechazo.component.ItemHandlerComponent
import com.flechazo.component.ModDataComponents
import com.flechazo.item.custom.LootBagItem
import com.flechazo.screen.custom.BagStorageMenu
import com.flechazo.util.*
import net.minecraft.core.Direction
import kotlin.math.min

class BagStorageBlockEntity(
    pos: BlockPos,
    blockState: BlockState
) : BlockEntity(ModBlockEntities.BAG_STORAGE, pos, blockState), MenuProvider, ItemHandlerComponent {
    enum class ContainerDataType {
        STORED_BAG_AMOUNT,
        TARGET_BAG_TYPE
    }
    // 实现 ItemHandlerComponent 接口
    override fun getItemHandler(side: Direction?): SimpleItemStackHandler {
        return when (side) {
            Direction.DOWN -> outputItemHandler
            else -> inputItemHandler
        }
    }
    // 实现 Component 接口的方法
    override fun readFromNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        // 从 NBT 读取物品处理器数据
        inputItemHandler.deserializeNBT(tag.getCompound("input_inventory"))
        outputItemHandler.deserializeNBT(tag.getCompound("output_inventory"))
    }

    override fun writeToNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        // 将物品处理器数据写入 NBT
        tag.put("input_inventory", inputItemHandler.serializeNBT())
        tag.put("output_inventory", outputItemHandler.serializeNBT())
    }

    private inner class BagStorageItemHandler : LootBagItemHandler(SLOTS_COUNT) {
        override fun isInputSlot(slot: Int): Boolean = slot == INPUT_SLOT

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (slot == OUTPUT_SLOT) {
                // 首先检查是否有足够的存储量
                val maxExtractable = min(amount, targetBagAmount)
                if (maxExtractable <= 0) {
                    return ItemStack.EMPTY
                }

                // 获取当前输出槽物品
                val currentStack = getStackInSlot(slot)
                if (currentStack.isEmpty) {
                    return ItemStack.EMPTY
                }

                // 计算实际可以提取的数量
                val actualExtract = min(maxExtractable, currentStack.count)

                // 如果是模拟，只返回可以提取的物品
                if (simulate) {
                    val result = currentStack.copy()
                    result.count = actualExtract
                    return result
                }

                // 实际提取物品
                val result = currentStack.copy()
                result.count = actualExtract

                // 减少存储的袋子数量
                val commonBagsEquivalent = actualExtract * targetBagType.amountFactorEquivalentTo(LootBagType.COMMON).toInt()
                storedBagAmount -= commonBagsEquivalent

                // 确保不会变成负数
                if (storedBagAmount < 0) {
                    storedBagAmount = 0
                }

                // 更新输出槽
                if (actualExtract >= currentStack.count) {
                    setStackInSlot(slot, ItemStack.EMPTY)
                } else {
                    val newStack = currentStack.copy()
                    newStack.shrink(actualExtract)
                    setStackInSlot(slot, newStack)
                }

                // 确保数据同步
                setChangedAndUpdateBlock()

                return result
            } else {
                // 对于其他槽位，使用默认行为
                return super.extractItem(slot, amount, simulate)
            }
        }

        override fun onContentsChanged(slot: Int) {
            if (slot != OUTPUT_SLOT) {
                // When slots that aren't the output slot are changed, update the output slot.
                updateOutputSlot()
            }
        }

        fun updateOutputSlot(setChanged: Boolean = true) {
            // 使用getDefaultMaxStackSize()方法
            setStackInSlot(OUTPUT_SLOT, ItemStack(targetBagType.asItem(), min(targetBagAmount, targetBagType.asItem().getDefaultMaxStackSize())))
            if (setChanged) {
                setChangedAndUpdateBlock()
            }
        }
    }

    companion object {
        const val INPUT_SLOT = 0
        const val OUTPUT_SLOT = 1
        const val SLOTS_COUNT = OUTPUT_SLOT + 1
    }

    val itemHandler: SimpleItemStackHandler = BagStorageItemHandler()
    override val inputItemHandler = InputOnlyItemHandler(itemHandler, INPUT_SLOT)
    override val outputItemHandler = OutputOnlyItemHandler(itemHandler, OUTPUT_SLOT)

    var storedBagAmount: Int = 0
    private var targetBagType: LootBagType = LootBagType.COMMON
    private val targetBagAmount: Int
        get() = (storedBagAmount.toFloat() * LootBagType.COMMON.amountFactorEquivalentTo(targetBagType)).toInt()
    private val data = object : ContainerData {
        override fun get(index: Int): Int = when (index) {
            ContainerDataType.STORED_BAG_AMOUNT.ordinal -> storedBagAmount
            ContainerDataType.TARGET_BAG_TYPE.ordinal -> targetBagType.ordinal
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                ContainerDataType.STORED_BAG_AMOUNT.ordinal -> storedBagAmount = value
                ContainerDataType.TARGET_BAG_TYPE.ordinal -> {
                    targetBagType = LootBagType.entries[value]
                }
            }
        }

        override fun getCount(): Int = ContainerDataType.entries.size
    }

    override fun getDisplayName(): Component = Component.translatable("block.lootbags.bag_storage")

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        BagStorageMenu(containerId, playerInventory, player.level(), this, data)

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.put("inventory", itemHandler.serializeNBT())
        tag.putInt("stored_bag_amount", storedBagAmount)
        tag.putInt("target_bag_type", targetBagType.ordinal)

        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        itemHandler.deserializeNBT(tag.getCompound("inventory"))
        storedBagAmount = tag.getInt("stored_bag_amount")
        val targetBagTypeOrdinal = tag.getInt("target_bag_type")
        if (targetBagTypeOrdinal in LootBagType.entries.indices) { // Make sure the ordinal is valid.
            targetBagType = LootBagType.entries[targetBagTypeOrdinal]
        }
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        val inputStack = itemHandler.getStackInSlot(INPUT_SLOT).copy()
        val inputItem = inputStack.item
        if (!inputStack.isEmpty && inputItem is LootBagItem) {
            // If input is detected, consume it and increase storedBagAmount accordingly.
            itemHandler.extractItem(INPUT_SLOT, inputStack.count, false)
            storedBagAmount += (inputStack.count.toFloat() * inputItem.asLootBagType()
                .amountFactorEquivalentTo(LootBagType.COMMON)).toInt()

            // 确保数据同步
            setChangedAndUpdateBlock()
        }

        // Update the output slot every tick to keep everything valid.
        (itemHandler as BagStorageItemHandler).updateOutputSlot()
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        saveWithoutMetadata(registries)

    private fun setChangedAndUpdateBlock() {
        setChangedAndUpdateBlock(level)
    }

    override fun collectImplicitComponents(components: DataComponentMap.Builder) {
        // Adds the stored bags amount as a data component into the DataComponentMap builder.
        val bagStorage = BagStorageRecord(storedBagAmount, targetBagType.ordinal)
        components.set(ModDataComponents.BAG_STORAGE, bagStorage)
    }

    override fun applyImplicitComponents(componentInput: DataComponentInput) {
        // Apply the stored bags data component if exists.
        componentInput.get(ModDataComponents.BAG_STORAGE)?.let { storedBags ->
            storedBagAmount = storedBags.storedBagAmount
            val ordinal = storedBags.targetBagTypeOrdinal
            if (ordinal in LootBagType.entries.indices) {
                targetBagType = LootBagType.entries[ordinal]
            }
        }
    }
}