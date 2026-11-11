package dev.lorleyan.ropebridge;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Arrow ammo so Anchor Shots can be fired from an ordinary bow. */
public final class AnchorShotItem extends ArrowItem {
    public AnchorShotItem(Settings settings) {
        super(settings);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack,
                                                   LivingEntity shooter, ItemStack weaponStack) {
        return new AnchorShotEntity(world, shooter, stack, weaponStack);
    }
}
