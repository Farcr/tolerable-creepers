package com.evandev.tolerable_creepers.core.mixin;

import com.evandev.tolerable_creepers.common.entity.Creepie;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AzaleaBlock.class)
public abstract class AzaleaBlockMixin extends BushBlock {

    protected AzaleaBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext collisionContext) {
        if (collisionContext instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Creepie creepie && creepie.isHiding())
            return Shapes.empty();
        return super.getCollisionShape(state, level, pos, collisionContext);
    }
}
