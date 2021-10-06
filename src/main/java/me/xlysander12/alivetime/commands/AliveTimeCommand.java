package me.xlysander12.alivetime.commands;

import me.xlysander12.alivetime.AliveTime;
import me.xlysander12.alivetime.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AliveTimeCommand implements CommandExecutor {

    AliveTime plugin = AliveTime.getPlugin(AliveTime.class);
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if(args.length < 1) { // Use self as target player
            if(!sender.hasPermission("alivetime.time")) { // Making sure sender has permission to see own time
                sender.sendMessage(plugin.getConfig().getString("Messages.No-Permission"));
                return true;
            }
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfig().getString("Messages.Not-Player"));
                return true;
            }
            player = (Player) sender;
        } else { // Use mentioned player as target
            if(!sender.hasPermission("alivetime.time.others")) { // Making sure sender has permission to see others' time
                sender.sendMessage(plugin.getConfig().getString("Messages.No-Permission"));
                return true;
            }
            player = Bukkit.getPlayer(args[0]);
    }

        if (player == null) { // If couldn't get the player, error it
            sender.sendMessage(plugin.getConfig().getString("Messages.Player-Not-Found"));
            return true;
        }

        sender.sendMessage(plugin.getConfig().getString("Messages.Alivetime-Command").replace("%player%", player.getName()).replace("%time%", Utils.formatTime(Utils.getPlayerAliveTime(player))));
        return false;
    }
}
