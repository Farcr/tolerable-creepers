package com.evandev.tolerable_creepers.core.registry;

import com.evandev.tolerable_creepers.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TCTags {
    public static TagKey<EntityType<?>> EXPLOSION_IMMUNE = registerEntity("explosion_immune");
    public static TagKey<EntityType<?>> EXPLOSION_PRONE = registerEntity("explosion_prone");

    public static TagKey<EntityType<?>> CREEPIE_AVOID = registerEntity("creepie_avoid");
    public static TagKey<EntityType<?>> CREEPIE_FRIENDS = registerEntity("creepie_friends");

    public static TagKey<Block> CREEPIE_REPELLENTS = registerBlock("creepie_repellents");
    public static TagKey<Block> CREEPIE_HIDING_SPOTS = registerBlock("creepie_hiding_spots");
    public static TagKey<Block> CREEPIE_PARTY_SPOTS = registerBlock("creepie_party_spots");
    public static TagKey<Block> CREEPIE_FORCE_PARTY_SPOTS = registerBlock("creepie_force_party_spots");

    private static TagKey<EntityType<?>> registerEntity(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }

    private static TagKey<Item> registerItem(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }

    private static TagKey<Block> registerBlock(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }

    public static void load() {
    }
}
