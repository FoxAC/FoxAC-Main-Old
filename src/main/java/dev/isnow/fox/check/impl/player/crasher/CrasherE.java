package dev.isnow.fox.check.impl.player.crasher;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.CrasherUtils;
import io.github.retrooper.packetevents.packetwrappers.play.in.windowclick.WrappedPacketInWindowClick;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

@CheckInfo(name = "Crasher", description = "Checks for invalid book tag in window_click packet.", type = "E")
public class CrasherE extends Check {
    public CrasherE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isWindowClick()) {
            WrappedPacketInWindowClick wrappedPacketInWindowClick = new WrappedPacketInWindowClick(packet.getRawPacket());

            if(wrappedPacketInWindowClick.getClickedItemStack() != null) {
                ItemStack is = wrappedPacketInWindowClick.getClickedItemStack();

                net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(is);
                if(CrasherUtils.isInvalidBookTag(stack.getTag())) {
                    packet.getProcessor().setCancelled(true);
                    fail();
                }
            }
        }
    }
}
