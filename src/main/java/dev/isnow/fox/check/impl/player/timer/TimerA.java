package dev.isnow.fox.check.impl.player.timer;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MovingStats;

@CheckInfo(name = "Timer", type = "A", description = "Checks for game speed which is too fast.")
public final class  TimerA extends Check {

    private final MovingStats movingStats = new MovingStats(20);

    private long lastFlying = 0L;
    private long allowance = 0;

    public TimerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long now = now();

            final boolean exempt = this.isExempt(ExemptType.NEARANVIL, ExemptType.LAGGINGHARD, ExemptType.RESPAWN, ExemptType.TELEPORT_DELAY_2TICK, ExemptType.LONG_JOINED, ExemptType.VEHICLE);

            debug(exempt);
            handle: {
                if (exempt) break handle;

                final long delay = now - lastFlying;
                if (delay < 1) break handle;

                movingStats.add(delay);

                final double threshold = 6.972;
                final double deviation = movingStats.getStdDev(threshold);

                if (deviation < threshold) {
                    allowance += 50;
                    allowance -= delay;

                    if (allowance > Math.ceil(threshold) && increaseBuffer() > 5) fail("Dev: " + deviation);
                } else {
                    allowance = 0;
                    decreaseBufferBy(0.25);
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            movingStats.add(125L);
        }
    }
}