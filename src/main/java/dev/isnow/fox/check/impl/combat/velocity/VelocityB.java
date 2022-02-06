package dev.isnow.fox.check.impl.combat.velocity;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.data.processor.VelocityProcessor;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.math.RoundingMode;

@CheckInfo(name = "Velocity", type = "B", description = "Checks for horizontal velocity modifications.")
public final class VelocityB extends Check {


    public int hitTicks;

    public VelocityB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        VelocityProcessor vData = data.getVelocityProcessor();

        if (packet.isHitEntity()) {
            hitTicks = 0;
        } else if (packet.isFlying()) {
            hitTicks++;
        }
        if (packet.isPosition() && vData.getTicksSinceVelocity() == 1) {

            if (Math.abs(data.getVelocityProcessor().getVelocityX()) < 0.005 ||
                    Math.abs(data.getVelocityProcessor().getVelocityY()) < 0.005 ||
                    Math.abs(data.getVelocityProcessor().getVelocityZ()) < 0.005) return;
            double givenVelocity = vData.getVelocityXZ() - calculateVelocity();
            double takenVelocity = data.getPositionProcessor().getDeltaXZ();;

            if (takenVelocity < givenVelocity && !isExempt(ExemptType.WEB, ExemptType.LIQUID, ExemptType.NEAR_WALL) && !data.getPositionProcessor().isOnClimbable()) {
                if (hitTicks >= 2) {
                    buffer += 20;
                    if (buffer > 30) {
                        fail(Math.max(0, MathUtil.preciseRound(takenVelocity / givenVelocity, 3) * 100) + "%");
                    }
                }
            } else {
                buffer = Math.max(0, buffer - 5);
            }

        }
    }

    private double calculateVelocity() {

        final double preD = 0.01D;

        final double mx = data.getPositionProcessor().getDeltaX();
        final double mz = data.getPositionProcessor().getDeltaZ();

        float motionYaw = (float) (Math.atan2(mz, mx) * 180.0D / Math.PI) - 90.0F;

        motionYaw -= data.getRotationProcessor().getYaw();

        while (motionYaw > 360.0F)
            motionYaw -= 360.0F;
        while (motionYaw < 0.0F)
            motionYaw += 360.0F;

        motionYaw /= 45.0F;

        float moveS = 0.0F;
        float moveF = 0.0F;

        if (Math.abs(mx + mz) > preD) {
            final int direction = (int) new BigDecimal(motionYaw).setScale(1, RoundingMode.HALF_UP).doubleValue();

            if (direction == 1) {
                moveF = 1F;
                moveS = -1F;
            } else if (direction == 2) {
                moveS = -1F;
            } else if (direction == 3) {
                moveF = -1F;
                moveS = -1F;
            } else if (direction == 4) {
                moveF = -1F;
            } else if (direction == 5) {
                moveF = -1F;
                moveS = 1F;
            } else if (direction == 6) {
                moveS = 1F;
            } else if (direction == 7) {
                moveF = 1F;
                moveS = 1F;
            } else if (direction == 8) {
                moveF = 1F;
            } else if (direction == 0) {
                moveF = 1F;
            }
        }

        moveS *= 0.98F;
        moveF *= 0.98F;

        float strafe = moveS, forward = moveF;
        float f = strafe * strafe + forward * forward;

        float friction;

        float var3 = (0.6F * 0.91F);

        float attributeSpeed = 1;

        attributeSpeed += PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) * (float) 0.2 * attributeSpeed;
        attributeSpeed += PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SLOW) * (float) -.15 * attributeSpeed;

        float getAIMoveSpeed = 0.13000001F * attributeSpeed;

        float var4 = 0.16277136F / (var3 * var3 * var3);

        if (data.getPositionProcessor().isLastOnGround()) {
            friction = getAIMoveSpeed * var4;
        } else {
            friction = 0.026F;
        }

        if (f >= 1.0E-4F) {
            f = (float) Math.sqrt(f);
            if (f < 1.0F) {
                f = 1.0F;
            }
            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = (float) Math.sin(motionYaw * (float) Math.PI / 180.0F);
            float f2 = (float) Math.cos(motionYaw * (float) Math.PI / 180.0F);
            float motionXAdd = (strafe * f2 - forward * f1);
            float motionZAdd = (forward * f2 + strafe * f1);
            return Math.hypot(motionXAdd, motionZAdd);
        }
        return 0;
    }

}