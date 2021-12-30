package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.data.processor.PositionProcessor;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.Verbose;

@CheckInfo(name="Motion", type = "H", description="Checks if player is low hopping")

public final class MotionH extends Check {

    private final Verbose verbose = new Verbose();
    private boolean jumped;
    private boolean valid;

    public MotionH(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            PositionProcessor processor = data.getPositionProcessor();
            final double deltaY = processor.getDeltaY();
            final boolean exempt = isExempt(ExemptType.LAGGINGHARD, ExemptType.FLYING, ExemptType.UNDERBLOCK, ExemptType.TELEPORT_DELAY, ExemptType.CHUNK, ExemptType.LIQUID, ExemptType.VELOCITY,ExemptType.VELOCITY_ON_TICK, ExemptType.NEARSTAIRS, ExemptType.CLIMBABLE, ExemptType.SLIME, ExemptType.WEB, ExemptType.PLACING, ExemptType.PISTON);
            if (deltaY >= 0.01 && processor.isLastOnGround()) jumped = true;
            if (deltaY >= 0.4199999) valid = true;
            if (deltaY == 0 || exempt) {
                jumped = false;
                valid = false;
            }

            debug("deltaY=" + deltaY + ", jumped=" + jumped + ", valid=" + valid);
            if (!valid && deltaY != 0 && jumped) {
                if (verbose.flag(3, 500)) {
                    fail(deltaY);
                }

            }
        }
    }
}