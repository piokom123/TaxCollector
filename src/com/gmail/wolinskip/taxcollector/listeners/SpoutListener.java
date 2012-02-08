package com.gmail.wolinskip.taxcollector.listeners;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;

import com.gmail.wolinskip.taxcollector.TaxCollector;

public class SpoutListener implements Listener {
	Logger log	= Logger.getLogger("Minecraft");
	private TaxCollector plugin;
	private Economy economy;
	
	public SpoutListener(TaxCollector plugin, Economy economy) {
		this.plugin = plugin;
		this.economy = economy;
	}
	
	@EventHandler
	public void onCraft(InventoryCraftEvent event) {
		TaxListener.perform(this.plugin, this.economy, "craft", event.getResult().getTypeId(), event.getPlayer());
	}

}
