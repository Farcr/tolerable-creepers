package com.evandev.tolerable_creepers.mixin;

import com.evandev.tolerable_creepers.common.entity.CreeperSpores;
import com.evandev.tolerable_creepers.config.ModConfig;
import com.evandev.tolerable_creepers.core.registry.TCEntities;
import com.evandev.tolerable_creepers.core.registry.TCTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    @ModifyVariable(
            method = "explode",
            at = @At(value = "STORE", ordinal = 0)
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
            EntityType<?> type = creeper.getType();

            if (ModConfig.get().preventCreeperBlockDamage) {
                if (!type.is(TCTags.EXPLOSION_PRONE) && (type == EntityType.CREEPER || type == TCEntities.CREEPIE.get() || type.is(TCTags.EXPLOSION_IMMUNE))) {
                    this.clearToBlow();
                }
            }

            if (type != EntityType.CREEPER) return;

            boolean day = level.getBrightness(LightLayer.SKY, creeper.blockPosition()) > 10 && level.isDay();
            RandomSource random = creeper.getRandom();

            int baseCount = day ? ModConfig.get().sporeCountDayBase : ModConfig.get().sporeCountNightBase;
            int randomBound = day ? ModConfig.get().sporeCountDayRandom : ModConfig.get().sporeCountNightRandom;

            int randomAdd = randomBound > 0 ? random.nextInt(randomBound) : 0;
            int sporeCount = Math.round((baseCount + randomAdd) * creeper.getHealth() / creeper.getMaxHealth());
            if (creeper.isPowered()) sporeCount *= 2;

            CreeperSpores creeperSpores = new CreeperSpores(level, creeper.getX(), creeper.getY() + 0.01, creeper.getZ(), sporeCount);
            if (!creeper.isInvisible()) creeperSpores.setOwner(creeper);

            level.addFreshEntity(creeperSpores);
        }
    }
}