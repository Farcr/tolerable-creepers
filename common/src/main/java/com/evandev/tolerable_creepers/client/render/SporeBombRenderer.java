package com.evandev.tolerable_creepers.client.render;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.entity.SporeBomb;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SporeBombRenderer extends ThrowableBombRenderer<SporeBomb> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "entity/spore_bomb");

    public SporeBombRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getModelLocation(SporeBomb entity) {
        return MODEL;
    }
}
