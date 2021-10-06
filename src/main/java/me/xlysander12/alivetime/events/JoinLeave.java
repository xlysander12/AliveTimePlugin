package me.xlysander12.alivetime.events;

import me.xlysander12.alivetime.AliveTime;
import me.xlysander12.alivetime.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeave implements Listener {

    static AliveTime plugin = AliveTime.getPlugin(AliveTime.class);

    @EventHandler
    public static void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        double playertime = Utils.getPlayerAliveTime(player);
        plugin.playerTimes.remove(player);
        plugin.configManager.getPlayers().set(player.getUniqueId() + ".Time", playertime);
        plugin.configManager.savePlayers();
    }
}
