package com.evandev.tolerable_creepers.common.item;

import com.evandev.tolerable_creepers.common.entity.SporeBomb;
import com.evandev.tolerable_creepers.common.entity.ThrowableBomb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SporeBombItem extends BombItem {

    public SporeBombItem(Properties properties) {
        super(properties);
    }

    @Override
    public ThrowableBomb createBomb(LivingEntity entity, Level level) {
        return new SporeBomb(entity, level);
    }
}
