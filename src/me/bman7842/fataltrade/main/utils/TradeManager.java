package me.bman7842.fataltrade.main.utils;

import me.bman7842.fataltrade.main.Main;
import me.bman7842.fataltrade.main.commands.Trade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by brand on 10/30/2015.
 */
public class TradeManager {

    //SETUP - STATIC\\
    private static boolean hasBeenInitialized = false;
    private static TradeManager instance;
    private static Main main;

    public static void initialize(Main plugin) {
        if (hasBeenInitialized == false) {
            instance = new TradeManager();
            hasBeenInitialized = true;
            Bukkit.getLogger().info("TradeManager initialized!");
            main = plugin;
        } else {
            Bukkit.getLogger().warning("Trying to re-initialize TradeManager, something went wrong!");
        }
    }

    public static TradeManager getInstance() { return instance; }

    //HANDLING - PUBLIC\\
    private HashSet<TradeStats> trades = new HashSet<TradeStats>();

    public void createNewTrade(Player CreatedTrade, Player TradingWith) {
        trades.add(new TradeStats(CreatedTrade.getUniqueId(), TradingWith.getUniqueId()));
    }

    public void clearTradeFromCreator(Player CreatedTrade) {
        for (TradeStats tradestat : trades) {
            if (tradestat.getWhoSentTrade().equals(CreatedTrade)) {
                trades.remove(tradestat);
            }
        }
    }

    public void clearTradeFromTradeStat(TradeStats tradestat) {
        if (trades.contains(tradestat)) {
            trades.remove(tradestat);
            System.out.println("Deleted");
        } else {
            Bukkit.getLogger().warning("Trying to delete an invalid trade with clearTradeFromTradeStat.");
        }
    }

    public void acceptTrade(Player playerWhoTradingWith) {
        for (TradeStats tradestat : trades) {
            if (tradestat.getWhoTradingWith().equals(playerWhoTradingWith.getUniqueId())) {
                tradestat.setAccepted(true);
                return;
            }
        }
    }

    public TradeStats getTradeStatFromWhoSent(Player WhoSentTrade) {
        for (TradeStats tradestat : trades) {
            if (tradestat.getWhoSentTrade().getUniqueId().equals(WhoSentTrade.getUniqueId())) {
                return tradestat;
            }
        }
        return null;
    }
    public TradeStats getTradeStatFromTradingWith(Player WhoTradingWith) {
        for (TradeStats tradestat : trades) {
            System.out.println("Hello");
            if (tradestat.getWhoTradingWith().getUniqueId().equals(WhoTradingWith.getUniqueId())) {
                System.out.println("It is");
                return tradestat;
            }
        }
        return null;
    }

    public boolean isTrading(Player p) {
        for (TradeStats tradestat : trades) {
            if (tradestat.getWhoSentTrade().equals(p.getUniqueId())
                    || tradestat.getWhoTradingWith().equals(p.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

//    public void updateTradeInv(Player p, Inventory inv) {
//        boolean containsItem = false;
//        for (ItemStack itemInInv : p.getOpenInventory().getTopInventory()) {
//            //System.out.println(itemInInv);
//            if (itemInInv == null) { System.out.println("NULL");
//            } else { System.out.println(itemInInv.toString()); }
////            if (itemInInv == item) {
////                containsItem = true;
////                break;
////            }
//        }
//        TradeStats tradestat = null;
//        if (getTradeStatFromWhoSent(p) == null) {
//            tradestat = getTradeStatFromTradingWith(p);
//        } else { tradestat = getTradeStatFromWhoSent(p); }
//
//        Player whoSentTrade = tradestat.getWhoSentTrade();
//        Player whoTradingWith = tradestat.getWhoTradingWith();
//
//        System.out.println(whoSentTrade.getDisplayName());
//        System.out.println(p.getDisplayName());
//
////        if (containsItem) {
////            System.out.println("true");
////            if (p == whoSentTrade) {
////                System.out.println("hello");
////                Inventory tradeWithInv = tradestat.getWhoTradingWithInv();
////                Inventory newInv = addItemToInv(item, tradeWithInv);
////                tradestat.updateWhoTradingWithInv(inv);
////                tradestat.addSentTradeItem(item);
////                whoTradingWith.openInventory(tradeWithInv);
////                whoTradingWith.closeInventory();
////                tradestat.getWhoTradingWith().closeInventory();
////                //whoTradingWith.updateInventory();
////            }
////        }
//    }

    public void updateTradeInv(Player p) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
            @Override
            public void run() {
                if (getTradeStatFromTradingWith(p) == null && getTradeStatFromWhoSent(p) == null) { return;}

                Player whoSent = null;
                Player tradingWith = null;
                TradeStats tradestat = null;
                if (getTradeStatFromTradingWith(p) != null) {
                    tradestat = getTradeStatFromTradingWith(p);
                    tradingWith = p;
                    whoSent = tradestat.getWhoSentTrade();
                } else if (getTradeStatFromWhoSent(p) != null) {
                    tradestat = getTradeStatFromWhoSent(p);
                    whoSent = p;
                    tradingWith = tradestat.getWhoTradingWith();
                }
                if (whoSent == null || tradingWith == null || tradestat == null) {
                    Bukkit.getLogger().warning("Error in updating trade, something was null.");
                    return;
                }
                if (p == whoSent) {
                    Inventory otherUserInv = tradingWith.getOpenInventory().getTopInventory();
                    tradestat.clearPWhoSentTradeItems();
                    for (int i = 0; i < 4; i++) {
                        ItemStack item = p.getOpenInventory().getTopInventory().getItem(i);
                        if (item == null) {
                            ItemStack newitem = new ItemStack(Material.AIR, 1);
                            otherUserInv.setItem((i+5), newitem);
                        } else {
                            otherUserInv.setItem((i+5), item);
                            tradestat.addSentTradeItem(item);
                        }
                    }
                    for (int i = 9; i < 13; i++) {
                        ItemStack item = p.getOpenInventory().getTopInventory().getItem(i);
                        if (item == null) {
                            ItemStack newitem = new ItemStack(Material.AIR, 1);
                            otherUserInv.setItem((i+5), newitem);
                        } else {
                            otherUserInv.setItem((i+5), item);
                            tradestat.addSentTradeItem(item);
                        }
                    }
                    for (int i = 18; i < 22; i++) {
                        ItemStack item = p.getOpenInventory().getTopInventory().getItem(i);
                        if (item == null) {
                            ItemStack newitem = new ItemStack(Material.AIR, 1);
                            otherUserInv.setItem((i+5), newitem);
                        } else {
                            otherUserInv.setItem((i+5), item);
                            tradestat.addSentTradeItem(item);
                        }
                    }
                    for (int i = 27; i < 31; i++) {
                        ItemStack item = p.getOpenInventory().getTopInventory().getItem(i);
                        if (item == null) {
                            ItemStack newitem = new ItemStack(Material.AIR, 1);
                            otherUserInv.setItem((i+5), newitem);
                        } else {
                            otherUserInv.setItem((i+5), item);
                            tradestat.addSentTradeItem(item);
                        }
                    }
                    tradingWith.updateInventory();
                } else if (p == tradingWith) {
                    Inventory otherUserInv = whoSent.getOpenInventory().getTopInventory();
                    tradestat.clearPWhoTradingWithItems();
                    for (int i = 0; i < 4; i++) {
                        ItemStack item = p.getOpenInventory().getTopInventory().getItem(i);
                        if (item == null) {
                            ItemStack newitem = new ItemStack(Material.AIR, 1);
                            otherUserInv.setItem((i+5), newitem);
                        } else {
                            otherUserInv.setItem((i+5), item);
                            tradestat.addTradingWithItem(item);
                        }
                    }
                    for (int i = 9; i < 13; i++) {
                        ItemStack item = p.getOpenInventory().getTopInventory().getItem(i);
                        if (item == null) {
                            ItemStack newitem = new ItemStack(Material.AIR, 1);
                            otherUserInv.setItem((i+5), newitem);
                        } else {
                            otherUserInv.setItem((i + 5), item);
                            tradestat.addTradingWithItem(item);
                        }
                    }
                    for (int i = 18; i < 22; i++) {
                        ItemStack item = p.getOpenInventory().getTopInventory().getItem(i);
                        if (item == null) {
                            ItemStack newitem = new ItemStack(Material.AIR, 1);
                            otherUserInv.setItem((i+5), newitem);
                        } else {
                            otherUserInv.setItem((i+5), item);
                            tradestat.addTradingWithItem(item);
                        }
                    }
                    for (int i = 27; i < 31; i++) {
                        ItemStack item = p.getOpenInventory().getTopInventory().getItem(i);
                        if (item == null) {
                            ItemStack newitem = new ItemStack(Material.AIR, 1);
                            otherUserInv.setItem((i+5), newitem);
                        } else {
                            otherUserInv.setItem((i+5), item);
                            tradestat.addTradingWithItem(item);
                        }
                    }
                    whoSent.updateInventory();
                } else {
                    Bukkit.getLogger().warning("Error in updating trade, the player was neither who sent or who trading with.");
                }
            }
        }, 20L);

    }

    public void tradeItems() {

    }

    private Inventory addItemToInv(ItemStack item, Inventory inv) {
        Inventory newinv = inv;
        for (int i = 5; i < 9; i++) {
            if (newinv.getItem(i) == null) {
                newinv.setItem(i, item);
                return newinv;
            }
        }
        return newinv;
    }
}
