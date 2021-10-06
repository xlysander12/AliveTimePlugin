package me.xlysander12.alivetime.commands;

import me.xlysander12.alivetime.AliveTime;
import me.xlysander12.alivetime.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HighscoreCommand implements CommandExecutor {
    AliveTime plugin = AliveTime.getPlugin(AliveTime.class);
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(args.length < 1) {
            if(!(sender.hasPermission("alivetime.highscore"))) { // Making sure sender has permission to see own highscore
                sender.sendMessage(plugin.getConfig().getString("Messages.No-Permission"));
                return true;
            }
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfig().getString("Messages.Not-Player"));
                return true;
            }
            player = (Player) sender;

        } else {
            if(!(sender.hasPermission("alivetime.highscore.others"))) {
                sender.sendMessage(plugin.getConfig().getString("Messages.No-Permission"));
                return true;
            }
            player = Bukkit.getPlayer(args[0]);
        }

        if (player == null) {
            sender.sendMessage(plugin.getConfig().getString("Messages.Player-Not-Found"));
            return true;
        }

        sender.sendMessage(plugin.getConfig().getString("Messages.Highscore-Command").replace("%player%", player.getName()).replace("%highscore%", Utils.formatTime(Utils.getPlayerHighscore(player))));
        return false;
    }
}
