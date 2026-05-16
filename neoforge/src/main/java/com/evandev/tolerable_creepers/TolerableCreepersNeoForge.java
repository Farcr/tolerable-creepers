package com.evandev.tolerable_creepers;

import com.evandev.tolerable_creepers.client.ClientConfigSetup;
import com.evandev.tolerable_creepers.client.model.CreepieModel;
import com.evandev.tolerable_creepers.client.particle.CreeperSporesParticle;
import com.evandev.tolerable_creepers.client.render.CreepieRenderer;
import com.evandev.tolerable_creepers.client.render.MischiefArrowRenderer;
import com.evandev.tolerable_creepers.client.render.SporeBarrelRenderer;
import com.evandev.tolerable_creepers.client.render.SporeBombRenderer;
import com.evandev.tolerable_creepers.common.TCNeoForgeRegistries;
import com.evandev.tolerable_creepers.common.entity.CreeperSpores;
import com.evandev.tolerable_creepers.common.entity.Creepie;
import com.evandev.tolerable_creepers.core.TolerableCreepers;
import com.evandev.tolerable_creepers.core.mixin.accessor.MobAccessor;
import com.evandev.tolerable_creepers.core.registry.TCEntities;
import com.evandev.tolerable_creepers.core.registry.TCItems;
import com.evandev.tolerable_creepers.core.registry.TCParticles;
import com.evandev.tolerable_creepers.core.registry.TCTags;
import com.evandev.tolerable_creepers.platform.NeoForgeRegistrationProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

@Mod(Constants.MOD_ID)
public class TolerableCreepersNeoForge {
    public TolerableCreepersNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        TolerableCreepers.init();
        NeoForgeRegistrationProvider.registerAll(modEventBus);
        TCNeoForgeRegistries.register(modEventBus);

        modEventBus.addListener(this::addCreativeTabItems);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerAttributes);

        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register(modContainer);
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(this::registerRenderers);
            modEventBus.addListener(this::registerLayerDefinitions);
            modEventBus.addListener(this::registerParticles);
            modEventBus.addListener(this::registerAdditionalModels);
        }

        NeoForge.EVENT_BUS.addListener(this::onEntityJoinLevel);
        NeoForge.EVENT_BUS.addListener(this::onExplosionDetonate);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TolerableCreepers.postInit();
            ComposterBlock.COMPOSTABLES.put(TCItems.CREEPER_SPORES.get(), 0.65F);

            if (ModList.get().isLoaded("nomansland") && TCNeoForgeRegistries.SPORE_BOMB != null) {
                DispenserBlock.registerBehavior(TCNeoForgeRegistries.SPORE_BOMB.get(),
                        new ProjectileDispenseBehavior(TCNeoForgeRegistries.SPORE_BOMB.get()));
            }
        });
    }

    private void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(new ModelResourceLocation(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "entity/spore_bomb"),
                "standalone"
        ));
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(TCEntities.CREEPIE.get(), Creepie.createAttributes().build());
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(TCItems.MISCHIEF_ARROW.get());
            event.accept(TCItems.SPORE_BARREL.get());

            if (ModList.get().isLoaded("nomansland") && TCNeoForgeRegistries.SPORE_BOMB != null) {
                event.accept(TCNeoForgeRegistries.SPORE_BOMB.get());
            }
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(TCItems.CREEPER_SPORES.get());
        } else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(TCItems.CREEPIE_SPAWN_EGG.get());
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(Items.CROSSBOW, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "mischief_arrow"),
                    (itemStack, clientLevel, livingEntity, i) -> {
                        if (livingEntity == null) return 0.0F;
                        ChargedProjectiles projectiles = itemStack.get(DataComponents.CHARGED_PROJECTILES);
                        return projectiles != null && projectiles.contains(TCItems.MISCHIEF_ARROW.get()) ? 1.0F : 0.0F;
                    }
            );
        });
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TCEntities.CREEPER_SPORES.get(), NoopRenderer::new);
        event.registerEntityRenderer(TCEntities.CREEPIE.get(), CreepieRenderer::new);
        event.registerEntityRenderer(TCEntities.SPORE_BARREL.get(), SporeBarrelRenderer::new);
        event.registerEntityRenderer(TCEntities.MISCHIEF_ARROW.get(), MischiefArrowRenderer::new);

        if (ModList.get().isLoaded("nomansland") && TCNeoForgeRegistries.SPORE_BOMB_ENTITY != null) {
            event.registerEntityRenderer(TCNeoForgeRegistries.SPORE_BOMB_ENTITY.get(), SporeBombRenderer::new);
        }
    }

    private void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CreepieModel.LAYER_LOCATION, CreepieModel::createBodyLayer);
        event.registerLayerDefinition(CreepieModel.LAYER_LOCATION_ARMOR, CreepieModel::createBodyLayer);
    }

    private void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(TCParticles.CREEPER_SPORES.get(), CreeperSporesParticle.Provider::new);
    }

    private void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
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
    }

    private void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();
        var explosion = event.getExplosion();
        var entityList = event.getAffectedEntities();

        entityList.removeIf(entity -> !(entity instanceof LivingEntity || entity.getType().is(TCTags.EXPLOSION_PRONE)) || entity.getType().is(TCTags.EXPLOSION_IMMUNE));

        if (explosion.getIndirectSourceEntity() instanceof Creeper creeper) {
            event.getAffectedBlocks().clear();
            if (creeper.getType() != net.minecraft.world.entity.EntityType.CREEPER) return;

            boolean day = level.getBrightness(LightLayer.SKY, creeper.blockPosition()) > 10 && level.isDay();
            RandomSource random = creeper.getRandom();
            int sporeCount = Math.round(((day ? 1 : 2) + random.nextInt(day ? 2 : 3)) * creeper.getHealth() / creeper.getMaxHealth());

            CreeperSpores creeperSpores = new CreeperSpores(level, creeper.getX(), creeper.getY() + 0.01, creeper.getZ(), sporeCount, creeper.isPowered());
            if (!creeper.isInvisible()) creeperSpores.setOwner(creeper);

            level.addFreshEntity(creeperSpores);
        }
    }
}