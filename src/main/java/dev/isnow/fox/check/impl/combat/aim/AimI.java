package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", type = "I", description = "Checks for a valid sensitivity in the rotation packet.")
public class AimI extends Check {

    public AimI(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = this.data.getRotationProcessor().getDeltaPitch();
            final float lastDeltaPitch = this.data.getRotationProcessor().getLastDeltaPitch();
            final long expandedDeltaPitch = (long)(deltaPitch * MathUtil.EXPANDER);
            final long expandedLastDeltaPitch = (long)(lastDeltaPitch * MathUtil.EXPANDER);
            final long gcd = MathUtil.getGcd(expandedDeltaPitch, expandedLastDeltaPitch);
            final boolean exempt = deltaPitch == 0.0f || lastDeltaPitch == 0.0f || this.isExempt(ExemptType.CINEMATIC_TIME, ExemptType.CINEMATIC);
            if (!exempt && gcd < 131072L) {
                if (increaseBuffer() > 20.0) {
                    fail("gcd=" + gcd);
                }
            }
            else {
                setBuffer(Math.max(0.0, getBuffer() - 2.0));
            }
        }
    }
}
