package com.evandev.tolerable_creepers;

import com.evandev.tolerable_creepers.common.entity.Creepie;
import com.evandev.tolerable_creepers.core.TolerableCreepers;
import com.evandev.tolerable_creepers.core.mixin.accessor.MobAccessor;
import com.evandev.tolerable_creepers.core.registry.TCEntities;
import com.evandev.tolerable_creepers.core.registry.TCItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.CreativeModeTabs;

public class TolerableCreepersFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        TolerableCreepers.init();

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof IronGolem golem) {
                GoalSelector targetSelector = ((MobAccessor) entity).getTargetSelector();
                targetSelector.getAvailableGoals().stream().map(WrappedGoal::getGoal)
                        .filter(g -> g instanceof NearestAttackableTargetGoal).findAny().ifPresent(g -> {
                            targetSelector.removeGoal(g);
                            targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(golem, Mob.class, 5, false, false, e -> e instanceof Enemy));
                        });
            }
            if (entity instanceof Ocelot || entity instanceof Cat) {
                GoalSelector targetSelector = ((MobAccessor) entity).getTargetSelector();
                targetSelector.addGoal(1, new NearestAttackableTargetGoal<>((Mob) entity, Creepie.class, false));
            }
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(TCItems.MISCHIEF_ARROW.get());
            entries.accept(TCItems.FIRE_BOMB.get());
            entries.accept(TCItems.SPORE_BOMB.get());
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.accept(TCItems.CREEPER_SPORES.get());
        });

        FabricDefaultAttributeRegistry.register(TCEntities.CREEPIE.get(), Creepie.createAttributes());
        CompostingChanceRegistry.INSTANCE.add(TCItems.CREEPER_SPORES.get(), 0.65F);

        TolerableCreepers.postInit();

        // TODO: For the Golem/Cat/Explosion events, register them via standard Fabric API events like ServerEntityEvents.ENTITY_LOAD
    }
}