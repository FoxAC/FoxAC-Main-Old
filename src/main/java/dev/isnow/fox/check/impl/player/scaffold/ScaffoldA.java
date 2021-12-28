

package dev.isnow.fox.check.impl.player.scaffold;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;

@CheckInfo(name = "Scaffold", type = "A", description = "Checks if player is bridging down (LMFAO)")
public final class ScaffoldA extends Check {
    public ScaffoldA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace() && isBridging()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            final double locationY = data.getPositionProcessor().getY();
            final double blockY = wrapper.getBlockPosition().y;

            final Direction direction = wrapper.getDirection();

            final boolean exempt = isExempt(ExemptType.TELEPORT);
            final boolean invalid = locationY > blockY && direction == Direction.DOWN;

            if (invalid && !exempt) fail();
        }
    }
}
