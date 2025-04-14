package com.flechazo.block.entity.custom

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import com.flechazo.block.entity.ModBlockEntities
import com.flechazo.component.ItemHandlerComponent
import com.flechazo.item.ModItems
import com.flechazo.item.custom.LootBagItem
import com.flechazo.screen.custom.BagOpenerMenu
import com.flechazo.util.asLootBagType
import com.flechazo.util.setChangedAndUpdateBlock
import com.flechazo.util.SimpleItemStackHandler
import net.minecraft.core.Direction

class BagOpenerBlockEntity(
    pos: BlockPos,
    blockState: BlockState
) : BlockEntity(ModBlockEntities.BAG_OPENER, pos, blockState), MenuProvider, ItemHandlerComponent {
    enum class ContainerDataType {
        PROGRESS,
        MAX_PROGRESS
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


    private inner class BagOpenerItemHandler : SimpleItemStackHandler(INPUT_SLOTS_COUNT) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean = true

        override fun onContentsChanged(slot: Int) {
            setChangedAndUpdateBlock()
        }
    }

    companion object {
        const val INPUT_SLOTS_COUNT = 9
        const val OUTPUT_SLOTS_COUNT = 9 * 3
        const val SLOTS_COUNT = INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT
    }

    override val inputItemHandler: SimpleItemStackHandler = BagOpenerItemHandler()
    override val outputItemHandler: SimpleItemStackHandler = object : SimpleItemStackHandler(OUTPUT_SLOTS_COUNT) {
        // Refuse to insert any items because the slots are used for output purpose only.
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean = false

        // Refuse to insert any items because the slots are used for output purpose only.
        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = stack.copy()

        override fun onContentsChanged(slot: Int) {
            setChangedAndUpdateBlock()
        }
    }

    private var progress = 0
    private var maxProgress = 60
    val data = object : ContainerData {
        override fun get(index: Int): Int = when (index) {
            ContainerDataType.PROGRESS.ordinal -> progress
            ContainerDataType.MAX_PROGRESS.ordinal -> maxProgress
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                ContainerDataType.PROGRESS.ordinal -> progress = value
                ContainerDataType.MAX_PROGRESS.ordinal -> maxProgress = value
            }
        }

        override fun getCount(): Int = ContainerDataType.entries.size
    }

    override fun getDisplayName(): Component = Component.translatable("block.lootbags.bag_opener")

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        BagOpenerMenu(containerId, playerInventory, player.level(), this, data)

    fun drops() {
        level?.let { level ->
            for (handler in listOf(inputItemHandler, outputItemHandler)) {
                val inventory = SimpleContainer(handler.slots)
                for (i in 0 until handler.slots) {
                    inventory.setItem(i, handler.getStackInSlot(i))
                }
                Containers.dropContents(level, worldPosition, inventory)
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        // 使用传入的registries作为Provider
        tag.put("input_inventory", inputItemHandler.serializeNBT(registries))
        tag.put("output_inventory", outputItemHandler.serializeNBT(registries))
        tag.putInt("progress", progress)
        tag.putInt("max_progress", maxProgress)

        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        // 使用传入的registries作为Provider
        inputItemHandler.deserializeNBT(tag.getCompound("input_inventory"), registries)
        outputItemHandler.deserializeNBT(tag.getCompound("output_inventory"), registries)
        progress = tag.getInt("progress")
        maxProgress = tag.getInt("max_progress")
    }
    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        val inputSlot = availableInputSlotForCrafting
        if (inputSlot == null) {
            decreaseCraftingProgress()
        } else {
            increaseCraftingProgress()
            setChanged()

            if (hasCraftingFinished) {
                craftItem(inputSlot)
                resetProgress()
            }
        }
    }

    private val availableInputSlotForCrafting: Int?
        get() {
            // If all the output slots are occupied, the crafting is not available.
            var allOccupied = true
            for (i in 0 until outputItemHandler.slots) {
                allOccupied = allOccupied && !outputItemHandler.getStackInSlot(i).isEmpty
            }
            if (allOccupied) {
                return null
            }

            // Then check whether there is any input item in the input slots.
            for (i in 0 until inputItemHandler.slots) {
                if (!inputItemHandler.getStackInSlot(i).isEmpty) {
                    return i
                }
            }

            // If no input item is found, the crafting is not available.
            return null
        }

    private fun increaseCraftingProgress() {
        if (progress < maxProgress) {
            progress++
        }
    }

    private fun decreaseCraftingProgress() {
        if (progress > 0) {
            progress--
        }
    }

    private val hasCraftingFinished: Boolean
        get() = progress >= maxProgress

    private fun craftItem(inputSlot: Int) {
        // Ensure the input slot index is valid.
        if (inputSlot < 0 || inputSlot >= inputItemHandler.slots) {
            return
        }

        // Get the input item and check whether it is valid for crafting.
        val inputItemStack = inputItemHandler.getStackInSlot(inputSlot).copy()
        if (inputItemStack.isEmpty) {
            return
        }
        var isValid = false
        for (bag in ModItems.LOOT_BAGS) {
            if (inputItemStack.item == bag) {
                isValid = true
            }
        }
        if (!isValid) {
            return
        }
        val bagItem = inputItemStack.item as? LootBagItem ?: return
        val level = this.level ?: return
        val serverLevel = level as? ServerLevel ?: return

        // Consume one item from the input slot and put the output loot item into the output slots.
        val extracted = inputItemHandler.extractItem(inputSlot, 1, false)
        if (extracted.isEmpty) {
            return
        }

        // 简化参数，只使用基本的 ServerLevel
        val loots = bagItem.asLootBagType().lootGenerator.generateLoot(serverLevel)

        for (loot in loots) {
            if (!putIntoOutputSlots(loot)) {
                break
            }
        }
        setChangedAndUpdateBlock()
    }


    private fun putIntoOutputSlots(stack: ItemStack): Boolean {
        val remainingStack = stack.copy()
        for (i in 0 until outputItemHandler.slots) {
            val currentStack = outputItemHandler.getStackInSlot(i)
            if (currentStack.isEmpty) {
                outputItemHandler.setStackInSlot(i, remainingStack.copy())
                remainingStack.count = 0
                setChangedAndUpdateBlock()
                break
            }
            if (ItemStack.isSameItemSameComponents(remainingStack, currentStack)) {
                val maxCapacity = currentStack.maxStackSize
                val amountToPutInto = maxCapacity - currentStack.count
                val remainingAfterPut = remainingStack.count - amountToPutInto
                if (remainingAfterPut < 0) {
                    // Too few items to be put into the current stack, put the available count and set the
                    // remaining stack to empty.
                    outputItemHandler.setStackInSlot(i, currentStack.copy().also { it.count += remainingStack.count })
                    remainingStack.count = 0
                } else {
                    // Remaining items are enough to be put into the current stack, put the amount to let
                    // the current stack become full.
                    outputItemHandler.setStackInSlot(i, currentStack.copy().also { it.count = maxCapacity })
                    remainingStack.count = remainingAfterPut
                }
            }
            if (remainingStack.isEmpty) {
                // No items left to be put; quit the loop.
                break
            }
        }

        return remainingStack.isEmpty
    }

    private fun resetProgress() {
        progress = 0
        maxProgress = 60 // Reset max progress as well in order to get rid of unexpected situations.
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        // 使用简化版本的保存方法，不需要完整的注册表查找
        val tag = CompoundTag()
        tag.put("input_inventory", inputItemHandler.serializeNBT())
        tag.put("output_inventory", outputItemHandler.serializeNBT())
        tag.putInt("progress", progress)
        tag.putInt("max_progress", maxProgress)
        return tag
    }

    private fun setChangedAndUpdateBlock() {
        setChanged()
        level?.sendBlockUpdated(blockPos, blockState, blockState, 3)
    }
    // Add these methods to support the menu
    fun getItem(slot: Int): ItemStack {
        return if (slot < INPUT_SLOTS_COUNT) {
            inputItemHandler.getStackInSlot(slot)
        } else if (slot < SLOTS_COUNT) {
            outputItemHandler.getStackInSlot(slot - INPUT_SLOTS_COUNT)
        } else {
            ItemStack.EMPTY
        }
    }

    fun setItem(slot: Int, stack: ItemStack) {
        if (slot < INPUT_SLOTS_COUNT) {
            inputItemHandler.setStackInSlot(slot, stack)
        } else if (slot < SLOTS_COUNT) {
            outputItemHandler.setStackInSlot(slot - INPUT_SLOTS_COUNT, stack)
        }
        setChanged()
    }


}