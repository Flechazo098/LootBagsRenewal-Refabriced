package top.srcres258.renewal.lootbags.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

@FunctionalInterface
public interface LivingDropsCallback {
    boolean onLivingDrops(
            LivingEntity entity,
            DamageSource source,
            Collection<ItemEntity> drops,
            boolean recentlyHit
    );

    Event<LivingDropsCallback> EVENT = EventFactory.createArrayBacked(LivingDropsCallback.class,
            callbacks -> (entity, source, drops, recentlyHit) -> {
                for (LivingDropsCallback callback : callbacks) {
                    if (!callback.onLivingDrops(entity, source, drops, recentlyHit)) {
                        return false;
                    }
                }
                return true;
            }
    );
}