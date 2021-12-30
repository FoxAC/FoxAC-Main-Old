

package dev.isnow.fox.data.processor;

import dev.isnow.fox.Fox;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.PlayerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;


@Getter
public final class GhostBlockProcessor {

    private final PlayerData data;

    private boolean onGhostBlock, yGround, lastYGround;

    private int ghostTicks;

    public GhostBlockProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleFlying() {
        if (!Config.GHOST_BLOCK_ENABLED) return;

        onGhostBlock = false;

        if(Config.GHOST_BLOCK_MODE== Mode.FOX) {
            if(PlayerUtil.isOnBoat(data) || data.getExemptProcessor().isExempt(ExemptType.VEHICLE, ExemptType.WEB, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.VELOCITY)) {
                return;
            }

            final boolean onGhostBlock = data.getPositionProcessor().isOnGround() && data.getPositionProcessor().getY() % 0.015625 < 0.03 && data.getPositionProcessor().isInAir();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            if (this.onGhostBlock) ++ghostTicks;
            else ghostTicks = 0;

            double predictedY = (lastDeltaY - 0.08) * 0.98F;
            if (Math.abs(predictedY) < 0.005) predictedY = 0.0;

            final boolean underGhostBlock = data.getPositionProcessor().getSinceBlockNearHeadTicks() > 3
                    && Math.abs(deltaY - ((-0.08) * 0.98F)) < 1E-5
                    && Math.abs(deltaY - predictedY) > 1E-5;

            this.onGhostBlock = onGhostBlock || underGhostBlock;

            if (onGhostBlock && ghostTicks > 5) {
                data.dragDown();
                data.getPlayer().sendMessage("Lagged Back for ghost blocks. [5170]");
            }
        }
    }

    public enum Mode {
        FOX
    }
}
