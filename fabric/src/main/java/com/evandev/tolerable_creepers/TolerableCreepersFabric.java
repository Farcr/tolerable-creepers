package com.evandev.tolerable_creepers;

import com.evandev.tolerable_creepers.core.TolerableCreepers;
import net.fabricmc.api.ModInitializer;

public class TolerableCreepersFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        TolerableCreepers.init();
        TolerableCreepers.postInit();
    }
}