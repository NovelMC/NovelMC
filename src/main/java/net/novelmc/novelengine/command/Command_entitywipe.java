package net.novelmc.novelengine.command;

import net.novelmc.novelengine.command.util.CommandBase;
import net.novelmc.novelengine.command.util.CommandParameters;
import net.novelmc.novelengine.command.util.SourceType;
import net.novelmc.novelengine.rank.Rank;
import net.novelmc.novelengine.util.NUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.Arrays;
import java.util.List;

@CommandParameters(description = "Wipe those pesky entities that aren't players.", usage = "/<command> [mob,monster,mobs]", aliases = "ew", source = SourceType.BOTH, rank = Rank.TRAINEE)
public class Command_entitywipe extends CommandBase
{

    public static final List<EntityType> DONTCLEAR = Arrays.asList(EntityType.ITEM_FRAME, EntityType.MINECART, EntityType.ARMOR_STAND);
    public EntityType Fk;

    public static int AllEntities()
    {
        int removed = 0;
        for (World eworld : Bukkit.getWorlds())
        {
            removed = eworld.getEntities().stream().filter((entity) -> ( ! (entity instanceof Player) &&  ! DONTCLEAR.contains(entity.getType()))).map((entity) ->
            {
                entity.remove();
                return entity;
            }).map((_item) -> 1).reduce(removed, Integer :: sum);
        }
        return removed;
    }

    public static int AllMobs()
    {
        int removed = 0;
        for (World eworld : Bukkit.getWorlds())
        {
            removed = eworld.getLivingEntities().stream().filter((entity) -> (entity instanceof Creature || entity instanceof Ghast || entity instanceof Slime || entity instanceof EnderDragon || entity instanceof Ambient)).map((entity) ->
            {
                entity.remove();
                return entity;
            }).map((_item) -> 1).reduce(removed, Integer :: sum);
        }
        return removed;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args)
    {

        Player player = (Player) sender;

        if (args.length == 0)
        {
            NUtil.playerAction(player, "Removing all entities", false);
            sender.sendMessage(NUtil.colorize("&2&lINFO >&r &7" + AllEntities() + " entities removed."));
            return true;
        }
        if (args.length <= 1)
        {
            if (args[0].equalsIgnoreCase("mob") || args[0].equalsIgnoreCase("monsters") || args[0].equalsIgnoreCase("mobs"))
            {
                NUtil.playerAction(sender, "Purging all mobs", true);
                sender.sendMessage(NUtil.colorize("&2&lINFO >&r &7" + AllMobs() + " mobs removed."));
                return true;
            }
            sender.sendMessage(NUtil.colorize("&2&lINFO >&r &7/ew [mobs,mob,monsters]"));
            return true;
        }
        return true;
    }

}
