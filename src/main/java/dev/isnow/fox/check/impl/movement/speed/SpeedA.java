package dev.isnow.fox.check.impl.movement.speed;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "A", description = "Checks for any modified speed advantage")
public final class SpeedA extends Check {
    public SpeedA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if(data.getPositionProcessor().isOnGround() && (data.getPlayer().getWalkSpeed() < 0.2 || data.getPlayer().getWalkSpeed() > 0.30)) {
                return;
            }

            String modifiers = "";

            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final int groundTicks = data.getPositionProcessor().getGroundTicks();
            final int airTicks = data.getPositionProcessor().getClientAirTicks();

            final float modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
            final float jumpMotion = 0.42F + modifierJump;

            double groundLimit = PlayerUtil.getBaseGroundSpeed(data.getPlayer());
            double airLimit = PlayerUtil.getBaseSpeed(data.getPlayer());


            if (Math.abs(deltaY - jumpMotion) < 1.0E-4 && airTicks == 1 && sprinting) {
                final float f = data.getRotationProcessor().getYaw() * 0.017453292F;

                final double x = lastDeltaX - (Math.sin(f) * 0.2F);
                final double z = lastDeltaZ + (Math.cos(f) * 0.2F);

                airLimit += Math.hypot(x, z);
                modifiers = modifiers + ", jump";
            }

            if (isExempt(ExemptType.SLIME) || data.getPositionProcessor().getSinceIceTicks() < 20) {
                airLimit += 0.34F;
                groundLimit += 0.34F;
                modifiers = modifiers + ", ice/slime";
            }

            if (isExempt(ExemptType.WASUNDERBLOCK)) {
                airLimit += 0.91F;
                groundLimit += 0.91F;
                modifiers = modifiers + ", underblock";
            }

            if (groundTicks < 7) {
                groundLimit += (0.25F / groundTicks);
                modifiers = modifiers + ", freshground";
            }

            if (isExempt(ExemptType.NEARSTAIRS)) {
                airLimit += 0.91F;
                groundLimit += 0.91F;
                modifiers = modifiers + ", stairs";
            }

            if (data.getPositionProcessor().getSinceTeleportTicks() < 15) {
                airLimit += 0.1;
                groundLimit += 0.1;
                modifiers = modifiers + ", teleport";
            }

            if (data.getVelocityProcessor().isTakingVelocity()) {
                groundLimit += data.getVelocityProcessor().getVelocityXZ() + 0.05;
                airLimit += data.getVelocityProcessor().getVelocityXZ() + 0.05;
                modifiers = modifiers + ", velocity";
            }

            final boolean exempt = isExempt(ExemptType.VEHICLE_NO_DELAY, ExemptType.PISTON, ExemptType.FLYING, ExemptType.TELEPORT, ExemptType.CHUNK);

            if (!exempt) {
                if (airTicks > 0) {
                    modifiers = modifiers + ", Air";
                    if (deltaXZ > airLimit) {
                        if (increaseBuffer() > 3) {
                            fail("DeltaXZ: " + deltaXZ + " Modifiers:" + modifiers);
                        }
                    } else {
                        decreaseBufferBy(0.15);
                    }
                } else {
                    modifiers = modifiers + ", Ground";
                    if (deltaXZ > groundLimit) {
                        if (increaseBuffer() > 7) {
                            fail("DeltaXZ: " + deltaXZ + " Modifiers:" + modifiers);
                        }
                    } else {
                        decreaseBufferBy(0.2);
                    }
                }
            }
        }
    }
}