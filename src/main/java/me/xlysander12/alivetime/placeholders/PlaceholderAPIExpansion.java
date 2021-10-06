package me.xlysander12.alivetime.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xlysander12.alivetime.AliveTime;
import me.xlysander12.alivetime.Utils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    private AliveTime plugin;

    public PlaceholderAPIExpansion(AliveTime plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "alivetime";
    }

    @Override
    public @NotNull String getAuthor() {
        return "xlysander12";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("alivetime")) {
//            return String.valueOf(new DecimalFormat("#").format(Math.floor(Utils.getPlayerAliveTime(player.getPlayer()))));
            return Utils.formatTime(Utils.getPlayerAliveTime(player.getPlayer()));
        }
        if(params.equalsIgnoreCase("highscore")) {
//            return String.valueOf(new DecimalFormat("#").format(Math.floor(Utils.getPlayerHighscore(player.getPlayer()))));
            return Utils.formatTime(Utils.getPlayerHighscore(player.getPlayer()));
        }
        return null;
    }
}
