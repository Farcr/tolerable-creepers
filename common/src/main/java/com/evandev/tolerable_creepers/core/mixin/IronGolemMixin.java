package com.evandev.tolerable_creepers.core.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends AbstractGolem implements NeutralMob {

    protected IronGolemMixin(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "doPush", at = @At("HEAD"))
    private void onDoPush(Entity entity, CallbackInfo ci) {
        if (entity instanceof Creeper && this.getRandom().nextInt(20) == 0) {
            this.setTarget((LivingEntity) entity);
        }
    }

    @Inject(method = "canAttackType", at = @At("HEAD"), cancellable = true)
    private void overrideCreeperTargeting(EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        if (entityType == EntityType.CREEPER) {
            cir.setReturnValue(super.canAttackType(entityType));
        }
    }
}