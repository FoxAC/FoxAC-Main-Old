

package dev.isnow.fox.listener.packet;

import dev.isnow.fox.Fox;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.event.PacketListenerDynamic;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.event.impl.PostPlayerInjectEvent;
import io.github.retrooper.packetevents.event.priority.PacketEventPriority;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Bukkit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class NetworkManager extends PacketListenerDynamic {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public NetworkManager() {
        super(PacketEventPriority.MONITOR);
    }

    @Override
    public void onPacketPlayReceive(final PacketPlayReceiveEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());

        if (data != null) {
            executorService.execute(() -> Fox.INSTANCE.getReceivingPacketProcessor().handle(
                    data, new Packet(Packet.Direction.RECEIVE, event.getNMSPacket(), event.getPacketId(), event.getTimestamp()))
            );
        }
    }

    @Override
    public void onPacketPlaySend(final PacketPlaySendEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());

        if (data != null) {
            executorService.execute(() -> Fox.INSTANCE.getSendingPacketProcessor().handle(
                    data, new Packet(Packet.Direction.SEND, event.getNMSPacket(), event.getPacketId(), event.getTimestamp()))
            );
        }
    }

    @Override
    public void onPostPlayerInject(final PostPlayerInjectEvent event) {
        final ClientVersion version = event.getClientVersion();

        final boolean unsupported = version.isHigherThan(ClientVersion.v_1_16_4) || version.isLowerThan(ClientVersion.v_1_7_10);

        if (unsupported) {
            final String message = String.format("Player '%s' joined with a client version that is not supported, this might cause false positives. Please take the appropriate action. (Version: %s)", event.getPlayer().getName(), version.name());

            Bukkit.getLogger().warning(message);
            AlertManager.sendMessage(message);
        }
    }

}
