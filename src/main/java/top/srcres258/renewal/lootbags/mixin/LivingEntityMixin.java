package top.srcres258.renewal.lootbags.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.srcres258.renewal.lootbags.event.LivingDropsCallback;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected int lastHurtByPlayerTime;

    @Inject(
            method = "dropAllDeathLoot",
            at = @At("TAIL")
    )
    private void injectLivingDrops(ServerLevel serverLevel, DamageSource damageSource, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;

        boolean recentlyHit = lastHurtByPlayerTime > 0;

        List<ItemEntity> drops = collectNewDrops(serverLevel, self);

        if (drops.isEmpty())
            return;

        boolean allow = LivingDropsCallback.EVENT.invoker().onLivingDrops(self, damageSource, drops, recentlyHit);

        if (!allow) {
            return;
        }

        for (ItemEntity e : drops) {
            serverLevel.addFreshEntity(e);
        }
    }
    private List<ItemEntity> collectNewDrops(ServerLevel level, LivingEntity entity) {
        var bbox = entity.getBoundingBox().inflate(1.5);

        List<ItemEntity> drops = new ArrayList<>();
        for (ItemEntity e : level.getEntitiesOfClass(ItemEntity.class, bbox)) {
            if (e.getOwner() == entity) {
                drops.add(e);
            }
        }

        return drops;
    }
}