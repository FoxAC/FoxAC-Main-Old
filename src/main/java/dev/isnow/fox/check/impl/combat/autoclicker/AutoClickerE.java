package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "E", description = "Detects Vape autoclicker [METHOD 2].")
public class AutoClickerE extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>();
    private final List<Double> stdDelays = new ArrayList<>();
    private double threshold, lastAverage, lastDelta;
    public AutoClickerE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.AUTOCLICKER)) {
            movements++;
        }
        if(packet.isArmAnimation()) {
            if (movements < 15) {

                delays.add(movements);

                double mean = MathUtil.getMedian(delays);
                double std = MathUtil.getStandardDeviation(delays);
                double kurtosis = MathUtil.getKurtosis(delays);


                if (mean < 2.5 && delays.size() >= 20) {
                    if (stdDelays.size() > 30) {
                        double average = MathUtil.getAverage(stdDelays);
                        double delta = Math.abs(average - lastAverage);
                        double outlier = data.getClickProcessor().getOutlier();

                        if (lastDelta < 0.0855) {

                            double newDelta = Math.abs(delta - lastDelta);

                            if (newDelta < (.43 % 5) && kurtosis < 1.7 && outlier < 15) {
                                threshold++;

                                if (threshold > 6) {
                                    fail("Average: " + average);
                                }
                            } else {
                                threshold -= Math.min(threshold, .1);
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.1);
                        }


                        lastDelta = delta;
                        lastAverage = average;
                        stdDelays.clear();
                    }

                    stdDelays.add(std);

                    if (delays.size() >= 100) {
                        delays.clear();
                    }
                }
            }
            movements = 0;
        }
    }
}
