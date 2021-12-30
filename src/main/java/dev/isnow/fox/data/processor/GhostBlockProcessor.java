

package dev.isnow.fox.data.processor;

import dev.isnow.fox.Fox;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.PlayerUtil;
import lombok.Getter;


@Getter
public final class GhostBlockProcessor {

    private final PlayerData data;

    private double flags;

    private boolean onGhostBlock;

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

            boolean ground = data.getPositionProcessor().isOnGround() || data.getPositionProcessor().isLastOnGround();

            boolean serverPositionGround = data.getPositionProcessor().isServerYGround()
                    || data.getPositionProcessor().isLastPositionYGround();

            boolean serverGround = data.getPositionProcessor().isOnGroundCollided();

            if (ground && serverPositionGround
                    && !serverGround) {


                if (++flags > 2) {
                    onGhostBlock = true;
                    data.dragDown();
                    data.getPlayer().sendMessage("Lagged Back for ghost blocks. [5170]");
                    flags = 0;
                }
            }
        }
    }

    public enum Mode {
        FOX
    }
}
