package com.evandev.tolerable_creepers.mixin;

import com.evandev.tolerable_creepers.common.entity.CreeperSpores;
import com.evandev.tolerable_creepers.core.registry.TCTags;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow
    @Final
    private Level level;

    @Shadow
    public abstract LivingEntity getIndirectSourceEntity();

    @Shadow
    public abstract void clearToBlow();

    @Definition(id = "getEntities", method = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;")
    @Expression("? = ?.getEntities(?, ?)")
    @ModifyVariable(
            method = "explode",
            at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER)
    )
    private List<Entity> filterHitEntities(List<Entity> list) {
        List<Entity> filteredList = new ArrayList<>(list);
        filteredList.removeIf(entity -> !(entity instanceof LivingEntity || entity.getType().is(TCTags.EXPLOSION_PRONE)) || entity.getType().is(TCTags.EXPLOSION_IMMUNE));
        return filteredList;
    }

    @Inject(method = "explode", at = @At("TAIL"))
    private void onDetonateTail(CallbackInfo ci) {
        LivingEntity source = this.getIndirectSourceEntity();
        if (source instanceof Creeper creeper) {
            this.clearToBlow();

            if (creeper.getType() != net.minecraft.world.entity.EntityType.CREEPER) return;

            boolean day = level.getBrightness(LightLayer.SKY, creeper.blockPosition()) > 10 && level.isDay();
            RandomSource random = creeper.getRandom();
            int sporeCount = Math.round(((day ? 1 : 2) + random.nextInt(day ? 2 : 3)) * creeper.getHealth() / creeper.getMaxHealth());

            CreeperSpores creeperSpores = new CreeperSpores(level, creeper.getX(), creeper.getY() + 0.01, creeper.getZ(), sporeCount, creeper.isPowered());
            if (!creeper.isInvisible()) creeperSpores.setOwner(creeper);

            level.addFreshEntity(creeperSpores);
        }
    }
}