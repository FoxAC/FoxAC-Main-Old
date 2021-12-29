package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(type = "Aim", name = "N", description = "Invalid yaw movements.")
public class AimN extends Check {

    public AimN(PlayerData data) {
        super(data);
    }

    private double lastSTD;
    private double lastDeltaYaw;
    private List<Double> deltaYawList = new ArrayList<>();

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {

            double yaw = data.getRotationProcessor().getDeltaYaw();

            if (yaw > 1.0) {
                deltaYawList.add(yaw);

                if (deltaYawList.size() >= 25) {
                    double std = MathUtil.getStandardDeviation(deltaYawList);


                    if (std < 0.03 || Math.abs(std - lastSTD) < 0.001) {
                        fail();
                    }


                    lastSTD = std;
                    deltaYawList.clear();
                }


                lastDeltaYaw = yaw;
            }
        }
    }
}