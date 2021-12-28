

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "BadPackets", type = "E", description = "Checks for blink by checking if client doesn't send flying while being still connected.", experimental = true)
public final class BadPacketsE extends Check {
    public BadPacketsE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
    }
}
