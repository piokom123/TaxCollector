package com.gmail.wolinskip.taxcollector.data;

import com.gmail.wolinskip.taxcollector.TaxCollector;

public class Aliases {
	/*
	 * returns receiver based on alias or given alias (not found in aliases, probably it's username) 
	 */
	public static String getReceiver(String alias) {
		if(TaxCollector.getStaticConfig().contains("aliases." + alias)) {
			return TaxCollector.getStaticConfig().getString("aliases." + alias);
		} else {
			return alias;
		}
	}
}
