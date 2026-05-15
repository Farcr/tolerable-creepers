package com.evandev.tolerable_creepers.common.item;

import com.evandev.tolerable_creepers.common.entity.MischiefArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class MischiefArrowItem extends ArrowItem {

    public MischiefArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(@NotNull Level level, @NotNull ItemStack ammo, @NotNull LivingEntity shooter, @Nullable ItemStack weapon) {
        return new MischiefArrow(level, shooter, ammo, weapon);
    }
}