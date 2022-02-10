package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@CheckInfo(name = "Aim", description = "Checks for pitch deviation.", type = "L")
public final class AimL extends Check {

    public AimL(final PlayerData data) {
        super(data);
    }

    private final List<Double> deltaPitchList = new ArrayList<>();


    @Override
    public void handle(final Packet packet) {
        if(packet.isPosLook()) {
            double deltaPitch = Math.abs(data.getRotationProcessor().getPitch()
                    - data.getRotationProcessor().getLastPitch());


            if (deltaPitch > 0.8) {
                deltaPitchList.add(deltaPitch);

                if (deltaPitchList.size() > 125) {
                    double std = MathUtil.getStandardDeviation(deltaPitchList);

                    if (std < 0.9) {
                        fail("Dev: " + std);
                    }

                    deltaPitchList.clear();
                }
            }
        }
    }
}