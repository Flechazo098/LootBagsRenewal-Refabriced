package top.srcres258.renewal.lootbags.block.entity.custom

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.core.NonNullList
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Containers
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.server.level.ServerPlayer
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
import top.srcres258.renewal.lootbags.block.entity.ModBlockEntities
import top.srcres258.renewal.lootbags.item.ModItems
import top.srcres258.renewal.lootbags.item.custom.LootBagItem
import top.srcres258.renewal.lootbags.screen.custom.BagOpenerMenu
import top.srcres258.renewal.lootbags.util.asLootBagType
import top.srcres258.renewal.lootbags.util.setChangedAndUpdateBlock

class BagOpenerBlockEntity(
    pos: BlockPos,
    blockState: BlockState
) : BlockEntity(ModBlockEntities.BAG_OPENER, pos, blockState), ExtendedScreenHandlerFactory<BlockPos> {
    enum class ContainerDataType {
        PROGRESS,
        MAX_PROGRESS
    }

    private inner class BagOpenerItemHandler : LootBagItemHandler(INPUT_SLOTS_COUNT) {
        override fun isInputSlot(slot: Int): Boolean = true

        override fun onContentsChanged(slot: Int) {
            setChangedAndUpdateBlock()
        }
    }

    companion object {
        const val INPUT_SLOTS_COUNT = 9
        const val OUTPUT_SLOTS_COUNT = 9 * 3
        const val SLOTS_COUNT = INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT
    }

    val inputInventory: LootBagItemHandler = BagOpenerItemHandler()
    val outputInventory: SimpleContainer = object : SimpleContainer(OUTPUT_SLOTS_COUNT) {
        override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = false
        override fun setItem(slot: Int, stack: ItemStack) {
            super.setItem(slot, stack)
            setChangedAndUpdateBlock()
        }
    }

    private var progress = 0
    private var maxProgress = 60
    private val data = object : ContainerData {
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

    override fun getScreenOpeningData(player: ServerPlayer): BlockPos = blockPos

    fun drops() {
        level?.let { level ->
            for (handler in listOf(inputInventory, outputInventory)) {
                val inventory = SimpleContainer(handler.containerSize)
                for (i in 0 until handler.containerSize) {
                    inventory.setItem(i, handler.getItem(i))
                }
                Containers.dropContents(level, worldPosition, inventory)
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        run {
            val sub = CompoundTag()
            val items = NonNullList.withSize(inputInventory.containerSize, ItemStack.EMPTY)
            for (i in 0 until inputInventory.containerSize) items[i] = inputInventory.getItem(i)
            ContainerHelper.saveAllItems(sub, items, registries)
            tag.put("input_inventory", sub)
        }
        run {
            val sub = CompoundTag()
            val items = NonNullList.withSize(outputInventory.containerSize, ItemStack.EMPTY)
            for (i in 0 until outputInventory.containerSize) items[i] = outputInventory.getItem(i)
            ContainerHelper.saveAllItems(sub, items, registries)
            tag.put("output_inventory", sub)
        }
        tag.putInt("progress", progress)
        tag.putInt("max_progress", maxProgress)

        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        run {
            val sub = tag.getCompound("input_inventory")
            val items = NonNullList.withSize(inputInventory.containerSize, ItemStack.EMPTY)
            ContainerHelper.loadAllItems(sub, items, registries)
            for (i in 0 until inputInventory.containerSize) inputInventory.setItem(i, items[i])
        }
        run {
            val sub = tag.getCompound("output_inventory")
            val items = NonNullList.withSize(outputInventory.containerSize, ItemStack.EMPTY)
            ContainerHelper.loadAllItems(sub, items, registries)
            for (i in 0 until outputInventory.containerSize) outputInventory.setItem(i, items[i])
        }
        progress = tag.getInt("progress")
        maxProgress = tag.getInt("max_progress")
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        val inputSlot = availableInputSlotForCrafting
        if (inputSlot == null) {
            decreaseCraftingProgress()
        } else {
            increaseCraftingProgress()
            setChanged(level, pos, state)

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
            for (i in 0 until outputInventory.containerSize) {
                allOccupied = allOccupied && !outputInventory.getItem(i).isEmpty
            }
            if (allOccupied) {
                return null
            }

            // Then check whether there is any input item in the input slots.
            for (i in 0 until inputInventory.containerSize) {
                if (!inputInventory.getStackInSlot(i).isEmpty) {
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
        if (inputSlot < 0 || inputSlot >= inputInventory.containerSize) {
            return
        }

        // Get the input item and check whether it is valid for crafting.
        val inputItemStack = inputInventory.getStackInSlot(inputSlot).copy()
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
        val extracted = inputInventory.extractItem(inputSlot, 1, false)
        if (extracted.isEmpty) {
            return
        }
        val pos = Vec3(blockPos.x.toDouble() + 0.5, blockPos.y.toDouble() + 0.5, blockPos.z.toDouble() + 0.5)
        val loots = bagItem.asLootBagType().lootGenerator.generateLoot(
            serverLevel,
            LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.BLOCK_STATE, blockState)
                .withParameter(LootContextParams.BLOCK_ENTITY, this)
                .withParameter(LootContextParams.ORIGIN, pos)
        )
        for (loot in loots) {
            if (!putIntoOutputSlots(loot)) {
                break
            }
        }
    }

    private fun putIntoOutputSlots(stack: ItemStack): Boolean {
        val remainingStack = stack.copy()
        for (i in 0 until outputInventory.containerSize) {
            val currentStack = outputInventory.getItem(i)
            if (currentStack.isEmpty) {
                outputInventory.setItem(i, remainingStack.copy())
                remainingStack.count = 0
                break
            }
            if (ItemStack.isSameItemSameComponents(remainingStack, currentStack)) {
                val maxCapacity = currentStack.maxStackSize
                val amountToPutInto = maxCapacity - currentStack.count
                val remainingAfterPut = remainingStack.count - amountToPutInto
                if (remainingAfterPut < 0) {
                    outputInventory.setItem(i, currentStack.copy().also { it.count += remainingStack.count })
                    remainingStack.count = 0
                } else {
                    outputInventory.setItem(i, currentStack.copy().also { it.count = maxCapacity })
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

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        saveWithoutMetadata(registries)

    private fun setChangedAndUpdateBlock() {
        setChangedAndUpdateBlock(level)
    }
}
