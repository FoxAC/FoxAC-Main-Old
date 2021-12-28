

package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import org.bukkit.potion.PotionEffect;

@CheckInfo(name = "Motion", type = "B", description = "Checks for terminal fall velocity.")
public final class MotionB extends Check {
    public MotionB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final boolean exempt = isExempt(ExemptType.CREATIVE, ExemptType.JOINED, ExemptType.TELEPORT, ExemptType.CHUNK);
            final boolean invalid = deltaY < -3.92;
            for(PotionEffect pot : data.getPlayer().getActivePotionEffects()) {
                if(pot.getAmplifier() > 200){
                    return;
                }
            }
            if (invalid && !exempt) {
                fail();
            }
        }
    }
}
