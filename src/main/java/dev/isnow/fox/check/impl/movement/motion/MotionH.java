package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", description = "Checks for invalid jump motion.", type = "H", experimental = true)
public class MotionH extends Check {

    public MotionH(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastPosY = data.getPositionProcessor().getLastY();
            final boolean onGround = data.getPositionProcessor().isOnGround();
            final boolean step = deltaY % 0.015625 == 0.0 && lastPosY % 0.015625 == 0.0;
            final double expectedJumpMotion = 0.41999998688697815 + PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1f;
            final double maxHighJump = 0.41999998688697815 + PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1f + ((data.getVelocityProcessor().getTicksSinceVelocity() < 5) ? ((data.getVelocityProcessor().getVelocityY() > 0.0) ? data.getVelocityProcessor().getVelocityY() : 0.0) : 0.0);
            final boolean jumped = deltaY > 0.0 && lastPosY % 0.015625 == 0.0 && !onGround && !step;
            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.CLIMBABLE, ExemptType.CREATIVE, ExemptType.SLIME_ON_TICK, ExemptType.LAGGINGHARD, ExemptType.LAGGING, ExemptType.WASUNDERBLOCK, ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.SLIME, ExemptType.UNDERBLOCK, ExemptType.PISTON, ExemptType.LIQUID, ExemptType.BOAT, ExemptType.TELEPORT, ExemptType.WEB);
            if (jumped && !exempt && !isExempt(ExemptType.VELOCITY) && deltaY < expectedJumpMotion) {
                fail("DeltaY: " + deltaY);
            }
            if (!exempt && !step && deltaY > (data.getPositionProcessor().isOnGround() ? 0.6 : maxHighJump)) {
                fail("DeltaY: " + deltaY);
            }
        }
    }
}
