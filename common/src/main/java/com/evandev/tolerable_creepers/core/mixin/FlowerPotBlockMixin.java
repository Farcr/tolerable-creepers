package com.evandev.tolerable_creepers.core.mixin;

import com.evandev.tolerable_creepers.core.registry.TCBlocks;
import com.evandev.tolerable_creepers.core.registry.TCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowerPotBlock.class)
public class FlowerPotBlockMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    public void placeCreeperSpores(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> cir) {

        if (state.is(Blocks.FLOWER_POT) && stack.is(TCItems.CREEPER_SPORES.get())) {
            level.setBlock(pos, TCBlocks.POTTED_CREEPER_SPORES.get().defaultBlockState(), 3);
            player.awardStat(Stats.POT_FLOWER);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            cir.setReturnValue(ItemInteractionResult.sidedSuccess(level.isClientSide));
        }
    }
}