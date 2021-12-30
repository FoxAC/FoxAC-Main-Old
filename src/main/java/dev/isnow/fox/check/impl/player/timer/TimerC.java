package dev.isnow.fox.check.impl.player.timer;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.type.EvictingList;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Timer", type = "C", description = "Checks for game speed which is too slow.")
public class TimerC extends Check {

    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlyingTime;

    public TimerC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.TPS)) {
            final long now = now();
            final long delta = now - lastFlyingTime;
            samples.add(delta);
            if (samples.isFull()) {
                final double average = samples.stream().mapToDouble(value -> value).average().orElse(1.0);
                final double speed = 50 / average;
                if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9) && String.format("%.2f", speed).equals("0.05")) {
                    return;
                }
                final double deviation = MathUtil.getStandardDeviation(samples);
                if (speed <= 0.75 && deviation < 50) {
                    if (increaseBuffer() > 10) {
                        fail(String.format("Speed: %.2f Deviation: %.2f", speed, deviation));
                    }
                }
                else {
                    decreaseBufferBy(2);
                }
            }
            lastFlyingTime = now;
        }
    }
}