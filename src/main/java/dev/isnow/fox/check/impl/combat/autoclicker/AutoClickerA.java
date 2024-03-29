package dev.isnow.fox.check.impl.combat.autoclicker;

import com.google.common.collect.Lists;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.type.Pair;

import java.util.Deque;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "A", description = "Checks for invalid outliers and kurtosis.")
public class AutoClickerA extends Check {

    private int movements = 0;
    private final Deque<Integer> samples = Lists.newLinkedList();


    public AutoClickerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation()) {
            final boolean valid = movements < 4 && !isExempt(ExemptType.AUTOCLICKER) && movements != 0;

            if (valid) samples.add(movements);

            if (samples.size() == 15) {
                final Pair<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(samples);

                final double skewness = MathUtil.getSkewness(samples);
                final double kurtosis = MathUtil.getKurtosis(samples);
                final double outliers = outlierPair.getX().size() + outlierPair.getY().size();

                if (skewness < 0.035 && kurtosis < 0.1 && outliers < 2) fail("Kurtosis: " + kurtosis + " Outliers: " + outliers);

                samples.clear();
            }
            movements = 0;
        } else if (packet.isFlying()) {
            ++movements;
        }
    }
}
