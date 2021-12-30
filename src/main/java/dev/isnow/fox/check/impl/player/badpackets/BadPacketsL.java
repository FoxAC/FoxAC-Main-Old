

package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "BadPackets", type = "L", description = "Checks for 0 rotation with a rotation packet.")
public final class BadPacketsL extends Check {
    public BadPacketsL(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean exempt = isExempt(ExemptType.TELEPORT_DELAY, ExemptType.VEHICLE, ExemptType.CREATIVE, ExemptType.JOINED, ExemptType.GHOST_BLOCK, ExemptType.TELEPORT) && (data.getPositionProcessor().getSinceTeleportTicks() > 20);
            final boolean invalid = deltaPitch == 0.0F && deltaYaw  == 0.0F;

            if(invalid && !exempt) {
                fail();
            }

        }
    }
}
