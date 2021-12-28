

package dev.isnow.fox.check.impl.movement.noslow;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "NoSlow", type = "B", description = "Checks if player is sneaking and sprinting.")
public final class NoSlowB extends Check {
    public NoSlowB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int groundTicks = data.getPositionProcessor().getGroundTicks();

            final int sprintingTicks = data.getActionProcessor().getSprintingTicks();
            final int sneakingTicks = data.getActionProcessor().getSneakingTicks();
            final boolean exempt = isExempt(ExemptType.CHUNK) || groundTicks < 10;
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                if(this.isExempt(ExemptType.WEB, ExemptType.LIQUID)) {
                    return;
                }
            }
            final boolean invalid = sprintingTicks > 10 && sneakingTicks > 10;

            if (invalid && !exempt) {
                if (increaseBuffer() > 10) {
                    fail();
                }
            } else {
                resetBuffer();
            }
        }
    }
}
