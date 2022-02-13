package dev.isnow.fox.listener.bukkit;

import dev.isnow.fox.Fox;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.AFKManager;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.util.ColorUtil;
import dev.isnow.fox.util.PlayerUtil;
import dev.isnow.fox.util.vpn.VPNResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

public final class RegistrationListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if(Config.VPN_ENABLED) {
            VPNResponse info = PlayerUtil.isUsingVPN(event.getPlayer());
            if(info != null && info.isSuccess() && info.isProxy()) {
                event.getPlayer().kickPlayer(Config.VPN_MESSAGE.replaceAll("%country%", info.getCountryName()));
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(p.hasPermission("fox.alerts")) {
                        AlertManager.sendVPNMessage(event.getPlayer().getName() + " tried to join using a vpn/proxy");
                    }
                }
                return;
            }
        }

        PlayerDataManager.getInstance().add(event.getPlayer());

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!event.getPlayer().isOnline()) {
                    cancel();
                }
                if(PlayerDataManager.getInstance().getPlayerData(event.getPlayer()).getPositionProcessor().getY() == 0) {
                    event.getPlayer().kickPlayer(ColorUtil.translate("&cFailed to load your data."));
                }
            }
        }.runTaskLater(Fox.INSTANCE.getPlugin(), 40);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        PlayerDataManager.getInstance().remove(event.getPlayer());
        PlayerDataManager.getInstance().suspectedPlayers.remove(event.getPlayer());
        BukkitEventManager.wannadelet.remove(event.getPlayer());
        AFKManager.INSTANCE.removePlayer(event.getPlayer());
    }
}
