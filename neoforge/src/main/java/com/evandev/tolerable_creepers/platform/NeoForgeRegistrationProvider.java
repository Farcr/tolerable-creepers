package com.evandev.tolerable_creepers.platform;

import com.evandev.tolerable_creepers.platform.registry.RegistrationProvider;
import com.evandev.tolerable_creepers.platform.registry.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.function.Supplier;

public class NeoForgeRegistrationProvider<T> implements RegistrationProvider<T> {

    private static final Set<DeferredRegister<?>> REGISTRIES = new HashSet<>();

    private final DeferredRegister<T> deferredRegister;
    private final Set<RegistryObject<T>> entries = new LinkedHashSet<>();

    public NeoForgeRegistrationProvider(ResourceKey<? extends Registry<T>> registry, String modId) {
        this.deferredRegister = DeferredRegister.create(registry, modId);
        REGISTRIES.add(this.deferredRegister);
    }

    public static void registerAll(IEventBus modEventBus) {
        for (DeferredRegister<?> register : REGISTRIES) {
            register.register(modEventBus);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
        var obj = deferredRegister.register(name, supplier);
        RegistryObject<I> registryObject = new RegistryObject<I>() {
            @Override
            public ResourceLocation getId() {
                return obj.getId();
            }

            @Override
            public I get() {
                return obj.get();
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