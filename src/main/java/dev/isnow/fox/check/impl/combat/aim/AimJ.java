package dev.isnow.fox.check.impl.combat.aim;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;

@CheckInfo(name = "Aim", description = "Checks if player is following AIM GCD properly. [Method 5]", type = "J")
public class AimJ extends Check {
    public AimJ(PlayerData data) {
        super(data);
    }

    private float lastDeltaYaw = 0.0f, lastDeltaPitch = 0.0f;

    @Override
    public void handle(Packet packet) {
        if(packet.isRotation()) {

            // Get the deltas from the rotation update
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            // Grab the gcd using an expander.
            final double divisorYaw = MathUtil.getGcd((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));
            final double divisorPitch = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

            // Get the constant for both rotation updates by dividing by the expander
            final double constantYaw = divisorYaw / MathUtil.EXPANDER;
            final double constantPitch = divisorPitch / MathUtil.EXPANDER;

            // Get the estimated mouse delta from the constant
            final double currentX = deltaYaw / constantYaw;
            final double currentY = deltaPitch / constantPitch;

            // Get the estimated mouse delta from the old rotations using the new constant
            final double previousX = lastDeltaYaw / constantYaw;
            final double previousY = lastDeltaPitch / constantPitch;

            // Make sure the player is attacking or placing to filter out the check
            final boolean action = data.getCombatProcessor().getHitTicks() < 3 || data.getActionProcessor().getLastPlaceTick() < 3;

            // Make sure the rotation is not very large and not equal to zero and get the modulo of the xys
            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f && action) {
                final double moduloX = currentX % previousX;
                final double moduloY = currentY % previousY;

                // Get the floor delta of the the modulos
                final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                // Impossible to have a different constant in two rotations
                final boolean invalidX = moduloX > 90F && floorModuloX > 0.1F;
                final boolean invalidY = moduloY > 90F && floorModuloY > 0.1F;

                if (invalidX && invalidY) {

                    if (increaseBuffer() > 6) fail();
                } else {
                    decreaseBuffer();
                }
            }

            this.lastDeltaYaw = deltaYaw;
            this.lastDeltaPitch = deltaPitch;
        }
    }
}