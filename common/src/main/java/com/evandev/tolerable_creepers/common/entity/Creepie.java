package com.evandev.tolerable_creepers.common.entity;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.config.ModConfig;
import com.evandev.tolerable_creepers.core.extension.CreeperExtension;
import com.evandev.tolerable_creepers.core.mixin.accessor.CreeperAccessor;
import com.evandev.tolerable_creepers.core.registry.TCEntities;
import com.evandev.tolerable_creepers.core.registry.TCItems;
import com.evandev.tolerable_creepers.core.registry.TCTags;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;
import java.util.UUID;

/**
 * @author Ocelot
 */
public class Creepie extends Creeper {

    /**
     * The maximum distance a creeper can be from a creepie before it becomes sad.
     */
    public static final double CREEPER_DISTANCE = 32.0D;

    /**
     * After 6000 ticks (2 minutes) of a creepie being sad, it disappears.
     */
    public static final int MAXIMUM_SAD_TIME = 6000;

    /**
     * The distance to check for jukeboxes and spore blossoms.
     */
    public static final float PARTY_DISTANCE = 8.0F;

    protected static final ImmutableList<SensorType<? extends Sensor<? super Creepie>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.HURT_BY,
            TCEntities.CREEPIE_ATTACKABLES_SENSOR.get(),
            TCEntities.CREEPIE_SPECIFIC_SENSOR.get(),
            TCEntities.CREEPIE_FRIEND_SENSOR.get()
    );
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.VISIBLE_VILLAGER_BABIES,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.AVOID_TARGET,
            MemoryModuleType.CELEBRATE_LOCATION,
            MemoryModuleType.DANCING,
            MemoryModuleType.NEAREST_REPELLENT,
            TCEntities.HIDING_SPOT.get(),
            TCEntities.HAS_FRIENDS.get()
    );

    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(Creepie.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DANCING = SynchedEntityData.defineId(Creepie.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState hurtAnimationState = new AnimationState();
    public final AnimationState sadAnimationState = new AnimationState();
    public final AnimationState hideAnimationState = new AnimationState();
    public final AnimationState danceAnimationState = new AnimationState();
    public final AnimationState idleNoveltyAnimationState = new AnimationState();

    private int age;
    private int forcedAge;
    private int forcedAgeTimer;
    private int sadTimer;
    private int sadAnimationTimer = -1;
    private int noveltyTimer;

    private int customFuseTime = 15;
    private int currentFuse = 0;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;

    public Creepie(EntityType<? extends Creepie> entityType, Level level) {
        super(entityType, level);
        this.age = -24000;
        ((CreeperAccessor) this).setExplosionRadius(ModConfig.get().creepieExplosionRadius);
    }

    public Creepie(Level level, @Nullable Entity owner, boolean powered) {
        this(TCEntities.CREEPIE.get(), level);
        this.setOwner(owner);
        ((CreeperExtension) this).tolerablecreepers$setPowered(powered);
        this.updateState();
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.MOVEMENT_SPEED, 0.345);
    }

    private void updateState() {
        Entity owner = this.getOwner();
        if (owner != null) {
            this.setType(owner instanceof Player ? CreepieType.FRIENDLY : CreepieType.NORMAL);
        } else {
            this.setType(CreepieType.ENRAGED);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TYPE_ID, 0);
        builder.define(DATA_DANCING, false);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.BONE_MEAL)) {
            if (!player.isCreative())
                stack.shrink(1);
            this.ageUp((int) ((float) (-this.getAge() / 20) * 0.1F), true);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0)
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
                --this.forcedAgeTimer;
            }
        } else if (this.isAlive() && this.getAge() < 0) {
            this.setAge(this.getAge() + 1);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        } else {
            boolean isHiding = this.isHiding();
            boolean isWalking = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
            boolean isDancing = this.isDancing();

            if (this.sadAnimationTimer <= 0 && !isDancing) {
                if (!isHiding && !isWalking) {
                    if (this.noveltyTimer <= 0) {
                        this.noveltyTimer = (2 + this.random.nextInt(9)) * 20;
                    }
                    if (--this.noveltyTimer <= 0) {
                        this.level().broadcastEntityEvent(this, (byte) 10);
                        this.noveltyTimer = (2 + this.random.nextInt(9)) * 20;
                    }
                }
            }

            if (this.isAlive()) {
                int swellDir = this.getSwellDir();
                if (this.isIgnited()) {
                    swellDir = 1;
                }

                this.currentFuse += swellDir;

                if (this.currentFuse < 0) {
                    this.currentFuse = 0;
                }

                if (this.currentFuse >= this.customFuseTime) {
                    this.explodeCustom();
                }
            }
        }
    }

    protected void explodeCustom() {
        if (!this.level().isClientSide()) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), ModConfig.get().creepieExplosionRadius * f, Level.ExplosionInteraction.NONE);
            this.discard();
            this.spawnLingeringCloudCustom();
        }
    }

    private void spawnLingeringCloudCustom() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            areaeffectcloud.setRadius(2.5F);
            areaeffectcloud.setRadiusOnUse(-0.5F);
            areaeffectcloud.setWaitTime(10);
            areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
            areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float) areaeffectcloud.getDuration());

            for (MobEffectInstance mobeffectinstance : collection) {
                areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
            }

            this.level().addFreshEntity(areaeffectcloud);
        }
    }

    private void setupAnimationStates() {
        boolean isWalking = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
        boolean isHiding = this.isHiding();
        boolean isDancing = this.isDancing();
        boolean isSad = this.sadAnimationTimer > 0;

        this.walkAnimationState.animateWhen(isWalking && !isHiding && !isSad && !isDancing, this.tickCount);
        this.idleAnimationState.animateWhen(!isWalking && !isHiding && !isSad && !isDancing, this.tickCount);
        this.hideAnimationState.animateWhen(isHiding && !isSad, this.tickCount);
        this.danceAnimationState.animateWhen(isDancing && !isSad, this.tickCount);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 2) {
            this.hurtAnimationState.start(this.tickCount);
        } else if (id == 10) {
            this.idleNoveltyAnimationState.start(this.tickCount);
        } else if (id == 11) {
            this.sadAnimationState.start(this.tickCount);
            this.sadAnimationTimer = 40;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("creepieBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        CreepieAi.updateActivity(this);

        if (this.sadTimer > 0) {
            this.sadTimer--;
            if (this.sadTimer <= 0) {
                this.level().broadcastEntityEvent(this, (byte) 11);
                this.sadAnimationTimer = 40; // Length of the sad animation
                this.getNavigation().stop();
                return;
            }
        }

        if (this.sadAnimationTimer > 0) {
            this.sadAnimationTimer--;
            if (this.sadAnimationTimer <= 0) {
                CreeperSpores.spawnParticleSphere(this, this.random, this.position(), 50, 1.5F);
                this.discard();
            }
        }

        super.customServerAiStep();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.ownerUUID != null)
            nbt.putUUID("Owner", this.ownerUUID);
        nbt.putString("Type", this.getCreepieType().name().toLowerCase(Locale.ROOT));
        nbt.putInt("Age", this.getAge());
        nbt.putInt("ForcedAge", this.forcedAge);
        nbt.putInt("SadTime", this.sadTimer);
        nbt.putInt("CurrentFuse", this.currentFuse);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.ownerUUID = nbt.hasUUID("Owner") ? nbt.getUUID("Owner") : null;
        this.setType(CreepieType.byName(nbt.getString("Type")));
        this.setAge(nbt.contains("Age", Tag.TAG_ANY_NUMERIC) ? nbt.getInt("Age") : -24000);
        this.forcedAge = nbt.getInt("ForcedAge");
        this.sadTimer = nbt.getInt("SadTime");
        this.currentFuse = nbt.getInt("CurrentFuse");
    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel level) {
            return this.cachedOwner = level.getEntity(this.ownerUUID);
        } else {
            return null;
        }
    }

    public void setOwner(@Nullable Entity entity) {
        this.ownerUUID = entity != null ? entity.getUUID() : null;
        this.cachedOwner = entity;
        this.updateState();
    }

    public void setType(CreepieType type) {
        this.entityData.set(DATA_TYPE_ID, type.ordinal());
    }

    public CreepieType getCreepieType() {
        int type = this.entityData.get(DATA_TYPE_ID);
        if (type < 0 || type >= CreepieType.values().length) {
            this.entityData.set(DATA_TYPE_ID, 0);
            return CreepieType.NORMAL;
        }
        return CreepieType.values()[type];
    }

    public void ageUp(int amount, boolean forceAge) {
        int j = this.getAge();
        j += amount * 20;
        if (j > 0) {
            j = 0;
        }

        this.setAge(j);
        if (forceAge) {
            if (this.forcedAgeTimer == 0) {
                this.forcedAgeTimer = 40;
            }
        }

        if (this.getAge() == 0)
            this.setAge(this.forcedAge);
    }

    public int getAge() {
        return this.level().isClientSide() ? -1 : this.age;
    }

    public void setAge(int age) {
        this.age = age;
        if (!this.level().isClientSide() && age >= 0)
            this.convertTo(EntityType.CREEPER, false);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        PathNavigation navigation = super.createNavigation(level);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel level, @NotNull DamageSource damageSource, boolean recentlyHit) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack = this.getItemBySlot(equipmentSlot);
            float f = this.getEquipmentDropChance(equipmentSlot);
            boolean guaranteedDrop = f > 1.0F;
            boolean hasVanishingCurse = EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP);

            if (!itemStack.isEmpty() && !hasVanishingCurse && (guaranteedDrop || this.random.nextFloat() < f)) {
                if (!guaranteedDrop && itemStack.isDamageableItem()) {
                    itemStack.setDamageValue(itemStack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
                }

                this.spawnAtLocation(itemStack);
                this.setItemSlot(equipmentSlot, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(TCItems.CREEPER_SPORES.get());
    }

    @Override
    protected Brain.@NotNull Provider<Creepie> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected @NotNull Brain<?> makeBrain(@NotNull Dynamic<?> dynamic) {
        return CreepieAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Brain<Creepie> getBrain() {
        return (Brain<Creepie>) super.getBrain();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType type, @Nullable SpawnGroupData data) {
        CreepieAi.initMemories(this);
        return super.finalizeSpawn(level, difficulty, type, data);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            Entity attacker = source.getEntity();
            Entity direct = source.getDirectEntity();

            Creepie otherCreepie = null;
            if (attacker instanceof Creepie c) {
                otherCreepie = c;
            } else if (direct instanceof Creepie c) {
                otherCreepie = c;
            }

            if (otherCreepie != null) {
                if (otherCreepie != this) {
                    LivingEntity myTarget = this.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
                    LivingEntity theirTarget = otherCreepie.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
                    if (myTarget != null && myTarget.equals(theirTarget)) {
                        Vec3 knockbackDir = this.position().subtract(otherCreepie.position());

                        if (knockbackDir.lengthSqr() < 1.0E-4D) {
                            knockbackDir = new Vec3(this.random.nextDouble() - 0.5D, 0.0D, this.random.nextDouble() - 0.5D);
                        }

                        knockbackDir = knockbackDir.normalize().scale(0.8D);
                        this.setDeltaMovement(this.getDeltaMovement().add(knockbackDir.x, 0.25D, knockbackDir.z));
                        this.hasImpulse = true;
                    }
                }

                return false;
            }
        }

        boolean bl = super.hurt(source, amount);
        if (bl && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 2); // Entity Hurt event
            if (source.getEntity() instanceof LivingEntity livingAttacker) {
                CreepieAi.wasHurtBy(this, livingAttacker);
            }
        }
        return bl;
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    public void playSound(@NotNull SoundEvent soundEvent) {
        this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
    }

    public boolean isDancing() {
        return this.entityData.get(DATA_DANCING);
    }

    public void setDancing(boolean dancing) {
        this.entityData.set(DATA_DANCING, dancing);
    }

    public boolean isSad() {
        return this.sadTimer > 0 || this.sadAnimationTimer > 0;
    }

    public void setSad(boolean sad) {
        this.sadTimer = sad ? MAXIMUM_SAD_TIME : 0;
    }

    public boolean canMove() {
        return !this.isDancing() && this.sadAnimationTimer <= 0 && !this.isHiding();
    }

    public boolean isHiding() {
        return this.brain.hasMemoryValue(TCEntities.HIDING_SPOT.get()) || this.level().getBlockState(this.blockPosition()).is(TCTags.CREEPIE_HIDING_SPOTS);
    }

    public enum CreepieType {
        NORMAL, ENRAGED, FRIENDLY;

        private final ResourceLocation texture;

        CreepieType() {
            this.texture = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/creepie/creepie_" + this.name().toLowerCase(Locale.ROOT) + ".png");
        }

        public static CreepieType byName(String name) {
            for (CreepieType type : values())
                if (type.name().toLowerCase(Locale.ROOT).equals(name))
                    return type;
            return NORMAL;
        }

        public ResourceLocation getTexture() {
            return texture;
        }
    }
}