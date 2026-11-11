package dev.lorleyan.ropebridge;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

/** Server-authoritative Anchor Shot impact handling. */
public final class AnchorShotEntity extends ArrowEntity {
    public AnchorShotEntity(World world, LivingEntity owner, ItemStack stack, ItemStack weaponStack) {
        super(world, owner, stack, weaponStack);
    }

    @Override
    protected void onBlockHit(BlockHitResult hit) {
        super.onBlockHit(hit);
        if (!getWorld().isClient) {
            BridgeBuilder.tryBuild(this, hit);
        }
    }
}
