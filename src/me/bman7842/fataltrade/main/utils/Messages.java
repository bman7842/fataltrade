package me.bman7842.fataltrade.main.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 10/30/2015.
 */
public class Messages {

    public static void Error(Player p, String msg) {
        p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR" + ChatColor.WHITE + " - " + ChatColor.GRAY + msg);
    }

    public static void Alert(Player p, String msg) {
        p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "ALERT" + ChatColor.WHITE + " - " + ChatColor.GRAY + msg);
    }

}
