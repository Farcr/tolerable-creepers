package com.evandev.tolerable_creepers.client.render;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.entity.Creepie;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CreepiePowerLayer extends EnergySwirlLayer<Creepie, CreepieModel> {

    private static final ResourceLocation POWER_LOCATION = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/creepie/creepie_armor.png");
    private final CreepieModel model;

    public CreepiePowerLayer(RenderLayerParent<Creepie, CreepieModel> renderLayerParent, EntityModelSet modelSet) {
        super(renderLayerParent);
        this.model = new CreepieModel(modelSet.bakeLayer(CreepieModel.LAYER_LOCATION_ARMOR));
    }

    @Override
    protected float xOffset(float tickCount) {
        return tickCount * 0.01F;
    }

    @Override
    protected @NotNull ResourceLocation getTextureLocation() {
        return POWER_LOCATION;
    }

    @Override
    protected @NotNull EntityModel<Creepie> model() {
        return this.model;
    }
}