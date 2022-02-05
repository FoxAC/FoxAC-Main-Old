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

@CheckInfo(name = "Motion", description = "Checks for invalid jump motion.", type = "H", experimental = true)
public class MotionH extends Check {

    public MotionH(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isPosition()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastPosY = data.getPositionProcessor().getLastY();
            final boolean onGround = data.getPositionProcessor().isOnGround();
            final boolean step = deltaY % 0.015625 == 0.0 && lastPosY % 0.015625 == 0.0;
            final double expectedJumpMotion = 0.41999998688697815 + PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1f;
            final double maxHighJump = 0.41999998688697815 + PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1f + ((data.getVelocityProcessor().getTicksSinceVelocity() < 5) ? (Math.max(data.getVelocityProcessor().getVelocityY(), 0.0)) : 0.0);
            final boolean jumped = deltaY > 0.0 && lastPosY % 0.015625 == 0.0 && !onGround && !step;
            final boolean exempt = isExempt(ExemptType.TELEPORT_DELAY, ExemptType.PEARL, ExemptType.BOAT, ExemptType.CLIMBABLE, ExemptType.CREATIVE, ExemptType.SLIME_ON_TICK, ExemptType.LAGGINGHARD, ExemptType.LAGGING, ExemptType.UNDERBLOCKWAS, ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.SLIME, ExemptType.UNDERBLOCK, ExemptType.PISTON, ExemptType.LIQUID, ExemptType.BOAT, ExemptType.TELEPORT, ExemptType.WEB);
            if(data.getPositionProcessor().getSinceSlimeTicks() < 10 && String.format("%.2f", deltaY).equals("1.00")) {
                return;
            }
            if (jumped && !exempt && !isExempt(ExemptType.VELOCITY) && deltaY < expectedJumpMotion) {
                if(deltaY > 0.36 && deltaY < 0.37) {
                    return;
                }
                fail("DeltaY: " + deltaY + " [V]");
            }
            if (!exempt && !step && deltaY > (data.getPositionProcessor().isOnGround() ? 0.6 : maxHighJump)) {
                fail("DeltaY: " + deltaY + " [NV]");
            }
        }
    }
}
