

package dev.isnow.fox.data.processor;

import dev.isnow.fox.Fox;
import dev.isnow.fox.data.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class VelocityProcessor {

    private final PlayerData data;
    private double velocityX, velocityY, velocityZ, velocityXZ, velocityH;
    private double lastVelocityX, lastVelocityY, lastVelocityZ, lastVelocityXZ;
    private int maxVelocityTicks, velocityTicks, ticksSinceVelocity, takingVelocityTicks;
    private short velocityID;
    private boolean verifyingVelocity;

    public VelocityProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final double velocityX, final double velocityY, final double velocityZ) {
        this.ticksSinceVelocity = 0;

        lastVelocityX = this.velocityX;
        lastVelocityY = this.velocityY;
        lastVelocityZ = this.velocityZ;
        lastVelocityXZ = this.velocityXZ;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.velocityXZ = Math.hypot(velocityX, velocityZ);

        this.velocityID = (short) ThreadLocalRandom.current().nextInt(32767);
        this.velocityH = ((int)(((velocityX + velocityZ) / 2.0 + 2.0) * 15.0));
        this.verifyingVelocity = true;
        PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), new WrappedPacketOutTransaction(0, (short) ThreadLocalRandom.current().nextInt(32767), false));
    }

    public void handleTransaction(final WrappedPacketInTransaction wrapper) {
        if (this.verifyingVelocity && wrapper.getActionNumber() == this.velocityID) {
            this.verifyingVelocity = false;
            this.velocityTicks = Fox.INSTANCE.getTickManager().getTicks();
            this.maxVelocityTicks = (int) (((lastVelocityZ + lastVelocityX) / 2 + 2) * 15);
        }
    }

    public void handleFlying() {
        ++ticksSinceVelocity;

        if (isTakingVelocity()) {
            ++takingVelocityTicks;
        } else {
            takingVelocityTicks = 0;
        }
    }

    public boolean isTakingVelocity() {
        return Math.abs(Fox.INSTANCE.getTickManager().getTicks() - this.velocityTicks) < this.maxVelocityTicks;
    }
}
