package dev.isnow.fox.check.impl.combat.autoclicker;


import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "F", description = "Checks for invalid standard deviation of delays.")
public class AutoClickerF extends Check {
    private final List<Double> delays = new ArrayList<>();
    private double threshold;

    public AutoClickerF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER)) {
            double currentCps = data.getClickProcessor().getCurrentCps();
            double kurtosis = data.getClickProcessor().getKurtosis();
            double median = data.getClickProcessor().getMedian();
            if (median < 2.5 && data.getClickProcessor().getMovements().size() >= 20) {

                if (currentCps > 8) {
                    delays.add(kurtosis);

                    if (delays.size() == 25) {


                        double std = MathUtil.getStandardDeviation(delays);

                        if (std < 0.1) {
                            if (++threshold > 2) {
                                fail("STD: " + std);
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.15);
                        }

                        delays.clear();
                    }
                }
            }
        }
    }
}
