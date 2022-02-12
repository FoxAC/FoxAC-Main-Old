package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Aim", description = "Checks if player is following AIM GCD properly. [Method 6]", type = "M")
public final class AimM extends Check {
    private float lastPitchDifference;
    private float lastYawDifference;

    private final double offset = Math.pow(2.0, 24.0);

    public AimM(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            WrappedPacketInUseEntity useEntityPacket = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (useEntityPacket.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {

                float pitchDifference = Math.abs(data.getRotationProcessor().getPitch()
                        - data.getRotationProcessor().getLastPitch());

                float yawDifference = Math.abs(data.getRotationProcessor().getYaw()
                        - data.getRotationProcessor().getLastYaw());

                float yawAccel = Math.abs(pitchDifference - lastPitchDifference);
                float pitchAccel = Math.abs(yawDifference - lastYawDifference);

                long gcd = MathUtil.getGcd((long) (pitchDifference * offset), (long) (lastPitchDifference * offset));

                if (yawDifference > 2.0F && yawAccel > 1.0F && pitchAccel > 0.0F && pitchDifference > 0.009f) {

                    if (gcd < 131072L && pitchAccel < 6.5) {

                        if (increaseBufferBy(0.89) > 12.5) {
                            fail("GCD: " + gcd);
                        }
                    } else {
                        decreaseBufferBy(0.25);
                    }
                }

                lastYawDifference = yawDifference;
                lastPitchDifference = pitchDifference;
            }
        }
    }
}