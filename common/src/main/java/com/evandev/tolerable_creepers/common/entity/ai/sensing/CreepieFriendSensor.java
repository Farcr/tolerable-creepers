package com.evandev.tolerable_creepers.common.entity.ai.sensing;

import com.evandev.tolerable_creepers.common.entity.Creepie;
import com.evandev.tolerable_creepers.core.registry.TCEntities;
import com.evandev.tolerable_creepers.core.registry.TCTags;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class CreepieFriendSensor extends Sensor<Creepie> {

    private static final Set<MemoryModuleType<?>> REQUIRES = ImmutableSet.of(
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.VISIBLE_VILLAGER_BABIES,
            TCEntities.HAS_FRIENDS.get()
    );

    @Override
    public @NotNull Set<MemoryModuleType<?>> requires() {
        return REQUIRES;
    }

    @Override
    protected void doTick(@NotNull ServerLevel level, @NotNull Creepie creepie) {
        List<LivingEntity> friends = this.getNearestFriends(creepie);
        creepie.getBrain().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, friends.stream().filter(entity -> entity.getType() == TCEntities.CREEPIE.get()).toList()); // Filter for playing friends
        creepie.getBrain().setMemory(TCEntities.HAS_FRIENDS.get(), !friends.isEmpty());
    }

    private List<LivingEntity> getNearestFriends(Creepie creepie) {
        return ImmutableList.copyOf(this.getVisibleEntities(creepie).findAll(entity -> this.isFriend(creepie, entity)));
    }

    private boolean isFriend(Creepie creepie, LivingEntity entity) {
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity))
            return false;
        if (!entity.getType().is(TCTags.CREEPIE_FRIENDS) || (entity instanceof Creepie otherCreepie && otherCreepie.getCreepieType() != creepie.getCreepieType()))
            return false;
        return creepie.distanceToSqr(entity) < Creepie.CREEPER_DISTANCE * Creepie.CREEPER_DISTANCE;
    }

    private NearestVisibleLivingEntities getVisibleEntities(LivingEntity entity) {
        return entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
    }
}
