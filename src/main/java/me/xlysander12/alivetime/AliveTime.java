package me.xlysander12.alivetime;

import me.xlysander12.alivetime.commands.AliveTimeCommand;
import me.xlysander12.alivetime.commands.HighscoreCommand;
import me.xlysander12.alivetime.events.Die;
import me.xlysander12.alivetime.events.JoinLeave;
import me.xlysander12.alivetime.placeholders.PlaceholderAPIExpansion;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;

public final class AliveTime extends JavaPlugin {

    public HashMap<Player, Double> playerTimes = new HashMap<>();
    // private ArrayList<Player> already_got_achievement = new ArrayList<>();
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

                    if(getConfig().getBoolean("Config.Achievement.Set-Group") && Utils.getPlayerAliveTime(player) > Utils.parseConfigAchievementStringToSeconds() && !configManager.getPlayers().getBoolean(player.getUniqueId() + ".Achievement")) {
                        Utils.setPlayerGroup(player, getConfig().getString("Config.Achievement.Group"));
                        player.sendMessage(getConfig().getString("Messages.Achievement-Completed").replace("%time%", Utils.formatTime(Utils.parseConfigAchievementStringToSeconds())).replace("%group%", getConfig().getString("Config.Achievement.Group")));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                        configManager.getPlayers().set(player.getUniqueId() + ".Achievement", true);
                        configManager.savePlayers();
                    }
                }
                // System.out.println(playerTimes.toString());
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


}
