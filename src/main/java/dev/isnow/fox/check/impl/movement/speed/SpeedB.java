package dev.isnow.fox.check.impl.movement.speed;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.BlockUtil;
import dev.isnow.fox.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "B", description = "Checks for invalid friction.")
public final class SpeedB extends Check {

    private double blockSlipperiness = 0.91;
    private double lastHorizontalDistance = 0.0;

    public SpeedB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final Player player = data.getPlayer();

            final double deltaY = data.getPositionProcessor().getDeltaY();

            double blockSlipperiness = this.blockSlipperiness;
            double attributeSpeed = 1.d;

            final boolean lastOnGround = data.getPositionProcessor().isLastOnGround();

            final boolean exempt = this.isExempt(ExemptType.WEB, ExemptType.INWEB, ExemptType.TELEPORT_DELAY, ExemptType.PISTON, ExemptType.VELOCITY_ON_TICK,
                    ExemptType.FLYING, ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.CHUNK, ExemptType.GHOST_BLOCK);

            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SPEED) * (float) 0.2 * attributeSpeed;
            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SLOW) * (float) -.15 * attributeSpeed;

            if (lastOnGround) {
                blockSlipperiness *= 0.91f;

                attributeSpeed *= 1.3;
                attributeSpeed *= 0.16277136 / Math.pow(blockSlipperiness, 3);

                if (deltaY > 0.0) {
                    attributeSpeed += 0.2;
                }
            } else {
                attributeSpeed = 0.026f;
                blockSlipperiness = 0.91f;
            }

            final double horizontalDistance = data.getPositionProcessor().getDeltaXZ();
            final double movementSpeed = (horizontalDistance - lastHorizontalDistance) / attributeSpeed;

            if (movementSpeed > 1.0 && !exempt) {

                if (increaseBufferBy(10) > 20) {
                    fail("Friction: " + movementSpeed);
                }
            } else {
                decreaseBufferBy(1);
            }

            final double x = data.getPositionProcessor().getX();
            final double y = data.getPositionProcessor().getY();
            final double z = data.getPositionProcessor().getZ();

            final Location blockLocation = new Location(data.getPlayer().getWorld(), x, Math.floor(y) - 1, z);

            this.blockSlipperiness = BlockUtil.getBlockFriction(blockLocation);
            this.lastHorizontalDistance = horizontalDistance * blockSlipperiness;
        }
    }
}