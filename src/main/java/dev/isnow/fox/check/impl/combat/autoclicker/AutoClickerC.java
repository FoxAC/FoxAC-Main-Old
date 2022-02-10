package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "C", description = "Checks for consistent drop time")
public class AutoClickerC extends Check {

    private int movements;
    private List<Integer> delays = new ArrayList<>();

    public AutoClickerC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition() && !isExempt(ExemptType.AUTOCLICKER)) {
            movements++;
        } else if (packet.isArmAnimation()) {
            if (movements < 10) {

                delays.add(movements);

                double getCps = MathUtil.getCPS(delays);


                if (delays.size() >= 20 && getCps > 8.5f) {

                    if (movements > 2 && movements < 6) {
                        resetBuffer();
                    } else {
                        increaseBufferBy(0.8);

                        if (increaseBuffer() > 125) {
                            fail("CPS: " + getCps);
                        }
                    }

                }

                if (delays.size() >= 100) {
                    delays.clear();
                }
            }
        }
    }
}
