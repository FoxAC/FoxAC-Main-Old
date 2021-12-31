package dev.isnow.fox.check.impl.movement.speed;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "A", description = "Checks for any modified speed advantage")
public final class SpeedA extends Check {
    public SpeedA(PlayerData data) {
        super(data);
    }

    private boolean prevPrevOnGround, prevOnGround, onGround, flying;
    private int teleportTicks, buffer;

    @Override
    public void handle(Packet packet) {
        if (packet.isTeleport()) {
            teleportTicks = 0;
        }
        if (packet.isFlyingType()) {
            if (data.getPlayer().isFlying()) flying = true;
            if (onGround
                    && prevOnGround
                    && prevPrevOnGround) flying = false;
        }
        if(packet.isFlyingType()) {

            teleportTicks++;

            final Player player = data.getPlayer();
            prevPrevOnGround = prevOnGround;
            prevOnGround = onGround;
            onGround = data.getPositionProcessor().isOnGround();
            double friction;
            double prevFriction;

            float attributeSpeed = 1;

            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SPEED) * (float) 0.2 * attributeSpeed;
            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SLOW) * (float) -.15 * attributeSpeed;

            friction = data.getPositionProcessor().getFriction() / 0.91;
            prevFriction = data.getPositionProcessor().getPrevFriction() / 0.91;

            final double prevDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();
            final double momentum = prevDeltaXZ * (prevFriction * 0.91);
            double acceleration = 0;

            int calculation = 0;
            double movementType = 1.3;

            if (onGround && prevOnGround) {
                calculation = 1;
                acceleration = 0.1 * movementType * attributeSpeed * Math.pow(0.6 / friction, 3);
            } else if (onGround || prevOnGround) {
                calculation = 2;
                acceleration = 0.1 * movementType * attributeSpeed * Math.pow(0.6 / 0.91, 3) + 0.2 + 0.26;
            } else {
                calculation = 3;
                acceleration = 0.026;
            }

            if (onGround && prevOnGround && !prevPrevOnGround) {
                acceleration += 0.13 * attributeSpeed;
            }

            if (data.getVelocityProcessor().getTicksSinceVelocity() <= 2) {
                acceleration += data.getVelocityProcessor().getVelocityXZ();
            }

            double limit = Math.max(momentum + acceleration, 0.26);

            if (teleportTicks < 10) return;

            if (data.getPositionProcessor().getDeltaXZ() - limit > 0.0001 && !flying) {

                if ((buffer += 5) > 15 && !isExempt(ExemptType.FLYING, ExemptType.CREATIVE, ExemptType.TELEPORT_DELAY, ExemptType.VELOCITY_ON_TICK, ExemptType.UPWARDS_VEL)) {
                    buffer = Math.max(30, buffer);
                    fail(data.getPositionProcessor().getDeltaXZ() - limit + " \nPrevGround: " + prevOnGround + "\nGround: " + onGround + "\nCalculation: " + calculation);
                } else {
                    buffer = Math.max(0, buffer - 1);
                }

            }

        }
    }
}