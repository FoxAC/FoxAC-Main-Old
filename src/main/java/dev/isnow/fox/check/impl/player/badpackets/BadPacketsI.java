

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "BadPackets", type = "I", description = "Checks for no position packet in 20 ticks.")
public final class BadPacketsI extends Check {

    public BadPacketsI(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            if (wrapper.isPosition() || wrapper.isLook() || data.getPlayer().isInsideVehicle()) {
                resetBuffer();
                return;
            }

            if (increaseBuffer() > 20 && !isExempt(ExemptType.LAGGING)) {
                fail("Buffer: " + getBuffer());
            }
        } else if (packet.isSteerVehicle()) {
            resetBuffer();
        }
    }
}
