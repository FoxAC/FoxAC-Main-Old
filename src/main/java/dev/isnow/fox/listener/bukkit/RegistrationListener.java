package dev.isnow.fox.listener.bukkit;

import dev.isnow.fox.config.Config;
import dev.isnow.fox.manager.AFKManager;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.util.PlayerUtil;
import dev.isnow.fox.util.vpn.VPNResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public final class RegistrationListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        PlayerDataManager.getInstance().add(event.getPlayer());
        if(Config.VPN_ENABLED) {
            VPNResponse info = PlayerUtil.isUsingVPN(event.getPlayer());
            if(info == null || !info.isSuccess()) {
                return;
            }
            if(!info.isProxy()) {
                return;
            }
            event.getPlayer().kickPlayer(Config.VPN_MESSAGE.replaceAll("%country%", info.getCountryName()));
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("fox.alerts")) {
                    AlertManager.sendVPNMessage(event.getPlayer().getName() + " tried to join using a vpn/proxy");
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        PlayerDataManager.getInstance().remove(event.getPlayer());
        PlayerDataManager.getInstance().suspectedPlayers.remove(event.getPlayer());
        BukkitEventManager.wannadelet.remove(event.getPlayer());
        AFKManager.INSTANCE.removePlayer(event.getPlayer());
    }
}
