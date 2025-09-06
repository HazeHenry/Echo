package eu.northpoint.echo.commands.mainmenu.structure;

import eu.northpoint.echo.Echo;
import eu.northpoint.echo.localization.Messages;
import eu.northpoint.echo.utils.DatabaseUtils;
import eu.northpoint.echo.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuItemBuilder {

    public static ItemStack joinMessageItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (Echo.getInstance().getConfig().getBoolean("glowing-items")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        meta.setDisplayName(StringUtils.process(Messages.MENU_ITEM_JOIN_MESSAGE_NAME));
        meta.setLore(StringUtils.process(Messages.MENU_ITEM_JOIN_MESSAGE_LORE));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack leaveMessageItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (Echo.getInstance().getConfig().getBoolean("glowing-items")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        meta.setDisplayName(StringUtils.process(Messages.MENU_ITEM_LEAVE_MESSAGE_NAME));
        meta.setLore(StringUtils.process(Messages.MENU_ITEM_LEAVE_MESSAGE_LORE));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack messageInfoItem(Player p) {
        String[] messages = DatabaseUtils.getMessages(p.getUniqueId());
        String joinMessage = messages.length > 0 && messages[0] != null
                ? messages[0] : StringUtils.process(Messages.NO_MESSAGE_FOUND);
        String leaveMessage = messages.length > 1 && messages[1] != null
                ? messages[1] : StringUtils.process(Messages.NO_MESSAGE_FOUND);

        joinMessage = StringUtils.process(joinMessage);
        leaveMessage = StringUtils.process(leaveMessage);

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtils.process(Messages.MENU_ITEM_MESSAGE_INFO_NAME));

        List<String> template = Messages.MENU_ITEM_MESSAGE_INFO_LORE;

        List<String> lore = new ArrayList<>();
        for (String line : template) {
            String replaced = line.replace("%joinmessage%", joinMessage)
                    .replace("%leavemessage%", leaveMessage);
            replaced = StringUtils.process(replaced);

            if (ChatColor.stripColor(replaced).length() > 35) {
                lore.addAll(StringUtils.wrapLore(replaced, 35));
            } else {
                lore.add(replaced);
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack generalInfoItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringUtils.process(Messages.MENU_ITEM_GENERAL_INFO_NAME));

        List<String> lore = new ArrayList<>();
        for (String line : Messages.MENU_ITEM_GENERAL_INFO_LORE) {
            lore.add(StringUtils.process(line, false));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
