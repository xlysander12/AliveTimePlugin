package me.xlysander12.alivetime;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {

    static AliveTime plugin = AliveTime.getPlugin(AliveTime.class);

    public static double getPlayerAliveTime(Player player) {
        if(plugin.playerTimes.containsKey(player)) {
            return plugin.playerTimes.get(player);
        } else {
            return plugin.configManager.getPlayers().getDouble(player.getUniqueId() + ".Time");
        }
    }

    public static void clearPlayerAliveTime(Player player) {
        double highscore = plugin.configManager.getPlayers().getLong(player.getUniqueId() + ".Highscore");
        double playerTime = getPlayerAliveTime(player);
        if(playerTime > highscore) { // Check if current time is higher than their Highscore
            setPlayerHighscore(player, playerTime); // Update Highscore
        }

        if(plugin.playerTimes.containsKey(player)) { // Reset Time
            plugin.playerTimes.replace(player, 0.0);
        }

        plugin.configManager.getPlayers().set(player.getUniqueId() + ".Achievement", false);
        plugin.configManager.savePlayers();

    }

    private static void setPlayerHighscore(Player player, double score) {
        plugin.configManager.getPlayers().set(player.getUniqueId() + ".Highscore", score);
        plugin.configManager.savePlayers();
    }

    public static double getPlayerHighscore(Player player) {
        double configTime = plugin.configManager.getPlayers().getDouble(player.getUniqueId() + ".Highscore");
        double mapTime = 0;
        try {
            mapTime = plugin.playerTimes.get(player);
        } catch (Exception ignored) {}

        return Math.max(mapTime, configTime);
    }

    public static String formatTime(double seconds) {
        StringBuilder builder = new StringBuilder();
        int days = (int) Math.floor(seconds / (24 * 3600));
        seconds %= 24 * 3600;
        int hours = (int) Math.floor(seconds / 3600);
        seconds %= 3600;
        int minutes = (int) Math.floor(seconds / 60);
        seconds %= 60;

        if(!(days == 0)) {
            builder.append(days).append(" days ");
        }
        if(!(hours == 0)) {
            builder.append(hours).append(" hours ");
        }
        if(!(minutes == 0)) {
            builder.append(minutes).append(" minutes ");
        }
        if(!(seconds == 0)) {
            builder.append((int) seconds).append(" seconds");
        }

        return builder.toString();
    }

    public static void setPlayerGroup(Player player, String groupname) {
        Group group = ((LuckPerms) plugin.permsAPI).getGroupManager().getGroup(groupname);
        if(group == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Couldn't find the group " + ChatColor.DARK_RED + groupname + ChatColor.RED + ". Make sure you spell it right in the config.yml");
            return;
        }
        ((LuckPerms) plugin.permsAPI).getUserManager().modifyUser(player.getUniqueId(), (User user) -> {
            Node node = InheritanceNode.builder(groupname).build();
            user.data().add(node);
        });
    }

    public static void removePlayerGorup(Player player, String groupname) {
        Group group = ((LuckPerms) plugin.permsAPI).getGroupManager().getGroup(groupname);
        if(group == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Couldn't find the group " + ChatColor.DARK_RED + groupname + ChatColor.RED + ". Make sure you spell it right in the config.yml");
            return;
        }

        ((LuckPerms) plugin.permsAPI).getUserManager().modifyUser(player.getUniqueId(), (User user) -> {
            Node node = InheritanceNode.builder(groupname).build();
            user.data().remove(node);
        });
    }

    public static double parseConfigAchievementStringToSeconds() {
        String configString = plugin.getConfig().getString("Config.Achievement.Time");
        double seconds = 0;
        int i = 0;
        for(String sub: configString.split(":")) {
            switch (i++) {
                case 0:
                    seconds += Integer.parseInt(sub) * 86400;
                    break;
                case 1:
                    seconds += Integer.parseInt(sub) * 3600;
                    break;
                case 2:
                    seconds += Integer.parseInt(sub) * 60;
                    break;
                case 3:
                    seconds += Integer.parseInt(sub);
                    break;
            }
        }
        return seconds;
    }
}
