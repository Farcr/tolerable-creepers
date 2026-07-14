package com.evandev.tolerable_creepers.client.render;

import com.evandev.tolerable_creepers.client.model.CreepieModel;
import com.evandev.tolerable_creepers.common.entity.Creepie;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class CreepieRenderer extends MobRenderer<Creepie, CreepieModel<Creepie>> {

    public CreepieRenderer(EntityRendererProvider.Context context) {
        super(context, new CreepieModel<>(context.bakeLayer(CreepieModel.LAYER_LOCATION)), 0.25F);
    }

    @Override
    protected void scale(Creepie creepie, PoseStack poseStack, float partialTicks) {
        float g = creepie.getSwelling(partialTicks) * (30.0F / 15.0F);
        float h = 1.0F + Mth.sin(g * 100.0F) * g * 0.01F;
        g = Mth.clamp(g, 0.0F, 1.0F);
        g *= g * g;
        float i = (1.0F + g * 0.4F) * h;
        float j = (1.0F + g * 0.1F) / h;
        poseStack.scale(i, j, i);
    }

    @Override
    protected float getWhiteOverlayProgress(Creepie creepie, float partialTicks) {
        float swell = creepie.getSwelling(partialTicks) * (30.0F / 15.0F);
        return (int) (swell * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swell, 0.5F, 1.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Creepie creepie) {
        return creepie.getCreepieType().getTexture();
    }
}