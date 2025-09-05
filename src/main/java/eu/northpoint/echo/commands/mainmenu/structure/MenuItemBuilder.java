package eu.northpoint.echo.commands.mainmenu.structure;

import eu.northpoint.echo.Echo;
import eu.northpoint.echo.localization.Messages;
import eu.northpoint.echo.utils.DatabaseUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuItemBuilder {

    public static ItemStack joinMessageItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (Echo.getInstance().getConfig().getBoolean("glowing-items")) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setDisplayName(Messages.format(Messages.MENU_ITEM_JOIN_MESSAGE_NAME));
        meta.setLore(Messages.format(Messages.MENU_ITEM_JOIN_MESSAGE_LORE));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack leaveMessageItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (Echo.getInstance().getConfig().getBoolean("glowing-items")) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setDisplayName(Messages.format(Messages.MENU_ITEM_LEAVE_MESSAGE_NAME));
        meta.setLore(Messages.format(Messages.MENU_ITEM_LEAVE_MESSAGE_LORE));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack messageInfoItem(Player p) {
        String[] messages = DatabaseUtils.getMessages(p.getUniqueId());
        String joinMessage = messages.length > 0 && messages[0] != null
                ? messages[0] : Messages.format(Messages.NO_MESSAGE_FOUND);
        String leaveMessage = messages.length > 1 && messages[1] != null
                ? messages[1] : Messages.format(Messages.NO_MESSAGE_FOUND);

        joinMessage = ChatColor.translateAlternateColorCodes('&', joinMessage);
        leaveMessage = ChatColor.translateAlternateColorCodes('&', leaveMessage);

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                Messages.format(Messages.MENU_ITEM_MESSAGE_INFO_NAME)));

        List<String> template = Messages.MENU_ITEM_MESSAGE_INFO_LORE;

        List<String> lore = new ArrayList<>();
        for (String line : template) {
            String replaced = line.replace("%joinmessage%", joinMessage)
                    .replace("%leavemessage%", leaveMessage);
            replaced = ChatColor.translateAlternateColorCodes('&', replaced);

            if (ChatColor.stripColor(replaced).length() > 35) {
                lore.addAll(wrapLore(replaced, 35));
            } else {
                lore.add(replaced);
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static List<String> wrapLore(String input, int maxLength) {
        List<String> lines = new ArrayList<>();
        if (input == null) return lines;

        StringBuilder current = new StringBuilder();
        String lastColor = "";

        for (String word : input.split(" ")) {
            Matcher matcher = Pattern.compile("§[0-9a-f]").matcher(word.toLowerCase());
            if (matcher.find()) {
                lastColor = matcher.group();
            }

            String coloredWord = lastColor + word;

            if (ChatColor.stripColor(current + coloredWord + " ").length() > maxLength) {
                lines.add(current.toString().trim());
                current = new StringBuilder();
            }

            current.append(coloredWord).append(" ");
        }

        if (!current.isEmpty()) {
            lines.add(lastColor + current.toString().trim());
        }

        return lines;
    }
}
