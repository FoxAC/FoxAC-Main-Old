package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.PlayerUtil;
import dev.isnow.fox.util.type.AABB;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.Queue;

@CheckInfo(name = "BadPackets", type = "P", description = "Checks if player is not interacting with the block properly.", experimental = true)
public class BadPacketsP extends Check {

    public long ls;

    private final Queue<Integer> delays = new LinkedList<>();

    private int movements;

    private WrappedPacketInBlockPlace wrapper = null;


    public BadPacketsP(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            ++movements;
            if (wrapper != null) {
                final Vector eyeLocation = data.getPlayer().getEyeLocation().toVector();
                final Vector blockLocation = new Vector(
                        wrapper.getBlockPosition().getX(),
                        wrapper.getBlockPosition().getY(),
                        wrapper.getBlockPosition().getZ()
                );

                final Vector directionToDestination = blockLocation.clone().subtract(eyeLocation);
                final Vector playerDirection = data.getPlayer().getEyeLocation().getDirection();

                final float angle = directionToDestination.angle(playerDirection);
                final float distance = (float) eyeLocation.distance(blockLocation);

                final boolean exempt = blockLocation.getX() == -1.0 && blockLocation.getY() == -1.0 && blockLocation.getZ() == -1.0
                        || isExempt(ExemptType.GHOST_BLOCK, ExemptType.LAGGING, ExemptType.TELEPORT_DELAY);
                final boolean invalid = angle > 1.0F && distance > 1.5;

                if (invalid && !exempt) {
                    if (increaseBuffer() > 3) {
                        fail("AngleCos: " + Math.acos(angle));
                    }
                } else {
                    decreaseBuffer();
                }
            }

            wrapper = null;
        }
        if(packet.isBlockPlace()) {
            wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
        }
        if(packet.isArmAnimation()) {
            ls = System.currentTimeMillis();
        }
        if(packet.isBukkitBlockPlace()) {
            if(ls != 0 &&(System.currentTimeMillis() - ls) >= 2500) {
                fail("No Swing, Last swing: " + (System.currentTimeMillis() - ls) / 1000 + "s");
            }
        }
        if(packet.isBlockDig()) {
            WrappedPacketInBlockDig wrappedPacketInBlockDig = new WrappedPacketInBlockDig(packet.getRawPacket());
            Vector pos = getHeadPosition();
            Vector dir = MathUtil.getDirection(data.getRotationProcessor().getYaw(), data.getRotationProcessor().getPitch());
            Vector extraDir = MathUtil.getDirection(data.getRotationProcessor().getYaw() + data.getRotationProcessor().getDeltaYaw(),  data.getRotationProcessor().getPitch() +  data.getRotationProcessor().getDeltaPitch());
            if(wrappedPacketInBlockDig.getDigType() != WrappedPacketInBlockDig.PlayerDigType.STOP_DESTROY_BLOCK) {
                return;
            }
            final Location blockLocation = new Location(
                    data.getPlayer().getWorld(),
                    wrappedPacketInBlockDig.getBlockPosition().getX(),
                    wrappedPacketInBlockDig.getBlockPosition().getY(),
                    wrappedPacketInBlockDig.getBlockPosition().getZ()
            );
            Vector min = blockLocation.toVector();
            Vector max = blockLocation.toVector().clone().add(new Vector(1, 1, 1));
            AABB targetAABB = new AABB(min, max);

            if(!targetAABB.betweenRays(pos, dir, extraDir) && !isRight(min, max)) {
                fail("BoundingBox Not between vectors, Min: " + min + " Max: " + max);
            }
        }
        if(packet.isBlockPlace()) {

            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
            final Direction direction = wrapper.getDirection();
            final Location blockLocation = new Location(
                    data.getPlayer().getWorld(),
                    wrapper.getBlockPosition().getX(),
                    wrapper.getBlockPosition().getY(),
                    wrapper.getBlockPosition().getZ()
            );
            final Location eyeLocation = data.getPlayer().getEyeLocation();
            final Location blockAgainstLocation = getBlockAgainst(direction, blockLocation);
            final boolean validInteraction = interactedCorrectly(blockAgainstLocation, eyeLocation, direction);
            if (!validInteraction) {
                assert direction != null;
                fail("Invalid interact, FaceValue: " + direction.getFaceValue());
            }

            if (!(wrapper.getBlockPosition().getX() == 1 && wrapper.getBlockPosition().getY() == 1 && wrapper.getBlockPosition().getZ() == 1)) {
                if (data.getPlayer().getItemInHand().getType().isBlock()) {
                    if (direction == Direction.DOWN) {
                        if (wrapper.getBlockPosition().getY() < data.getPositionProcessor().getY()) fail("Downwards, BlockY: " + wrapper.getBlockPosition().getY());
                    }
                }
            }

            if (direction == Direction.INVALID) fail("Invaid Direction");

            final float x = wrapper.getCursorPosition().get().getX();
            final float y = wrapper.getCursorPosition().get().getY();
            final float z = wrapper.getCursorPosition().get().getZ();

            for (final float value : new float[]{x, y, z}) {
                if (value > 1.0 || value < 0.0) fail("Invalid value [https://wiki.vg/Protocol#Player_Block_Placement], " + value);
            }

            if (movements < 10) {
                if (delays.add(movements) && delays.size() == 35) {
                    double avg = MathUtil.getAverage(delays);
                    double stDev = MathUtil.getStandardDeviation(delays);

                    if (avg < 4 && stDev < 0.15) {
                        fail("FastPlace");
                    }

                    delays.clear();
                }
            }

            movements = 0;

            if(direction == Direction.UP || direction == Direction.DOWN) {
                return;
            }

            double limit = 0.270;
            limit = data.getPlayer().hasPotionEffect(PotionEffectType.SPEED) ? limit + ((PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.05)) : limit;

            debug(data.getPositionProcessor().getDeltaXZ());
            final boolean invalid = data.getPositionProcessor().getDeltaXZ() > limit && isBridgingV2();
            if(invalid) {
                fail("Sprinting while bridging, DeltaXZ: " + data.getPositionProcessor().getDeltaXZ());
            }

        }

    }

    public boolean isBridgingV2() {
        return data.getPlayer().getLocation().clone().subtract(0, 2, 0).getBlock().getType() == Material.AIR && data.getPlayer().getLocation().clone().subtract(0, 1, 0).getBlock().getType().isSolid() && data.getRotationProcessor().getPitch() > 70 && data.getRotationProcessor().getPitch() < 81;
    }

    private Location getBlockAgainst(final Direction direction, final Location blockLocation) {
        if (Direction.UP.equals(direction)) {
            return blockLocation.clone().add(0, -1, 0);
        } else if (Direction.DOWN.equals(direction)) {
            return blockLocation.clone().add(0, 1, 0);
        } else if (Direction.EAST.equals(direction) || Direction.SOUTH.equals(direction)) {
            return blockLocation;
        } else if (Direction.WEST.equals(direction)) {
            return blockLocation.clone().add(1, 0, 0);
        } else if (Direction.NORTH.equals(direction)) {
            return blockLocation.clone().add(0, 0, 1);
        }
        return null;
    }
    private boolean interactedCorrectly(Location block, Location player, Direction face) {
        if (Direction.UP.equals(face)) {
            return player.getY() > block.getY();
        } else if (Direction.DOWN.equals(face)) {
            return player.getY() < block.getY();
        } else if (Direction.WEST.equals(face)) {
            return player.getX() < block.getX();
        } else if (Direction.EAST.equals(face)) {
            return player.getX() > block.getX();
        } else if (Direction.NORTH.equals(face)) {
            return player.getZ() < block.getZ();
        } else if (Direction.SOUTH.equals(face)) {
            return player.getZ() > block.getZ();
        }
        return true;
    }

    public boolean isRight(Vector min, Vector max) {
        return (min.getX() - 1) == max.getX() && (min.getY() - 1) == max.getY() && (min.getZ() - 1) == max.getZ();
    }

    public Vector getHeadPosition() {
        Vector add = new Vector(0, 0, 0);
        add.setY(data.getActionProcessor().isSneaking() ? 1.54 : 1.62);
        return data.getPlayer().getLocation().clone().add(add).toVector();
    }

}
