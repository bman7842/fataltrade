package me.bman7842.fataltrade.main;

import me.bman7842.fataltrade.main.commands.Trade;
import me.bman7842.fataltrade.main.events.InventoryEvents;
import me.bman7842.fataltrade.main.utils.TradeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by brand on 10/30/2015.
 */
public class Main extends JavaPlugin {

    Trade trade;

    @Override
    public void onEnable() {
        TradeManager.initialize(this);

        //Classes\\
        trade = new Trade(this);

        //Registering Commands\\
        getCommand("trade").setExecutor(trade);
        getCommand("test").setExecutor(trade);

        //Registering Events\\
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new InventoryEvents(this), this);
    }

}
