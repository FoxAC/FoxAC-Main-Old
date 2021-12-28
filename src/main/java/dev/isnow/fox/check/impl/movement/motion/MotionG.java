package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import org.bukkit.GameMode;

@CheckInfo(name = "Motion", type = "G", description = "Checks if player has vertical acceleration while in web.")
public class MotionG extends Check {

    public MotionG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying() && (data.getPlayer().getGameMode() == GameMode.SURVIVAL || data.getPlayer().getGameMode() == GameMode.ADVENTURE)) {
            final double acceleration = data.getPositionProcessor().getDeltaY();
            debug(isExempt(ExemptType.WEBRN) + " ACC: " + acceleration);
            if(isExempt(ExemptType.JOINED, ExemptType.COMBAT, ExemptType.UPWARDS_VEL, ExemptType.PEARL)) {
                return;
            }
            if(!isExempt(ExemptType.WEBRN)) {
                return;
            }
            boolean invalid = acceleration > 0.0209999998 && data.getPositionProcessor().getWebTicks() > 3 && !data.getPositionProcessor().isLastOnGround();
            if(invalid) {
                fail("DeltaY: " + acceleration);
            }
        }
    }
}
