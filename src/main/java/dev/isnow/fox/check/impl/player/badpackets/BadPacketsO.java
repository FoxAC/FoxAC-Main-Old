package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;

@CheckInfo(name = "BadPackets", type = "O", description = "Checks if player is sprinting and sneaking at the same time.")
public class BadPacketsO extends Check {
    public BadPacketsO(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            if(data.getActionProcessor().isSprinting() && data.getActionProcessor().isSneaking()) {
                fail();
            }
        }
    }
}
