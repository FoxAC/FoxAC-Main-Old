package dev.isnow.fox.check.impl.movement.flight;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Flight", type = "C", description = "Checks if player isn't falling whilst in air.")
public final class FlightC extends Check {

    private double stableY;

    public FlightC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {

            final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.TELEPORT_DELAY, ExemptType.CREATIVE, ExemptType.PLACING);
            stableY = data.getPositionProcessor().getY() == data.getPositionProcessor().getLastY() && data.getPositionProcessor().isInAir() ? stableY + 1.0 : 0.0;
            if (stableY > 2.0 && !exempt && increaseBuffer() > 10) {
                fail("Y: " + stableY);
            } else {
                decreaseBuffer();
            }
        }
    }
}