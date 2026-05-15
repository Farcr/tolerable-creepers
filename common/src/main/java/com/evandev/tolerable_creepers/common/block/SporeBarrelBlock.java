package com.evandev.tolerable_creepers.common.block;

import com.evandev.tolerable_creepers.common.entity.PrimedSporeBarrel;
import com.evandev.tolerable_creepers.core.registry.TCParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SporeBarrelBlock extends Block {

    public SporeBarrelBlock(Properties properties) {
        super(properties);
    }

    public static void explode(Level level, BlockPos blockPos, @Nullable LivingEntity livingEntity) {
        if (!level.isClientSide()) {
            PrimedTnt primedTnt = new PrimedSporeBarrel(level, (double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5, livingEntity);
            level.addFreshEntity(primedTnt);
            level.playSound(null, primedTnt.getX(), primedTnt.getY(), primedTnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(livingEntity, GameEvent.PRIME_FUSE, blockPos);
        }
    }

    @Override
    public void stepOn(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        RandomSource random = level.getRandom();
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            BlockPos neighborPos = pos.relative(direction);
            if (!level.getBlockState(neighborPos).isSolidRender(level, neighborPos)) {
                Direction.Axis axis = direction.getAxis();
                double xOffset = axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : (double) random.nextFloat();
                double yOffset = axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : (double) random.nextFloat();
                double zOffset = axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : (double) random.nextFloat();
                level.addParticle(TCParticles.CREEPER_SPORES.get(), (double) pos.getX() + xOffset, (double) pos.getY() + yOffset, (double) pos.getZ() + zOffset, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult blockHitResult) {
        if (!itemStack.is(Items.FLINT_AND_STEEL) && !itemStack.is(Items.FIRE_CHARGE))
            return super.useItemOn(itemStack, state, level, pos, player, hand, blockHitResult);

        explode(level, pos, player);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        Item item = itemStack.getItem();
        if (!player.isCreative()) {
            if (itemStack.is(Items.FLINT_AND_STEEL)) {
                EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                itemStack.hurtAndBreak(1, player, slot);
            } else {
                itemStack.shrink(1);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(item));
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onPlace(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState2.is(blockState.getBlock()) && level.hasNeighborSignal(blockPos)) {
            explode(level, blockPos, null);
            level.removeBlock(blockPos, false);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Block block, @NotNull BlockPos blockPos2, boolean bl) {
        if (level.hasNeighborSignal(blockPos)) {
            explode(level, blockPos, null);
            level.removeBlock(blockPos, false);
        }
    }

    @Override
    public void wasExploded(Level level, @NotNull BlockPos blockPos, @NotNull Explosion explosion) {
        if (!level.isClientSide()) {
            PrimedTnt primedTnt = new PrimedSporeBarrel(level, (double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5, explosion.getIndirectSourceEntity());
            primedTnt.setFuse((short) (level.getRandom().nextInt(15) + 5));
            level.addFreshEntity(primedTnt);
        }
    }

    @Override
    public void onProjectileHit(Level level, @NotNull BlockState blockState, @NotNull BlockHitResult blockHitResult, @NotNull Projectile projectile) {
        if (!level.isClientSide()) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire() && projectile.mayInteract(level, blockPos)) {
                explode(level, blockPos, entity instanceof LivingEntity ? (LivingEntity) entity : null);
                level.removeBlock(blockPos, false);
            }
        }
    }

    @Override
    public boolean dropFromExplosion(@NotNull Explosion explosion) {
        return false;
    }
}
