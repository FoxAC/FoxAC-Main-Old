

package dev.isnow.fox.data.processor;

import dev.isnow.fox.Fox;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.PlayerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;


@Getter
public final class GhostBlockProcessor {

    private final PlayerData data;

    private boolean onGhostBlock;
    private int ghostTicks;

    private Location lastGroundLocation;

    public GhostBlockProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleFlying() {
        if (!Config.GHOST_BLOCK_ENABLED) return;

        final boolean onGhostBlock = data.getPositionProcessor().isOnGround()
                && data.getPositionProcessor().getY() % 0.015625 < 0.03
                && data.getPositionProcessor().isInAir()
                && data.getPositionProcessor().getAirTicks() > 2;

        final double deltaY = data.getPositionProcessor().getDeltaY();
        final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

        double predictedY = (lastDeltaY - 0.08) * 0.98F;
        if (Math.abs(predictedY) < 0.005) predictedY = 0.0;

        final boolean underGhostBlock = data.getPositionProcessor().getSinceBlockNearHeadTicks() > 3
                && Math.abs(deltaY - ((-0.08) * 0.98F)) < 1E-5
                && Math.abs(deltaY - predictedY) > 1E-5;

        this.onGhostBlock = onGhostBlock || underGhostBlock;

        if (this.onGhostBlock) ++ghostTicks;
        else ghostTicks = 0;

        if (Config.GHOST_BLOCK_LAG_BACK) {
            int ticks = 1;

            if (Config.GHOST_BLOCK_MODE == Mode.FOX) {
                ticks = 4;
            }

            if (ghostTicks > ticks && lastGroundLocation != null) {
                Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () ->
                        data.getPlayer().teleport(lastGroundLocation, PlayerTeleportEvent.TeleportCause.PLUGIN));
            }
        }

        if (!data.getPositionProcessor().isInAir()
                && data.getPositionProcessor().isOnGround()) {
            final Location location = new Location(data.getPlayer().getWorld(), data.getPositionProcessor().getX(), data.getPositionProcessor().getY(), data.getPositionProcessor().getZ());

            location.setYaw(data.getRotationProcessor().getYaw());
            location.setPitch(data.getRotationProcessor().getPitch());

            lastGroundLocation = location;
        }
    }

    public enum Mode {
        FOX
    }
}
