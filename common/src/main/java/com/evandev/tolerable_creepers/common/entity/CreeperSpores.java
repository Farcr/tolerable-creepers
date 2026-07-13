package com.evandev.tolerable_creepers.common.entity;

import com.evandev.tolerable_creepers.core.registry.TCEntities;
import com.evandev.tolerable_creepers.core.registry.TCParticles;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreeperSpores extends ThrowableProjectile {

    private static final EntityDataAccessor<Boolean> LANDED = SynchedEntityData.defineId(CreeperSpores.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(CreeperSpores.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CLOUD_SIZE = SynchedEntityData.defineId(CreeperSpores.class, EntityDataSerializers.INT);

    private Creepie spawnCreepie;
    @Nullable
    private LivingEntity target;
    private int cloudTime;

    public CreeperSpores(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
        this.entityData.set(CLOUD_SIZE, 2);
    }

    public CreeperSpores(Level level, double x, double y, double z, int cloudSize, boolean powered) {
        super(TCEntities.CREEPER_SPORES.get(), x, y, z, level);
        this.entityData.set(CLOUD_SIZE, cloudSize);
        this.entityData.set(POWERED, powered);
    }

    public CreeperSpores(LivingEntity thrower, Level level, int cloudSize) {
        super(TCEntities.CREEPER_SPORES.get(), thrower, level);
        this.entityData.set(CLOUD_SIZE, cloudSize);
    }

    private static LivingEntity updateTarget(Mob owner) {
        LivingEntity lastHurt = owner.getLastHurtMob();
        LivingEntity lastHurtBy = owner.getLastHurtByMob();
        if (lastHurt == null && lastHurtBy == null) {
            return null;
        }

        if (lastHurt == null) {
            return lastHurtBy;
        }

        if (lastHurtBy == null) {
            return lastHurt;
        }

        long lastHurtTime = owner.getLastHurtMobTimestamp();
        long lastHurtByTime = owner.getLastHurtByMobTimestamp();
        return lastHurtByTime >= lastHurtTime ? lastHurtBy : lastHurt;
    }

    public static void spawnParticleSphere(Entity entity, RandomSource random, Vec3 pos, int amount, float cloudSize) {
        spawnParticleSphere(entity, random, pos, amount, cloudSize, TCParticles.CREEPER_SPORES.get());
    }

    public static void spawnParticleSphere(Entity entity, RandomSource random, Vec3 pos, int amount, float cloudSize, SimpleParticleType particleType) {
        for (int i = 0; i < 4 * amount * cloudSize; i++) {
            float theta = (float) (random.nextFloat() * 2 * Math.PI);
            float phi = (float) (random.nextFloat() * 2 * Math.PI);

            double xPos = Mth.sin(phi) * Mth.cos(theta) * cloudSize * random.nextFloat();
            double yPos = Mth.sin(phi) * Mth.sin(theta) * cloudSize * random.nextFloat();
            double zPos = Mth.cos(phi) * cloudSize * random.nextFloat();

            if (entity.level().clip(new ClipContext(pos, pos.add(xPos, yPos, zPos), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS) {
                if (entity.level().isClientSide()) {
                    entity.level().addParticle(particleType, true, pos.x() + xPos, pos.y() + yPos, pos.z() + zPos, 0.0D, 0.0D, 0.0D);
                } else {
                    ((ServerLevel) entity.level()).sendParticles(particleType, pos.x() + xPos, pos.y() + yPos, pos.z() + zPos, 1, 0.0D, 0.0D, 0.0D, 0.0);
                }
            }
        }
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
        this.target = entity instanceof Mob mob ? updateTarget(mob) : null;
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return;
        }

        boolean landed = this.hasLanded();
        if (hitResult.getType() != HitResult.Type.BLOCK || (!((BlockHitResult) hitResult).isInside() && ((BlockHitResult) hitResult).getDirection() == Direction.UP)) {
            this.setOnGround(true);
            this.setLanded();
        }

        this.setDeltaMovement(Vec3.ZERO);
        if (landed != this.hasLanded()) {
            if (this.level().isClientSide()) {
                float visualSize = this.getVisualCloudSize();
                int particleCount = Math.round(30 * visualSize);
                for (int i = 0; i < particleCount; i++) {
                    double theta = this.random.nextFloat() * 2 * Math.PI;
                    double xVelocity = Math.cos(theta) * (this.random.nextFloat() * 0.3 + 0.7) * visualSize;
                    double zVelocity = Math.sin(theta) * (this.random.nextFloat() * 0.3 + 0.7) * visualSize;
                    this.level().addParticle(this.getSporeParticleType(), false, this.getX(), this.getY(), this.getZ(), xVelocity, this.random.nextFloat() * 0.2, zVelocity);
                }
            } else {
                this.cloudTime = this.getInitialCloudTime(this.getCloudSize());
            }

            this.setPos(hitResult.getLocation());
        }
    }

    @Override
    public void tick() {
        if (!this.onGround())
            super.tick();

        if (!this.hasLanded()) {
            this.level().addParticle(this.getSporeParticleType(), true, this.getX(), this.getY(), this.getZ(), 0.0f, 0.0f, 0.0f);
        } else {
            if (this.getCloudSize() <= 0) {
                this.discard();
                return;
            }

            if (this.level().isClientSide()) {
                this.tickLandedClient();
            } else {
                this.cloudTime--;
                if (this.cloudTime <= 0) {
                    this.discard();
                    return;
                }

                this.tickLandedServer();
            }
        }
    }

    /**
     * Ticks that a cloud lingers after landing, before it starts counting down toward {@link #discard()}.
     */
    protected int getInitialCloudTime(int cloudSize) {
        return 20 * cloudSize + 200;
    }

    /**
     * Visual radius/density used for the landed particle sphere and the landing burst, independent of
     * {@link #getCloudSize()}'s creepie-spawn budget so a subclass can keep a large spawn count without
     * an oversized particle field.
     */
    protected float getVisualCloudSize() {
        return this.getCloudSize();
    }

    /**
     * Radius within which {@link #tickLandedServer()} scatters creepie spawn points, independent of
     * {@link #getCloudSize()}'s creepie-spawn budget.
     */
    protected float getSpawnRadius() {
        return this.getCloudSize();
    }

    /**
     * Ticks between creepie spawn attempts while landed; confirmation happens at double this interval.
     */
    protected int getCreepieSpawnIntervalTicks() {
        return 20;
    }

    /**
     * Called every client tick while landed and not yet discarded.
     */
    protected void tickLandedClient() {
        this.spawnParticleSphere(this.position(), 1, this.getVisualCloudSize());
    }

    /**
     * Called every server tick while landed, with {@code cloudTime > 0} and {@code getCloudSize() > 0}.
     */
    protected void tickLandedServer() {
        int interval = this.getCreepieSpawnIntervalTicks();
        if (this.spawnCreepie == null && this.cloudTime % (interval / 2) == 0) {
            int cloudSize = this.getCloudSize();
            float spawnRadius = this.getSpawnRadius();
            for (int i = 0; i < 4 * cloudSize; i++) {
                float theta = (float) (this.random.nextFloat() * 2 * Math.PI);
                float phi = (float) (this.random.nextFloat() * 2 * Math.PI);

                double xPos = this.getX() + Mth.sin(phi) * Mth.cos(theta) * spawnRadius * this.random.nextFloat();
                double zPos = this.getZ() + Mth.cos(phi) * spawnRadius * this.random.nextFloat();
                double yPos = this.getY();

                if (this.level().clip(new ClipContext(this.position(), new Vec3(xPos, yPos, zPos), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS) {
                    Vec3 creepieSpawnPos = new Vec3(xPos, yPos, zPos);
                    this.spawnCreepie = this.createCreepie(creepieSpawnPos);
                    this.spawnParticleSphere(creepieSpawnPos, 50, 1.5F);
                    break;
                }
            }
        }
        if (this.spawnCreepie != null && this.cloudTime % interval == 0) {
            if (this.level().noCollision(this.spawnCreepie, this.spawnCreepie.getBoundingBox())) {
                this.level().addFreshEntity(this.spawnCreepie);
                this.setCloudSize(this.getCloudSize() - 1);
            }
            this.spawnCreepie = null;
        }
    }

    /**
     * Constructs and positions the creepie {@link #tickLandedServer()} places at {@code pos}.
     */
    protected Creepie createCreepie(Vec3 pos) {
        Creepie creepie = new Creepie(this.level(), this.getOwner(), this.isPowered());
        this.initializeCreepie(creepie);
        creepie.setPos(pos);
        return creepie;
    }

    protected void initializeCreepie(Creepie creepie) {
        creepie.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, this.target);
        creepie.setType(Creepie.CreepieType.NORMAL);
    }

    protected void spawnParticleSphere(Vec3 pos, int amount, float cloudSize) {
        spawnParticleSphere(this, this.random, pos, amount, cloudSize, this.getSporeParticleType());
    }

    protected SimpleParticleType getSporeParticleType() {
        return TCParticles.CREEPER_SPORES.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(LANDED, false);
        builder.define(POWERED, false);
        builder.define(CLOUD_SIZE, 0);
    }

    protected boolean hasLanded() {
        return this.entityData.get(LANDED);
    }

    protected boolean isPowered() {
        return this.entityData.get(POWERED);
    }

    protected int getCloudSize() {
        return this.entityData.get(CLOUD_SIZE);
    }

    protected void setCloudSize(int cloudSize) {
        this.entityData.set(CLOUD_SIZE, cloudSize);
    }

    protected int getCloudTime() {
        return this.cloudTime;
    }

    protected void setCloudTime(int cloudTime) {
        this.cloudTime = cloudTime;
    }

    private void setLanded() {
        this.entityData.set(LANDED, true);
    }

    @Override
    protected double getDefaultGravity() {
        return (float) ((this.hasLanded() ? 0.06F : 1.0F) * super.getDefaultGravity());
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("CloudSize", this.getCloudSize());
        nbt.putInt("CloudTime", this.cloudTime);
        nbt.putBoolean("Powered", this.isPowered());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("CloudSize", Tag.TAG_ANY_NUMERIC))
            this.setCloudSize(nbt.getInt("CloudSize"));
        if (nbt.contains("CloudTime", Tag.TAG_ANY_NUMERIC)) {
            this.cloudTime = nbt.getInt("CloudTime");
            this.setLanded();
        }
        this.entityData.set(POWERED, nbt.getBoolean("Powered"));
    }
}
