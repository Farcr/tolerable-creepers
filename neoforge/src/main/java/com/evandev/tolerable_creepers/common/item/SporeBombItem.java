package com.evandev.tolerable_creepers.common.item;

import com.evandev.tolerable_creepers.common.entity.SporeBomb;
import com.farcr.nomansland.common.entity.bombs.ThrowableBombEntity;
import com.farcr.nomansland.common.item.ThrowableBombItem;
import com.farcr.nomansland.common.registry.NMLSounds;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SporeBombItem extends ThrowableBombItem {

    public SporeBombItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ThrowableBombEntity asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
        return new SporeBomb(level, position.x(), position.y(), position.z());
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingTicks) {
        super.onUseTick(level, entity, stack, remainingTicks);
        int timeUsed = this.getUseDuration(stack, entity) - remainingTicks;
        if (timeUsed == DEFAULT_THROW_TIME && entity.isShiftKeyDown()) {
            entity.playSound(NMLSounds.BOMB_PRIMED.get(), 1.0F, 1.0F);
        }
    }
}