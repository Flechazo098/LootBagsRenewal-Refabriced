package com.flechazo.util

import net.minecraft.world.item.ItemStack

/**
 * 只允许输出的物品处理器包装类
 */
class OutputOnlyItemHandler(
    private val itemHandler: SimpleItemStackHandler,
    private val outputSlot: Int
) : SimpleItemStackHandler(1) {

    override fun getStackInSlot(slot: Int): ItemStack = itemHandler.getStackInSlot(outputSlot)

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        // 不允许直接设置物品
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = stack.copy()

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
        itemHandler.extractItem(outputSlot, amount, simulate)

    override fun getStackLimit(slot: Int, stack: ItemStack): Int = itemHandler.getStackLimit(outputSlot, stack)

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean =
        itemHandler.isItemValid(outputSlot, stack)
}