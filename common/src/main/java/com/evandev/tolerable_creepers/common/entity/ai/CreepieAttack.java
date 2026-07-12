package com.evandev.tolerable_creepers.common.entity.ai;

import com.evandev.tolerable_creepers.common.entity.Creepie;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.jetbrains.annotations.NotNull;

public class CreepieAttack extends Behavior<Creepie> {

    public CreepieAttack() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel level, Creepie creeper) {
        LivingEntity livingEntity = creeper.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        double startRangeSqr = Math.min(4.0, creeper.getSwellRange() * creeper.getSwellRange());
        return creeper.getSwellDir() > 0 || livingEntity != null && creeper.distanceToSqr(livingEntity) < startRangeSqr;
    }

    @Override
    protected void stop(@NotNull ServerLevel serverLevel, Creepie creeper, long l) {
        creeper.getNavigation().setSpeedModifier(1.0);
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel level, @NotNull Creepie creeper, long l) {
        return this.checkExtraStartConditions(level, creeper);
    }

    @Override
    protected void tick(@NotNull ServerLevel level, Creepie creeper, long l) {
        LivingEntity target = creeper.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        double rangeSqr = creeper.getSwellRange() * creeper.getSwellRange();
        if (target == null) {
            creeper.setSwellDir(-1);
            creeper.getNavigation().setSpeedModifier(1.4);
        } else if (creeper.distanceToSqr(target) > rangeSqr) {
            creeper.setSwellDir(-1);
            creeper.getNavigation().setSpeedModifier(1.4);
        } else if (!creeper.getSensing().hasLineOfSight(target)) {
            creeper.setSwellDir(-1);
            creeper.getNavigation().setSpeedModifier(1.4);
        } else {
            creeper.setSwellDir(1);
            creeper.getNavigation().setSpeedModifier(0.5);
        }
    }
}
