package net.novelmc.novelengine.banning;

import lombok.Getter;
import lombok.Setter;
import net.novelmc.novelengine.util.NLog;
import net.novelmc.novelengine.util.NUtil;
import net.novelmc.novelengine.util.NovelBase;
import net.novelmc.novelengine.util.SQLManager;
import org.bukkit.ChatColor;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class Ban extends NovelBase
{

    @Getter
    @Setter
    private String UUID;
    @Getter
    @Setter
    private String ip;
    @Getter
    @Setter
    private String by;
    @Getter
    @Setter
    private String reason;
    @Getter
    @Setter
    private String UID;
    @Getter
    @Setter
    private Date expiry;
    @Getter
    @Setter
    private BanType type = BanType.NORMAL;

    public String getKickMessage()
    {
        if (type == BanType.PERMANENT_IP)
        {
            return ChatColor.RED
                    + "Your IP address is currently permanently banned from this server." + NEW_LINE
                    + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + (reason != null ? reason : "Reason not specified") + NEW_LINE
                    + ChatColor.GRAY + "More Information: " + ChatColor.GOLD + ChatColor.UNDERLINE + "https://novelmc.net/docs/support/" + NEW_LINE
                    + ChatColor.GRAY + "Ban ID: " + UID;
        }
        if (type == BanType.PERMANENT_NAME)
        {
            return ChatColor.RED
                    + "Your uuid is currently permanently banned from this server." + NEW_LINE
                    + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + (reason != null ? reason : "Reason not specified") + NEW_LINE
                    + ChatColor.GRAY + "More Information: " + ChatColor.GOLD + ChatColor.UNDERLINE + "https://novelmc.net/docs/support/" + NEW_LINE
                    + ChatColor.GRAY + "Ban ID: " + UID;
        }
        if (type == BanType.IP)
        {
            return ChatColor.RED
                    + "Your IP address is currently banned from this server." + NEW_LINE
                    + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + (reason != null ? reason : "Reason not specified") + NEW_LINE
                    + ChatColor.GRAY + "Your ban will expire on: " + ChatColor.GOLD + NUtil.DATE_FORMAT.format(expiry) + NEW_LINE
                    + ChatColor.GRAY + "More Information: " + ChatColor.GOLD + ChatColor.UNDERLINE + "https://novelmc.net/docs/support/" + NEW_LINE
                    + ChatColor.GRAY + "Ban ID: " + UID;
        }

        // Normal ban
        return ChatColor.RED
                + "You are currently banned from this server." + NEW_LINE
                + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + (reason != null ? reason : "Reason not specified") + NEW_LINE
                + ChatColor.GRAY + "Your ban will expire on: " + ChatColor.GOLD + NUtil.DATE_FORMAT.format(expiry) + NEW_LINE
                + ChatColor.GRAY + "More Information: " + ChatColor.GOLD + ChatColor.UNDERLINE + "https://novelmc.net/docs/support/" + NEW_LINE
                + ChatColor.GRAY + "Ban ID: " + UID;
    }

    public String makeUID()
    {
        return BanUIDGen.idGen(type);
    }

    public boolean isExpired()
    {
        if (expiry == null)
        {
            return false;
        }

        return expiry.after(new Date());
    }

    public void save()
    {
        if (config.isSQLEnabled())
        {
            Connection c = SQLManager.getConnection();
            try
            {
                makeUID();
                PreparedStatement statement = c.prepareStatement("INSERT INTO bans (uuid, ip, `by`, reason, expiry, type) VALUES (?, ?, ?, ?, ?, ?)");
                statement.setString(1, UUID);
                statement.setString(2, ip);
                statement.setString(3, by);
                statement.setString(4, reason);
                statement.setString(5, UID);
                statement.setLong(6, expiry.getTime());
                statement.setString(7, type.toString());
                statement.executeUpdate();
            } catch (SQLException ex)
            {
                NLog.severe(ex);
            }
        } else
        {
            JSONObject database = plugin.sqlManager.getDatabase();
            JSONObject bans = database.getJSONObject("bans");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uuid", UUID);
            jsonObject.put("ip", ip);
            jsonObject.put("by", by);
            jsonObject.put("reason", reason);
            jsonObject.put("uid", UID);
            jsonObject.put("expiry", expiry);
            jsonObject.put("type", type);

            bans.put(UUID, jsonObject);
            database.put("bans", bans);
            plugin.sqlManager.saveDatabase(database);
        }
    }

    public void delete()
    {
        if (config.isSQLEnabled())
        {
            Connection c = SQLManager.getConnection();
            try
            {
                PreparedStatement statement = c.prepareStatement("DELETE FROM bans WHERE uuid = ? OR ip = ?");
                statement.setString(1, UUID);
                statement.setString(2, ip);
                statement.executeUpdate();
            } catch (SQLException ex)
            {
                NLog.severe(ex);
            }
        } else
        {
            JSONObject database = plugin.sqlManager.getDatabase();
            JSONObject bans = database.getJSONObject("bans");
            bans.remove(UUID);
            database.put("bans", bans);

            plugin.sqlManager.saveDatabase(database);
        }
    }
}
