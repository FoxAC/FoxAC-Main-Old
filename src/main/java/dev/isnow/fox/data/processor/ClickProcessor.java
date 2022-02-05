

package dev.isnow.fox.data.processor;

import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.type.Pair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class ClickProcessor {

    private final PlayerData data;
    private double outlier, kurtosis, skewness, std, median, averageCps, currentCps, velocityH, velocityV, velocityHNoTrans;
    private Pair<List<Double>, List<Double>> outlierTuple;

    private final List<Integer> movements = new ArrayList<>();

    private int movementTicks;

    public ClickProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleArmAnimation() {
        if (movementTicks < 15) {
            if (data.getExemptProcessor().isExempt(ExemptType.AUTOCLICKER))
                movementTicks = 20;
            movements.clear();
        }

        movements.add(movementTicks);

        double average = MathUtil.getAverage(movements);
        double cps = MathUtil.getCPS(movements);
        double std = MathUtil.getStandardDeviation(movements);
        double median = MathUtil.getMedian(movements);
        double kurtosis = MathUtil.getKurtosis(movements);
        double skewness = MathUtil.getSkewness(movements);

        Pair<List<Double>, List<Double>> outlierTuple = MathUtil.getOutliers(movements);

        if (outlierTuple != null) {
            this.outlierTuple = outlierTuple;
            this.outlier = this.outlierTuple.getX().size() + this.outlierTuple.getY().size();
        }

        this.std = std;
        this.median = median;
        this.kurtosis = kurtosis;
        this.averageCps = average;
        this.currentCps = cps;
        this.skewness = skewness;

        if (movements.size() >= 100) {
            movements.clear();
        }


        movementTicks = 0;
    }

    public void handleFlying() {
        movementTicks++;
    }
}
