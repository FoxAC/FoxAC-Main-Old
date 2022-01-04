package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.BlockUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.armanimation.WrappedPacketInArmAnimation;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

@CheckInfo(name = "BadPackets", type = "P", description = "Checks if player is not interacting with the block properly.", experimental = true)
public class BadPacketsP extends Check {

    public long ls;

    public BadPacketsP(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isArmAnimation()) {
            ls = System.currentTimeMillis();
        }
        if(packet.isBukkitBlockPlace()) {
            if(ls != 0 &&(System.currentTimeMillis() - ls) >= 2500) {
                fail("No Swing, Last swing: " + (System.currentTimeMillis() - ls) / 1000 + "s");
            }
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
                    if (wrapper.getDirection() == Direction.DOWN) {
                        if (wrapper.getBlockPosition().getY() < data.getPositionProcessor().getY()) fail("Downwards, BlockY: " + wrapper.getBlockPosition().getY());
                    }
                }
            }

            if (wrapper.getDirection() == Direction.INVALID) fail("Invaid Direction");

            final float x = wrapper.getCursorPosition().get().getX();
            final float y = wrapper.getCursorPosition().get().getY();
            final float z = wrapper.getCursorPosition().get().getZ();
            for (final float value : new float[]{x, y, z}) {
                // The variable value cannot be larger than 1 or smaller than 0, as stated here.
                // https://wiki.vg/Protocol#Player_Block_Placement

                if (value > 1.0 || value < 0.0) fail("Invalid value [https://wiki.vg/Protocol#Player_Block_Placement], " + value);
            }
            final Block block = BlockUtil.getBlockAsync(blockLocation);
            if (block == null) return;

            if (block.getType().isSolid()) {

                if ((y - block.getX()) > 0.45) {
                    final Location location = new Location(data.getPlayer().getWorld(), x, y + data.getPlayer().getEyeHeight(), z);

                    final boolean invalid = !interactedCorrectly(blockLocation, location, direction);

                    if (invalid) {
                        if (increaseBuffer() > 1) {
                            fail();
                        }
                    } else {
                        resetBuffer();
                    }
                }
            }

        }


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
}
