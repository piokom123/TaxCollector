package com.gmail.wolinskip.taxcollector;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.wolinskip.taxcollector.data.Signs;
import com.gmail.wolinskip.taxcollector.data.Taxes;
import com.gmail.wolinskip.taxcollector.listeners.TaxListener;

public class TaxCollector extends JavaPlugin {
	private static Logger log = Logger.getLogger("Miencraft");
	public static String pluginName = "TaxCollector";
	private static FileConfiguration config;
	private static Economy economy = null;
	private static Signs signs;
	private static Taxes taxes;

	@Override
	public void onEnable() {
		try {
			// check if plugin is up to date
			UpdateChecker.checkForUpdate(pluginName);
			
			// start PluginMetrics
			try {
			    Metrics metrics = new Metrics(this);
			    metrics.start();
			} catch (IOException e) {
			    log.warning("Error occured while starting PluginMetrics");
			}
			
			// get config file
			File configFile = new File("plugins" + File.separator + pluginName);
			if(!configFile.exists()) {
				configFile.mkdir();
			}
			
			configFile = new File("plugins" + File.separator + pluginName + File.separator + "config.yml");
			if(!configFile.exists()) {
				configFile.createNewFile();
			}

			config = getConfig();

			// set default values
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
			
			if(!config.contains("taxes.fish")) {
				config.set("taxes.fish", null);
			}
			
			// signs config
			if(!config.contains("signs.refresh")) {
				config.set("signs.refresh", 5*60);
			}
			
			// save updated config file
			//saveConfig();
			
			// load signs data
			signs = new Signs();
			signs.loadLocations();
			
			//load tax data
			taxes = new Taxes();
			taxes.loadTaxes();
			
			// set economy plugin
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
	        if (economyProvider != null) {
	            economy = economyProvider.getProvider();
	        } else {
	        	log.warning("Error while loading economy");
	            Bukkit.getPluginManager().disablePlugin(this);
	            return;
	        }
	        
			// register listener event
			final TaxListener taxListener = new TaxListener(this, economy);
			getServer().getPluginManager().registerEvents(taxListener, this);
			
			// save tax statistics and refresh signs
			Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			    public void run() {
			    	taxes.saveTaxes();
			    	signs.refreshAll();
			    }
			}, 40L, config.getLong("signs.refresh")*20);
			
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
		log.info("TaxCollector disabled.");
	}
	
	public static Economy getEconomy() {
		return economy;
	}
	
	/*
	 * handling static config, I'll rewrite it, I promise ;)
	 */
	public static FileConfiguration getStaticConfig() {
		return config;
	}
	
	public static void setStaticConfig(FileConfiguration newConfig) {
		try {
			config = newConfig;
			File configFile = new File("plugins" + File.separator + pluginName + File.separator + "config.yml");
			if(!configFile.exists()) {
					configFile.createNewFile();
			}
			config.save(configFile);
		} catch (IOException e) {
			log.warning("Error while saving config!");
		}
	}
	
	/*
	 * gets Signs object
	 */
	public static Signs getSigns() {
		return signs;
	}
	
	/*
	 * saves Signs object
	 */
	public static void setSigns(Signs signs) {
		TaxCollector.signs = signs;
	}
	
	/*
	 * gets Taxes object
	 */
	public static Taxes getTaxes() {
		return taxes;
	}
	
	/*
	 * saves Taxes object
	 */
	public static void setTaxes(Taxes taxes) {
		TaxCollector.taxes = taxes;
	}
}
