package top.srcres258.renewal.lootbags.screen.custom

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import top.srcres258.renewal.lootbags.LootBags
import top.srcres258.renewal.lootbags.util.setShaderTexture

private val BAG_STORAGE_GUI_TEXTURE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID,
    "textures/gui/bag_storage_gui.png")

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
        if (slot is LootBagSlot && slot.isOutputSlot &&
            menu is LootBagContainerMenu && menu.targetBagAmount == 0) {
            val stack = ItemStack(menu.targetBagType.asItem())
            val i = slot.x
            val j = slot.y
            if (slot.isFake()) {
                guiGraphics.renderFakeItem(stack, i, j)
            } else {
                guiGraphics.renderItem(stack, i, j)
            }
            guiGraphics.renderItemDecorations(this.font, stack, i, j, null)
            setShaderTexture(0, BAG_STORAGE_GUI_TEXTURE)
            renderUnavailableIcon(guiGraphics, i, j)
        } else {
            super.renderSlot(guiGraphics, slot)
        }
    }

}

private fun renderUnavailableIcon(guiGraphics: GuiGraphics, originX: Int, originY: Int) {
    guiGraphics.blit(BAG_STORAGE_GUI_TEXTURE, originX, originY, 176, 0, 16, 16)
}
