package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", description = "Checks for invalid pitch rotations.", type = "V")
public class AimV extends Check {
    public AimV(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {

        if(packet.isRotation()) {
            final double accelPitch = data.getRotationProcessor().getDeltaPitch();

            final double pitch = data.getRotationProcessor().getPitch();

            final boolean exempt = !(pitch < 82.5F && pitch > -82.5F) || data.getRotationProcessor().getDeltaYaw() < 9.5D;

            if(accelPitch <= 0.001 && !exempt) {
                if(++buffer > 7)
                    fail();
            } else if(buffer > 0) buffer -= 0.1025;
        }

    }
}
