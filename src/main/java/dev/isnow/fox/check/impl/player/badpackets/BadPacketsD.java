

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle.WrappedPacketInSteerVehicle;
import org.bukkit.entity.Entity;

import java.util.List;

@CheckInfo(name = "BadPackets", type = "D", description = "Detects steer vehicle disabler.")
public final class BadPacketsD extends Check {
    public BadPacketsD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isSteerVehicle()) {
            final WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(packet.getRawPacket());

            handle: {
                if (data.getPlayer().getVehicle() == null) {
                    final List<Entity> nearbyEntities = data.getPositionProcessor().getNearbyEntities();
                    if (nearbyEntities == null) break handle;

                    if (nearbyEntities.isEmpty()) {
                        fail();
                    }
                }
            }

            final float forward = Math.abs(wrapper.getForwardValue());
            final float sideways = Math.abs(wrapper.getSideValue());

            if (forward != 0.0F && forward != 0.98F) {
                fail();
            }

            if (sideways != 0.0F && sideways != 0.98F) {
                fail();
            }
        }
    }
}
