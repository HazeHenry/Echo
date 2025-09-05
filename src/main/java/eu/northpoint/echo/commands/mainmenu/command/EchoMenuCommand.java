package eu.northpoint.echo.commands.mainmenu.command;

import eu.northpoint.echo.Echo;
import eu.northpoint.echo.commands.mainmenu.structure.MenuItemBuilder;
import eu.northpoint.echo.gui.Gui;
import eu.northpoint.echo.localization.Messages;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

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

        openMainMenu(p);
        return true;
    }

    private Gui openMainMenu(Player p) {
        Gui gui = new Gui(Messages.format(Messages.MENU_TITLE), 5);

        gui.addClose();
        gui.fillBorder(Material.GRAY_STAINED_GLASS_PANE);
        gui.fillBottom(Material.GRAY_STAINED_GLASS_PANE);

        gui.addItem(MenuItemBuilder.joinMessageItem(), inventoryClickEvent -> handleMessage(p, "join"), 20);
        gui.addItem(MenuItemBuilder.leaveMessageItem(), inventoryClickEvent -> handleMessage(p, "leave"), 21);
        gui.addItem(MenuItemBuilder.messageInfoItem(p), inventoryClickEvent -> {}, 24);

        return gui.show(p);
    }

    private void handleMessage(Player p, String type) {
        if (!p.hasPermission("echo.token.1") && !p.hasPermission("echo.token.infinite")) {
            p.sendMessage(Messages.format(Messages.NO_PERMISSION));
            return;
        }

        p.closeInventory();
        p.setMetadata("echo.editactive", new FixedMetadataValue(Echo.getInstance(), true));
        activeMessageType.put(p.getUniqueId(), type);
        p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        p.sendTitle(Messages.format(Messages.TITLE_MAIN), Messages.format(Messages.TITLE_SUB), 30, 20000, 0);
    }
}
