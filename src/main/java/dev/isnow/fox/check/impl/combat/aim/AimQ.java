package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.type.EvictingList;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Aim", description = "Checks for invalid yaw rotations [METHOD 1].", type = "Q", experimental = true)
public class AimQ extends Check {
    public AimQ(PlayerData data) {
        super(data);
    }

    private final EvictingList<Double> differenceSamples = new EvictingList<>(25);

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation() && isExempt(ExemptType.COMBAT)) {
            final Player player = data.getPlayer();
            final Entity target = data.getCombatProcessor().getTarget();

            if (target != null) {
                final Location origin = player.getLocation().clone();
                final Vector end = target.getLocation().clone().toVector();

                final float optimalYaw = origin.setDirection(end.subtract(origin.toVector())).getYaw() % 360F;
                final float rotationYaw = data.getRotationProcessor().getYaw();
                final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
                final float fixedRotYaw = (rotationYaw % 360F + 360F) % 360F;

                final double difference = Math.abs(fixedRotYaw - optimalYaw);

                if (deltaYaw > 3f) {
                    differenceSamples.add(difference);
                }
                if (differenceSamples.isFull()) {
                    final double average = MathUtil.getAverage(differenceSamples);
                    final double deviation = MathUtil.getStandardDeviation(differenceSamples);

                    final boolean invalid = average < 7 && deviation < 12;

                    if (invalid) {
                        if (++buffer > 20) {
                            fail("Deviation: " + deviation + " Average: " + average);
                        }
                    } else {
                        buffer -= buffer > 0 ? 1 : 0;
                    }
                }
            }
        }
    }
}
