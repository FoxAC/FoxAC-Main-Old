package dev.isnow.fox.check.impl.player.inventory;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;

@CheckInfo(name = "Inventory", type = "G", description = "Checks if player is interacting with inventory while closed.")
public class InventoryG extends Check {

    public InventoryG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isWindowClick()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_16)) {
                return;
            }
            PacketPlayInWindowClick click = (PacketPlayInWindowClick) packet.getRawPacket().getRawNMSPacket();
            if(click.a() == 0 && !data.getActionProcessor().isInventory()) {
                fail();
            }
        }
    }
}
