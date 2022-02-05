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

@CheckInfo(name = "Motion", description = "Detects step.", type = "I")
public class MotionI extends Check {

    public MotionI(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_12_2)) {
                return;
            }
            final double deltaY = this.data.getPositionProcessor().getDeltaY();
            final double lastPosY = this.data.getPositionProcessor().getLastY();
            final boolean step = deltaY % 0.015625 == 0.0 && lastPosY % 0.015625 == 0.0;
            if (step && !this.isExempt(ExemptType.TELEPORT, ExemptType.JOINED) && data.getPositionProcessor().getSinceSlimeTicks() > 8 && deltaY > 0.6000000238418579) {
                this.fail(String.format("DeltaY %.2f", deltaY));
            }
        }
    }
}
