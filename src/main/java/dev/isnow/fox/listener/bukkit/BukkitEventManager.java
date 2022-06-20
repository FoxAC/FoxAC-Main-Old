

package dev.isnow.fox.listener.bukkit;

import dev.isnow.fox.Fox;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.ColorUtil;
import dev.isnow.fox.util.LogUtil;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public final class BukkitEventManager implements Listener {

    public static ArrayList<Player> wannadelete = new ArrayList<>();

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());
        if (data != null) {
            data.getActionProcessor().handleInteract(event);
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        if(wannadelete.contains(event.getPlayer())) {
            event.setCancelled(true);
            if(event.getMessage().startsWith("cancel")) {
                wannadelete.remove(event.getPlayer());
                event.getPlayer().sendMessage(ChatColor.GREEN + "Cancelled the action.");
            }
            else if(event.getMessage().equals("YES")) {
                new File(Fox.INSTANCE.getPlugin().getDataFolder() + File.separator + "/logs").mkdir();
                for(File file : Objects.requireNonNull(new File(Fox.INSTANCE.getPlugin().getDataFolder() + File.separator + "/logs").listFiles())) {
                    if(!file.getName().startsWith("config")) {
                        LogUtil.TextFile filetext = new LogUtil.TextFile(file.getName(), file.getPath());
                        LogUtil.resetFile(filetext);
                    }
                }
                event.getPlayer().sendMessage(ColorUtil.translate("&aCompleted resetting the files!"));
                wannadelete.remove(event.getPlayer());
            }
            else {
                event.getPlayer().sendMessage(ChatColor.GREEN + "Please respond to the action started before, or cancel it by typing \"cancel\".");
            }
        }
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());
        if (data != null) {
            data.getActionProcessor().handleBukkitPlace();
            Fox.INSTANCE.getReceivingPacketProcessor().handle(data, new Packet(Packet.Direction.RECEIVE,
                    new NMSPacket(event), Byte.MAX_VALUE, System.currentTimeMillis(), null));
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if(event.getEntity() != null && event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player && PlayerDataManager.getInstance().getPlayerData((Player) event.getEntity().getShooter()) != null) {
            PlayerDataManager.getInstance().getPlayerData((Player) event.getEntity().getShooter()).setEnderpearlTime(System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        PlayerDataManager.getInstance().getPlayerData(event.getPlayer()).setRespawnTime(System.currentTimeMillis());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        PlayerDataManager.getInstance().getPlayerData(event.getPlayer()).getActionProcessor().handleDrop();
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if(PlayerDataManager.getInstance().getPlayerData(event.getPlayer()) != null) { // RELOAD NPE
            PlayerDataManager.getInstance().getPlayerData(event.getPlayer()).getPositionProcessor().handleTeleport(event);
        }
    }
}
