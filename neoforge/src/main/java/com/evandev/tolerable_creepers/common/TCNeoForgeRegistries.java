package com.evandev.tolerable_creepers.common;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.integration.NMLCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TCNeoForgeRegistries {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Constants.MOD_ID);

    public static void register(IEventBus bus) {
        if (ModList.get().isLoaded("nomansland")) {
            NMLCompat.register();
        }

        ITEMS.register(bus);
        ENTITIES.register(bus);
    }
}