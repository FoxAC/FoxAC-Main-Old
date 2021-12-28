package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Aim", type = "K", description = "Checks for impossible rotation.")
public class AimK extends Check {

    public AimK(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                return;
            }
            final float deltaYaw = this.data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = this.data.getRotationProcessor().getDeltaPitch();
            if (deltaPitch == 0.0f && deltaYaw == 0.0f && !this.isExempt(ExemptType.TELEPORT_DELAY, ExemptType.BOAT, ExemptType.NEARANVIL, ExemptType.VEHICLE, ExemptType.JOINED, ExemptType.TELEPORT) && increaseBuffer() > 1) {
                this.fail();
            } else {
                decreaseBufferBy(0.20);
            }
        }
    }
}
