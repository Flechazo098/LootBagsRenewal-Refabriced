package com.flechazo.screen.custom

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import com.flechazo.LootBags
import com.flechazo.util.setShaderTexture

private val BAG_STORAGE_GUI_TEXTURE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "textures/gui/bag_storage_gui.png")

abstract class LootBagContainerScreen<T : LootBagContainerMenu>(
    menu: T,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<T>(menu, playerInventory, title) {
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun renderSlot(guiGraphics: GuiGraphics, slot: Slot) {
        // Override this method to render fake the loot bag item if no loot bag from the output slot is available.

        if (slot is LootBagSlot && slot.isOutputSlot &&
            menu is LootBagContainerMenu && menu.targetBagAmount == 0) {
            // Render a fake loot bag item.
            val stack = ItemStack(menu.targetBagType.asItem())

            guiGraphics.pose().pushPose()
            guiGraphics.pose().translate(0.0F, 0.0F, 100.0F)

            // 渲染物品
            val i = slot.x
            val j = slot.y
            val k = slot.x + slot.y * imageWidth
            guiGraphics.renderFakeItem(stack, i, j, k)

            guiGraphics.pose().popPose()

            // 渲染不可用图标
            if (menu.targetBagAmount == 0) {
                setShaderTexture(0, BAG_STORAGE_GUI_TEXTURE)
                renderUnavailableIcon(guiGraphics, slot.x, slot.y)
            }
        } else {
            super.renderSlot(guiGraphics, slot)
        }
    }
}

private fun renderUnavailableIcon(guiGraphics: GuiGraphics, originX: Int, originY: Int) {
    guiGraphics.blit(BAG_STORAGE_GUI_TEXTURE, originX, originY, 176, 0, 16, 16)
}