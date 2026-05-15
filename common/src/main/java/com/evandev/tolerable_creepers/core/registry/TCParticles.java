package com.evandev.tolerable_creepers.core.registry;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.platform.registry.RegistrationProvider;
import com.evandev.tolerable_creepers.platform.registry.RegistryObject;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

public class TCParticles {
    public static final RegistrationProvider<ParticleType<?>> PARTICLES = RegistrationProvider.get(Registries.PARTICLE_TYPE, Constants.MOD_ID);

    public static final RegistryObject<SimpleParticleType> CREEPER_SPORES = PARTICLES.register("creeper_spores", () -> new SimpleParticleType(false) {
    });
}
