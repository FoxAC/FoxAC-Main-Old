

package dev.isnow.fox.command.impl;

import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "alerts", purpose = "Toggles cheat alerts.")
public final class Alerts extends FoxCommand {

    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("fox.alerts")) {
                final Player player = (Player) sender;
                final PlayerData data = PlayerDataManager.getInstance().getPlayerData(player);

                if (data != null) {
                    if (AlertManager.toggleAlerts(data) == AlertManager.ToggleAlertType.ADD) {
                        sendMessage(sender, ColorUtil.translate("&cToggled your Fox alerts &aon&a."));
                    } else {
                        sendMessage(sender, ColorUtil.translate("&cToggled your Fox alerts &coff&a."));
                    }
                    return true;
                }
            }
        } else {
            sendMessage(sender, "Only players can execute this command.");
        }
        return false;
    }
}
