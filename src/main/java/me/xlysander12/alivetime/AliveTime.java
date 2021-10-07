package me.xlysander12.alivetime;

import me.xlysander12.alivetime.commands.AliveTimeCommand;
import me.xlysander12.alivetime.commands.HighscoreCommand;
import me.xlysander12.alivetime.events.Die;
import me.xlysander12.alivetime.events.JoinLeave;
import me.xlysander12.alivetime.models.Achievement;
import me.xlysander12.alivetime.placeholders.PlaceholderAPIExpansion;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public final class AliveTime extends JavaPlugin {

    public HashMap<Player, Double> playerTimes = new HashMap<>();
    // private ArrayList<Player> already_got_achievement = new ArrayList<>();
    public ArrayList<Achievement> achievements = new ArrayList<>();
    public Object permsAPI;
    public CfgManager configManager;

    @Override
    public void onEnable() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) {
            getConfig().options().copyDefaults(true);
        }
        saveDefaultConfig();

        configManager = new CfgManager();
        configManager.setup();
        configManager.savePlayers();
        configManager.reloadPlayers();

        loadAchievements();

        Bukkit.getPluginManager().registerEvents(new JoinLeave(), this);
        Bukkit.getPluginManager().registerEvents(new Die(), this);
        getCommand("alivetime").setExecutor(new AliveTimeCommand());
        getCommand("highscore").setExecutor(new HighscoreCommand());
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "PlaceholderAPI found. Registering placeholders");
            new PlaceholderAPIExpansion(this).register();
        }

        if(Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            permsAPI = LuckPermsProvider.get();
        }
        countPlayerTimes();
        keepConfigUpdated();
    }

    @Override
    public void onDisable() {
        for(Player player: playerTimes.keySet()) {
            configManager.getPlayers().set(player.getUniqueId() + ".Time", playerTimes.get(player));
        }
        configManager.savePlayers();
    }

    private void countPlayerTimes() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player: Bukkit.getOnlinePlayers()) {
                    if(!player.isDead()) {
                        double sum = Utils.getPlayerAliveTime(player) + 0.5;
                        // System.out.println("Sum for player " + player.getName() + ": " + sum);
                        if(playerTimes.containsKey(player)) {
                            // System.out.println("Key already in hashmap");
                            playerTimes.replace(player, sum);
                        } else {
                            // System.out.println("Key not in hash");
                            playerTimes.put(player, sum);
                        }
                    }

                    if(getConfig().getBoolean("Config.Achievements.Set-Group")) {
                        for(Achievement achievement: achievements) {
                            if(Utils.getPlayerAliveTime(player) > achievement.getAlivetime() && !configManager.getPlayers().getBoolean(player.getUniqueId() + ".Achievements." + achievement.getId())) {
                                Utils.setPlayerGroup(player, achievement.getGroupname());
                                configManager.getPlayers().set(player.getUniqueId() + ".Achievements." + achievement.getId(), true);
                                configManager.savePlayers();
                                if(achievement.isSendMessage()) {
                                    player.sendMessage(getConfig().getString("Messages.Achievement-Completed").replace("%time%", Utils.formatTime(achievement.getAlivetime())).replace("%group%", achievement.getGroupname()));
                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                                }
                                if(getConfig().getBoolean("Config.Achievements.Replace-Group")) {
                                    if (achievement.getId() >= 2) {
                                        Achievement lastAchievement = getAchievementById(achievement.getId() - 1);
                                        Utils.removePlayerGroup(player, lastAchievement.getGroupname());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 10L);
    }

    private void keepConfigUpdated() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for(Player player: playerTimes.keySet()) {
                    configManager.getPlayers().set(player.getUniqueId() + ".Time", playerTimes.get(player));
                }
                configManager.savePlayers();
            }
        }.runTaskTimerAsynchronously(this, 0L, 6000L);
    }

    private void loadAchievements() {
        for(int i = 1; true; i++) {
            ConfigurationSection section = getConfig().getConfigurationSection("Config.Achievements." + i);
            if(section == null) {
                System.out.println("Ended looking for achievements");
                break;
            }
            achievements.add(new Achievement(i, section.getString("Group"), Utils.parseConfigAchievementStringToSeconds(section.getString("Time")), section.getBoolean("Send-Message")));
            System.out.println("Added Achievement number " + i);
        }
    }

    public Achievement getAchievementById(int id) {
        if(id > achievements.size()) {
            return null;
        }

        for(Achievement achievement: achievements) {
            if(achievement.getId() == id) {
                return achievement;
            }
        }
        return null;
    }
}
