package top.srcres258.renewal.lootbags.block.entity.custom

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import top.srcres258.renewal.lootbags.item.ModItems
import top.srcres258.renewal.lootbags.util.LootBagType

abstract class LootBagItemHandler(size: Int) : SimpleContainer(size) {
    abstract fun isInputSlot(slot: Int): Boolean

    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean {
        if (isInputSlot(slot)) {
            for (bagItem in ModItems.LOOT_BAGS) {
                if (stack.item == bagItem) {
                    return true
                }
            }
            return false
        } else {
            return false
        }
    }

    fun getStackInSlot(slot: Int): ItemStack = getItem(slot)

    fun setStackInSlot(slot: Int, stack: ItemStack) {
        setItem(slot, stack)
    }

    open fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        val current = getItem(slot)
        if (current.isEmpty) return ItemStack.EMPTY
        val toExtract = amount.coerceAtMost(current.count)
        val result = current.copy().also { it.count = toExtract }
        if (!simulate) {
            if (current.count <= toExtract) {
                setItem(slot, ItemStack.EMPTY)
            } else {
                current.shrink(toExtract)
                setItem(slot, current)
            }
        }
        return result
    }

    val slots: Int get() = containerSize

    open fun onContentsChanged(slot: Int) {}

    override fun setItem(slot: Int, stack: ItemStack) {
        super.setItem(slot, stack)
        onContentsChanged(slot)
    }

    override fun getMaxStackSize(): Int = LootBagType.COMMON.asItem().defaultMaxStackSize
}
