package com.gmail.wolinskip.taxcollector.listeners;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerFishEvent;

import com.gmail.wolinskip.taxcollector.TaxCollector;

public class TaxListener implements Listener {
	Logger log	= Logger.getLogger("Minecraft");
	private TaxCollector plugin;
	private Economy economy;
	
	public TaxListener(TaxCollector plugin, Economy economy) {
		this.plugin = plugin;
		this.economy = economy;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		perform(this.plugin, this.economy, "break", event.getBlock().getTypeId(), event.getPlayer());
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		perform(this.plugin, this.economy, "place", event.getBlock().getTypeId(), event.getPlayer());
	}
	
	@EventHandler
	public void onCatch(PlayerFishEvent event) {
		perform(this.plugin, this.economy, "catch", event.getCaught().getEntityId(), event.getPlayer());
	}
	
	public static void perform(TaxCollector plugin, Economy economy, String configSection, Integer itemId, Player player) {
		// skip if player have immunity
		if(player.hasPermission("taxcollector.immunity")) {
			if(plugin.getConfig().getBoolean("debug")) {
				player.sendMessage("You have got tax immunity!");
			}
			return;
		}
		
		if(plugin.getConfig().contains("taxes." + configSection + "." + itemId)) {
			Double tax = plugin.getConfig().getDouble("taxes." + configSection + "." + itemId + ".amount");
			
			// get tax from player
			economy.depositPlayer(player.getName(), tax*-1);
			
			// pay tax to receiver if is set
			if((plugin.getConfig().contains("receiver") && !plugin.getConfig().getString("receiver").equals(null)) || (plugin.getConfig().contains("taxes." + configSection + "." + itemId + ".receiver") && !plugin.getConfig().getString("taxes." + configSection + "." + itemId + ".receiver").equals(null))) {
				// receiver is set individually for this item
				if(plugin.getConfig().contains("taxes." + configSection + "." + itemId + ".receiver") && !plugin.getConfig().getString("taxes." + configSection + "." + itemId + ".receiver").equals(null)) {
					economy.depositPlayer(plugin.getConfig().getString("taxes." + configSection + "." + itemId + ".receiver"), tax);
				} else { // using global receiver
					economy.depositPlayer(plugin.getConfig().getString("receiver"), tax);
				}
			}
			
			if(plugin.getConfig().getBoolean("debug")) {
				player.sendMessage("Tax paid: " + (Double.toString(tax*-1)));
			}
		} else if(plugin.getConfig().getBoolean("debug")) {
			player.sendMessage("This action is not taxed.");
		}
	}

}
