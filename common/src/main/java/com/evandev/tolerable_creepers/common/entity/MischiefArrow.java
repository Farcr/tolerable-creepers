package com.evandev.tolerable_creepers.common.entity;

import com.evandev.tolerable_creepers.core.registry.TCEntities;
import com.evandev.tolerable_creepers.core.registry.TCItems;
import com.evandev.tolerable_creepers.core.registry.TCParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MischiefArrow extends AbstractArrow {

    public MischiefArrow(EntityType<? extends MischiefArrow> entityType, Level level) {
        super(entityType, level);
    }

    public MischiefArrow(Level level, LivingEntity livingEntity, ItemStack ammo, @Nullable ItemStack weapon) {
        super(TCEntities.MISCHIEF_ARROW.get(), livingEntity, level, ammo, weapon);
    }

    public MischiefArrow(Level level, double x, double y, double z, ItemStack ammo, @Nullable ItemStack weapon) {
        super(TCEntities.MISCHIEF_ARROW.get(), x, y, z, level, ammo, weapon);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() && !this.inGround) {
            this.level().addParticle(TCParticles.CREEPER_SPORES.get(), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return new ItemStack(TCItems.MISCHIEF_ARROW.get());
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(TCItems.MISCHIEF_ARROW.get());
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);

        if (hitResult.getType() != HitResult.Type.MISS) {
            Vec3 pos = hitResult.getLocation();
            this.level().addFreshEntity(new CreeperSpores(this.level(), pos.x(), pos.y() + 0.01, pos.z(), 1, false));
            this.discard();
        }
    }
}