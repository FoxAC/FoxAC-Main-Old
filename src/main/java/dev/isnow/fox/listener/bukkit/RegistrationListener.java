package dev.isnow.fox.listener.bukkit;

import dev.isnow.fox.Fox;
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

import java.io.IOException;

public final class RegistrationListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) throws IOException {
        PlayerDataManager.getInstance().add(event.getPlayer());
        if (Fox.INSTANCE.getUpdateChecker().isUpdateAvailable()) {
            if (event.getPlayer().hasPermission("fox.alerts")) {
                final String version = Fox.INSTANCE.getVersion();
                final String latestVersion = Fox.INSTANCE.getUpdateChecker().getLatestVersion();

                AlertManager.sendMessage("An update is available for &cFox&8! You have &c" + version + "&8 latest is &c" + latestVersion + "&8.");
            }
        }
        if(Config.VPN_ENABLED) {
            VpnInfo info = PlayerUtil.isUsingVPN(event.getPlayer());
            if(!info.getIsVpn()) {
                return;
            }
            event.getPlayer().kickPlayer(Config.VPN_MESSAGE.replaceAll("%country%", info.getCountry()));
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("fox.alerts")) {
                    p.sendMessage(Config.PREFIX + event.getPlayer().getName() + " Tried to join With VPN/Proxy. Country: " + info.getCountry());
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
