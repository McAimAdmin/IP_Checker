package main;

import Listeners.IP_Checker;
import commands.IP_Commands;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class IP_Main extends JavaPlugin
{
    // Store main class reference
    private static IP_Main pluginInstance;

    // Return instance of plugin
    public static IP_Main getPluginInstance() { return pluginInstance; }

    @Override
    public void onEnable()
    {
        // Set instance of main class on enable
        pluginInstance = this;

        // Registered Listeners
        getServer().getPluginManager().registerEvents(new IP_Checker(), this);

        // Registered Commands
        Objects.requireNonNull(getCommand("ips")).setExecutor(new IP_Commands());

        // Loading config
        loadConfig();
    }

    public void onDisable()
    {

    }

    public void loadConfig()
    {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
