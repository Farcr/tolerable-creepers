package com.evandev.tolerable_creepers.core.registry;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.entity.*;
import com.evandev.tolerable_creepers.common.entity.ai.sensing.CreepieAttackablesSensor;
import com.evandev.tolerable_creepers.common.entity.ai.sensing.CreepieFriendSensor;
import com.evandev.tolerable_creepers.common.entity.ai.sensing.CreepieSpecificSensor;
import com.evandev.tolerable_creepers.core.mixin.accessor.SensorTypeInvoker;
import com.evandev.tolerable_creepers.platform.registry.RegistrationProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.Optional;
import java.util.function.Supplier;

public class TCEntities {
    public static final RegistrationProvider<EntityType<?>> ENTITIES = RegistrationProvider.get(Registries.ENTITY_TYPE, Constants.MOD_ID);

    public static final RegistrationProvider<MemoryModuleType<?>> MEMORY_MODULES = RegistrationProvider.get(Registries.MEMORY_MODULE_TYPE, Constants.MOD_ID);
    public static final RegistrationProvider<SensorType<?>> SENSOR_TYPES = RegistrationProvider.get(Registries.SENSOR_TYPE, Constants.MOD_ID);

    public static final Supplier<EntityType<CreeperSpores>> CREEPER_SPORES = ENTITIES.register("creeper_spores", () -> EntityType.Builder.<CreeperSpores>of(CreeperSpores::new, MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).build("creeper_spores"));
    public static final Supplier<EntityType<Creepie>> CREEPIE = ENTITIES.register("creepie", () -> EntityType.Builder.<Creepie>of(Creepie::new, MobCategory.MONSTER).sized(0.625F, 0.875F).clientTrackingRange(8).build("creepie"));
    public static final Supplier<EntityType<PrimedSporeBarrel>> SPORE_BARREL = ENTITIES.register("spore_barrel", () -> EntityType.Builder.<PrimedSporeBarrel>of(PrimedSporeBarrel::new, MobCategory.MISC).fireImmune().sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10).build("spore_barrel"));
    public static final Supplier<EntityType<MischiefArrow>> MISCHIEF_ARROW = ENTITIES.register("mischief_arrow", () -> EntityType.Builder.<MischiefArrow>of(MischiefArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("mischief_arrow"));
    public static final Supplier<EntityType<FireBomb>> FIRE_BOMB = ENTITIES.register("fire_bomb", () -> EntityType.Builder.<FireBomb>of(FireBomb::new, MobCategory.MISC).sized(0.375F, 0.375F).clientTrackingRange(4).updateInterval(20).build("fire_bomb"));
    public static final Supplier<EntityType<SporeBomb>> SPORE_BOMB = ENTITIES.register("spore_bomb", () -> EntityType.Builder.<SporeBomb>of(SporeBomb::new, MobCategory.MISC).sized(0.375F, 0.375F).clientTrackingRange(4).updateInterval(20).build("spore_bomb"));

    public static final Supplier<MemoryModuleType<BlockPos>> HIDING_SPOT = MEMORY_MODULES.register("hide_azalea", () -> new MemoryModuleType<>(Optional.of(BlockPos.CODEC)));
    public static final Supplier<MemoryModuleType<Boolean>> HAS_FRIENDS = MEMORY_MODULES.register("has_friends", () -> new MemoryModuleType<>(Optional.empty()));

    public static final Supplier<SensorType<CreepieAttackablesSensor>> CREEPIE_ATTACKABLES_SENSOR = SENSOR_TYPES.register("creepie_attackables_sensor", () -> SensorTypeInvoker.create(CreepieAttackablesSensor::new));
    public static final Supplier<SensorType<CreepieSpecificSensor>> CREEPIE_SPECIFIC_SENSOR = SENSOR_TYPES.register("creepie_specific_sensor", () -> SensorTypeInvoker.create(CreepieSpecificSensor::new));
    public static final Supplier<SensorType<CreepieFriendSensor>> CREEPIE_FRIEND_SENSOR = SENSOR_TYPES.register("creepie_friend_sensor", () -> SensorTypeInvoker.create(CreepieFriendSensor::new));

    public static void load() {
    }
}