package dev.isnow.fox.check.impl.player.ground;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Ground", type = "C", description = "Detects no-fall.")
public final class GroundC extends Check {
    public GroundC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final int groundTicks = data.getPositionProcessor().getGroundTicks();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastY = data.getPositionProcessor().getLastY();

            final boolean step = deltaY % 0.015625 == 0.0 && lastY % 0.015625 == 0.0;

            boolean exempt = isExempt(ExemptType.GHOST_BLOCK, ExemptType.LONG_BUKKIT_PLACING, ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.WEB, ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CHUNK);
            final boolean invalid = groundTicks > 5 && deltaY != 0.0 && !step;

            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isOlderThan(ClientVersion.v_1_8)) {
                exempt = isExempt(ExemptType.GHOST_BLOCK, ExemptType.PEARL, ExemptType.LONG_BUKKIT_PLACING, ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.WEB, ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CHUNK);
            }
            if (invalid && !exempt) {
                if (increaseBuffer() > 1) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }
}