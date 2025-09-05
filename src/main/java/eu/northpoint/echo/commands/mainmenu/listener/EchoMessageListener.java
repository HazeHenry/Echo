package eu.northpoint.echo.commands.mainmenu.listener;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import eu.northpoint.echo.Echo;
import eu.northpoint.echo.commands.mainmenu.command.EchoMenuCommand;
import eu.northpoint.echo.gui.Gui;
import eu.northpoint.echo.localization.Messages;
import eu.northpoint.echo.utils.DatabaseUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class EchoMessageListener implements Listener {

    private HashMap<UUID, String> activeMessage = new HashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!p.hasMetadata("echo.editactive")) return;
        e.setCancelled(true);

        String message = e.getMessage()
                .replace("%name%", p.getName())
                .replace("%prefix%", p.getDisplayName());
        if (Echo.getInstance().getConfig().getBoolean("ph-world-enabled")) {
            message = message.replace("%world%", p.getWorld().getName());
        }

        if (message.equalsIgnoreCase("cancel")) {
            p.removeMetadata("echo.editactive", Echo.getInstance());
            EchoMenuCommand.getActiveMessageType().remove(p.getUniqueId());
            p.playSound(p, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            p.sendMessage(Messages.format(Messages.CANCEL_MESSAGE));
            return;
        }

        message = IridiumColorAPI.process(message);
        message = ChatColor.translateAlternateColorCodes('&', message);

        openConfirmationMenu(p, message);
        activeMessage.put(p.getUniqueId(), message);
    }

    private void openConfirmationMenu(Player p, String message) {
        Gui gui = new Gui("§2§lECHO §7┃ §aMessage Preview & Confirmation", 5);

        gui.fillBorder(Material.GRAY_STAINED_GLASS_PANE);
        gui.fillBottom(Material.GRAY_STAINED_GLASS_PANE);
        gui.addClose();

        gui.addItem(getConfirm(), inventoryClickEvent -> handleConfirm(p), 20);
        gui.addItem(getCancel(), inventoryClickEvent -> handleCancel(p), 21);
        gui.addItem(getRefund(), inventoryClickEvent -> handleRefund(p), 22);
        gui.addItem(getPreview(message), inventoryClickEvent -> {}, 24);

        gui.show(p);
    }

    // Items

    private ItemStack getPreview(String message) {
        ItemStack item = new ItemStack(Material.LIME_WOOL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lMESSAGE §7┃ §aPreview");
        meta.setLore(Arrays.asList(
                "",
                ChatColor.translateAlternateColorCodes('&', message)
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getConfirm() {
        ItemStack item = new ItemStack(Material.LIME_WOOL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a§l✔");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getCancel() {
        ItemStack item = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c§l✘");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getRefund() {
        ItemStack item = new ItemStack(Material.ORANGE_WOOL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§l⇄");
        item.setItemMeta(meta);
        return item;
    }

    private void handleRefund(Player p) {
        p.playSound(p, Sound.BLOCK_COMPOSTER_FILL, 1f,1f);
        p.closeInventory();
    }

    private void handleCancel(Player p) {
        p.removeMetadata("echo.editactive", Echo.getInstance());
        EchoMenuCommand.getActiveMessageType().remove(p.getUniqueId());
        p.sendTitle("", "");
        p.playSound(p, Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        p.sendMessage(Messages.format(Messages.CANCEL_MESSAGE));
        p.performCommand("echo");
    }

    private void handleConfirm(Player p) {
        if (!p.hasPermission("echo.token.1") && !p.hasPermission("echo.custommessage.unlimited")) {
            p.sendMessage(Messages.format(Messages.NO_PERMISSION));
            return;
        }

        p.removeMetadata("echo.editactive", Echo.getInstance());
        p.sendTitle("", "");
        p.closeInventory();
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1f, 0.8f);

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

        EchoMenuCommand.getActiveMessageType().remove(p.getUniqueId());
        activeMessage.remove(p.getUniqueId());
    }
}
