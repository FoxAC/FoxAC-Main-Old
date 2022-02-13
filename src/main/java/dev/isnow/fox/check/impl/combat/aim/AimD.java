package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.data.processor.RotationProcessor;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", description = "Checks if player is following AIM GCD properly. [Method 8].", type = "D")
public class AimD
        extends Check {

    public AimD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final RotationProcessor processor = data.getRotationProcessor();

            final boolean cinematic = processor.isCinematic();

            if(cinematic && data.getRotationProcessor().getSensitivity() < 0)
                return;

            final boolean attack = data.getCombatProcessor().getLastAttackTick() < 10;

            final float deltaYaw = processor.getDeltaYaw();
            final float lastDeltaYaw = processor.getLastDeltaYaw();
            final float deltaPitch = processor.getDeltaPitch();

            final double divisorYaw = MathUtil.getGcd((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));

            final double gcdYaw = data.getRotationProcessor().getGcd() / divisorYaw;
            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 1 && deltaPitch < 1 && attack && !isExempt(ExemptType.CINEMATIC_TIME)) {
                if (gcdYaw > 9.9E-7) {
                    if (increaseBuffer() > 6) {
                        setBuffer(0);
                        fail("GCD: " + data.getRotationProcessor().getGcd());
                    }
                } else {
                    decreaseBufferBy(2);
                }
            }
        }
    }
}