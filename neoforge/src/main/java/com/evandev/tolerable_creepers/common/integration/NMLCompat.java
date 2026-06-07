package com.evandev.tolerable_creepers.common.integration;

import com.evandev.tolerable_creepers.client.render.SporeBombRenderer;
import com.evandev.tolerable_creepers.common.TCNeoForgeRegistries;
import com.evandev.tolerable_creepers.common.entity.SporeBomb;
import com.evandev.tolerable_creepers.common.item.SporeBombItem;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.function.Supplier;

public class NMLCompat {

    public static Supplier<Item> SPORE_BOMB;
    public static Supplier<EntityType<SporeBomb>> SPORE_BOMB_ENTITY;

    public static void register() {
        SPORE_BOMB_ENTITY = TCNeoForgeRegistries.ENTITIES.register("spore_bomb", () ->
                EntityType.Builder.<SporeBomb>of(SporeBomb::new, MobCategory.MISC)
                        .sized(0.375F, 0.375F).clientTrackingRange(4).updateInterval(20).build("spore_bomb")
        );

        SPORE_BOMB = TCNeoForgeRegistries.ITEMS.register("spore_bomb", () -> new SporeBombItem(new Item.Properties().stacksTo(8)));
    }

    public static void commonSetup() {
        if (SPORE_BOMB != null) {
            DispenserBlock.registerBehavior(SPORE_BOMB.get(),
                    new ProjectileDispenseBehavior(SPORE_BOMB.get()));
        }
    }

    public static void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (SPORE_BOMB != null) {
            event.accept(SPORE_BOMB.get());
        }
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (SPORE_BOMB_ENTITY != null) {
            event.registerEntityRenderer(SPORE_BOMB_ENTITY.get(), SporeBombRenderer::new);
        }
    }
}