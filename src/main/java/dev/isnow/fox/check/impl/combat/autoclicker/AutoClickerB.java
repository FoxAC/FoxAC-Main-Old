package dev.isnow.fox.check.impl.combat.autoclicker;


import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.type.EvictingList;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "B", description = "Vape Autoclicker check.", experimental = true)
public class AutoClickerB extends Check {

    private int movements;
    private List<Integer> delays = new ArrayList<>();
    private double threshold;

    public AutoClickerB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isPosition() && !isExempt(ExemptType.AUTOCLICKER)) {
            movements++;
        }
        if (packet.isArmAnimation()) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 100) {

                    int outliers = (int) delays.stream()
                            .filter(delay -> delay > 3)
                            .count();

                    if (outliers < 7) {
                        if (++threshold > 1) {
                            fail("Outliers: " + outliers);
                        }
                    } else {
                        threshold -= Math.min(threshold, 1.5);
                    }

                    delays.clear();
                }

            }
            movements = 0;
        }
    }
}