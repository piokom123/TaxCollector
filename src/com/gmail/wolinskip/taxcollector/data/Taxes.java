package com.gmail.wolinskip.taxcollector.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.wolinskip.taxcollector.TaxCollector;

public class Taxes {
	private Map<String, Double> taxes = new HashMap<String, Double>();
	private Logger log = Logger.getLogger("Minecraft");
	
	/*
	 * adds tax to memory
	 */
	public void addTax(String alias, Double tax) {
		if(taxes.containsKey(alias)) {
			taxes.put(alias, taxes.get(alias) + tax);
		} else {
			taxes.put(alias, tax);
			saveTaxes();
		}
	}
	
	/*
	 * gets alias taxes
	 */
	public Double getTaxesByAlias(String alias) {
		if(taxes.containsKey(alias)) {
			return Math.floor(taxes.get(alias)*100)/100;
		}
		return 0.0;
	}
	
	/*
	 * removes tax from memory
	 */
	public void removeTax(String alias) {
		taxes.remove(alias);
		saveTaxes();
	}
	
	/*
	 * removes all taxes
	 */
	public void removeAll() {
		taxes.clear();
	}
	
	/*
	 * loads taxes from file
	 */
	public void loadTaxes() {
		File file = new File("plugins" + File.separator + TaxCollector.pluginName + File.separator + "data.yml");
	    if(!file.exists()) {
	    	try {
				file.createNewFile();
			} catch (IOException e) {
				log.info("Can't create tax file!");
				e.printStackTrace();
				return;
			}
	    }
	    YamlConfiguration taxes = YamlConfiguration.loadConfiguration(file);
	    if(taxes.contains("taxes")) {
		    for(String key : taxes.getConfigurationSection("taxes").getKeys(false)) {
		    	this.addTax(key, taxes.getDouble("taxes." + key));
		    }
	    }
	}
	
	/*
	 * saves taxes to file
	 */
	public void saveTaxes() {
		if(this.taxes.size() > 0) {
	    	try {
				File file = new File("plugins" + File.separator + TaxCollector.pluginName + File.separator + "data.yml");
			    if(!file.exists()) {
					file.createNewFile();
			    }
			    YamlConfiguration taxes = YamlConfiguration.loadConfiguration(file);
			    for(String key : this.taxes.keySet()) {
			    	taxes.set("taxes." + key, this.taxes.get(key));
			    }
			    taxes.save(file);
			} catch (IOException e) {
				log.info("Can't save tax file!");
				e.printStackTrace();
				return;
			}
		}
	}
	
	/*
	 * updates 
	 */
}
