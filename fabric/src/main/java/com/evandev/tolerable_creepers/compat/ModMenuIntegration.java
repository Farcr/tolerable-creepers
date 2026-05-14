package com.evandev.tolerable_creepers.compat;

import com.evandev.tolerable_creepers.client.integration.ClothConfigIntegration;
import com.evandev.tolerable_creepers.platform.Services;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (Services.PLATFORM.isModLoaded("cloth-config")) {
            return ClothConfigIntegration::createScreen;
        }
        return null;
    }
}