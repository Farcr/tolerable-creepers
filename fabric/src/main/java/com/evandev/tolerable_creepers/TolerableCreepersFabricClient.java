package com.evandev.tolerable_creepers;

import com.evandev.tolerable_creepers.core.TolerableCreepersClient;
import net.fabricmc.api.ClientModInitializer;

public class TolerableCreepersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TolerableCreepersClient.init();
        TolerableCreepersClient.postInit();
    }
}
