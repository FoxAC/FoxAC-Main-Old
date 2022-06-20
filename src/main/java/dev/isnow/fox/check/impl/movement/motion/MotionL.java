package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Motion", description = "Checks for invalid motion in web.", type = "L")
public class MotionL extends Check {

    public MotionL(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying() && data.getPositionProcessor().isInWeb()) {
            final double acceleration = data.getPositionProcessor().getDeltaY();
            if(isExempt(ExemptType.FLYING, ExemptType.SLIME_ON_TICK, ExemptType.JOINED, ExemptType.COMBAT, ExemptType.UPWARDS_VEL, ExemptType.PEARL)) {
                return;
            }
            boolean invalid = acceleration > 0.2 && data.getPositionProcessor().getWebTicks() > 12 && !data.getPositionProcessor().isLastOnGround();
            if(invalid) {
                debug(data.getPositionProcessor().getWebTicks());
                fail("DeltaY: " + acceleration);
            }
        }
    }
}
