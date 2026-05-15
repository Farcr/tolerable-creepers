package com.evandev.tolerable_creepers.common.entity;

import com.evandev.tolerable_creepers.core.registry.TCEntities;
import com.evandev.tolerable_creepers.core.registry.TCParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

public class SporeBomb extends ThrowableBomb {

    public SporeBomb(EntityType<? extends ThrowableBomb> entityType, Level level) {
        super(entityType, level);
    }

    public SporeBomb(LivingEntity livingEntity, Level level) {
        super(TCEntities.SPORE_BOMB.get(), livingEntity, level);
    }

    public SporeBomb(Level level, double x, double y, double z) {
        super(TCEntities.SPORE_BOMB.get(), x, y, z, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
    }

    @Override
    protected void explode() {
        double height = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING, this.blockPosition().getX(), this.blockPosition().getZ());

        double yPos = this.getY();
        if (Math.abs(yPos - height) < 4) {
            yPos = height;
        }

        this.level().explode(this, this.getX(), this.getY(0.0625), this.getZ(), 1.0F, Level.ExplosionInteraction.NONE);
        CreeperSpores spores = new CreeperSpores(this.level(), this.getX(), yPos + 0.01, this.getZ(), 1 + this.random.nextInt(2), false);
        if (!(this.getOwner() instanceof LivingEntity livingEntity) || !livingEntity.hasEffect(MobEffects.INVISIBILITY)) {
            spores.setOwner(this.getOwner());
        }
        this.level().addFreshEntity(spores);
        this.discard();
    }

    @Override
    protected ParticleOptions getParticle() {
        return TCParticles.CREEPER_SPORES.get();
    }
}
