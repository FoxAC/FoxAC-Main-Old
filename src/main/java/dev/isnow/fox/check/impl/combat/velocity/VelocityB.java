

package dev.isnow.fox.check.impl.combat.velocity;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Velocity", type = "B", description = "Checks for vertical velocity modifications (0%).")
public final class VelocityB extends Check {
    public VelocityB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            if (data.getPositionProcessor().getY() > data.getPositionProcessor().getLastY() || data.getPositionProcessor().getSinceVehicleTicks() < 2) {
                return;
            }

            double deltaY = data.getPositionProcessor().getDeltaY();

            double velocity = data.getVelocityProcessor().getVelocityY();

            if (data.getCombatProcessor().getHitTicks() <= 20) {
                if (data.getVelocityProcessor().getVelocityTicks() == 1 && data.getPositionProcessor().isOnGround() && data.getPositionProcessor().isLastOnGround()) {
                    if ((deltaY / velocity) == 0.0) {
                        if (increaseBuffer() > 2) {
                            fail("DELTAY: " + deltaY + " VELOCITY: " + velocity);
                        }
                    } else {
                        double buffer = 0;
                        buffer -= Math.min(getBuffer(), 0.0626f);
                        setBuffer(buffer);
                    }
                }
            }
        }
    }


}
