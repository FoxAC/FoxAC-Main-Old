package dev.isnow.fox.check.impl.movement.motion;


import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import org.bukkit.potion.PotionEffect;

@CheckInfo(name = "Motion", type = "I", description = "Checks if the players vertical/horizontal movement is faster than possible")
public class MotionI extends Check {

    public MotionI(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final double deltaXZ = Math.abs(data.getPositionProcessor().getDeltaXZ());

            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.JOINED, ExemptType.TELEPORT, ExemptType.TELEPORT_DELAY);

            final double deltaY = Math.abs(data.getPositionProcessor().getDeltaY());

            final boolean invalid = deltaXZ > 30.0 || deltaY > 10.0;

            for(PotionEffect pot : data.getPlayer().getActivePotionEffects()) {
                if(pot.getAmplifier() > 200){
                    return;
                }
            }
            if (invalid && !exempt) fail();
        }
    }
}
