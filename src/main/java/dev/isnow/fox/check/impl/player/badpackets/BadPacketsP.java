package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.armanimation.WrappedPacketInArmAnimation;

@CheckInfo(name = "BadPackets", type = "P", description = "Checks if player is not swinging while placing a block.", experimental = true)
public class BadPacketsP extends Check {

    public boolean swung = false;

    public BadPacketsP(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            swung = false;
        }
        else if(packet.isArmAnimation()) {
            swung = true;
        }
        else if(packet.isBukkitBlockPlace()) {
            if(!swung) {
                fail();
            }
        }
    }
}
