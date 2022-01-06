package dev.isnow.fox.listener.bukkit;

import dev.isnow.fox.config.Config;
import dev.isnow.fox.manager.AFKManager;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.util.PlayerUtil;
import dev.isnow.fox.util.type.VpnInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class RegistrationListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if(event.getPlayer().getUniqueId() == null) {
            event.getPlayer().kickPlayer("Failed to load data");
        }
        PlayerDataManager.getInstance().add(event.getPlayer());
        if(Config.VPN_ENABLED) {
            VpnInfo info = PlayerUtil.isUsingVPN(event.getPlayer());
            if(!info.getIsVpn()) {
                return;
            }
            event.getPlayer().kickPlayer(Config.VPN_MESSAGE.replaceAll("%country%", info.getCountry()));
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("fox.alerts")) {
                    AlertManager.sendMessage(event.getPlayer().getName() + " tried to join using a vpn/proxy");
                }
            }
        }
    }
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        PlayerDataManager.getInstance().remove(event.getPlayer());
        PlayerDataManager.getInstance().suspectedPlayers.remove(event.getPlayer());
        BukkitEventManager.wannadelet.remove(event.getPlayer());
        AFKManager.INSTANCE.removePlayer(event.getPlayer());
    }
}
