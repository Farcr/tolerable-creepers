package com.evandev.tolerable_creepers.core;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.entity.PrimedSporeBarrel;
import com.evandev.tolerable_creepers.config.ModConfig;
import com.evandev.tolerable_creepers.core.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class TolerableCreepers {

    public static void init() {
        TCBlocks.load();
        TCItems.load();
        TCEntities.load();
        TCParticles.load();
        TCTags.load();
        ModConfig.load();
    }

    public static void postInit() {
        DispenserBlock.registerBehavior(TCItems.MISCHIEF_ARROW.get(), new ProjectileDispenseBehavior(TCItems.MISCHIEF_ARROW.get()));
        DispenserBlock.registerBehavior(TCItems.FIRE_BOMB.get(), new ProjectileDispenseBehavior(TCItems.FIRE_BOMB.get()));
        DispenserBlock.registerBehavior(TCItems.SPORE_BOMB.get(), new ProjectileDispenseBehavior(TCItems.SPORE_BOMB.get()));

        DispenserBlock.registerBehavior(TCItems.CREEPER_SPORES.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected @NotNull ItemStack execute(@NotNull BlockSource level, @NotNull ItemStack stack) {
                Direction direction = level.state().getValue(DispenserBlock.FACING);
                try {
                    TCEntities.CREEPIE.get().spawn(level.level(), stack, null, level.pos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
                } catch (Exception var6) {
                    Constants.LOG.error("Error while dispensing spawn egg from dispenser at {}", level.pos(), var6);
                    return ItemStack.EMPTY;
                }
                stack.shrink(1);
                level.level().gameEvent(null, GameEvent.ENTITY_PLACE, level.pos());
                return stack;
            }
        });

        DispenserBlock.registerBehavior(TCBlocks.SPORE_BARREL.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected @NotNull ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
                Level level = source.level();
                BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                PrimedSporeBarrel sporeBarrel = new PrimedSporeBarrel(level, (double) blockpos.getX() + 0.5, blockpos.getY(), (double) blockpos.getZ() + 0.5, null);
                level.addFreshEntity(sporeBarrel);
                level.playSound(null, sporeBarrel.getX(), sporeBarrel.getY(), sporeBarrel.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.ENTITY_PLACE, blockpos);
                stack.shrink(1);
                return stack;
            }
        });
    }
}