

package dev.isnow.fox.check.impl.combat.autoclicker;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.type.EvictingList;

@CheckInfo(name = "AutoClicker", type = "F", description = "Checks for bad randomization.")
public final class AutoClickerF extends Check {

    public AutoClickerF(final PlayerData data) {
        super(data);
    }
    private final EvictingList<Integer> clickerData = new EvictingList<>(50);

    private int delayTime;

    @Override
    public void handle(Packet packet) {
        if (packet.isArmAnimation()) {

            if (delayTime < 5) {
                if(!data.getActionProcessor().isDigging()) {
                    clickerData.add(delayTime);
                }

                if (clickerData.isFull()) {

                    final double std = MathUtil.getStandardDeviation(clickerData);

                    if (std < 0.7) {
                        fail("std" + MathUtil.preciseRound(std, 2));
                    }
                }

            }

            delayTime = 0;
        }
        else if (packet.isFlyingType()) {
            delayTime++;
        }
    }
}