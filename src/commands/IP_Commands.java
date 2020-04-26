package commands;

import main.IP_Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IP_Commands implements CommandExecutor
{
    // Plugin instance variable
    private IP_Main plugin = IP_Main.getPluginInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if (player.hasPermission(plugin.getConfig().getString("CommandPermission")))
            {
                if (args.length > 0)
                {
                    if (args[0].equalsIgnoreCase("reload"))
                    {
                        plugin.reloadConfig();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Reload")));
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED + "Invalid argument! Allowed argument(s): Reload");
                    }
                }
                else
                {
                    player.sendMessage(ChatColor.RED + "Not enough arguments! Allowed argument(s): Reload");
                }
            }
            else
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("NoPermission")));
            }
        }
        return false;
    }
}
