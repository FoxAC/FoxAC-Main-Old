package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayList;
import java.util.List;


@CheckInfo(name = "AutoClicker", experimental = true, description = "Invalid Average Delay.", type = "G")
public final class AutoClickerG extends Check {

    private List<Double> delays = new ArrayList<>();
    private double threshold;

    public AutoClickerG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation()) {
            double skewness = data.getClickProcessor().getSkewness();
            double currentCps = data.getClickProcessor().getCurrentCps();
            double median = data.getClickProcessor().getMedian();

            if (median < 2.5 && data.getClickProcessor().getMovements().size() >= 20) {
                if (currentCps > 8) {
                    delays.add(skewness);

                    if (delays.size() == 25) {

                        double average = MathUtil.getAverage(delays);

                        if (average < -2) {
                            if (++threshold > 3) {
                                fail("AVG: " + average);
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.25);
                        }

                        delays.clear();
                    }
                }
            }
        }
    }
}