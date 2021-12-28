

package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayDeque;
import java.util.Deque;

@CheckInfo(name = "AutoClicker", type = "E", description = "Checks if kurtosis is too low or NaN.")
public final class AutoClickerE extends Check {

    private final Deque<Long> samples = new ArrayDeque<>();
    private int ticks;

    public AutoClickerE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.DROP, ExemptType.AUTOCLICKER)) {
            if (ticks > 50) samples.clear();
            else samples.add(ticks * 50L);

            if (samples.size() == 30) {
                final double kurtosis = MathUtil.getKurtosis(samples);

                final boolean invalid = kurtosis < 40000 || Double.isNaN(kurtosis);

                if (invalid) {
                    if (increaseBuffer() > 2) {
                        fail(kurtosis);
                    }
                } else {
                    resetBuffer();
                }

                samples.clear();
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ticks++;
        }
    }
}
