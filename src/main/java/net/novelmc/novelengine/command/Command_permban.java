package net.novelmc.novelengine.command;

import net.novelmc.novelengine.banning.BanManager;
import net.novelmc.novelengine.banning.BanType;
import net.novelmc.novelengine.command.util.CommandBase;
import net.novelmc.novelengine.command.util.CommandParameters;
import net.novelmc.novelengine.command.util.SourceType;
import net.novelmc.novelengine.rank.Rank;
import net.novelmc.novelengine.util.NUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.novelmc.novelengine.banning.BanUIDGen;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandParameters(description = "Permanently ban a username or IP", usage = "/<command> <username | ip> [reason]", source = SourceType.BOTH, rank = Rank.ADMIN)
public class Command_permban extends CommandBase
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args)
    {
        if (args.length < 1)
        {
            return false;
        }

        String reason = null;
        if (args.length > 2)
        {
            reason = StringUtils.join(args, " ", 1, args.length);
        }

        //check if input is IP
        Pattern patt = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
        Matcher m = patt.matcher(args[0]);

        if (m.matches())
        {
            // check if the ip is banned
            if (BanManager.isIPPermBanned(args[0]))
            {
                sender.sendMessage(NUtil.colorize("&4&lSTAFF >&r &7That IP-address has already been permanently banned!"));
                return true;
            }

            BanManager.addBan("", args[0], sender.getName(), reason, BanUIDGen.idGen(BanType.PERMANENT_IP), null, BanType.PERMANENT_IP);
            NUtil.playerAction(sender, "Permanently Banning IP " + m, true);
            sender.sendMessage(NUtil.colorize("&4&lSTAFF >&r &7Added permanent ban for IP: " + args[0]));
            Bukkit.getOnlinePlayers().forEach((player) ->
            {
                if (player.getAddress().getAddress().getHostAddress().matches(m.toString()))
                {
                    player.kickPlayer(StringUtils.join(args, " ", 1, args.length));
                }
            });
            return true;
        }

        // ban name
        if (BanManager.isNamePermBanned(args[0]))
        {
            sender.sendMessage(ChatColor.GRAY + "That username has already been permanently banned.");
            return true;
        }

        String ip = null;
        String uuid = null;
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (args[0].matches(player.getName()))
            {
                ip = player.getAddress().getAddress().getHostAddress();
                uuid = player.getUniqueId().toString();
            }
        }

        BanManager.addBan(uuid, ip, sender.getName(), reason, BanUIDGen.idGen(BanType.PERMANENT_NAME), null, BanType.PERMANENT_NAME);
        NUtil.playerAction(sender, "Permanently Banning Username: " + args[0], true);
        sender.sendMessage(NUtil.colorize("&4&lSTAFF >&r &7Added permanent ban for name " + args[0]));
        Bukkit.getOnlinePlayers().forEach((player) ->
        {
            if (player.getName().matches(args[0]))
            {
                player.kickPlayer(StringUtils.join(args, " ", 1, args.length));
            }
        });
        return true;
    }
}
