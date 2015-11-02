package me.bman7842.fataltrade.main.commands;

import me.bman7842.fataltrade.main.Main;
import me.bman7842.fataltrade.main.events.InventoryEvents;
import me.bman7842.fataltrade.main.utils.Messages;
import me.bman7842.fataltrade.main.utils.TradeManager;
import me.bman7842.fataltrade.main.utils.TradeStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sun.misc.resources.Messages_sv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by brand on 10/30/2015.
 */
public class Trade implements CommandExecutor {

    TradeManager trademanager;

    public Trade(Main main) {
        trademanager = TradeManager.getInstance();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                for (Player p : tradeDelay.keySet()) {
                    tradeDelay.put(p, tradeDelay.get(p) - 1);
                    if (tradeDelay.get(p) == 0) {
                        tradeDelay.remove(p);
                    }
                }
                for (Player p : waitingToAccept.keySet()) {
                    waitingToAccept.put(p, waitingToAccept.get(p) - 1);
                    if (waitingToAccept.get(p) == 0) {
                        waitingToAccept.remove(p);
                        Messages.Error(p, waitingToAcceptWith.get(p).getDisplayName() + " did not accept your trade request!");
                        trademanager.clearTradeFromCreator(p);
                        waitingToAcceptWith.remove(p);
                        return;
                    }
                }
            }
        }, 20, 20);
    }

    //Make tradeDelay customizable
    private Integer td = 10;
    private Integer acceptTime = 60;
    HashMap<Player, Integer> tradeDelay = new HashMap<Player, Integer>();
    HashMap<Player, Integer> waitingToAccept = new HashMap<Player, Integer>();
    HashMap<Player, Player> waitingToAcceptWith = new HashMap<Player, Player>();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player)sender;

        if (cmd.getName().equalsIgnoreCase("trade")) {
            if (args.length == 0) {
                Messages.Error(p, "Invalid usage, try /trade {username} or if accepting or denying a trade do /trade {accept/deny}");
                return false;
            }

            if (args[0].equalsIgnoreCase("accept")) {
                if (waitingToAcceptWith.containsValue(p)) {
                    Player whoSentTrade = trademanager.getTradeStatFromTradingWith(p).getWhoSentTrade();
                    Messages.Alert(whoSentTrade, p.getDisplayName() + " has accepted the trade, opening inventory!");
                    Messages.Alert(p, "You accepted the trade, opening inventory!");
                    trademanager.acceptTrade(p);
                    //Inventory tradeInv = createTradeInventory();
                    //p.openInventory(tradeInv);
                    //whoSentTrade.openInventory(tradeInv);
                    p.openInventory(createTradeInventory(p.getDisplayName(), whoSentTrade.getDisplayName()));
                    whoSentTrade.openInventory(createTradeInventory(whoSentTrade.getDisplayName(), p.getDisplayName()));
                    if (waitingToAccept.containsKey(whoSentTrade)) {
                        waitingToAccept.remove(whoSentTrade);
                    }
                    waitingToAcceptWith.remove(whoSentTrade);
                    //TODO: Remove this:
                    //trademanager.getTradeStatFromTradingWith(p).updateWhoTradingWithInv(createTradeInventory());
                    //trademanager.getTradeStatFromTradingWith(p).updateWhoSentTradeInv(createTradeInventory());
                    return false;
                } else {
                    Messages.Error(p, "You have no pending trade requests at this time.");
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("deny")) {
                if (waitingToAcceptWith.containsValue(p)) {
                    Player whoSentTrade = trademanager.getTradeStatFromTradingWith(p).getWhoSentTrade();
                    Messages.Alert(whoSentTrade, p.getDisplayName() + " has declined your trade request!");
                    tradeDelay.put(whoSentTrade, td);

                    Messages.Alert(p, "You declined " + whoSentTrade.getDisplayName() + "'s trade request");

                    if (waitingToAccept.containsKey(whoSentTrade)) {
                        waitingToAccept.remove(whoSentTrade);
                    }
                    waitingToAcceptWith.remove(whoSentTrade);
                    trademanager.clearTradeFromCreator(whoSentTrade);
                    return false;
                } else {
                    Messages.Error(p, "You have no pending trade requests at this time.");
                    return false;
                }
            }

            if (tradeDelay.containsKey(p)) {
                Messages.Error(p, "Please wait " + tradeDelay.get(p) + " second(s) before using /trade");
                return false;
            }
            if (waitingToAccept.containsKey(p)) {
                Player waitingToTradeWith = trademanager.getTradeStatFromWhoSent(p).getWhoTradingWith();
                Messages.Error(p, "You are currently waiting for " + waitingToTradeWith.getDisplayName() + " to accept your trade, they have " + waitingToAccept.get(p) + " second(s) to accept.");
                return false;
            }
            if (InventoryEvents.returnClosingTrade().keySet().contains(p) || InventoryEvents.returnClosingTrade().values().contains(p)) {
                Messages.Error(p, "Your previous trade is still being processed, please be patient.");
            }

            if (Bukkit.getPlayer(args[0]) == null) {
                Messages.Error(p, "No player found with this name!");
                return false;
            }

            Player tradingWith = Bukkit.getPlayer(args[0]);

            if (tradingWith.getDisplayName().equals(p.getDisplayName())) {
                Messages.Error(p, "You can't trade with yourself...");
                return false;
            }

            if (trademanager.isTrading(p)) {
                Messages.Error(p, "Hmm you appear to be in a trade, something didn't close right?");
                return false;
            }
            if (trademanager.isTrading(tradingWith) || waitingToAccept.containsKey(tradingWith)) {
                Messages.Error(p, "This player is already in a trade, please wait!");
                tradeDelay.put(p, td);
                return false;
            }

            trademanager.createNewTrade(p, tradingWith);
            waitingToAccept.put(p, acceptTime);
            waitingToAcceptWith.put(p, tradingWith);
            Messages.Alert(tradingWith, p.getDisplayName() + " wants to trade with you, type /trade accept to accept!");
            Messages.Alert(p, tradingWith.getDisplayName() + " has " + acceptTime.toString() + " second(s) to accept your trade.");

            return false;
//        } else if (cmd.getName().equalsIgnoreCase("test")) {
//            p.openInventory(createTradeInventory());
//            return false;
        }
        return false;
    }

    public Inventory createTradeInventory(String pInventoryName, String pTradingWithName) {
        Inventory tradeInv = Bukkit.createInventory(null, 54, (ChatColor.GREEN + "" + ChatColor.BOLD + "PLACE ITEMS BELOW"));

        for (int i = 4; i < 54; i = i+9) {
            ItemStack divider = new ItemStack(Material.NETHER_FENCE, 1);
            ItemMeta im = divider.getItemMeta();
            im.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "<--");
            ArrayList<String> newLore = new ArrayList<String>();
            newLore.add("Place your items on the left");
            im.setLore(newLore);
            divider.setItemMeta(im);
            tradeInv.setItem(i, divider);
        }

        for (int i = 36; i < 40; i++) {
            ItemStack acceptTrade = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            ItemMeta im = acceptTrade.getItemMeta();
            im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "ACCEPT TRADE");
            ArrayList<String> newLore = new ArrayList<String>();
            newLore.add(ChatColor.RED + "You have not accepted the trade!");
            im.setLore(newLore);
            acceptTrade.setItemMeta(im);
            tradeInv.setItem(i, acceptTrade);
        }

        for (int i = 41; i < 45; i++) {
            ItemStack acceptTrade = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            ItemMeta im = acceptTrade.getItemMeta();
            im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + pTradingWithName);
            ArrayList<String> newLore = new ArrayList<String>();
            newLore.add(ChatColor.RED + "They have not accepted the trade!");
            im.setLore(newLore);
            acceptTrade.setItemMeta(im);
            tradeInv.setItem(i, acceptTrade);
        }

        return tradeInv;
    }
}
