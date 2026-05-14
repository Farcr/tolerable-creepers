package com.evandev.tolerable_creepers.client.render;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.entity.Creepie;
import gg.moonflower.pollen.api.render.animation.v1.entity.AnimatedGeometryEntityModel;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryBufferSource;
import net.minecraft.resources.ResourceLocation;

public class CreepieModelWrapper extends AnimatedGeometryEntityModel<Creepie> {
    private GeometryBufferSource buffer;

    public CreepieModelWrapper() {
        super(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "creepie_armor"));
    }

    public void setBuffer(GeometryBufferSource buffer) {
        this.buffer = buffer;
    }

    @Override
    public GeometryBufferSource getGeometryBuffers() {
        return buffer;
    }
}
