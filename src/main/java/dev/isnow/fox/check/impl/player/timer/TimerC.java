package dev.isnow.fox.check.impl.player.timer;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Timer", type = "C", description = "Checks for game speed which is too fast [Balanced].")
public class TimerC extends Check {
    public TimerC(PlayerData data) {
        super(data);
    }

    private long balance = 0L;
    private long lastFlying = 0L;

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            final long now = packet.getTimeStamp();
            handle: {
                if (isExempt(ExemptType.TELEPORT_DELAY_2TICK, ExemptType.JOINED, ExemptType.PEARL)) break handle;
                if (lastFlying == 0L) break handle;

                final long delay = now - lastFlying;

                balance += 50L - delay;

                if (balance > 5L) {
                    if (increaseBuffer() > 5) {
                        fail("balance: " + balance);
                    }

                    balance = 0;
                } else {
                    decreaseBufferBy(0.1);
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            if (isExempt(ExemptType.JOINED)) return;
            if (lastFlying == 0L) return;

            balance -= 50L;
        }
    }
}