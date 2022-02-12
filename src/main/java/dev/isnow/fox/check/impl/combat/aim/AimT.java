package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", description = "Checks for invalid rotations [YAW + PITCH].", type = "T")
public class AimT extends Check {
    public AimT(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean invalid = deltaYaw == 0.0F && deltaPitch >= 20.0F;

            if (invalid) {
                if (increaseBuffer() > 3) {
                    fail("DeltaPitch: " + deltaPitch + " DeltaYaw: " + deltaYaw);
                }
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }
}
