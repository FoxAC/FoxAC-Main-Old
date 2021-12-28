

package dev.isnow.fox.check.impl.movement.fastclimb;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

@CheckInfo(name = "FastClimb", type = "B", description = "Checks if player is going faster than possible on a climbable.")
public final class FastClimbB extends Check {
    public FastClimbB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final List<Block> blocks = data.getPositionProcessor().getBlocks();
            if (blocks == null) return;

            final boolean onClimbable = blocks.stream().allMatch(block -> block.getType() == Material.LADDER || block.getType() == Material.VINE);

            final float deltaY = (float) data.getPositionProcessor().getDeltaY();
            final float limit = 0.1176F;

            final boolean exempt = isExempt(ExemptType.COMBAT, ExemptType.TELEPORT, ExemptType.PISTON, ExemptType.FLYING, ExemptType.BOAT, ExemptType.VEHICLE);
            final boolean invalid = deltaY > limit && onClimbable;

            if (invalid && !exempt) {
                if (increaseBuffer() > 3 || deltaY > (limit * 5.0F)) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
