package com.evandev.tolerable_creepers.common.entity;

import com.evandev.tolerable_creepers.common.integration.NMLCompat;
import com.evandev.tolerable_creepers.core.registry.TCParticles;
import com.farcr.nomansland.common.entity.bombs.ThrowableBombEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SporeBomb extends ThrowableBombEntity {

    private BlockPos hitPos;

    public SporeBomb(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SporeBomb(LivingEntity livingEntity, Level level) {
        super(NMLCompat.SPORE_BOMB_ENTITY.get(), livingEntity, level);
    }

    public SporeBomb(Level level, double x, double y, double z) {
        super(NMLCompat.SPORE_BOMB_ENTITY.get(), x, y, z, level);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        Vec3 motion = this.getDeltaMovement();

        if (motion.lengthSqr() < 0.1) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setOnGround(true);
        } else {
            Direction direction = result.getDirection();
            switch (direction.getAxis()) {
                case X -> this.setDeltaMovement(-motion.x() * 0.4D, motion.y(), motion.z());
                case Y -> this.setDeltaMovement(motion.x() * 0.3D, -motion.y() * 0.3D, motion.z() * 0.3D);
                case Z -> this.setDeltaMovement(motion.x(), motion.y(), -motion.z() * 0.4D);
            }

            if (!this.shouldFuse()) {
                this.startFuse(30);
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        setDeltaMovement(getDeltaMovement().scale(-0.1));
        if (!shouldFuse()) {
            startFuse(30);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (isOnFire()) explode();
    }

    @Override
    public void startFuse(int maxFuse) {
        super.startFuse(maxFuse);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1.0F, 1.0F);
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
    protected ParticleOptions getParticle(LevelAccessor levelAccessor) {
        return TCParticles.CREEPER_SPORES.get();
    }
}