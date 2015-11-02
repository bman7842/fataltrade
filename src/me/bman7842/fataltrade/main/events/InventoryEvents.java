package me.bman7842.fataltrade.main.events;

import me.bman7842.fataltrade.main.Main;
import me.bman7842.fataltrade.main.utils.Messages;
import me.bman7842.fataltrade.main.utils.TradeManager;
import me.bman7842.fataltrade.main.utils.TradeStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by brand on 10/30/2015.
 */
public class InventoryEvents implements Listener {

    TradeManager trademanager;
    Main main = null;

    private static HashMap<Player, Player> closingTrade = new HashMap<Player, Player>();

    public static HashMap<Player, Player> returnClosingTrade() { return closingTrade; }

    public InventoryEvents(Main plugin) {
        trademanager = TradeManager.getInstance();

        main = plugin;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                for (UUID pUUID : tradeCountdown.keySet()) {
                    Player p = Bukkit.getPlayer(pUUID);
                    Integer amount = tradeCountdown.get(p);
                    Inventory inv = p.getInventory();

                    ItemStack divider = new ItemStack(Material.FENCE, 1);
                    ItemMeta im = divider.getItemMeta();
                    im.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "<--");
                    ArrayList<String> newLore = new ArrayList<String>();
                    newLore.add("Place your items on the left");
                    im.setLore(newLore);
                    divider.setItemMeta(im);

                    inv.setItem((amount*9), divider);
                }
            }
        }, 20, 20);
    }

    HashMap<UUID, Integer> tradeCountdown = new HashMap<UUID, Integer>();

    @EventHandler
    public void InvClickEvent(InventoryClickEvent event) {
        ItemStack selectedItem = event.getCurrentItem();
        Player p = (Player)event.getWhoClicked();
        Inventory inv = event.getInventory();

        if (!inv.getName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "PLACE ITEMS BELOW")) {
            return;
        }

        if (selectedItem == null) { return; }
        if (selectedItem.getType().equals(Material.AIR)) {
            return;
        }

        if (selectedItem.getType().equals(Material.NETHER_FENCE)
                && selectedItem.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "" + ChatColor.BOLD + "<--")) {
            event.setCancelled(true);
        }

        if (selectedItem.getType().equals(Material.STAINED_GLASS_PANE)) {
            if (selectedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "ACCEPT TRADE")) {
                event.setCancelled(true);
                tradeCountdown.put(event.getWhoClicked().getUniqueId(), 6);
                if (event.getInventory().getItem(41).getItemMeta().getDisplayName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "TRADE ACCEPTED")) {
                    tradeCountdown.put(event.getWhoClicked().getUniqueId(), 6);
                }
            }
        }
    }

    @EventHandler
    public void InvCloseEvent(InventoryCloseEvent event) {
        Inventory closedInv = event.getInventory();
        Player p = (Player)event.getPlayer();
        if (closedInv.getName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "PLACE ITEMS BELOW")) {
            TradeStats tradestat = null;
            if (trademanager.getTradeStatFromTradingWith(p) != null) {
                tradestat = trademanager.getTradeStatFromTradingWith(p);
            } else {
                tradestat = trademanager.getTradeStatFromWhoSent(p);
            }

            if (tradestat == null) {
                Bukkit.getLogger().warning("TradeStat is null for InvCloseEvent.");
                return;
            }

            if (closingTrade.keySet().contains(p) || closingTrade.values().contains(p)) {
                System.out.println("yes");
                return; }

            Player whoSentTrade = tradestat.getWhoSentTrade();
            Player whoTradingWith = tradestat.getWhoTradingWith();

            for (ItemStack item : tradestat.getSentTradeItems()) {
                whoSentTrade.getInventory().addItem(item);
            }
            for (ItemStack item : tradestat.getTradingWithItems()) {
                whoTradingWith.getInventory().addItem(item);
            }

//            closingTrade.add(p, );
            Messages.Error(p, "You have cancelled the trade.");
//            if (whoSentTrade.equals(p)) {
//                Messages.Error(whoTradingWith, p.getDisplayName() + " has cancelled the trade.");
//                whoTradingWith.closeInventory();
//                closingTrade.put(p, whoTradingWith);
//            } else {
//                Messages.Error(whoSentTrade, p.getDisplayName() + " has cancelled the trade.");
//                whoSentTrade.closeInventory();
//                closingTrade.put(p, whoSentTrade);
//            }
            trademanager.clearTradeFromTradeStat(tradestat);
            final TradeStats tradestatfinal = tradestat;
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                @Override
                public void run() {
//                    for (Player playa : closingTrade.keySet()) {
//                        if (playa == p) {
//                            closingTrade.remove(p);
//                            break;
//                        } else if (closingTrade.get(playa).equals(p)) {
//                            closingTrade.remove(playa);
//                            break;
//                        }
//                    }
                    if (whoSentTrade.equals(p)) {
                        Messages.Error(whoTradingWith, p.getDisplayName() + " has cancelled the trade.");
                        whoTradingWith.closeInventory();
                        System.out.println("closed2");
                        //closingTrade.put(p, whoTradingWith);
                    } else {
                        Messages.Error(whoSentTrade, p.getDisplayName() + " has cancelled the trade.");
                        whoSentTrade.closeInventory();
                        System.out.println("closed1");
                        //closingTrade.put(p, whoSentTrade);
                    }
                }
            },20L);
        }
    }


    @EventHandler
    public void ItemMoveEvent(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();

        if (event.getClickedInventory() == null) { return; }
        if (!event.getClickedInventory().getName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "PLACE ITEMS BELOW")) { return; }

        trademanager.updateTradeInv((Player)event.getWhoClicked());
        //trademanager.loadNewInv(inv.getViewers());
    }
    //INV MOVE EVENT
}
