package com.evandev.tolerable_creepers.common.item;

import com.evandev.tolerable_creepers.common.entity.ThrowableBomb;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class BombItem extends Item implements ProjectileItem {
    private static final int DEFAULT_THROW_TIME = 10;
    private static final int COOLDOWN_TIME = 40;

    public BombItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack arg) {
        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(@NotNull Level level, LivingEntity entity, @NotNull ItemStack stack, int remainingTicks) {
        if (!entity.isShiftKeyDown() && this.getUseDuration(stack, entity) - remainingTicks >= DEFAULT_THROW_TIME) {
            entity.releaseUsingItem();
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int remainingTicks) {
        if (this.getUseDuration(stack, entity) - remainingTicks < DEFAULT_THROW_TIME) {
            return;
        }

        if (!level.isClientSide()) {
            ThrowableBomb bomb = this.createBomb(entity, level);
            bomb.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 0.8F, 1.0F);
            level.addFreshEntity(bomb);
        }

        level.playSound(entity instanceof Player player ? player : null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F));
        if (!(entity instanceof Player player) || !player.isCreative()) {
            stack.shrink(1);
        }

        if (entity instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
            player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TIME);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public @NotNull Projectile asProjectile(@NotNull Level level, Position pos, @NotNull ItemStack stack, @NotNull Direction direction) {
        ThrowableBomb bomb = this.createBomb(null, level);
        bomb.setPos(pos.x(), pos.y(), pos.z());
        return bomb;
    }

    @Override
    public ProjectileItem.@NotNull DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder()
                .uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.5F)
                .power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.25F)
                .build();
    }

    public abstract ThrowableBomb createBomb(LivingEntity entity, Level level);
}
