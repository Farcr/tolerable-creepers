package com.evandev.tolerable_creepers.platform.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface RegistryObject<T> extends Supplier<T> {
    ResourceLocation getId();

    T get();
}