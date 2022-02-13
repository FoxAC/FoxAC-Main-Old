package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", description = "Checks if player is following AIM GCD properly. [Method 7].", type = "U")
public class AimU extends Check {
    public AimU(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isRotation()) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();
            final float deltaYaw = data.getRotationProcessor().getDeltaPitch();

            final float gcd = (float) MathUtil.getGcd(deltaPitch, lastDeltaPitch);

            final boolean check = (Math.abs(deltaPitch) > 0.45 || Math.abs(deltaYaw) > 0.45)
                    && !isExempt(ExemptType.CINEMATIC_TIME) && deltaPitch < 15 && deltaYaw < 16;

            final double resYaw = deltaYaw % gcd;
            final double resPitch = deltaPitch % gcd;

            final double modulusResPitch = MathUtil.getReversedModulus(gcd, deltaPitch, resPitch);
            final double modulusResYaw = MathUtil.getReversedModulus(gcd, deltaYaw, resYaw);


            if ((Double.isNaN(modulusResPitch) || Double.isNaN(modulusResYaw)) && check) {
                if (++buffer > 13) {
                    fail("modulusPitch: " + modulusResPitch + " modulusYaw: " + modulusResYaw +
                            " deltaYaw: " + deltaYaw + " deltaPitch: " + deltaPitch);
                }
            } else if (buffer > 0) buffer -= 0.5D;
        }
    }
}
