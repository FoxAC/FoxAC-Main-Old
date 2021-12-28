package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Aim", type = "J", description = "Checks for snappy rotations in the rotation packet.")
public class AimJ extends Check {

    private float lastDeltaYaw;
    private float lastLastDeltaYaw;

    public AimJ(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            if (this.isExempt(ExemptType.NEARHORSE, ExemptType.RESPAWN, ExemptType.TELEPORT, ExemptType.JOINED)) {
                return;
            }
            final float deltaYaw = this.data.getRotationProcessor().getDeltaYaw();
            if (deltaYaw < 5.0f && this.lastDeltaYaw > 30.0f && this.lastLastDeltaYaw < 5.0f) {
                final double low = (deltaYaw + this.lastLastDeltaYaw) / 2.0f;
                final double high = this.lastDeltaYaw;
                if(increaseBuffer() > 5) {
                    fail(String.format("low=%.2f, high=%.2f", low, high));
                } else {
                    decreaseBufferBy(0.10);
                }
            }
            this.lastLastDeltaYaw = this.lastDeltaYaw;
            this.lastDeltaYaw = deltaYaw;
        }
    }
}
