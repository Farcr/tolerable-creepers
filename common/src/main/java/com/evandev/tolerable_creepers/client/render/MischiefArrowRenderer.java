package com.evandev.tolerable_creepers.client.render;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.entity.MischiefArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MischiefArrowRenderer extends ArrowRenderer<MischiefArrow> {

    public static final ResourceLocation MISCHIEF_ARROW_LOCATION = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/projectiles/mischief_arrow.png");

    public MischiefArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MischiefArrow entity) {
        return MISCHIEF_ARROW_LOCATION;
    }
}
