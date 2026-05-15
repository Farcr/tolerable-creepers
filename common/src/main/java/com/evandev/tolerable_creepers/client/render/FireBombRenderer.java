package com.evandev.tolerable_creepers.client.render;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.entity.FireBomb;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class FireBombRenderer extends ThrowableBombRenderer<FireBomb> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "entity/fire_bomb");

    public FireBombRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getModelLocation(FireBomb entity) {
        return MODEL;
    }
}
