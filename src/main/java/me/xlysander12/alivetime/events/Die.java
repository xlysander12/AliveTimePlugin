package me.xlysander12.alivetime.events;

import me.xlysander12.alivetime.AliveTime;
import me.xlysander12.alivetime.Utils;
import me.xlysander12.alivetime.models.Achievement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Die implements Listener {

    static AliveTime plugin = AliveTime.getPlugin(AliveTime.class);

    @EventHandler
    public static void onDeath(PlayerDeathEvent event) {
        if(plugin.getConfig().getBoolean("Config.Show-Alive-Time-On-Dead")) {
            event.getEntity().sendMessage(plugin.getConfig().getString("Messages.Death-Message").replace("%time%", Utils.formatTime(Utils.getPlayerAliveTime(event.getEntity()))));
        }

        if(Utils.getPlayerAliveTime(event.getEntity()) > Utils.getPlayerHighscore(event.getEntity()) && plugin.getConfig().getBoolean("Config.Show-New-Highscore-On-Dead")) {
            event.getEntity().sendMessage(plugin.getConfig().getString("Messages.Death-Message-Highscore").replace("%old_highscore%", Utils.formatTime(Utils.getPlayerHighscore(event.getEntity()))));
        }
        Utils.clearPlayerAliveTime(event.getEntity());

        if(plugin.getConfig().getBoolean("Config.Achievements.Remove-On-Death")) {
            for(Achievement achievement: plugin.achievements) {
                Utils.removePlayerGroup(event.getEntity(), achievement.getGroupname());
            }
        }
    }
}
