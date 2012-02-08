package com.gmail.wolinskip.taxcollector;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.wolinskip.taxcollector.listeners.SpoutListener;
import com.gmail.wolinskip.taxcollector.listeners.TaxListener;

public class TaxCollector extends JavaPlugin {
	private Logger log = Logger.getLogger("Miencraft");
	private static String pluginName = "TaxCollector";
	private FileConfiguration config;
	private static Economy economy = null;

	@Override
	public void onEnable() {
		try {
			// get config file
			config = getConfig();
			File configFile = new File("plugins" + File.separator + pluginName);
			if(!configFile.exists()) {
				configFile.mkdir();
			}
			
			configFile = new File("plugins" + File.separator + pluginName + File.separator + "config.yml");
			if(!configFile.exists()) {
				configFile.createNewFile();
			}
			
			// set default values
			if(!config.contains("economy")) {
				config.set("economy", null);
			}
			
			if(!config.contains("receiver")) {
				config.set("receiver", null);
			}
			
			if(!config.contains("debug")) {
				config.set("debug", false);
			}
			
			if(!config.contains("taxes.place")) {
				config.set("taxes.place", null);
			}
			
			if(!config.contains("taxes.break")) {
				config.set("taxes.break", null);
			}
			
			if(!config.contains("taxes.craft")) {
				config.set("taxes.craft", null);
			}
			
			// save updated config file
			saveConfig();
			
			// set economy plugin
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
	        if (economyProvider != null) {
	            economy = economyProvider.getProvider();
	        } else {
	        	log.warning("Error while loading economy");
	            Bukkit.getPluginManager().disablePlugin(this);
	            return;
	        }
	        
	        // register spout listener if spout is loaded
	        if(Bukkit.getServer().getPluginManager().isPluginEnabled("Spout")) {
				final SpoutListener spoutListener = new SpoutListener(this, economy);
				getServer().getPluginManager().registerEvents(spoutListener, this);
	        	log.info("Spout plugin found. Enabling Spout features.");
	        } else {
	        	log.info("Spout plugin not found. Few features will be disabled.");
	        }
	        
			// register listener event
			final TaxListener taxListener = new TaxListener(this, economy);
			getServer().getPluginManager().registerEvents(taxListener, this);
			
		} catch(Exception e1) {
			log.warning("Error while loading plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
			e1.printStackTrace();
			return;
		}
		
		log.info("TaxCollector enabled.");
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
		log.info("TaxCollector disabled.");
	}
	
	public static Economy getEconomy() {
		return economy;
	}

}
