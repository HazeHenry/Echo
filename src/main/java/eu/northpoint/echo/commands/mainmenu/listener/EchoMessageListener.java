package eu.northpoint.echo.commands.mainmenu.listener;

import eu.northpoint.echo.Echo;
import eu.northpoint.echo.commands.mainmenu.command.EchoMenuCommand;
import eu.northpoint.echo.gui.Gui;
import eu.northpoint.echo.localization.Messages;
import eu.northpoint.echo.utils.DatabaseUtils;
import eu.northpoint.echo.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EchoMessageListener implements Listener {

    private HashMap<UUID, String> activeMessage = new HashMap<>();

    @EventHandler
    public void onChat(PlayerChatEvent e) {
        Player p = e.getPlayer();

        if (!EchoMenuCommand.getActiveMessageType().containsKey(p.getUniqueId())) return;

        e.setCancelled(true);

        String message = e.getMessage()
                .replace("%name%", p.getName())
                .replace("%prefix%", p.getDisplayName());
        if (Echo.getInstance().getConfig().getBoolean("ph-world-enabled")) {
            message = message.replace("%world%", p.getWorld().getName());
        } else {
            message = message.replace("%world%", "");
        }

        if (message.equalsIgnoreCase("cancel")) {
            handleCancel(p);
            return;
        }

        message = message.substring(0, Math.min(message.length(), Echo.getInstance().getConfig().getInt("max-message-length")));

        message = StringUtils.process(message);

        openConfirmationMenu(p, message);
        activeMessage.put(p.getUniqueId(), message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        activeMessage.remove(e.getPlayer().getUniqueId());
        e.getPlayer().sendTitle("", "");
    }

    private void openConfirmationMenu(Player p, String message) {
        Gui gui = new Gui(StringUtils.process(Messages.PREVIEW_MENU_TITLE), 5);

        gui.fillBorder(Material.GRAY_STAINED_GLASS_PANE);
        gui.fillBottom(Material.GRAY_STAINED_GLASS_PANE);
        gui.addClose();

        gui.addItem(getConfirm(), inventoryClickEvent -> handleConfirm(p), 20);
        gui.addItem(getCancel(), inventoryClickEvent -> handleCancel(p), 21);
        gui.addItem(getRefund(), inventoryClickEvent -> handleRefund(p), 22);
        gui.addItem(getPreview(message), inventoryClickEvent -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1f,1f), 24);

        gui.show(p);
    }

    private ItemStack getPreview(String message) {
        ItemStack item = new ItemStack(Material.LIME_WOOL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtils.process(Messages.PREVIEW_MENU_TITLE));
        List<String> lore = new ArrayList<>();
        lore.add("");
        if (ChatColor.stripColor(message).length() > 35) {
            lore.addAll(StringUtils.wrapLore(message, 35));
        } else {
            lore.add(message);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getConfirm() {
        ItemStack item = new ItemStack(Material.LIME_WOOL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtils.process(Messages.PREVIEW_CONFIRM_ITEM));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getCancel() {
        ItemStack item = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtils.process(Messages.PREVIEW_CANCEL_ITEM));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getRefund() {
        ItemStack item = new ItemStack(Material.ORANGE_WOOL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtils.process(Messages.PREVIEW_REFUND_ITEM));
        item.setItemMeta(meta);
        return item;
    }

    private void handleRefund(Player p) {
        p.playSound(p, Sound.BLOCK_COMPOSTER_FILL, 1f,1f);
        p.closeInventory();
    }

    private void handleCancel(Player p) {
        p.closeInventory();
        EchoMenuCommand.getActiveMessageType().remove(p.getUniqueId());
        p.sendTitle("", "");
        p.playSound(p, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        p.sendMessage(StringUtils.process(Messages.CANCEL_MESSAGE));
    }

    private void handleConfirm(Player p) {
        if (!p.hasPermission("echo.token.1") && !p.hasPermission("echo.token.infinite")) {
            p.sendMessage(StringUtils.process(Messages.NO_PERMISSION));
            return;
        }

        p.sendTitle("", "");
        p.closeInventory();
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1f, 0.8f);
        if (p.hasPermission("echo.token.1"))
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"lp user " + p.getName() + " permission unset echo.token.1");

        String[] messages = DatabaseUtils.getMessages(p.getUniqueId());
        switch (EchoMenuCommand.getActiveMessageType().get(p.getUniqueId())) {
            case "join":
                DatabaseUtils.setMessages(
                        p.getUniqueId(),
                        activeMessage.get(p.getUniqueId()),
                        messages[1]
                );
                break;
            case "leave":
                DatabaseUtils.setMessages(
                        p.getUniqueId(),
                        messages[0],
                        activeMessage.get(p.getUniqueId())
                );
                break;
        }
        p.sendMessage(StringUtils.process(Messages.MESSAGE_SET_SUCCESS));

        EchoMenuCommand.getActiveMessageType().remove(p.getUniqueId());
        activeMessage.remove(p.getUniqueId());
    }
}
