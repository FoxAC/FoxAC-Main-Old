

package dev.isnow.fox.check.impl.player.scaffold;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Scaffold", type = "C", description = "Checks for bridging too quickly.")
public final class ScaffoldC extends Check {
    public ScaffoldC(final PlayerData data) {
        super(data);
    }

    private long lastFlying;

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace() && isBridging()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                return;
            }
            long timeDiff = time() - lastFlying;

            if (timeDiff < 5) {
                if (increaseBuffer() > 10) {
                    fail("TD: " + timeDiff);
                }
            } else setBuffer(0);
        }
        else if(packet.isFlying()) {
            lastFlying = time();
        }
    }


    public long time() {
        return System.nanoTime() / 1000000;
    }
}
