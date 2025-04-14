package com.flechazo.util

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import com.flechazo.LootBags
import com.flechazo.screen.custom.LootBagSlot

// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
// must assign a slot number to each of the slots used by the GUI.
// For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
// Each time we add a Slot to the container, it automatically increases the slotIndex, which means
//  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
//  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
//  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
private const val HOTBAR_SLOT_COUNT = 9
private const val PLAYER_INVENTORY_ROW_COUNT = 3
private const val PLAYER_INVENTORY_COLUMN_COUNT = 9
private const val PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_ROW_COUNT * PLAYER_INVENTORY_COLUMN_COUNT
private const val VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT
private const val VANILLA_FIRST_SLOT_INDEX = 0
private const val BE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT

/**
 * NOTE: This function **assumes** that the beginning indices of slots are supposed to be **vanilla** slots!
 * Ensure your slots' indices' order before calling this function.
 */
fun quickMoveStack(
    menu: AbstractContainerMenu,
    player: Player,
    index: Int,
    beInventorySlotCount: Int,
    moveItemStackTo: (ItemStack, Int, Int, Boolean) -> Boolean
): ItemStack {
    val sourceSlot = menu.slots[index]
    if (!sourceSlot.hasItem()) {
        return ItemStack.EMPTY
    }

    // 保存原始物品的副本
    val sourceStackOriginal = sourceSlot.item.copy()

    // 创建用于移动的物品副本
    val sourceStack = sourceStackOriginal.copy()

    if (sourceStack.isEmpty) {
        return ItemStack.EMPTY
    }

    // 创建用于返回的物品副本
    val resultStack = sourceStack.copy()

    // 记录原始数量
    val originalCount = sourceStack.count

    // Check if the slot clicked is one of the vanilla container slots
    if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
        // This is a vanilla container slot so merge the stack into the tile inventory
        if (!moveItemStackTo(sourceStack, BE_INVENTORY_FIRST_SLOT_INDEX,
                BE_INVENTORY_FIRST_SLOT_INDEX + beInventorySlotCount, false)) {
            return ItemStack.EMPTY
        }
    } else if (index < BE_INVENTORY_FIRST_SLOT_INDEX + beInventorySlotCount) {
        // This is a TE slot so merge the stack into the players inventory
        if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX,
                VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
            return ItemStack.EMPTY
        }
    } else {
        LootBags.LOGGER.warn("Invalid slot index: {}", index)
        return ItemStack.EMPTY
    }

    // 计算实际移动的物品数量
    val movedAmount = originalCount - sourceStack.count

    // 如果没有移动任何物品，直接返回空
    if (movedAmount <= 0) {
        return ItemStack.EMPTY
    }

    // 设置结果物品的数量为实际移动的数量
    resultStack.count = movedAmount

    // 更新源槽位
    if (sourceStack.isEmpty) {
        sourceSlot.set(ItemStack.EMPTY)
    } else {
        sourceSlot.set(sourceStack)
    }

    sourceSlot.setChanged()

    // 调用onTake方法，触发BlockEntity中的extractItem方法
    sourceSlot.onTake(player, resultStack)

    return resultStack
}

fun addPlayerInventorySlots(inv: Inventory, left: Int, top: Int, addSlot: (Slot) -> Slot) {
    for (i in 0 ..< 3) {
        for (j in 0 ..< 9) {
            addSlot(Slot(inv, j + i * 9 + 9, left + j * 18, top + i * 18))
        }
    }
}

fun addPlayerHotbarSlots(inv: Inventory, left: Int, top: Int, addSlot: (Slot) -> Slot) {
    for (i in 0 ..< 9) {
        addSlot(Slot(inv, i, left + i * 18, top))
    }
}