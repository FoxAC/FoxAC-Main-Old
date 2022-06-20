package dev.isnow.fox.check.impl.player.inventory;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Inventory", type = "E", description = "Checks if player is moving while in inventory.", experimental = true)
public class InventoryE extends Check {

    public boolean inInventory = false;
    public long lastInvOpen;

    public InventoryE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            if(inInventory && System.currentTimeMillis() - lastInvOpen > 300 && data.getPositionProcessor().getDeltaXZ() > 0.2 && !data.getPositionProcessor().isInAir() && !isExempt(ExemptType.VELOCITY, ExemptType.TELEPORT)) {
                data.getPlayer().closeInventory();
            }
        }
        if(packet.isCloseWindow()) {
            inInventory = false;
        }
        if(packet.isClientCommand()) {
            WrappedPacketInClientCommand wrappedPacketInClientCommand = new WrappedPacketInClientCommand(packet.getRawPacket());
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isOlderThan(ClientVersion.v_1_9)) {
                if(wrappedPacketInClientCommand.getClientCommand() == WrappedPacketInClientCommand.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                    inInventory = true;
                    lastInvOpen = System.currentTimeMillis();
                }
            }
        }
    }
}
