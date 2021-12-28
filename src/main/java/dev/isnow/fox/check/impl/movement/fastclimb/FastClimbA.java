

package dev.isnow.fox.check.impl.movement.fastclimb;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "FastClimb", type = "A", description = "Checks if player is going faster than possible on a climbable.")
public final class FastClimbA extends Check {
    public FastClimbA(final PlayerData data) {
        super(data);
    }



    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double acceleration = deltaY - lastDeltaY;

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.PISTON, ExemptType.FLYING, ExemptType.BOAT, ExemptType.VEHICLE);
            final boolean invalid = ((float) deltaY) > 0.1176F && acceleration == 0.0 && data.getPositionProcessor().isOnClimbable();

            if (invalid && !exempt) {
                fail();
            }
        }
    }
}
