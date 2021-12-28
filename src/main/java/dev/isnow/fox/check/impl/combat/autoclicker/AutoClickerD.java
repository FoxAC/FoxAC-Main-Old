

package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayDeque;
import java.util.Deque;

@CheckInfo(name = "AutoClicker", type = "D", description = "Checks if statistical values match.")
public final class AutoClickerD extends Check {

    private double lastKurtosis, lastSkewness, lastDeviation;
    private final Deque<Long> samples = new ArrayDeque<>();
    private int ticks;

    public AutoClickerD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER)) {
            if (ticks > 50) samples.clear();
            else samples.add(ticks * 50L);

            if (samples.size() == 30) {
                final double deviation = MathUtil.getStandardDeviation(samples);
                final double skewness = MathUtil.getSkewness(samples);
                final double kurtosis = MathUtil.getKurtosis(samples);

                final boolean invalid = deviation == lastDeviation && skewness == lastSkewness && kurtosis == lastKurtosis;

                if (invalid) {
                    if (increaseBuffer() > 3) {
                        fail();
                    }
                } else {
                    resetBuffer();
                }

                lastDeviation = deviation;
                lastSkewness = skewness;
                lastKurtosis = kurtosis;

                samples.clear();
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ++ticks;
        }
    }
}
