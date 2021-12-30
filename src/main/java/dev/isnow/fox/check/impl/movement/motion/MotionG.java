package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Motion", type = "G", description = "Checks for invalid fall motion.")
public class MotionG extends Check {

    public MotionG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying() && !isExempt(ExemptType.PEARL, ExemptType.NEARCACTUS, ExemptType.FIRE, ExemptType.RESPAWN, ExemptType.JOINED, ExemptType.UNDERBLOCK, ExemptType.UNDERBLOCKWAS)) {
            if(data.getPositionProcessor().isInAir() && data.getPositionProcessor().getDeltaY() < 0  && data.getPositionProcessor().getSinceGroundTicks() > 3 && data.getPositionProcessor().getSinceJumpingTicks() < 8) {
                debug(data.getPositionProcessor().getDeltaY());
                if(data.getPositionProcessor().getDeltaY() > -0.75 && data.getPositionProcessor().getDeltaY() < -0.156) {
                    fail("DeltaY: " + data.getPositionProcessor().getDeltaY());
                }
            }
        }
    }
}