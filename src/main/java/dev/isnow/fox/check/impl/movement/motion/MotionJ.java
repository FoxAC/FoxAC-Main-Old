package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

@CheckInfo(name = "Motion", description = "Checks for invalid motion on climbables.", type = "J")
public class MotionJ extends Check {
    public MotionJ(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            final List<Block> blocks = data.getPositionProcessor().getBlocks();
            if (blocks == null) return;

            final boolean onClimbable = blocks.stream().allMatch(block -> block.getType() == Material.LADDER || block.getType() == Material.VINE);

            final float deltaY = (float) data.getPositionProcessor().getDeltaY();
            final float limit = 0.1176F;

            final boolean exempt = isExempt(ExemptType.COMBAT, ExemptType.TELEPORT, ExemptType.PISTON, ExemptType.FLYING, ExemptType.BOAT, ExemptType.VEHICLE);
            final boolean invalid = deltaY > limit && onClimbable;

            if (invalid && !exempt) {
                if (increaseBuffer() > 6 || deltaY > (limit * 5.0F)) {
                    fail("DeltaY: " + deltaY + " Limit: " + limit);
                }
            } else {
                decreaseBufferBy(0.25);
            }
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double acceleration = deltaY - lastDeltaY;

            final boolean exempt1 = isExempt(ExemptType.TELEPORT, ExemptType.PISTON, ExemptType.FLYING, ExemptType.BOAT, ExemptType.VEHICLE);
            final boolean invalid1 = deltaY > limit && acceleration == 0.0 && onClimbable;

            if (invalid1 && !exempt1) {
                fail("Acceleration: " + acceleration);
            }
        }

    }
}
