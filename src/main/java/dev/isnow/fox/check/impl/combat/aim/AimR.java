package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Aim", description = "Checks for invalid yaw rotations [METHOD 2].", type = "R")
public class AimR extends Check {
    public AimR(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isUseEntity()) {
            WrappedPacketInUseEntity useEntityPacket = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (useEntityPacket.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {

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
