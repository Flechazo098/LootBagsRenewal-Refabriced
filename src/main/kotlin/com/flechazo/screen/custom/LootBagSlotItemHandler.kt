package com.flechazo.screen.custom

import net.minecraft.world.item.ItemStack
import com.flechazo.util.SimpleItemStackHandler

/**
 * 为了向后兼容而保留的类，实际使用 LootBagSlot 类
 * @see LootBagSlot
 */
@Deprecated("使用 LootBagSlot 替代", ReplaceWith("LootBagSlot"))
open class LootBagSlotItemHandler(
    private val itemHandler: SimpleItemStackHandler,
    private val index: Int,
    xPosition: Int,
    yPosition: Int,
    val isOutputSlot: Boolean
) : SimpleItemHandlerSlot(itemHandler, index, xPosition, yPosition) {

    fun extractItem(amount: Int, simulate: Boolean): ItemStack =
        itemHandler.extractItem(index, amount, simulate)
}