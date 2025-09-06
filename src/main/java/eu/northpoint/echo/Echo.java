package eu.northpoint.echo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.northpoint.echo.commands.admin.SetMessagesCommand;
import eu.northpoint.echo.commands.mainmenu.command.EchoMenuCommand;
import eu.northpoint.echo.commands.mainmenu.listener.EchoMessageListener;
import eu.northpoint.echo.gui.GuiManager;
import eu.northpoint.echo.listener.PlayerMessagesListener;
import eu.northpoint.echo.localization.Messages;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Echo extends JavaPlugin {

    @Getter private static Echo instance;

    @Getter public static Connection connection;

    @Getter @Setter public static String lang;

    @Override
    public void onEnable() {
        instance = this;

        GuiManager.register(this);
        Bukkit.getPluginManager().registerEvents(new PlayerMessagesListener(), this);
        Bukkit.getPluginManager().registerEvents(new EchoMessageListener(), this);

        getCommand("echo").setExecutor(new EchoMenuCommand());
        getCommand("setmessage").setExecutor(new SetMessagesCommand());

        connect();
        saveConfig();
        setLanguage();

        if (getConfig().getBoolean("check-updates")) {
            checkForUpdates();
        }
    }

    @Override
    public void onDisable() {
        instance = null;
        GuiManager.unregister(this);
        disconnect();
    }

    private void setLanguage() {
        lang = getConfig().getString("lang");
        if (!Messages.supportedLanguages.contains(lang)) {
            lang = "en";
        }
        Messages.load(lang);
    }

    private void connect() {
        try {
            File dbFile = new File(getDataFolder(), "messages");
            if (!dbFile.getParentFile().exists()) dbFile.getParentFile().mkdirs();

            Class.forName("eu.northpoint.echo.lib.h2.Driver");

            String dbUrl = "jdbc:h2:" + dbFile.getAbsolutePath().replace("\\", "/");
            connection = DriverManager.getConnection(dbUrl, "sa", "");

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_messages (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "join_message VARCHAR(255), " +
                        "leave_message VARCHAR(255)" +
                        ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().severe("Could not initialize H2 database!");
        }  catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkForUpdates() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.github.com/repos/HazeHenry/Echo/releases/latest");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
                    InputStreamReader reader = new InputStreamReader(con.getInputStream());
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    String latest = json.get("tag_name").getAsString();
                    reader.close();

                    String current = getDescription().getVersion();
                    if (!latest.equals("v" + current)) {
                        getLogger().info("§eA new version of Echo is available: §6" + latest);
                    } else {
                        getLogger().info("§aEcho is up to date!");
                    }
                } catch (Exception e) {
                    getLogger().warning("Failed to check for updates: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(this);
    }

    public void reload() {
        getLogger().info("Reloading plugin...");

        reloadConfig();
        saveConfig();

        setLanguage();
        checkForUpdates();

        getLogger().info("Plugin reloaded!");
    }

}
