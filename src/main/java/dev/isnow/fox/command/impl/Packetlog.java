package dev.isnow.fox.command.impl;

import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Packetlog extends FoxCommand {
    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final PlayerData data = PlayerDataManager.getInstance().getPlayerData(player);

            if (data != null) {
                if (AlertManager.togglePacketlog(data) == AlertManager.ToggleAlertType.ADD) {
                    sendMessage(sender, "ON");
                } else {
                    sendMessage(sender, "OFF");
                }
                return true;
            }

        } else {
            sendMessage(sender, "Only players can execute this command.");
        }
        return false;
    }
}
