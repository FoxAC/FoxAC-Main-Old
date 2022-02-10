package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", description = "Checks for snappy rotations.", type = "E")
public final class AimE extends Check {
    private float lastDeltaYaw;
    private float lastLastDeltaYaw;

    public AimE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            
            final boolean exempt = isExempt(ExemptType.TELEPORT);
            if (exempt) {
                lastDeltaYaw = deltaYaw;
                lastLastDeltaYaw = deltaYaw;
                return;
            }

            final boolean invalid = deltaYaw < 1.5f && lastDeltaYaw > 30.0f && lastLastDeltaYaw < 1.5f;
            if (invalid && increaseBuffer() > 3) {
                fail("DeltaYaw: " + deltaYaw + " LastDeltaYaw: " + lastDeltaYaw);
                
                lastLastDeltaYaw = lastDeltaYaw;
                lastDeltaYaw = deltaYaw;
            }
        } else {
            decreaseBufferBy(0.25);
        }
    }
}