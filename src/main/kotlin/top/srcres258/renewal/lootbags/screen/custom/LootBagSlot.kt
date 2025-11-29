package top.srcres258.renewal.lootbags.screen.custom

import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import top.srcres258.renewal.lootbags.block.entity.custom.LootBagItemHandler

open class LootBagSlot(
    container: Container,
    index: Int,
    xPosition: Int,
    yPosition: Int,
    val isOutputSlot: Boolean
) : Slot(container, index, xPosition, yPosition) {
    override fun isFake(): Boolean = false

    fun extractItem(amount: Int, simulate: Boolean): ItemStack {
        if (this.container is LootBagItemHandler) {
            return (this.container as LootBagItemHandler).extractItem(this.index, amount, simulate)
        }

        val stack = this.container.getItem(index)
        if (stack.isEmpty) {
            return ItemStack.EMPTY
        }

        val toExtract = amount.coerceAtMost(stack.count)
        val result = stack.copy()
        result.count = toExtract

        if (!simulate) {
            this.container.removeItem(index, toExtract)
        }

        return result
    }
}
