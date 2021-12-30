

package dev.isnow.fox.listener.packet;

import dev.isnow.fox.Fox;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.type.Pair;
import io.github.retrooper.packetevents.event.PacketListenerDynamic;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.event.impl.PostPlayerInjectEvent;
import io.github.retrooper.packetevents.event.priority.PacketEventPriority;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

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
            if(event.getPacketId() == PacketType.Play.Client.TRANSACTION) {
                WrappedPacketInTransaction transaction = new WrappedPacketInTransaction(event.getNMSPacket());
                short id = transaction.getActionNumber();

                // Vanilla always uses an ID starting from 1
                if (id <= 0) {
                    // Check if we sent this packet before cancelling it
                    if (data.getConnectionProcessor().addTransactionResponse(id)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public void onPacketPlaySend(final PacketPlaySendEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());

        if (data != null) {
            executorService.execute(() -> Fox.INSTANCE.getSendingPacketProcessor().handle(
                    data, new Packet(Packet.Direction.SEND, event.getNMSPacket(), event.getPacketId(), event.getTimestamp()))
            );
            if (event.getPacketId() == PacketType.Play.Server.TRANSACTION) {
                WrappedPacketInTransaction transaction = new WrappedPacketInTransaction(event.getNMSPacket());
                short id = transaction.getActionNumber();

                // Vanilla always uses an ID starting from 1
                if (id <= 0) {

                    if (data.getConnectionProcessor().didWeSendThatTrans.remove((Short) id)) {
                        data.getConnectionProcessor().transactionsSent.add(new Pair<>(id, System.nanoTime()));
                        data.getConnectionProcessor().lastTransactionSent.getAndIncrement();
                    }
                }
            }
        }
    }

    @Override
    public void onPostPlayerInject(final PostPlayerInjectEvent event) {
        final ClientVersion version = event.getClientVersion();


    }

}
