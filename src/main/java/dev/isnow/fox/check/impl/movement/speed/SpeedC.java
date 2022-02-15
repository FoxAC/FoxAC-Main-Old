package dev.isnow.fox.check.impl.movement.speed;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Speed", type = "C", description = "Checks for invalid deceleration when rotating, by Nik.")
public class SpeedC extends Check {
    public SpeedC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isRotation()) {

            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double accel = Math.abs(deltaXZ - lastDeltaXZ);

            final double squaredAccel = accel * 100;

            if(deltaYaw > 1.5F && deltaXZ > .150 && squaredAccel < 1.0E-5 && !isExempt(ExemptType.CLIMBABLE, ExemptType.JOINED, ExemptType.TELEPORT) && increaseBuffer() > 3) {
                fail("DeltaYaw: " + deltaYaw + " DeltaXZ: " + deltaXZ + " Acceleration: " + accel);
            } else {
                decreaseBufferBy(0.2);
            }
        }

    }
}
