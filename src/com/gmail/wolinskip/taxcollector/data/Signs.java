package com.gmail.wolinskip.taxcollector.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.wolinskip.taxcollector.TaxCollector;

// probably it'll be rewrited. This data is not accessed very often so it can be written from file every time it'll needed.
public class Signs {
	private ArrayList<Location> locations = new ArrayList<Location>();
	private Logger log = Logger.getLogger("Minecraft");
	
	/*
	 * adds location to memory
	 */
	public void addLocation(Location loc) {
		if(locations.contains(loc)) {
			return;
		} else {
			locations.add(loc);
			saveLocations();
		}
	}
	
	/*
	 * removes location from memory
	 */
	public void removeLocation(Location loc) {
		locations.remove(loc);
		saveLocations();
	}
	
	/*
	 * removes all locations
	 */
	public void removeAll() {
		locations.clear();
	}
	
	/*
	 * loads signs locations from file
	 */
	public void loadLocations() {
		File file = new File("plugins" + File.separator + TaxCollector.pluginName + File.separator + "data.yml");
	    if(!file.exists()) {
	    	try {
				file.createNewFile();
			} catch (IOException e) {
				log.info("Can't create signs file!");
				e.printStackTrace();
				return;
			}
	    }
	    YamlConfiguration locations = YamlConfiguration.loadConfiguration(file);
	    if(locations.contains("signs")) {
		    for(String key : locations.getConfigurationSection("signs").getKeys(false)) {
		    	if(!locations.contains("signs." + key + ".world") || !locations.contains("signs." + key + ".x") || !locations.contains("signs." + key + ".y") || !locations.contains("signs." + key + ".z")) {
		    		continue; // location is broken, can't load
		    	}
		    	Location loc = new Location(Bukkit.getWorld(locations.getString("signs." + key + ".world")), locations.getDouble("signs." + key + ".x"), locations.getDouble("signs." + key + ".y"), locations.getDouble("signs." + key + ".z"));
		    	this.addLocation(loc);
		    }
	    }
	}
	
	/*
	 * saves sign locations to file
	 */
	public void saveLocations() {
		if(this.locations.size() > 0) {
	    	try {
				File file = new File("plugins" + File.separator + TaxCollector.pluginName + File.separator + "data.yml");
			    if(!file.exists()) {
					file.createNewFile();
			    }
			    YamlConfiguration locations = YamlConfiguration.loadConfiguration(file);
			    Integer i = 0;
			    for(Location loc : this.locations) {
			    	locations.set("signs." + Integer.toString(i) + ".world", loc.getWorld().getName());
			    	locations.set("signs." + Integer.toString(i) + ".x", loc.getX());
			    	locations.set("signs." + Integer.toString(i) + ".y", loc.getY());
			    	locations.set("signs." + Integer.toString(i) + ".z", loc.getZ());
			    	i++;
			    }
			    locations.save(file);
			} catch (IOException e) {
				log.info("Can't save signs file!");
				e.printStackTrace();
				return;
			}
		}
	}
	
	/*
	 * updates all signs
	 */
	public void refreshAll() {
		for(Location loc : this.locations) {
			this.refreshSign(loc);
		}
	}
	
	/*
	 * updates sign at location
	 */
	public void refreshSign(Location loc) {
		if(loc.getBlock().getType().equals(Material.SIGN) || loc.getBlock().getType().equals(Material.SIGN_POST) || loc.getBlock().getType().equals(Material.WALL_SIGN)) {
			Sign sign = (Sign) loc.getBlock().getState();
			if(sign.getLine(0) != null && sign.getLine(1) != null && sign.getLine(0).contains("[TAX]")) {
				Taxes taxes = TaxCollector.getTaxes();
				sign.setLine(2, Double.toString(taxes.getTaxesByAlias(sign.getLine(1))));
				sign.update(true);
			}
		} else {
			//this.removeLocation(loc);
		}
	}
}
