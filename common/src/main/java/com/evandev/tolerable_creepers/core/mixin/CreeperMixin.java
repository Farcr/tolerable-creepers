package com.evandev.tolerable_creepers.core.mixin;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.entity.Creepie;
import com.evandev.tolerable_creepers.core.extension.CreeperExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster implements CreeperExtension {

    @Unique
    private static final ResourceKey<LootTable> DETONATE_LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "entities/creeper_explode"));

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_IS_POWERED;

    @Unique
    private boolean exploded;

    private CreeperMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tolerablecreepers$setPowered(boolean powered) {
        this.entityData.set(DATA_IS_POWERED, powered);
    }

    @ModifyArg(method = "spawnLingeringCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setRadius(F)V"))
    private float tolerablecreepers$reduceCloudRadius(float f) {
        if (((Creeper) (Object) this) instanceof Creepie) {
            return 0.75F;
        }
        return f;
    }

    @Override
    protected void dropFromLootTable(@NotNull DamageSource damageSource, boolean bl) {
        this.exploded = this.getType() == EntityType.CREEPER && damageSource.is(DamageTypeTags.IS_EXPLOSION);
        super.dropFromLootTable(damageSource, bl);
    }

    @Override
    protected @NotNull ResourceKey<LootTable> getDefaultLootTable() {
        return this.exploded ? DETONATE_LOOT_TABLE : super.getDefaultLootTable();
    }
}