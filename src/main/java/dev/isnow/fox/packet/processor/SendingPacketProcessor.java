

package dev.isnow.fox.packet.processor;

import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityvelocity.WrappedPacketOutEntityVelocity;

public final class SendingPacketProcessor  {

    public void handle(final PlayerData data, final Packet packet) {
        if (packet.isVelocity()) {
            final WrappedPacketOutEntityVelocity wrapper = new WrappedPacketOutEntityVelocity(packet.getRawPacket());

            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                data.getVelocityProcessor().handle(wrapper.getVelocity().getX(), wrapper.getVelocity().getY(), wrapper.getVelocity().getZ());
            }
        }
        if(packet.isServerPos()) {
            data.getGhostBlockProcessor().handleServerPos();
        }
        data.getChecks().forEach(check -> check.handle(packet));
    }
}
