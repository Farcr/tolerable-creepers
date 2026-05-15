package com.evandev.tolerable_creepers.client.render;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.entity.SporeBomb;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class SporeBombRenderer extends ThrowableBombRenderer<SporeBomb> {

    private static final ModelResourceLocation MODEL = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "entity/spore_bomb"), "standalone");

    public SporeBombRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ModelResourceLocation getModelLocation(SporeBomb entity) {
        return MODEL;
    }
}