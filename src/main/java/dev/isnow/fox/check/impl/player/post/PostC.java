

package dev.isnow.fox.check.impl.player.post;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Post", type = "C", description = "Checks for packet order for the packet 'BLOCK_PLACE'.")
public final class PostC extends Check {

    private boolean sent;
    public long lastFlying, lastPacket;

    public PostC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastPacket;

            if (sent) {
                if (delay > 40L && delay < 100L) {
                    increaseBufferBy(0.25);

                    if (getBuffer() > 0.75) {
                        fail();
                    }
                } else {
                    decreaseBufferBy(0.025);
                }

                sent = false;
            }

            this.lastFlying = now;
        } else if (packet.isBlockPlace()){
            final long now = System.currentTimeMillis();
            final long delay = now - lastFlying;

            if (delay < 10L) {
                lastPacket = now;
                sent = true;
            } else {
                decreaseBufferBy(0.0025);
            }
        }
    }
}
