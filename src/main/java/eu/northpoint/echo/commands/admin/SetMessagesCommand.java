package eu.northpoint.echo.commands.admin;

import eu.northpoint.echo.utils.DatabaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class SetMessagesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("echo.messages.set")) {
            sender.sendMessage("§cInsufficient permissions!");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("§e/setmessage <player> <join/leave> <message>");
            return false;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        String type = args[1];
        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if (type.equalsIgnoreCase("join")) {
            String leaveMessage = DatabaseUtils.getMessages(player.getUniqueId())[1];
            DatabaseUtils.setMessages(player.getUniqueId(), message, leaveMessage);
        } else if (type.equalsIgnoreCase("leave")) {
            String joinMessage = DatabaseUtils.getMessages(player.getUniqueId())[0];
            DatabaseUtils.setMessages(player.getUniqueId(), joinMessage, message);
        }

        sender.sendMessage("§aMessage set!");
        return true;
    }
}
