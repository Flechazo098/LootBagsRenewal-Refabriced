package com.flechazo.block.entity.custom

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.util.Mth
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import com.flechazo.block.entity.ModBlockEntities
import com.flechazo.component.ItemHandlerComponent
import com.flechazo.component.ModDataComponents
import com.flechazo.screen.custom.LootRecyclerMenu
import com.flechazo.util.*
import net.minecraft.core.Direction
import kotlin.math.min

class LootRecyclerBlockEntity(
    pos: BlockPos,
    blockState: BlockState
) : BlockEntity(ModBlockEntities.LOOT_RECYCLER, pos, blockState), MenuProvider, ItemHandlerComponent {
    enum class ContainerDataType {
        STORED_BAG_AMOUNT
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


    private inner class LootRecyclerItemHandler : LootBagItemHandler(SLOTS_COUNT) {
        override fun isInputSlot(slot: Int): Boolean = slot == INPUT_SLOT

        // 我们想要回收所有物品，所以只检查槽位是否为输入槽
        // (即不需要检查物品是否为战利品袋)
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean = isInputSlot(slot)

        // 我们想要回收所有物品，所以对输入槽使用最大堆叠数
        override fun getStackLimit(slot: Int, stack: ItemStack): Int = if (isInputSlot(slot)) {
            Item.ABSOLUTE_MAX_STACK_SIZE
        } else {
            super.getStackLimit(slot, stack)
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (simulate) { // 注意要进行模拟检查!!!
                return super.extractItem(slot, amount, true)
            } else {
                val resultStack = super.extractItem(slot, amount, false)
                if (slot == OUTPUT_SLOT) {
                    storedBagAmount -= resultStack.count * targetBagType.amountFactorEquivalentTo(LootBagType.COMMON).toInt()
                }
                updateOutputSlot()

                return resultStack
            }
        }

        override fun onContentsChanged(slot: Int) {
            if (slot != OUTPUT_SLOT) {
                // 当非输出槽的槽位内容变化时，更新输出槽
                updateOutputSlot()
            }
        }

        fun updateOutputSlot(setChanged: Boolean = true) {
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

    val itemHandler: SimpleItemStackHandler = LootRecyclerItemHandler()
    override val inputItemHandler = InputOnlyItemHandler(itemHandler, INPUT_SLOT)
    override val outputItemHandler = OutputOnlyItemHandler(itemHandler, OUTPUT_SLOT)

    var storedBagAmount: Int = 0
    /**
     * 目前 `targetBagType` 字段是常量（尚无更改选项）
     */
    private val targetBagType: LootBagType = LootBagType.COMMON
    private val targetBagAmount: Int
        get() = (storedBagAmount.toFloat() * LootBagType.COMMON.amountFactorEquivalentTo(targetBagType)).toInt()
    private val data = object : ContainerData {
        override fun get(index: Int): Int = when (index) {
            ContainerDataType.STORED_BAG_AMOUNT.ordinal -> storedBagAmount
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                ContainerDataType.STORED_BAG_AMOUNT.ordinal -> storedBagAmount = value
            }
        }

        override fun getCount(): Int = ContainerDataType.entries.size
    }

    private var accumulation = 0.0

    override fun getDisplayName(): Component = Component.translatable("block.lootbags.loot_recycler")

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        LootRecyclerMenu(containerId, playerInventory, player.level(), this, data)

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.put("inventory", itemHandler.serializeNBT())
        tag.putInt("stored_bag_amount", storedBagAmount)

        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        itemHandler.deserializeNBT(tag.getCompound("inventory"))
        storedBagAmount = tag.getInt("stored_bag_amount")
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        val inputStack = itemHandler.getStackInSlot(INPUT_SLOT).copy()
        if (!inputStack.isEmpty) {
            // 如果检测到输入，消耗（回收）它并相应增加 storedBagAmount
            itemHandler.extractItem(INPUT_SLOT, inputStack.count, false)

            // 随机增加 `accumulation`
            val random = level.random
            val rand = Mth.nextDouble(random, 0.0, 1.0)
            if (rand < 0.5) {
                val rand1 = Mth.nextDouble(random, 0.0, 0.1)
                accumulation += rand1 * inputStack.count
            }

            // 如果 `accumulation` 达到 1.0，我们可以相应增加 `storedBagAmount`
            if (accumulation >= 1.0) {
                val increment = accumulation.toInt()
                storedBagAmount += increment
                accumulation -= increment.toDouble()
            }
        }

        (itemHandler as LootRecyclerItemHandler).updateOutputSlot()
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        saveWithoutMetadata(registries)

    private fun setChangedAndUpdateBlock() {
        setChangedAndUpdateBlock(level)
    }

    override fun collectImplicitComponents(components: DataComponentMap.Builder) {
        // 将存储的袋子数量作为数据组件添加到 DataComponentMap 构建器中
        val bagStorage = BagStorageRecord(storedBagAmount, targetBagType.ordinal)
        components.set(ModDataComponents.BAG_STORAGE, bagStorage)
    }

    override fun applyImplicitComponents(componentInput: DataComponentInput) {
        // 如果存在，应用存储的袋子数据组件
        componentInput.get(ModDataComponents.BAG_STORAGE)?.let { storedBags ->
            storedBagAmount = storedBags.storedBagAmount
        }
    }
}