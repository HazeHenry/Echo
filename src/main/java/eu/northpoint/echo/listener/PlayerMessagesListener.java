package eu.northpoint.echo.listener;

import eu.northpoint.echo.Echo;
import eu.northpoint.echo.utils.DatabaseUtils;
import eu.northpoint.echo.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerMessagesListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String[] messages = DatabaseUtils.getMessages(e.getPlayer().getUniqueId());

        String joinMessage = messages[0];
        if (joinMessage == null || joinMessage.isEmpty()) return;

        joinMessage = joinMessage.replace("%name%", e.getPlayer().getName())
                        .replace("%prefix%", e.getPlayer().getDisplayName());

        if (Echo.getInstance().getConfig().getBoolean("ph-world-enabled")) {
            joinMessage = joinMessage.replace("%world%", e.getPlayer().getWorld().getName());
        } else {
            joinMessage = joinMessage.replace("%world%", "");
        }

        Bukkit.broadcastMessage(StringUtils.process(joinMessage));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        String[] messages = DatabaseUtils.getMessages(e.getPlayer().getUniqueId());

        String leaveMessage = messages[1];
        if (leaveMessage == null || leaveMessage.isEmpty()) return;

        leaveMessage = leaveMessage.replace("%name%", e.getPlayer().getName())
                        .replace("%prefix%", e.getPlayer().getDisplayName());

        if (Echo.getInstance().getConfig().getBoolean("ph-world-enabled")) {
            leaveMessage = leaveMessage.replace("%world%", e.getPlayer().getWorld().getName());
        } else {
            leaveMessage = leaveMessage.replace("%world%", "");
        }

        Bukkit.broadcastMessage(StringUtils.process(leaveMessage));
    }
}
