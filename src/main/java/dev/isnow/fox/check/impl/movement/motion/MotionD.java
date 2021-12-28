

package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "D", description = "Checks for invalid vertical acceleration.")
public final class MotionD extends Check {
    public MotionD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && data.getConnectionProcessor().getTransactionPing() != 0) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            if(deltaY == 0.0) {
                return;
            }
            final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1;
            final double modifierVelocity = isExempt(ExemptType.VELOCITY) ? data.getVelocityProcessor().getVelocityY() + 0.15 : 0.0;

            final double maximum = 0.6 + modifierJump + modifierVelocity;

            final boolean exempt = isExempt(ExemptType.NEARCACTUS, ExemptType.FIRE, ExemptType.SLIME_ON_TICK, ExemptType.NEARSLIME, ExemptType.JOINED, ExemptType.CREATIVE, ExemptType.RESPAWN, ExemptType.PEARL, ExemptType.BOAT, ExemptType.PISTON, ExemptType.LIQUID,
                    ExemptType.FLYING, ExemptType.WEB, ExemptType.TELEPORT, ExemptType.SLIME, ExemptType.CHUNK);
            final boolean invalid = deltaY > maximum;
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                if(isExempt(ExemptType.TELEPORT_DELAY)) {
                    return;
                }
            }
            if (invalid && !exempt) fail("DY: " + deltaY);
        }
    }
}
