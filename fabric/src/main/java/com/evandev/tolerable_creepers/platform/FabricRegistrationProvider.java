package com.evandev.tolerable_creepers.platform;

import com.evandev.tolerable_creepers.platform.registry.RegistrationProvider;
import com.evandev.tolerable_creepers.platform.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public class FabricRegistrationProvider<T> implements RegistrationProvider<T> {
    private final Registry<T> registry;
    private final String modId;
    private final Set<RegistryObject<T>> entries = new LinkedHashSet<>();

    @SuppressWarnings({"unchecked"})
    public FabricRegistrationProvider(ResourceKey<? extends Registry<T>> resourceKey, String modId) {
        this.registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(resourceKey.location());
        this.modId = modId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
        I registered = Registry.register(registry, id, supplier.get());

        RegistryObject<I> registryObject = new RegistryObject<>() {
            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public I get() {
                return registered;
            }
        };
        this.entries.add((RegistryObject<T>) registryObject);
        return registryObject;
    }

    @Override
    public Collection<RegistryObject<T>> getEntries() {
        return Collections.unmodifiableCollection(this.entries);
    }
}