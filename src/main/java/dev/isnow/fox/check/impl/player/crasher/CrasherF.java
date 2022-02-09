package dev.isnow.fox.check.impl.player.crasher;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.handshaking.setprotocol.WrappedPacketHandshakingInSetProtocol;

@CheckInfo(name = "Crasher", description = "Checks if player joined with invalid IP (null).", type = "F")
public class CrasherF extends Check {

    public CrasherF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isSetProtocol()) {
            WrappedPacketHandshakingInSetProtocol wrappedPacketHandshakingInSetProtocol = new WrappedPacketHandshakingInSetProtocol(packet.getRawPacket());

            if(wrappedPacketHandshakingInSetProtocol.getHostName() == null) {
                fail();
                packet.getProcessor().setCancelled(true);
            }
        }
    }
}
