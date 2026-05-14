package com.evandev.tolerable_creepers.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import com.evandev.tolerable_creepers.common.item.CreeperSporesItem;
import com.evandev.tolerable_creepers.common.item.FireBombItem;
import com.evandev.tolerable_creepers.common.item.MischiefArrowItem;
import com.evandev.tolerable_creepers.common.item.SporeBombItem;
import com.evandev.tolerable_creepers.core.TolerableCreepers;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class TCItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID, Registry.ITEM_REGISTRY);

    public static final Supplier<Item> CREEPER_SPORES = ITEMS.register("creeper_spores", () -> new CreeperSporesItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final Supplier<Item> MISCHIEF_ARROW = ITEMS.register("mischief_arrow", () -> new MischiefArrowItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final Supplier<Item> FIRE_BOMB = ITEMS.register("fire_bomb", () -> new FireBombItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static final Supplier<Item> SPORE_BOMB = ITEMS.register("spore_bomb", () -> new SporeBombItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
}
