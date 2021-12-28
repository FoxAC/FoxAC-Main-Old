

package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", type = "E", description = "Checks for snappy rotations.")
public final class AimE extends Check {

    private float lastDeltaYaw, lastLastDeltaYaw;

    public AimE(final PlayerData data) {
        super(data);
    }



    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean exempt = isExempt(ExemptType.TELEPORT_DELAY, ExemptType.TELEPORT);
            final boolean invalid = deltaYaw < 2.5F && lastDeltaYaw > 20F && lastLastDeltaYaw < 2.5F;

            if (exempt) {
                lastDeltaYaw = deltaYaw;
                lastLastDeltaYaw = deltaYaw;
            }

            if (invalid && !exempt && increaseBuffer() > 3) fail();
            else {
                decreaseBuffer();
            }
            this.lastLastDeltaYaw = lastDeltaYaw;
            this.lastDeltaYaw = deltaYaw;
        }
    }
}
