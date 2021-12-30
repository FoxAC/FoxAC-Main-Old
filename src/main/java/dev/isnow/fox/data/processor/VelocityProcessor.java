package dev.isnow.fox.data.processor;

import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.MathUtil;
import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Getter
public final class VelocityProcessor {

    private final PlayerData data;

    private double velocityX, velocityY, velocityZ, velocityXZ;
    private double lastVelocityX, lastVelocityY, lastVelocityZ, lastVelocityXZ;

    private int ticksSinceVelocity;
    private int velocityID;

    private final Map<Short, Vector> pendingVelocities = new ConcurrentHashMap<>();

    private int flyingTicks;

    public VelocityProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final double velocityX, final double velocityY, final double velocityZ) {
        this.velocityID = data.getConnectionProcessor().sendTransaction();
        pendingVelocities.put((short) velocityID, new Vector(velocityX, velocityY, velocityZ));
    }

    public void handleTransaction(short transaction) {
        pendingVelocities.computeIfPresent(transaction, (id, vector) -> {

            lastVelocityX = this.velocityX;
            lastVelocityY = this.velocityY;
            lastVelocityZ = this.velocityZ;
            lastVelocityXZ = this.velocityXZ;

            this.velocityX = vector.getX();
            this.velocityY = vector.getY();
            this.velocityZ = vector.getZ();

            if (Math.abs(this.velocityX) < 0.005) {
                this.velocityX = 0;
            }

            if (Math.abs(this.velocityZ) < 0.005) {
                this.velocityZ = 0;
            }
            this.velocityXZ = MathUtil.magnitude(velocityX, velocityZ);

            this.ticksSinceVelocity = 0;

            pendingVelocities.remove(transaction);

            return vector;
        });
    }

    public void handleFlying() {
        ++ticksSinceVelocity;
        ++flyingTicks;

        if (isTakingVelocity()) {
            velocityXZ *= data.getPositionProcessor().getFriction();
        }
    }

    public boolean isTakingVelocity() {
        return velocityXZ > 0.005;
    }
}