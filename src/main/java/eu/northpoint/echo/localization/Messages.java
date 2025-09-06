package eu.northpoint.echo.localization;

import eu.northpoint.echo.Echo;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Messages {

    public static List<String> supportedLanguages = Arrays.asList("en", "hu");

    public static String CANCEL_MESSAGE;
    public static String SET_JOIN_PROMPT;
    public static String SET_LEAVE_PROMPT;
    public static String RESET_CONFIRMATION;
    public static String MESSAGE_SET_SUCCESS;

    public static String MENU_TITLE;
    public static String MENU_JOIN_MESSAGE;
    public static String MENU_LEAVE_MESSAGE;
    public static String MENU_RESET_MESSAGES;

    public static String PREVIEW_MENU_TITLE;
    public static String PREVIEW_CONFIRM_ITEM;
    public static String PREVIEW_CANCEL_ITEM;
    public static String PREVIEW_REFUND_ITEM;

    public static String NO_PERMISSION;
    public static String NO_MESSAGE_FOUND;

    public static String MENU_ITEM_JOIN_MESSAGE_NAME;
    public static List<String> MENU_ITEM_JOIN_MESSAGE_LORE;
    public static String MENU_ITEM_LEAVE_MESSAGE_NAME;
    public static List<String> MENU_ITEM_LEAVE_MESSAGE_LORE;
    public static String MENU_ITEM_MESSAGE_INFO_NAME;
    public static List<String> MENU_ITEM_MESSAGE_INFO_LORE;
    public static String MENU_ITEM_GENERAL_INFO_NAME;
    public static List<String> MENU_ITEM_GENERAL_INFO_LORE;

    public static String TITLE_MAIN;
    public static String TITLE_SUB;

    public static void load(String language) {
        Echo instance = Echo.getInstance();

        File langFile = new File(Echo.getInstance().getDataFolder(), "lang/lang_" + language + ".yml");
        if (!langFile.exists()) {
            instance.saveResource("lang/lang_" + language + ".yml", false);
            instance.getLogger().warning(
                    "§cCould not find language file for " + language + ". Applying fallback messages, some messages could be missing!" +
                       "\nDownload a fresh copy of language files from SpigotMC page."
            );
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(langFile);

        CANCEL_MESSAGE = config.getString("cancel-message", "&cMessage editing cancelled.");
        SET_JOIN_PROMPT = config.getString("set-join-prompt", "&aType your &6join &amessage in chat. &7Type '&ccancel&7' to abort.");
        SET_LEAVE_PROMPT = config.getString("set-leave-prompt", "&aType your &6leave &amessage in chat. &7Type '&ccancel&7' to abort.");
        RESET_CONFIRMATION = config.getString("reset-confirmation", "&eYour join and leave messages have been reset.");
        MESSAGE_SET_SUCCESS = config.getString("message-set-success", "§aSuccessfully set message!");

        MENU_TITLE = config.getString("menu-title", "&2&lECHO &7┃ &aMain Menu");
        MENU_JOIN_MESSAGE = config.getString("menu-join-message", "&aSet &6Join &aMessage");
        MENU_LEAVE_MESSAGE = config.getString("menu-leave-message", "&aSet &6Leave &aMessage");
        MENU_RESET_MESSAGES = config.getString("menu-reset-messages", "&aReset &6Messages");

        PREVIEW_MENU_TITLE = config.getString("preview-menu-title", "&2&lECHO &7┃ &6Preview");
        PREVIEW_CONFIRM_ITEM = config.getString("preview-confirm-item", "&a&l✔");
        PREVIEW_CANCEL_ITEM = config.getString("preview-cancel-item", "&c&l✘");
        PREVIEW_REFUND_ITEM = config.getString("preview-refund-item", "&6&l⇄");

        NO_PERMISSION = config.getString("no-permission", "&cYou don't have permission to set your messages!");
        NO_MESSAGE_FOUND = config.getString("no-message-found", "&cNo message yet!");

        MENU_ITEM_JOIN_MESSAGE_NAME = config.getString("menu-item-join-message.name", "&aSet &6Join &aMessage");
        MENU_ITEM_JOIN_MESSAGE_LORE = new ArrayList<>(config.getStringList("menu-item-join-message.lore"));
        MENU_ITEM_LEAVE_MESSAGE_NAME = config.getString("menu-item-leave-message.name", "&aSet &6Leave &aMessage");
        MENU_ITEM_LEAVE_MESSAGE_LORE = new ArrayList<>(config.getStringList("menu-item-leave-message.lore"));
        MENU_ITEM_MESSAGE_INFO_NAME = config.getString("menu-item-message-info.name", "&aCurrent Messages");
        MENU_ITEM_MESSAGE_INFO_LORE = new ArrayList<>(config.getStringList("menu-item-message-info.lore"));
        MENU_ITEM_GENERAL_INFO_NAME = config.getString("menu-item-general-info.name", "&aInformation");
        MENU_ITEM_GENERAL_INFO_LORE = new ArrayList<>(config.getStringList("menu-item-general-info.lore"));

        TITLE_MAIN = instance.getConfig().getString("title-main", "&aMessage Editing");
        TITLE_SUB = instance.getConfig().getString("title-sub", "&7Type your desired &6join&7/&6leave &7message in the chat.");
    }
}
