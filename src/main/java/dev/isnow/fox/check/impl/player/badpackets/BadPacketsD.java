package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInfo(name = "BadPackets", type = "D", description = "Detects NoRotate (Might falseflag)", experimental = true)
public class BadPacketsD extends Check {
    public BadPacketsD(PlayerData data) {
        super(data);
    }

    private long balance = 0L;
    private long lastFlying = 0L;

    private double posYaw, teleportTicks, ticks;
    private Location serverPosLoc;

    @Override
    public void handle(Packet packet) {
        if (packet.isServerPos()) {
            WrappedPacketOutPosition positionPacket =
                    new WrappedPacketOutPosition(packet.getRawPacket());

            teleportTicks = 60;
            ticks = 3;
            posYaw = positionPacket.getYaw();

            serverPosLoc = new Location(data.getPlayer().getWorld(), positionPacket.getPosition().getX(), positionPacket.getPosition().getY(),
                    positionPacket.getPosition().getZ());
        } else if (packet.isPosition()) {
            if (serverPosLoc != null) {
                if (ticks-- > 0) {

                    double serverYaw = posYaw;
                    double currentYaw = data.getRotationProcessor().getYaw();

                    double yawDifference = Math.abs(serverYaw - currentYaw);

                    if (yawDifference < 1E-9) {
                        ticks = 0;
                    } else {
                        if (yawDifference > .3) {
                            ticks = 3;

                            if (teleportTicks-- < 1) {
                                teleportTicks = 20;

                                data.getPlayer().teleport(serverPosLoc,
                                        PlayerTeleportEvent.TeleportCause.PLUGIN);

                                fail();
                            }
                        }
                    }
                }
            }
        }
    }
}