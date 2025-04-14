package com.flechazo.component

import com.flechazo.LootBags
import com.flechazo.block.entity.custom.BagOpenerBlockEntity
import com.flechazo.block.entity.custom.BagStorageBlockEntity
import com.flechazo.block.entity.custom.LootRecyclerBlockEntity
import net.minecraft.resources.ResourceLocation
import org.ladysnake.cca.api.v3.block.BlockComponentFactoryRegistry
import org.ladysnake.cca.api.v3.block.BlockComponentInitializer
import org.ladysnake.cca.api.v3.component.ComponentKey
import org.ladysnake.cca.api.v3.component.ComponentRegistry

/**
 * 组件注册
 */
object ModComponents : BlockComponentInitializer {
    // 注册物品处理组件
    val ITEM_HANDLER: ComponentKey<ItemHandlerComponent> = ComponentRegistry.getOrCreate(
        ResourceLocation.fromNamespaceAndPath(LootBags.MOD_ID, "item_handler"),
        ItemHandlerComponent::class.java
    )

    override fun registerBlockComponentFactories(registry: BlockComponentFactoryRegistry) {
        // 修正：注册方块实体的组件工厂，使用正确的BlockEntity类型
        registry.registerFor(
            BagStorageBlockEntity::class.java,
            ITEM_HANDLER
        ) { blockEntity -> blockEntity as ItemHandlerComponent }

        registry.registerFor(
            BagOpenerBlockEntity::class.java,
            ITEM_HANDLER
        ) { blockEntity -> blockEntity as ItemHandlerComponent }

        registry.registerFor(
            LootRecyclerBlockEntity::class.java,
            ITEM_HANDLER
        ) { blockEntity -> blockEntity as ItemHandlerComponent }
    }
}