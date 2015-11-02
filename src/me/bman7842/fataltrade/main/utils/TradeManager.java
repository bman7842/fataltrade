package me.bman7842.fataltrade.main.utils;

import me.bman7842.fataltrade.main.commands.Trade;
import org.bukkit.Bukkit;
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

    public static void initialize() {
        if (hasBeenInitialized == false) {
            instance = new TradeManager();
            hasBeenInitialized = true;
            Bukkit.getLogger().info("TradeManager initialized!");
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

    public void clearTrade(Player CreatedTrade) {
        for (TradeStats tradestat : trades) {
            if (tradestat.getWhoSentTrade().equals(CreatedTrade.getUniqueId())) {
                trades.remove(tradestat);
            }
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

    public void updateTradeInv(Player p, Inventory inv) {
        boolean containsItem = false;
        for (ItemStack itemInInv : p.getOpenInventory().getTopInventory()) {
            //System.out.println(itemInInv);
            if (itemInInv == null) { System.out.println("NULL");
            } else { System.out.println(itemInInv.toString()); }
//            if (itemInInv == item) {
//                containsItem = true;
//                break;
//            }
        }
        TradeStats tradestat = null;
        if (getTradeStatFromWhoSent(p) == null) {
            tradestat = getTradeStatFromTradingWith(p);
        } else { tradestat = getTradeStatFromWhoSent(p); }

        Player whoSentTrade = tradestat.getWhoSentTrade();
        Player whoTradingWith = tradestat.getWhoTradingWith();

        System.out.println(whoSentTrade.getDisplayName());
        System.out.println(p.getDisplayName());

//        if (containsItem) {
//            System.out.println("true");
//            if (p == whoSentTrade) {
//                System.out.println("hello");
//                Inventory tradeWithInv = tradestat.getWhoTradingWithInv();
//                Inventory newInv = addItemToInv(item, tradeWithInv);
//                tradestat.updateWhoTradingWithInv(inv);
//                tradestat.addSentTradeItem(item);
//                whoTradingWith.openInventory(tradeWithInv);
//                whoTradingWith.closeInventory();
//                tradestat.getWhoTradingWith().closeInventory();
//                //whoTradingWith.updateInventory();
//            }
//        }
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
