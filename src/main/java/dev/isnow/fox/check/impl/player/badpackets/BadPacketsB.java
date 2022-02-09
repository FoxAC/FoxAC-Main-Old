

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "BadPackets", type = "B", description = "Checks for pingspoofing.", experimental = true)
public final class BadPacketsB extends Check {
    public BadPacketsB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
//        if(packet.isFlying()) {
//            if(data.getConnectionProcessor().getTransactionPing() > PacketEvents.get().getPlayerUtils().getPing(data.getPlayer()) + 5) {
//                fail("TPing > KPing");
//            }
//            if(data.getConnectionProcessor().getTransactionPing() + 5 < PacketEvents.get().getPlayerUtils().getPing(data.getPlayer())) {
//                fail("TPing < KPing");
//            }
//        }
    }
}
