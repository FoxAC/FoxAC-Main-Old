package dev.isnow.fox.check.impl.player.scaffold;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Scaffold", type = "E", description = "Checks for safewalk module.")
public class ScaffoldE extends Check {
    private int placedTicks;
    private double lastLastAccel;
    private double lastAccel;

    public ScaffoldE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                return;
            }
            if (++placedTicks < 5 && isBridging() && !data.getActionProcessor().isSneaking() && !isExempt(ExemptType.LAGGING)) {
                double accel = data.getPositionProcessor().getDeltaXZ() - data.getPositionProcessor().getLastDeltaXZ();
                if (accel < 1.0E-4 && lastAccel > 0.05 && lastLastAccel < 1.0E-4) {
                    if (increaseBuffer() > 10.0) {
                        fail();
                        setBuffer(0);
                    }
                }
                lastLastAccel = lastAccel;
                lastAccel = accel;
            }
        }
        else if (packet.isBlockPlace() && data.getPlayer().getItemInHand().getType().isBlock()) {
            placedTicks = 0;
        }
    }
}
