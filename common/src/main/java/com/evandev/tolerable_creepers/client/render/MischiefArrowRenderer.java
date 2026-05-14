package com.evandev.tolerable_creepers.client.render;

import com.evandev.tolerable_creepers.common.entity.MischiefArrow;
import com.evandev.tolerable_creepers.core.TolerableCreepers;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MischiefArrowRenderer extends ArrowRenderer<MischiefArrow> {

    public static final ResourceLocation MISCHIEF_ARROW_LOCATION = new ResourceLocation(Constants.MOD_ID, "textures/entity/projectiles/mischief_arrow.png");

    public MischiefArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(MischiefArrow entity) {
        return MISCHIEF_ARROW_LOCATION;
    }
}
