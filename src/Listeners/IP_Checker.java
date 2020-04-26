package Listeners;

import main.IP_Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class IP_Checker implements Listener
{
    private IP_Main plugin = IP_Main.getPluginInstance();

    private String password = plugin.getConfig().getString("Password");

    public static ArrayList<UUID> nonRecognizedIP = new ArrayList<>();

    private HashMap<UUID, Integer> ipAttempt = new HashMap<>();

    private String getIP(Player player) { return player.getAddress().getHostName(); }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event)
    {
        if (plugin.getConfig().getBoolean("CheckJoin"))
        {
            Player player = event.getPlayer();

            if (player.hasPermission(plugin.getConfig().getString("StaffPermission")))
            {
                for (int i = 0; i < plugin.getConfig().getStringList("AllowedIPS").size(); i++)
                {
                    if (getIP(player).equalsIgnoreCase(plugin.getConfig().getStringList("AllowedIPS").get(i)))
                    {
                        return;
                    }
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("EnterMessage")));
                nonRecognizedIP.add(player.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if (nonRecognizedIP.contains(player.getUniqueId()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("*"))
        {
            if (!nonRecognizedIP.contains(event.getPlayer().getUniqueId()))
            {
                for (int i = 0; i < plugin.getConfig().getStringList("AllowedIPS").size(); i++)
                {
                    if (getIP(event.getPlayer()).equalsIgnoreCase(plugin.getConfig().getStringList("AllowedIPS").get(i)))
                    {
                        return;
                    }
                }

                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Our system doesn't recognize you as an operator! Please enter the password to bypass this message: ");
                nonRecognizedIP.add(event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if (nonRecognizedIP.contains(player.getUniqueId()))
        {
            Location prevLoc = event.getFrom();

            prevLoc.setX(prevLoc.getBlockX() + 0.5D);
            prevLoc.setY(prevLoc.getBlockY());
            prevLoc.setZ(prevLoc.getBlockZ() + 0.5D);

            player.teleport(prevLoc);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();

        if (nonRecognizedIP.contains(player.getUniqueId()))
        {

            if (event.getMessage().equalsIgnoreCase(password))
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Success")));

                ipAttempt.remove(player.getUniqueId());
                nonRecognizedIP.remove(player.getUniqueId());

                Location newLocation = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() + 2.0D, player.getLocation().getZ());
                player.teleport(newLocation);

                List<String> list = plugin.getConfig().getStringList("AllowedIPS");
                list.add(getIP(player));
                plugin.getConfig().set("AllowedIPS", list);
                plugin.saveConfig();

            }
            else
            {
                if (!ipAttempt.containsKey(player.getUniqueId()))
                {
                   ipAttempt.put(player.getUniqueId(), 1);
                }

                int increment = (ipAttempt.get(player.getUniqueId()));

                ipAttempt.replace(player.getUniqueId(), increment + 1);

                if ((ipAttempt.get(player.getUniqueId()) == plugin.getConfig().getInt("AllowedAttempts") + 1))
                {
                    new BukkitRunnable()
                    {
                        public void run()
                        {
                            for (int i = 0; i < plugin.getConfig().getStringList("Commands").size(); i++)
                            {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (plugin.getConfig().getStringList("Commands").get(i))
                                .replace("%player%", player.getName()));
                            }
                            this.cancel();
                        }
                    }.runTaskLater(plugin, 5L);
                }

                if (increment <= plugin.getConfig().getInt("AllowedAttempts"))
                {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("IncorrectPassword").replace("%currentAttempts%", "" + increment))
                    .replace("%maxAttempts%", "" + plugin.getConfig().getInt("AllowedAttempts")));
                }
            }

            event.setCancelled(true);
        }
    }
}
