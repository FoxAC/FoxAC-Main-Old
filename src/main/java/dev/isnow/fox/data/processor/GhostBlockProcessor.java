

package dev.isnow.fox.data.processor;

import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.util.BlockUtil;
import dev.isnow.fox.util.PlayerUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;


@Getter
public final class GhostBlockProcessor {

    private final PlayerData data;

    private boolean onGhostBlock;

    private int sinceServerPosTicks;

    private double ghostBlockflags;

    public GhostBlockProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleFlying() {
        if (!Config.GHOST_BLOCK_ENABLED) return;

        sinceServerPosTicks++;
        onGhostBlock = false;

        if(Config.GHOST_BLOCK_MODE== Mode.FOX) {
            if(PlayerUtil.isOnBoat(data) || data.getExemptProcessor().isExempt(ExemptType.VEHICLE, ExemptType.WEB, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.VELOCITY)) {
                return;
            }

            final boolean isBridingUp = data.getPositionProcessor().isPlacementUnder() && data.getPositionProcessor().getDeltaY() > 0.0;

            final boolean onGhostBlock = data.getPositionProcessor().isOnGround() && data.getPositionProcessor().getY() % 0.015625 < 0.03 && data.getPositionProcessor().isInAir();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final int airTicks = data.getPositionProcessor().getAirTicks();

            double predictedY = (lastDeltaY - 0.08) * 0.98F;
            if (Math.abs(predictedY) < 0.005) predictedY = 0.0;

            final boolean underGhostBlock = data.getPositionProcessor().getSinceBlockNearHeadTicks() > 3
                    && Math.abs(deltaY - ((-0.08) * 0.98F)) < 1E-5
                    && Math.abs(deltaY - predictedY) > 1E-5;

            this.onGhostBlock = onGhostBlock || underGhostBlock;
            if (onGhostBlock && airTicks > 13 && !isBridingUp) {
                handleGhostblock();
            }
        }
    }

    public void handleClientPosition() {
        if(Config.GHOST_BLOCK_MODE== Mode.FOX2) {
            if(PlayerUtil.isOnBoat(data) || sinceServerPosTicks < 3 ||data.getExemptProcessor().isExempt(ExemptType.CLIMBABLE, ExemptType.SLIME, ExemptType.VEHICLE, ExemptType.LIQUID, ExemptType.BUKKIT_PLACING, ExemptType.WEB)) {
                ghostBlockflags = 0;
                return;
            }

            boolean ground = data.getPositionProcessor().isOnGround() || data.getPositionProcessor().isLastOnGround();

            boolean serverYGround = data.getPositionProcessor().isMathematicallyOnGround() || data.getPositionProcessor().isLastMathematicallyOnGround();

            boolean serverGround = data.getPositionProcessor().isOnSolidGround() || data.getPositionProcessor().isLastOnSolidGround();

            if (ground && serverYGround && !serverGround) {
                onGhostBlock = true;
                if(++ghostBlockflags > 1) {
                    handleGhostblock();
                    ghostBlockflags = 0;
                }
            }
        }
    }

    public void handleServrPos() {
        sinceServerPosTicks = 0;
    }

    public void handleGhostblock() {
        data.getPlayer().teleport(getGroundLocation());
        AlertManager.sendVPNMessage(data.getPlayer().getName() + " Lagged Back for ghost blocks [EXPERIMENTAL] ClientAirTicks: " + data.getPositionProcessor().getClientAirTicks());
    }

    public Location getGroundLocation() {
        World world = data.getPlayer().getWorld();

        Location location = new Location(world, data.getPositionProcessor().getX(), data.getPositionProcessor().getY(), data.getPositionProcessor().getZ());
        int i = 0;
        while (!BlockUtil.getBlockAsync(location).getRelative(BlockFace.DOWN).getType().isSolid()
                && location.getY() != 0) {
            if (i++ > 20) {
                break;
            }
            location.add(0, -1, 0);
        }


        if (location.getY() == 0){
            return location;
        }

        location.add(0, .05, 0);

        location.setYaw(data.getRotationProcessor().getYaw());
        location.setPitch(data.getRotationProcessor().getPitch());

        return location;
    }

    public enum Mode {
        FOX, FOX2
    }
}
