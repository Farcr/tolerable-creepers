package com.evandev.tolerable_creepers.core.registry;

import com.evandev.tolerable_creepers.Constants;
import com.evandev.tolerable_creepers.common.block.ItemFlowerPotBlock;
import com.evandev.tolerable_creepers.common.block.SporeBarrelBlock;
import com.evandev.tolerable_creepers.platform.registry.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class TCBlocks {
    public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK, Constants.MOD_ID);

    public static final Supplier<Block> SPORE_BARREL = BLOCKS.register("spore_barrel",
            () -> new SporeBarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL))
    );

    public static final Supplier<Block> POTTED_CREEPER_SPORES = BLOCKS.register("potted_creeper_spores",
            () -> new ItemFlowerPotBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion())
    );

    public static void load() {
    }
}