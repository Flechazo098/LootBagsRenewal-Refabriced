package com.flechazo.util

import net.minecraft.world.item.ItemStack

/**
 * 只允许输入的物品处理器包装类
 */
class InputOnlyItemHandler(
    private val itemHandler: SimpleItemStackHandler,
    private val inputSlot: Int
) : SimpleItemStackHandler(1) {

    override fun getStackInSlot(slot: Int): ItemStack = ItemStack.EMPTY

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        // 不允许直接设置物品
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack =
        itemHandler.insertItem(inputSlot, stack, simulate)

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack = ItemStack.EMPTY

    override fun getStackLimit(slot: Int, stack: ItemStack): Int = itemHandler.getStackLimit(inputSlot, stack)

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean =
        itemHandler.isItemValid(inputSlot, stack)
}