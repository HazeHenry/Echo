package eu.northpoint.echo.utils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class StringUtils {

    public String process(String input) {
        if (input == null) return "";

        Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]{6})>");
        Matcher matcher = hexPattern.matcher(input);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);

        String message = sb.toString();

        IridiumColorAPI.process(message);
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    public String process(String input, boolean translate) {
        if (input == null) return "";

        Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]{6})>");
        Matcher matcher = hexPattern.matcher(input);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);

        String message = sb.toString();

        IridiumColorAPI.process(message);
        if (translate) message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    public static List<String> process(List<String> messages) {
        for (String line : messages) {
            messages.set(messages.indexOf(line), process(line));
        }
        return messages;
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

            if (net.md_5.bungee.api.ChatColor.stripColor(current + coloredWord + " ").length() > maxLength) {
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
