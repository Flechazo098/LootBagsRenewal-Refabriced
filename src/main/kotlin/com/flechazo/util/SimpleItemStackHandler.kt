package com.flechazo.util

import com.flechazo.LootBags.LOGGER
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import java.util.stream.Stream

abstract class SimpleItemStackHandler(val slots: Int) {
    private val stacks: MutableList<ItemStack> = MutableList(slots) { ItemStack.EMPTY }

//    // Create a provider for serialization/deserialization
//    private val emptyProvider = HolderLookup.Provider.create(Stream.empty())

    open fun getStackInSlot(slot: Int): ItemStack {
        validateSlotIndex(slot)
        return stacks[slot]
    }

    open fun setStackInSlot(slot: Int, stack: ItemStack) {
        validateSlotIndex(slot)
        stacks[slot] = stack
        onContentsChanged(slot)
    }

    open fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.isEmpty || !isItemValid(slot, stack)) {
            return stack
        }

        validateSlotIndex(slot)

        val existing = stacks[slot]

        var limit = getStackLimit(slot, stack)

        if (!existing.isEmpty) {
            if (!ItemStack.isSameItemSameComponents(stack, existing)) {
                return stack
            }

            limit -= existing.count
        }

        if (limit <= 0) {
            return stack
        }

        val reachedLimit = stack.count > limit

        if (!simulate) {
            if (existing.isEmpty) {
                stacks[slot] = if (reachedLimit) {
                    val copy = stack.copy()
                    copy.count = limit
                    copy
                } else {
                    stack.copy()
                }
            } else {
                existing.grow(if (reachedLimit) limit else stack.count)
            }
            onContentsChanged(slot)
        }

        return if (reachedLimit) {
            val copy = stack.copy()
            copy.count = stack.count - limit
            copy
        } else {
            ItemStack.EMPTY
        }
    }

    open fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (amount == 0) {
            return ItemStack.EMPTY
        }

        validateSlotIndex(slot)

        val existing = stacks[slot]

        if (existing.isEmpty) {
            return ItemStack.EMPTY
        }

        val toExtract = amount.coerceAtMost(existing.count)

        if (existing.count <= toExtract) {
            if (!simulate) {
                stacks[slot] = ItemStack.EMPTY
                onContentsChanged(slot)
            }
            return existing
        } else {
            if (!simulate) {
                // 使用 copy 和 setCount 替代 copyWithCount
                val newStack = existing.copy()
                newStack.count = existing.count - toExtract
                stacks[slot] = newStack
                onContentsChanged(slot)
            }

            // 使用 copy 和 setCount 替代 copyWithCount
            val extractedStack = existing.copy()
            extractedStack.count = toExtract
            return extractedStack
        }
    }

    open fun getStackLimit(slot: Int, stack: ItemStack): Int {
        return stack.maxStackSize
    }

    open fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return true
    }

    open fun onContentsChanged(slot: Int) {}

    fun serializeNBT(provider: HolderLookup.Provider? = null): CompoundTag {
        val nbtTagList = ListTag()
        for (i in stacks.indices) {
            if (!stacks[i].isEmpty) {
                val itemTag = CompoundTag()
                itemTag.putInt("Slot", i)

                // 使用传入的provider或者简单地保存物品ID和数量
                if (provider != null) {
                    itemTag.put("Item", stacks[i].save(provider))
                } else {
                    // 简化的保存方式，只保存物品ID和数量
                    val simpleTag = CompoundTag()
                    // 修改这里，只保存资源位置的字符串表示，而不是整个ResourceKey
                    val resourceLocation = stacks[i].item.builtInRegistryHolder().key().location()
                    simpleTag.putString("id", resourceLocation.toString())
                    simpleTag.putInt("Count", stacks[i].count)

                    // 保存组件数据
                    val patchBuilder = DataComponentPatch.builder()
                    stacks[i].components.forEach { component ->
                        patchBuilder.set(component)
                    }
                    val patch = patchBuilder.build()
                    if (!patch.isEmpty) {
                        DataComponentPatch.CODEC.encodeStart(NbtOps.INSTANCE, patch)
                            .resultOrPartial { }
                            .ifPresent { simpleTag.put("components", it) }
                    }
                    itemTag.put("Item", simpleTag)
                }

                nbtTagList.add(itemTag)
            }
        }
        val nbt = CompoundTag()
        nbt.put("Items", nbtTagList)
        nbt.putInt("Size", stacks.size)
        return nbt
    }

    fun deserializeNBT(nbt: CompoundTag, provider: HolderLookup.Provider? = null) {
        val tagList = nbt.getList("Items", 10)
        for (i in 0 until tagList.size) {
            val itemTags = tagList.getCompound(i)
            val slot = itemTags.getInt("Slot")

            if (slot >= 0 && slot < stacks.size) {
                stacks[slot] = if (provider != null) {
                    ItemStack.parseOptional(provider, itemTags.getCompound("Item"))
                } else {
                    // 简化的解析方式
                    val itemTag = itemTags.getCompound("Item")
                    val id = itemTag.getString("id")
                    val count = itemTag.getInt("Count")

                    try {
                        // 安全地解析资源位置
                        val resourceLocation = ResourceLocation.tryParse(id)
                        if (resourceLocation != null) {
                            val item = BuiltInRegistries.ITEM.get(resourceLocation)
                            val stack = ItemStack(item, count)

                            // 恢复组件数据
                            if (itemTag.contains("components", 10)) {
                                val componentPatch = DataComponentPatch.CODEC.parse(NbtOps.INSTANCE, itemTag.get("components"))
                                    .resultOrPartial { }
                                    .orElse(DataComponentPatch.EMPTY)
                                stack.applyComponents(componentPatch)
                            }

                            stack
                        } else {
                            ItemStack.EMPTY
                        }
                    } catch (e: Exception) {
                        ItemStack.EMPTY
                    }
                }
            }
        }
    }


    private fun validateSlotIndex(slot: Int) {
        if (slot < 0 || slot >= slots) {
            throw RuntimeException("Slot $slot not in valid range - [0,$slots)")
        }
    }
}