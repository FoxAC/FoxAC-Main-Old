

package dev.isnow.fox.check.impl.movement.largemove;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import org.bukkit.potion.PotionEffect;

@CheckInfo(name = "LargeMove", type = "B", description = "Checks if the players vertical movement is faster than possible.")
public final class LargeMoveB extends Check {
    public LargeMoveB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = Math.abs(data.getPositionProcessor().getDeltaY());

            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.JOINED, ExemptType.TELEPORT);
            final boolean invalid = deltaY > 10.0;
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
