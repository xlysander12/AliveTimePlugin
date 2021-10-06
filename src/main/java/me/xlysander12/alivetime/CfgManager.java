package me.xlysander12.alivetime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CfgManager {
    public AliveTime plugin = AliveTime.getPlugin(AliveTime.class);

    private FileConfiguration playerscfg;
    private File playersfile;

    public void setup() {
        if(!(plugin.getDataFolder().exists())) {
            plugin.getDataFolder().mkdir();
        }

        playersfile = new File(plugin.getDataFolder(), "players.yml");

        if(!playersfile.exists()) {
            try {
                playersfile.createNewFile();
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Couldn't create players.yml file. Shutting down...");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }

        playerscfg = YamlConfiguration.loadConfiguration(playersfile);
    }

    public FileConfiguration getPlayers() {
        return playerscfg;
    }

    public void savePlayers() {
        try {
            playerscfg.save(playersfile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Couldn't save the players.yml file");
        }
    }

    public void reloadPlayers() {
        playerscfg = YamlConfiguration.loadConfiguration(playersfile);
    }
}
