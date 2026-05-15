package com.evandev.tolerable_creepers;

import com.evandev.tolerable_creepers.client.model.CreepieModel;
import com.evandev.tolerable_creepers.client.particle.CreeperSporesParticle;
import com.evandev.tolerable_creepers.client.render.*;
import com.evandev.tolerable_creepers.core.registry.TCEntities;
import com.evandev.tolerable_creepers.core.registry.TCItems;
import com.evandev.tolerable_creepers.core.registry.TCParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;

public class TolerableCreepersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(TCParticles.CREEPER_SPORES.get(), CreeperSporesParticle.Provider::new);

        EntityModelLayerRegistry.registerModelLayer(CreepieModel.LAYER_LOCATION, CreepieModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CreepieModel.LAYER_LOCATION_ARMOR, CreepieModel::createBodyLayer);

        EntityRendererRegistry.register(TCEntities.CREEPER_SPORES.get(), NoopRenderer::new);
        EntityRendererRegistry.register(TCEntities.CREEPIE.get(), CreepieRenderer::new);
        EntityRendererRegistry.register(TCEntities.SPORE_BARREL.get(), SporeBarrelRenderer::new);
        EntityRendererRegistry.register(TCEntities.MISCHIEF_ARROW.get(), MischiefArrowRenderer::new);
        EntityRendererRegistry.register(TCEntities.FIRE_BOMB.get(), FireBombRenderer::new);
        EntityRendererRegistry.register(TCEntities.SPORE_BOMB.get(), SporeBombRenderer::new);

        ItemProperties.register(Items.CROSSBOW, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "mischief_arrow"),
                (itemStack, clientLevel, livingEntity, i) -> {
                    if (livingEntity == null) return 0.0F;
                    ChargedProjectiles projectiles = itemStack.get(DataComponents.CHARGED_PROJECTILES);
                    return projectiles != null && projectiles.contains(TCItems.MISCHIEF_ARROW.get()) ? 1.0F : 0.0F;
                }
        );
    }
}