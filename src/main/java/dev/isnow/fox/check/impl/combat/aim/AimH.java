

package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", type = "H", description = "Checks for unlikely yaw deltas.")
public final class AimH extends Check {

    public AimH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean invalid = deltaYaw == 0.0F && deltaPitch >= 20.0F;

            if (invalid) {
                if (increaseBuffer() > 1) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }
}
