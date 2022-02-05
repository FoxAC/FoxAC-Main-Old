package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Material;

@CheckInfo(name = "Motion", description = "Checks for invalid fast-fall motion.", type = "K", experimental = true)
public class MotionK extends Check {
    public MotionK(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying() && data.getPositionProcessor().getSinceBlockNearHeadTicks() < 5 && !isExempt(ExemptType.PEARL, ExemptType.NEARCACTUS, ExemptType.TELEPORT, ExemptType.VELOCITY_ON_TICK, ExemptType.FIRE, ExemptType.RESPAWN, ExemptType.JOINED)) {
            if(data.getPositionProcessor().isInAir() && data.getPositionProcessor().getDeltaY() < 0  && data.getPositionProcessor().getSinceGroundTicks() > 3 && data.getPositionProcessor().getSinceJumpingTicks() < 8) {
                debug(data.getPositionProcessor().getDeltaY());
                if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_16) && data.getPositionProcessor().getBlocks().stream().anyMatch(block -> block.getType() == Material.SNOW)) {
                    return;
                }
                if(data.getPositionProcessor().getDeltaY() > -0.75 && data.getPositionProcessor().getDeltaY() < -0.156) {
                    fail("DeltaY: " + data.getPositionProcessor().getDeltaY());
                }
            }
        }
    }
}
