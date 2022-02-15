package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", description = "Checks if player is following AIM GCD properly. [Method 10].", type = "W", experimental = true)
public class AimW extends Check {

    public AimW(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final boolean cinematic = isExempt(ExemptType.CINEMATIC);

            if(cinematic && data.getRotationProcessor().getSensitivity() < 0)
                return;

            final boolean attack = data.getCombatProcessor().getLastAttackTick() < 10;

            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float lastDeltaYaw = data.getRotationProcessor().getLastDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final double divisorYaw = MathUtil.getGcd((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));

            final float gcd = data.getRotationProcessor().getGcd();

            final double gcdDivided = gcd / divisorYaw;
            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 1 && deltaPitch < 1 && attack) {
                if (gcdDivided > 9.9E-7) {
                    if (buffer++ > 6) {
                        buffer = 0;
                        fail("GCD: " + gcd);
                    }
                } else {
                    decreaseBufferBy(2);
                }
            }
        }
    }
}
