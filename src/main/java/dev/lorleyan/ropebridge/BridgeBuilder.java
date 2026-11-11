package dev.lorleyan.ropebridge;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/** Validates the two supports and lays a lightly sagging, walkable first-version bridge. */
final class BridgeBuilder {
    static final int START_SEARCH_BLOCKS = 3;
    static final int MAX_HORIZONTAL_SPAN = 16;
    static final int MAX_HEIGHT_DIFFERENCE = 3;
    private static final double SAG_BLOCKS = 1.25;

    private BridgeBuilder() {}

    static void tryBuild(ArrowEntity arrow, BlockHitResult hit) {
        World world = arrow.getWorld();
        Entity owner = arrow.getOwner();
        if (!(owner instanceof PlayerEntity player)) return;

        Direction facing = player.getHorizontalFacing();
        BlockPos startSupport = findStartSupport(world, player, facing);
        if (startSupport == null) {
            tell(player, "rope_bridge.message.no_start_anchor");
            return;
        }

        BlockPos endSupport = hit.getBlockPos();
        Direction endFace = hit.getSide();
        if (!endFace.getAxis().isHorizontal() || !isSolidSupport(world, endSupport)) {
            tell(player, "rope_bridge.message.no_end_anchor");
            return;
        }
        if (!clearSide(world, endSupport, endFace)) {
            tell(player, "rope_bridge.message.no_end_anchor");
            return;
        }

        Vec3d start = edgePoint(startSupport, facing);
        Vec3d end = edgePoint(endSupport, endFace);
        double horizontalDistance = Math.hypot(end.x - start.x, end.z - start.z);
        int heightDifference = Math.abs((int) Math.floor(start.y) - (int) Math.floor(end.y));
        if (horizontalDistance < 2 || horizontalDistance > MAX_HORIZONTAL_SPAN
                || heightDifference > MAX_HEIGHT_DIFFERENCE) {
            tell(player, "rope_bridge.message.out_of_range");
            return;
        }

        List<BlockPos> planks = samplePath(world, start, end);
        if (planks == null) {
            tell(player, "rope_bridge.message.path_blocked");
            return;
        }

        BlockState slab = Blocks.OAK_SLAB.getDefaultState().with(SlabType.TYPE, SlabType.BOTTOM);
        for (BlockPos pos : planks) {
            world.setBlockState(pos, slab);
        }
        tell(player, "rope_bridge.message.created");
        arrow.discard();
    }

    private static BlockPos findStartSupport(World world, PlayerEntity player, Direction facing) {
        BlockPos feet = BlockPos.ofFloored(player.getX(), player.getY(), player.getZ());
        BlockPos supportBase = feet.down();
        BlockPos last = null;
        for (int distance = 1; distance <= START_SEARCH_BLOCKS; distance++) {
            BlockPos candidate = supportBase.offset(facing, distance);
            if (isSolidSupport(world, candidate) && clearSide(world, candidate, facing)) {
                last = candidate;
            }
        }
        return last;
    }

    private static boolean isSolidSupport(World world, BlockPos pos) {
        return world.getBlockState(pos).isSolidBlock(world, pos);
    }

    private static boolean clearSide(World world, BlockPos support, Direction direction) {
        BlockPos adjacent = support.offset(direction);
        return world.getBlockState(adjacent).getCollisionShape(world, adjacent).isEmpty();
    }

    private static Vec3d edgePoint(BlockPos support, Direction direction) {
        return new Vec3d(support.getX() + 0.5 + direction.getOffsetX() * 0.5,
                support.getY() + 1.0,
                support.getZ() + 0.5 + direction.getOffsetZ() * 0.5);
    }

    /** Returns one plank position per step, or null if the plank volume intersects terrain. */
    private static List<BlockPos> samplePath(World world, Vec3d start, Vec3d end) {
        double horizontal = Math.hypot(end.x - start.x, end.z - start.z);
        int steps = Math.max(2, MathHelper.ceil(horizontal * 2));
        List<BlockPos> result = new ArrayList<>();
        BlockPos previous = null;
        for (int i = 1; i < steps; i++) {
            double t = (double) i / steps;
            double x = MathHelper.lerp(t, start.x, end.x);
            double z = MathHelper.lerp(t, start.z, end.z);
            double y = MathHelper.lerp(t, start.y, end.y) - SAG_BLOCKS * 4 * t * (1 - t);
            BlockPos pos = BlockPos.ofFloored(x, y - 0.25, z);
            if (pos.equals(previous)) continue;
            previous = pos;

            BlockState existing = world.getBlockState(pos);
            if (!existing.getCollisionShape(world, pos).isEmpty()
                    && !existing.getFluidState().isStill()) {
                return null;
            }
            result.add(pos);
        }
        return result.isEmpty() ? null : result;
    }

    private static void tell(PlayerEntity player, String translationKey) {
        if (player instanceof ServerPlayerEntity) {
            player.sendMessage(Text.translatable(translationKey), true);
        }
    }
}
