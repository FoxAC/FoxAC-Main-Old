package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.data.processor.RotationProcessor;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.TimeUtils;

@CheckInfo(name = "Aim", description = "Checks for unlikely rotations.", type = "C")
public class AimC extends Check {
    public AimC(PlayerData data) {
        super(data);

    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean invalid = deltaYaw == 0.0F && deltaPitch >= 20.0F;

            if (invalid && !isExempt(ExemptType.JOINED)) {
                if (increaseBuffer() > 1) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }
}
