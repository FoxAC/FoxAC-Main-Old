package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", description = "Checks if player is following AIM GCD properly. [Method 2]", type = "F")
public class AimF
        extends Check {
    public AimF(PlayerData data) {
        super(data);
    }


    @Override
    public void handle(Packet packet) {
        if(packet.isRotation()) {
            final float deltaYaw = Math.abs(data.getRotationProcessor().getDeltaYaw() % 360F);

            if (deltaYaw > 0.09) {
                if (deltaYaw == Math.round(deltaYaw)) {
                    if (increaseBuffer() > 4) {
                        fail("deltaYaw: " + deltaYaw);
                    }
                } else {
                    decreaseBufferBy(3);
                }
            }
        }
    }
}