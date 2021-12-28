

package dev.isnow.fox.check.impl.movement.noslow;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "NoSlow", type = "A", description = "Checks if the player is not slowing down while blocking (Flying packet).")
public final class NoSlowA extends Check {

    public NoSlowA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThan(ClientVersion.v_1_8)) {
                return;
            }
            final boolean sprinting = data.getActionProcessor().isSprinting();
            final boolean blocking = data.getPlayer().isBlocking() && this.data.getActionProcessor().isBlocking();
            long seconds = (System.currentTimeMillis() - data.getCombatProcessor().getLastUseEntityPacket()) / 1000;
            if(seconds < 2) {
                return;
            }
            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.VEHICLE, ExemptType.CHUNK) || data.getPositionProcessor().isInAir();
            final boolean invalid = blocking && sprinting;

            if (invalid && !exempt) {
                if (increaseBuffer() > 10) {
                    fail();
                    data.getPlayer().setItemInHand(data.getPlayer().getItemInHand());
                }
            } else {
                decreaseBufferBy(2);
            }
        }
    }
}
