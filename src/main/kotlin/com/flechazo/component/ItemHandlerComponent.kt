package com.flechazo.component

import com.flechazo.util.SimpleItemStackHandler
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import org.ladysnake.cca.api.v3.component.Component

/**
 * 物品处理组件接口
 */
interface ItemHandlerComponent : Component {
    /**
     * 获取指定方向的物品处理器
     */
    fun getItemHandler(side: Direction?): SimpleItemStackHandler

    /**
     * 获取输入物品处理器
     */
    val inputItemHandler: SimpleItemStackHandler

    /**
     * 获取输出物品处理器
     */
    val outputItemHandler: SimpleItemStackHandler

    /**
     * 从NBT读取组件数据
     */
    override fun readFromNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        // 默认实现，子类可以覆盖
    }

    /**
     * 将组件数据写入NBT
     */
    override fun writeToNbt(tag: CompoundTag, registryLookup: HolderLookup.Provider) {
        // 默认实现，子类可以覆盖
    }
}