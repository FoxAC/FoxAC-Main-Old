

package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", type = "B", description = "Checks for smaller gcd than possible.")
public final class AimB extends Check {
    public AimB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

            final boolean cinematic = data.getRotationProcessor().isCinematic() || isExempt(ExemptType.CREATIVE, ExemptType.BUKKIT_PLACING, ExemptType.CINEMATIC_TIME);

            final long gcd = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));
            final boolean invalid = gcd < 131072L && deltaPitch > 0.5F && deltaPitch < 20.0F && !cinematic;

            if (invalid) {
                if (increaseBuffer() > 7) {
                    fail("GCD:" + gcd);
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
