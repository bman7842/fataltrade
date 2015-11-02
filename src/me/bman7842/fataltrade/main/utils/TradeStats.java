package me.bman7842.fataltrade.main.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by brand on 10/30/2015.
 */
public class TradeStats {

    private UUID whoSentTrade;
    private UUID tradingWith;
    private boolean accepted = false;

    private HashSet<ItemStack> pSentTradeOfferingItems = new HashSet<ItemStack>();
    private HashSet<ItemStack> pTradingWithOfferingItems = new HashSet<ItemStack>();

    private boolean whoSentTradeAccepts = false;
    private boolean whoTradingWithAccepts = false;

    private Inventory whoSentTradeInv;
    private Inventory tradingWithInv;

    //TODO: Money trading

    public TradeStats(UUID pWhoSentTrade, UUID pwhoTradingWith) {
        whoSentTrade = pWhoSentTrade;
        tradingWith = pwhoTradingWith;
    }

    public Player getWhoTradingWith() {
        return Bukkit.getPlayer(tradingWith);
    }
    public Player getWhoSentTrade() {
        return Bukkit.getPlayer(whoSentTrade);
    }

    public void setAccepted(Boolean value) { accepted = value; }
    public boolean isAccepted() { return accepted; }

    public HashSet<ItemStack> getSentTradeItems() { return pSentTradeOfferingItems; }
    public HashSet<ItemStack> getTradingWithItems() { return pTradingWithOfferingItems; }
    public void addSentTradeItem(ItemStack item) { pSentTradeOfferingItems.add(item); }
    public void addTradingWithItem(ItemStack item) { pTradingWithOfferingItems.add(item); }
    public void deleteSentTradeItem(ItemStack item) { pSentTradeOfferingItems.remove(item); }
    public void deleteTradingWithItem(ItemStack item) { pTradingWithOfferingItems.remove(item); }

    public void updateWhoSentTradeInv(Inventory inv) { whoSentTradeInv = inv; }
    public void updateWhoTradingWithInv(Inventory inv) { tradingWithInv = inv; }

    public Inventory getWhoSentTradeInv() { return whoSentTradeInv; }
    public Inventory getWhoTradingWithInv() { return tradingWithInv; }

    public boolean playersTradingBothAccept() {
        if (whoSentTradeAccepts && whoTradingWithAccepts) {
            return true;
        }
        return false;
    }
}
