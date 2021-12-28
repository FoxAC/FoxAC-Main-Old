package dev.isnow.fox.check.impl.player.inventory;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Inventory", type = "E", description = "Checks if player is sending entity action packet while in inventory.")
public class InventoryE extends Check {

    public InventoryE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isEntityAction()) {
            if(data.getActionProcessor().isInventory()) {
                if(increaseBuffer() > 1) {
                    fail();
                }
                else {
                    setBuffer(0);
                }
            }
        }
    }
}
