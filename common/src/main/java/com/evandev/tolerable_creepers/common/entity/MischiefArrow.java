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

public class MischiefArrow extends AbstractArrow {

    public MischiefArrow(EntityType<? extends MischiefArrow> entityType, Level level) {
        super(entityType, level);
    }

    public MischiefArrow(Level level, LivingEntity livingEntity) {
        super(TCEntities.MISCHIEF_ARROW.get(), livingEntity, level);
    }

    public MischiefArrow(Level level, double d, double e, double f) {
        super(TCEntities.MISCHIEF_ARROW.get(), d, e, f, level);
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
