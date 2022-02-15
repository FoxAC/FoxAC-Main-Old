package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Motion", type = "C", description = "Checks for invalid strafing.")
public final class  MotionC extends Check {


    public MotionC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isPosition()) {
            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            final int airTicks = data.getPositionProcessor().getClientAirTicks();

            final double blockSlipperiness = 0.91F;
            final double attributeSpeed = 0.026;

            final double predictedDeltaX = lastDeltaX * blockSlipperiness;
            final double predictedDeltaZ = lastDeltaZ * blockSlipperiness;

            final double diffX = Math.abs(deltaX - predictedDeltaX);
            final double diffZ = Math.abs(deltaZ - predictedDeltaZ);

            final double diffXZ = Math.hypot(diffX, diffZ);

            final boolean exempt = isExempt(ExemptType.TELEPORT_DELAY, ExemptType.PISTON, ExemptType.FLYING,
                    ExemptType.UNDERBLOCK, ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.VELOCITY,
                    ExemptType.CHUNK, ExemptType.NEAR_WALL, ExemptType.GHOST_BLOCK);
            final boolean invalid = (diffX > attributeSpeed || diffZ > attributeSpeed) && deltaXZ > .05 && airTicks > 2;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail("diffXZ: " + diffXZ);
                }
            } else {
                decreaseBufferBy(0.1);
            }
        }
    }
}