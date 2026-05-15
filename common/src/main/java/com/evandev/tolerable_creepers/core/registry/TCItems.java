package com.evandev.tolerable_creepers.core.registry;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.item.CreeperSporesItem;
import com.evandev.tolerable_creepers.common.item.FireBombItem;
import com.evandev.tolerable_creepers.common.item.MischiefArrowItem;
import com.evandev.tolerable_creepers.common.item.SporeBombItem;
import com.evandev.tolerable_creepers.platform.registry.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

public class TCItems {
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Constants.MOD_ID);

    public static final Supplier<Item> CREEPIE_SPAWN_EGG = ITEMS.register("creepie_spawn_egg", () -> new SpawnEggItem(TCEntities.CREEPIE.get(), 894731, 3429663, new Item.Properties()));
    public static final Supplier<Item> CREEPER_SPORES = ITEMS.register("creeper_spores", () -> new CreeperSporesItem(new Item.Properties()));
    public static final Supplier<Item> MISCHIEF_ARROW = ITEMS.register("mischief_arrow", () -> new MischiefArrowItem(new Item.Properties()));
    public static final Supplier<Item> SPORE_BOMB = ITEMS.register("spore_bomb", () -> new SporeBombItem(new Item.Properties()));
    public static final Supplier<Item> SPORE_BARREL = ITEMS.register("spore_barrel", () -> new BlockItem(TCBlocks.SPORE_BARREL.get(), new Item.Properties()));

    public static void load() {
    }
}