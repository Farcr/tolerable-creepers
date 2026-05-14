package com.evandev.tolerable_creepers.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import com.evandev.tolerable_creepers.core.TolerableCreepers;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Supplier;

public class TCParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Constants.MOD_ID, Registry.PARTICLE_TYPE_REGISTRY);

    public static final RegistrySupplier<SimpleParticleType> CREEPER_SPORES = PARTICLES.register("creeper_spores", () -> new SimpleParticleType(false) {
    });
}
