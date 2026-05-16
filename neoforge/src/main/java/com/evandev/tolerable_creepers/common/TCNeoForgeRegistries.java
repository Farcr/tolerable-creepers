package com.evandev.tolerable_creepers.common;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.entity.SporeBomb;
import com.evandev.tolerable_creepers.common.item.SporeBombItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class TCNeoForgeRegistries {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Constants.MOD_ID);

    public static Supplier<Item> SPORE_BOMB;
    public static Supplier<EntityType<SporeBomb>> SPORE_BOMB_ENTITY;

    public static void register(IEventBus bus) {
        if (ModList.get().isLoaded("nomansland")) {
            SPORE_BOMB_ENTITY = ENTITIES.register("spore_bomb", () ->
                    EntityType.Builder.<SporeBomb>of(SporeBomb::new, MobCategory.MISC)
                            .sized(0.375F, 0.375F).clientTrackingRange(4).updateInterval(20).build("spore_bomb")
            );

            SPORE_BOMB = ITEMS.register("spore_bomb", () -> new SporeBombItem(new Item.Properties().stacksTo(8)));
        }

        ITEMS.register(bus);
        ENTITIES.register(bus);
    }
}