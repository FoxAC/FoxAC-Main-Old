package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "A", description = "Checks for consistent clicks")
public class AutoClickerA extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>();
    private double threshold;

    public AutoClickerA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isPosition()) {
            movements++;
        }
        if (packet.isArmAnimation() && !isExempt(ExemptType.AUTOCLICKER)) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 150) {
                    double std = MathUtil.getStandardDeviation(delays);

                    if (std < 0.45) {
                        if (threshold++ > 2) {
                            fail("Dev: " + std);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.125);
                    }

                    delays.clear();
                }
            }
            movements = 0;
        }
    }
}
