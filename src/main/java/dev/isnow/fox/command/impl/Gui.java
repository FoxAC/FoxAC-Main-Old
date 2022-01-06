package dev.isnow.fox.command.impl;

import dev.isnow.fox.Fox;
import dev.isnow.fox.command.CommandInfo;
import dev.isnow.fox.command.FoxCommand;
import dev.isnow.fox.gui.GuiManager;
import dev.isnow.fox.util.ColorUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "gui", syntax = "", purpose = "Opens Interactive GUI")
public class Gui extends FoxCommand {
    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 2) {
            return false;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.translate("&cOnly players are allowed to execute this command!"));
            return true;
        }
        Player p = (Player) sender;
        try {
            if(PacketEvents.get().getServerUtils().getVersion().isNewerThan(ServerVersion.v_1_12_2)) {
//                GuiManagerV_1_13 gui = (GuiManagerV_1_13) Fox.INSTANCE.getGuiManager();
//                gui.openMainMenu(p);
            }
            else {
                GuiManager gui = (GuiManager) Fox.INSTANCE.getGuiManager();
                gui.openMainMenu(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(PacketEvents.get().getServerUtils().getVersion().isNewerThan(ServerVersion.v_1_12_2)) {
//            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        else {
            p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
        }
        return true;
    }
}
