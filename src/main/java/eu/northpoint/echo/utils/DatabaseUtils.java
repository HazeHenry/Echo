package eu.northpoint.echo.utils;

import eu.northpoint.echo.Echo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseUtils {

    public static void setMessages(UUID uuid, String join, String leave) {
        try (PreparedStatement ps = Echo.getConnection().prepareStatement(
                "MERGE INTO player_messages (uuid, join_message, leave_message) KEY (uuid) VALUES (?, ?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, join);
            ps.setString(3, leave);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String[] getMessages(UUID uuid) {
        try (PreparedStatement ps = Echo.getConnection().prepareStatement(
                "SELECT join_message, leave_message FROM player_messages WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                            rs.getString("join_message"),
                            rs.getString("leave_message")
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[] {null, null};
    }
}
