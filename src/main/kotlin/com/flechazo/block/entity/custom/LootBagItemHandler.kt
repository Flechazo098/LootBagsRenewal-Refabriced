package com.flechazo.block.entity.custom

import net.minecraft.world.item.ItemStack
import com.flechazo.item.ModItems
import com.flechazo.util.LootBagType
import com.flechazo.util.SimpleItemStackHandler

abstract class LootBagItemHandler(size: Int) : SimpleItemStackHandler(size) {
    abstract fun isInputSlot(slot: Int): Boolean

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        if (isInputSlot(slot)) {
            for (bagItem in ModItems.LOOT_BAGS) {
                if (stack.item == bagItem) {
                    return true
                }
            }
            // 拒绝将非战利品袋物品插入输入槽
            return false
        } else {
            // 拒绝将物品插入输出槽和其他槽位
            return false
        }
    }

    override fun getStackLimit(slot: Int, stack: ItemStack): Int = LootBagType.COMMON.asItem().defaultMaxStackSize
}