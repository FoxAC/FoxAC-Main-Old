package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", type = "C", description = "Checks for invalid sensitivity.")
public final class AimC extends Check {
    public AimC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final double sensitivity = data.getRotationProcessor().getSensitivity();

            final boolean exempt = data.getRotationProcessor().isCinematic() && isExempt(ExemptType.CINEMATIC_TIME);
            final boolean invalid = sensitivity < 0.0F;

            if (invalid && !exempt) {
                if (increaseBuffer() > 5) {
                    fail();
                }
            } else {
                decreaseBufferBy(2);
            }
        }
    }
}
