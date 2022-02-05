package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Motion", description = "Checks for repeatable motion.", type = "M", experimental = true)
public class MotionM extends Check {

    public MotionM(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.NEAR_WALL, ExemptType.NEARANVIL, ExemptType.WEB, ExemptType.PEARL, ExemptType.UNDERBLOCK, ExemptType.PISTON, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.CHUNK, ExemptType.VEHICLE, ExemptType.BOAT);
            final boolean invalid = deltaY == -lastDeltaY && deltaY != 0.0;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail("DeltaY: " + deltaY + " lDeltaY: " + lastDeltaY);
                }
            } else {
                resetBuffer();
            }
        }
    }
}
