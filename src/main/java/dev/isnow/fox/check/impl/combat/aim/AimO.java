package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", description = "Checks if player is following AIM GCD properly. [Method 8]", type = "O", experimental = true)
public class AimO extends Check {
    public AimO(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && isExempt(ExemptType.COMBAT, ExemptType.PLACING)) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

            if (deltaPitch > 1.0 && !data.getPositionProcessor().isTeleported()) {
                final long expandedPitch = (long) (deltaPitch * MathUtil.EXPANDER);
                final long lastExpandedPitch = (long) (lastDeltaPitch * MathUtil.EXPANDER);

                final double divisorPitch = MathUtil.getGcd(expandedPitch, lastExpandedPitch);
                final double constantPitch = divisorPitch / MathUtil.EXPANDER;

                final double pitch = data.getRotationProcessor().getPitch();
                final double moduloPitch = Math.abs(pitch % constantPitch);

                if (moduloPitch < 1.2E-5) {
                    if (increaseBuffer() > 5) {
                        fail("GCD: " + divisorPitch);
                    }
                } else {
                    decreaseBufferBy(0.1);
                }
            }
        }
    }
}
