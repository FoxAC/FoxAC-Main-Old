package dev.isnow.fox.check.impl.player.inventory;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Inventory", type = "F", description = "Checks if player is sprinting while sending close window packet.")
public class InventoryF extends Check {

    public InventoryF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isCloseWindow() && data.getActionProcessor().isSprinting()) {
            fail();
        }
    }
}
