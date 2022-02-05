package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Aim", type = "N", description = "Invalid rotation ratio")
public class AimN extends Check {

    private double threshold, lastSTD;
    private List<Double> deltaYawList = new ArrayList<>();


    public AimN(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isPosLook()) {
            double yaw = MathUtil.wrapAngleTo180_float(data.getRotationProcessor().getYaw());

            if (yaw > 1.0) {
                deltaYawList.add(yaw);

                if (deltaYawList.size() >= 25) {
                    double std = MathUtil.getStandardDeviation(deltaYawList);


                    if (std < 0.02 || Math.abs(std - lastSTD) < 0.001) {
                        if (++threshold > 2) {
                            fail("STD: " + std);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.125);
                    }


                    lastSTD = std;
                    deltaYawList.clear();
                }
            }
        }
    }
}