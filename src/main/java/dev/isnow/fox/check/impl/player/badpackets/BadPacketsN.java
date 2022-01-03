package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;

@CheckInfo(name = "BadPackets", type = "N", description = "Checks if player is breaking block too fast/nukering.")
public class BadPacketsN extends Check {

    public BadPacketsN(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isArmAnimation()) {
            setBuffer(0);
        }
        else if(packet.isBlockDig()) {
            WrappedPacketInBlockDig wrapped = new WrappedPacketInBlockDig(packet.getRawPacket());
            if(wrapped.getDigType() == WrappedPacketInBlockDig.PlayerDigType.START_DESTROY_BLOCK || wrapped.getDigType() == WrappedPacketInBlockDig.PlayerDigType.STOP_DESTROY_BLOCK) {
                if(increaseBuffer() > 2) {
                    fail();
                }
            }
            else {
                decreaseBuffer();
            }
        }
    }
}
