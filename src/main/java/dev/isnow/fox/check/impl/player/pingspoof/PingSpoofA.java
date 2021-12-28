package dev.isnow.fox.check.impl.player.pingspoof;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "PingSpoof", type = "A", description = "Checks for keepalive packet and transaction packet difference.", experimental = true)
public final class PingSpoofA extends Check {
    public PingSpoofA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            final long transactionPing = data.getConnectionProcessor().getTransactionPing();
            final long keepAlivePing = data.getConnectionProcessor().getKeepAlivePing();

            final boolean exempt = isExempt(ExemptType.CHUNK, ExemptType.RESPAWN, ExemptType.AFK, ExemptType.LAGGING, ExemptType.TELEPORT_DELAY, ExemptType.JOINED, ExemptType.TPS, ExemptType.CHUNK);

            if (!exempt && transactionPing > keepAlivePing && Math.abs(transactionPing - keepAlivePing) > 100) fail();
        }
    }
}
