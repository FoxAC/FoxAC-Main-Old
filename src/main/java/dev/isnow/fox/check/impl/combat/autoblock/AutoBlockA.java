

package dev.isnow.fox.check.impl.combat.autoblock;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "AutoBlock", type = "A", description = "Checks if blocked and unblocked in the same thing.")
public final class AutoBlockA extends Check {
    public AutoBlockA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if(isExempt(ExemptType.BUKKIT_PLACING)) {
                return;
            }
            final boolean invalid = data.getActionProcessor().isPlacing() && !data.getActionProcessor().isBukkitPlacing()
                    && wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK;

            if (invalid) {
                fail();
            }
        }
    }
}
