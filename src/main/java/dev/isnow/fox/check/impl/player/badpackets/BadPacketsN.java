package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "BadPackets", type = "N", description = "Checks if player is breaking block too fast/nukering.")
public class BadPacketsN extends Check {

    private int ticks;
    private int stage;
    private int count;
    private long lastpacketDelta;

    private double fastBreakBuffer = 0;

    public BadPacketsN(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            if (stage == 2) {
                fastBreakBuffer -= Math.min(getBuffer() + 1, 0.01);
            }
            if(stage == 1) {
                ++ticks;
                stage = 2;
            }
            else {
                stage = 0;
            }
        }
        if(packet.isArmAnimation()) {
            setBuffer(0);
        }
        else if(packet.isBlockDig()) {
            WrappedPacketInBlockDig wrapped = new WrappedPacketInBlockDig(packet.getRawPacket());

            count++;
            final long packetDelta = System.currentTimeMillis() - lastpacketDelta;
            if(packetDelta >= 1000) {
                lastpacketDelta = System.currentTimeMillis();
                if(count >= 50) {
                    fail("Nuker [CREATIVE]");
                }
                count = 0;
            }

            if(wrapped.getDigType() == WrappedPacketInBlockDig.PlayerDigType.STOP_DESTROY_BLOCK) {
                stage = 1;
                fastBreakBuffer = fastBreakBuffer - 1.0E-4;
            }
            if(wrapped.getDigType() == WrappedPacketInBlockDig.PlayerDigType.START_DESTROY_BLOCK) {
                if(stage == 2 && (ticks != 1 || PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isOlderThan(ClientVersion.v_1_9))) {
                    fail("FastBreak");
                }
                stage = 0;
                ticks = 0;
            }
            if(wrapped.getDigType() == WrappedPacketInBlockDig.PlayerDigType.START_DESTROY_BLOCK || wrapped.getDigType() == WrappedPacketInBlockDig.PlayerDigType.STOP_DESTROY_BLOCK) {
                if(increaseBuffer() > 2) {
                    fail("Nuker");
                }
            }
            else {
                decreaseBuffer();
            }
        }
    }
}
