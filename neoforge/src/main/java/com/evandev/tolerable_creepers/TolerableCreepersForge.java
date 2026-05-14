package com.evandev.tolerable_creepers;

import com.evandev.tolerable_creepers.client.ClientConfigSetup;
import com.evandev.tolerable_creepers.core.TolerableCreepers;
import com.evandev.tolerable_creepers.core.TolerableCreepersClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class TolerableCreepersForge {
    public TolerableCreepersForge(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register(modContainer);
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        TolerableCreepersClient.postInit();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        TolerableCreepers.postInit();
    }
}