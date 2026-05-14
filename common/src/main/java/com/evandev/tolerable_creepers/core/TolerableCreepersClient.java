package com.evandev.tolerable_creepers.core;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import gg.moonflower.pollen.api.registry.render.v1.ModelRegistry;
import com.evandev.tolerable_creepers.client.particle.CreeperSporesParticle;
import com.evandev.tolerable_creepers.client.render.CreepieRenderer;
import com.evandev.tolerable_creepers.client.render.FireBombRenderer;
import com.evandev.tolerable_creepers.client.render.MischiefArrowRenderer;
import com.evandev.tolerable_creepers.client.render.SporeBarrelRenderer;
import com.evandev.tolerable_creepers.client.render.SporeBombRenderer;
import com.evandev.tolerable_creepers.core.registry.TCEntities;
import com.evandev.tolerable_creepers.core.registry.TCItems;
import com.evandev.tolerable_creepers.core.registry.TCParticles;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;

public class TolerableCreepersClient {

    public static void init() {
        ParticleProviderRegistry.register(TCParticles.CREEPER_SPORES, CreeperSporesParticle.Provider::new);
        ModelRegistry.registerSpecial(new ResourceLocation(Constants.MOD_ID, "entity/fire_bomb"));
        ModelRegistry.registerSpecial(new ResourceLocation(Constants.MOD_ID, "entity/spore_bomb"));

        EntityRendererRegistry.register(TCEntities.CREEPER_SPORES, NoopRenderer::new);
        EntityRendererRegistry.register(TCEntities.CREEPIE, CreepieRenderer::new);
        EntityRendererRegistry.register(TCEntities.SPORE_BARREL, SporeBarrelRenderer::new);
        EntityRendererRegistry.register(TCEntities.MISCHIEF_ARROW, MischiefArrowRenderer::new);
        EntityRendererRegistry.register(TCEntities.FIRE_BOMB, FireBombRenderer::new);
        EntityRendererRegistry.register(TCEntities.SPORE_BOMB, SporeBombRenderer::new);

        ItemPropertiesRegistry.register(
                Items.CROSSBOW,
                new ResourceLocation(Constants.MOD_ID, "mischief_arrow"),
                (itemStack, clientLevel, livingEntity, i) -> livingEntity != null
                        && CrossbowItem.isCharged(itemStack)
                        && CrossbowItem.containsChargedProjectile(itemStack, TCItems.MISCHIEF_ARROW.get())
                        ? 1.0F
                        : 0.0F
        );
    }

    public static void postInit() {
        ModelRegistry.registerSpecial(new ResourceLocation(Constants.MOD_ID, "entity/fire_bomb"));
        ModelRegistry.registerSpecial(new ResourceLocation(Constants.MOD_ID, "entity/spore_bomb"));
    }
}
