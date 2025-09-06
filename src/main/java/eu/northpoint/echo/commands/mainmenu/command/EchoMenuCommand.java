package eu.northpoint.echo.commands.mainmenu.command;

import eu.northpoint.echo.Echo;
import eu.northpoint.echo.commands.mainmenu.structure.MenuItemBuilder;
import eu.northpoint.echo.gui.Gui;
import eu.northpoint.echo.localization.Messages;
import eu.northpoint.echo.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EchoMenuCommand implements CommandExecutor {

    @Getter public static HashMap<UUID, String> activeMessageType = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cYou must be a player to use this command.");
            return false;
        }

        if (args.length < 1) {
            openMainMenu(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("echo.admin")) {
                sender.sendMessage("§cInsufficient permissions!");
                return false;
            }

            Echo.getInstance().reload();
            sender.sendMessage("§aPlugin reloaded!");
            return true;
        }

        return true;
    }

    private Gui openMainMenu(Player p) {
        Gui gui = new Gui(StringUtils.process(Messages.MENU_TITLE), 5);

        gui.fillBorder(Material.GRAY_STAINED_GLASS_PANE);
        gui.fillBottom(Material.GRAY_STAINED_GLASS_PANE);
        gui.addClose();

        gui.addItem(MenuItemBuilder.joinMessageItem(), inventoryClickEvent -> handleMessage(p, "join"), 20);
        gui.addItem(MenuItemBuilder.leaveMessageItem(), inventoryClickEvent -> handleMessage(p, "leave"), 21);
        gui.addItem(MenuItemBuilder.messageInfoItem(p), inventoryClickEvent -> p.playSound(p, Sound.ENTITY_PUFFER_FISH_FLOP, 0.5f,1f), 24);
        gui.addItem(MenuItemBuilder.generalInfoItem(), inventoryClickEvent -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f,1f), 44);

        return gui.show(p);
    }

    private void handleMessage(Player p, String type) {
        if (!p.hasPermission("echo.token.1") && !p.hasPermission("echo.token.infinite")) {
            p.sendMessage(StringUtils.process(Messages.NO_PERMISSION));
            return;
        }

        p.closeInventory();
        activeMessageType.put(p.getUniqueId(), type);
        p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        p.sendTitle(StringUtils.process(Messages.TITLE_MAIN), StringUtils.process(Messages.TITLE_SUB), 30, 200000, 0);
    }
}
