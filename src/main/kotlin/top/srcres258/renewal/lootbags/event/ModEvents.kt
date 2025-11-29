package top.srcres258.renewal.lootbags.event

import net.minecraft.util.Mth
import net.minecraft.world.item.ItemStack
import top.srcres258.renewal.lootbags.LootBags
import top.srcres258.renewal.lootbags.util.LootBagType
import top.srcres258.renewal.lootbags.util.newItemEntitiesForDropping

object ModEvents {
    fun register() {
        LivingDropsCallback.EVENT.register { entity, source, drops, recentlyHit ->
            for (bagType in LootBagType.entries) {
                if (!bagType.droppable) continue

                val random = entity.level().random
                val rand = Mth.nextDouble(random, 0.0, 1.0)
                if (rand <= bagType.dropChance) {
                    val entityPos = entity.getPosition(0F)
                    val amount = random.nextIntBetweenInclusive(
                        bagType.dropAmountRange.first.toInt(),
                        bagType.dropAmountRange.last.toInt()
                    )

                    if (amount <= 0) continue

                    val stack = ItemStack(bagType.asItem(), amount)
                    val entities = newItemEntitiesForDropping(entity.level(), entityPos, stack)
                    drops.addAll(entities)

                    LootBags.LOGGER.debug(
                        "Generated loot bags type {} amount {} for {} at {} in {}",
                        bagType, amount,
                        entity.type.description.string,
                        entityPos,
                        entity.level().dimension().location()
                    )

                    return@register true // 继续执行掉落
                }
            }

            true // 不影响默认掉落
        }

    }
}