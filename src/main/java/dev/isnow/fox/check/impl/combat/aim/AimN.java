package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Aim", type = "N", description = "Invalid rotation ratio")
public class AimN extends Check {


    public AimN(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isUseEntity()) {
            WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(packet.getRawPacket());

            if(wrappedPacketInUseEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                double pitch = Math.abs(data.getRotationProcessor().getPitch() - data.getRotationProcessor().getLastPitch());
                double yaw = Math.abs(data.getRotationProcessor().getPitch() - data.getRotationProcessor().getLastPitch());

                if (pitch > 3.0 && yaw < 0.0001D) {
                    if (increaseBuffer() > 3) {
                        fail();
                    }
                } else {
                    decreaseBuffer();
                }
            }

        }
    }
}