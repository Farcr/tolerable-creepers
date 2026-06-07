package com.evandev.tolerable_creepers.client.integration;

import com.evandev.tolerable_creepers.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigIntegration {

    public static Screen createScreen(Screen parent) {
        ModConfig config = ModConfig.get();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.tolerable_creepers.title"));

        builder.setSavingRunnable(ModConfig::save);

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.tolerable_creepers.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.tolerable_creepers.prevent_block_damage"), config.preventCreeperBlockDamage)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.tolerable_creepers.prevent_block_damage.tooltip"))
                .setSaveConsumer(newValue -> config.preventCreeperBlockDamage = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.tolerable_creepers.explosion_radius"), config.creepieExplosionRadius)
                .setDefaultValue(1)
                .setTooltip(Component.translatable("config.tolerable_creepers.explosion_radius.tooltip"))
                .setSaveConsumer(newValue -> config.creepieExplosionRadius = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.tolerable_creepers.spore_day_base"), config.sporeCountDayBase)
                .setDefaultValue(1)
                .setSaveConsumer(newValue -> config.sporeCountDayBase = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.tolerable_creepers.spore_day_random"), config.sporeCountDayRandom)
                .setDefaultValue(2)
                .setSaveConsumer(newValue -> config.sporeCountDayRandom = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.tolerable_creepers.spore_night_base"), config.sporeCountNightBase)
                .setDefaultValue(2)
                .setSaveConsumer(newValue -> config.sporeCountNightBase = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.tolerable_creepers.spore_night_random"), config.sporeCountNightRandom)
                .setDefaultValue(3)
                .setSaveConsumer(newValue -> config.sporeCountNightRandom = newValue)
                .build());

        return builder.build();
    }
}