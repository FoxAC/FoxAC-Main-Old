

package dev.isnow.fox.check.impl.player.groundspoof;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "GroundSpoof", type = "D", description = "Checks for subtle ground modifications.")
public final class GroundSpoofD extends Check {
    public GroundSpoofD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isOnGround();
            final boolean inAir = data.getPositionProcessor().getAirTicks() > 5;
            final boolean mathGround = data.getPositionProcessor().isMathematicallyOnGround();

            final boolean exempt = isExempt(ExemptType.BUKKIT_PLACING, ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.WEB, ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CHUNK);
            final boolean invalid = onGround && inAir && !mathGround;

            if (invalid && !exempt && increaseBuffer() > 2) {
                fail();
            } else {
                decreaseBuffer();
            }
        }
    }
}
