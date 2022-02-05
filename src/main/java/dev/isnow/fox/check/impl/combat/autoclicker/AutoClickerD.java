package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", description = "Checks for average kurtosis", type = "D")
public final class AutoClickerD extends Check {

    private final List<Double> kurtosisList = new ArrayList<>();
    private double threshold;

    public AutoClickerD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation()) {
            double mean = data.getClickProcessor().getMedian();
            double kurtosis = data.getClickProcessor().getKurtosis();
            double cps = data.getClickProcessor().getCurrentCps();

            if (mean < 2.5 && cps > 8) {
                if (kurtosisList.size() > 20) {
                    double average = MathUtil.getAverage(kurtosisList);

                    if (average > 10) {
                        if (++threshold > 5) {
                            fail("AVG: " + average);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.2);
                    }

                    kurtosisList.clear();
                }

                kurtosisList.add(kurtosis);
            }
        }
    }
}