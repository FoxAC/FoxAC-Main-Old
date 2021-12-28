

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;

@CheckInfo(name = "BadPackets", type = "B", description = "Checks for invalid sprint packets.")
public final class BadPacketsB extends Check {
    public BadPacketsB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isEntityAction()) {
            final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(packet.getRawPacket());

            final boolean sprinting = wrapper.getAction() == WrappedPacketInEntityAction.PlayerAction.START_SPRINTING
                    || wrapper.getAction() == WrappedPacketInEntityAction.PlayerAction.STOP_SPRINTING;

            if (sprinting) {
                if (increaseBuffer() > 1) {
                    fail();
                }
            }
        } else if (packet.isFlying()) {
            resetBuffer();
        }
    }
}
