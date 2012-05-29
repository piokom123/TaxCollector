package com.gmail.wolinskip.taxcollector.listeners;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.Packet103SetSlot;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.wolinskip.taxcollector.TaxCollector;
import com.gmail.wolinskip.taxcollector.data.Aliases;
import com.gmail.wolinskip.taxcollector.data.Signs;
import com.gmail.wolinskip.taxcollector.data.Taxes;

public class TaxListener implements Listener {
	Logger log	= Logger.getLogger("Minecraft");
	private TaxCollector plugin;
	private Economy economy;
	
	public TaxListener(TaxCollector plugin, Economy economy) {
		this.plugin = plugin;
		this.economy = economy;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if(event.getLine(0) != null && event.getLine(1) != null && event.getLine(0).contains("[TAX]")) {
			if(!event.getPlayer().hasPermission("taxcollector.signs.create")) {
				event.getPlayer().sendMessage(ChatColor.RED + "You can't create tax signs!");
				return;
			}
			
			Signs signs = TaxCollector.getSigns();
			signs.addLocation(event.getBlock().getLocation());
			
			// update sign lines
			if(event.getBlock().getType().equals(Material.SIGN) || event.getBlock().getType().equals(Material.SIGN_POST) || event.getBlock().getType().equals(Material.WALL_SIGN)) {
				if(event.getLine(0) != null && event.getLine(1) != null && event.getLine(0).contains("[TAX]")) {
					Taxes taxes = TaxCollector.getTaxes();
					event.setLine(2, Double.toString(taxes.getTaxesByAlias(event.getLine(1))));
				}
			}
			
			TaxCollector.setSigns(signs);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		Signs signs = TaxCollector.getSigns();

		// only right button will refresh sign
		if(action == Action.RIGHT_CLICK_BLOCK && (block.getType() == Material.SIGN || (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN))) {
			if(!event.getPlayer().hasPermission("taxcollector.signs.refresh")) {
				event.getPlayer().sendMessage(ChatColor.RED + "You can't do that!");
				return;
			}
			signs.refreshSign(block.getLocation());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled()) {
			if(event.getBlock().getType() == Material.SIGN || (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN)) { // remove sign from list
				Signs signs = TaxCollector.getSigns();
				signs.removeLocation(event.getBlock().getLocation());
				TaxCollector.setSigns(signs);
			}
			perform(this.plugin, this.economy, "break", event.getBlock().getTypeId(), event.getPlayer(), 1);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.canBuild() && !event.isCancelled()) {
			perform(this.plugin, this.economy, "place", event.getBlock().getTypeId(), event.getPlayer(), 1);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCatch(PlayerFishEvent event) {
		if(!event.isCancelled() && event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH) || event.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY)) {
			perform(this.plugin, this.economy, "catch", 349, event.getPlayer(), 1);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onCraft(CraftItemEvent event) {
		// check if event is cancelled, item in hand is the same as crafting result, can add crafting result to items in hand
		if(event.isCancelled() || (event.getCursor().getTypeId() != 0 && (event.getCursor().getTypeId() != event.getRecipe().getResult().getTypeId() || (event.getCursor().getAmount() + event.getRecipe().getResult().getAmount()) > event.getWhoClicked().getItemInHand().getMaxStackSize()))) {
			event.setCancelled(true);
			return;
		}
		if(event.isShiftClick()) {
			((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Sorry, but you can't use shift here.");
			event.setCancelled(true);
			TaxListener.update(((Player) event.getWhoClicked()));
			return;
		}
		TaxListener.perform(this.plugin, this.economy, "craft", event.getRecipe().getResult().getTypeId(), ((Player) event.getWhoClicked()), event.getRecipe().getResult().getAmount());
	}
	
	public static void perform(TaxCollector plugin, Economy economy, String configSection, Integer itemId, Player player, Integer itemsCount) {
		// skip if player have immunity
		if(player.hasPermission("taxcollector.immunity")) {
			if(plugin.getConfig().getBoolean("debug")) {
				player.sendMessage("You have got tax immunity!");
			}
			return;
		}
		
		if(plugin.getConfig().contains("taxes." + configSection + "." + itemId)) {
			Double tax = plugin.getConfig().getDouble("taxes." + configSection + "." + itemId + ".amount") * itemsCount;
			
			// get tax from player
			EconomyResponse resp = economy.withdrawPlayer(player.getName(), tax);
			
			// check withdraw result if debug mode is on
			if(plugin.getConfig().getBoolean("debug") && resp.transactionSuccess()) {
                player.sendMessage(String.format("You were given %s and now have %s", economy.format(resp.amount), economy.format(resp.balance)));
            } else {
                player.sendMessage(String.format("An error occured: %s", resp.errorMessage));
            }
			
			// pay tax to receiver if is set
			if((plugin.getConfig().contains("receiver") && !plugin.getConfig().getString("receiver").equals(null)) || (plugin.getConfig().contains("taxes." + configSection + "." + itemId + ".receiver") && !plugin.getConfig().getString("taxes." + configSection + "." + itemId + ".receiver").equals(null))) {
				// receiver is set individually for this item
				String receiver = "";
				if(plugin.getConfig().contains("taxes." + configSection + "." + itemId + ".receiver") && !plugin.getConfig().getString("taxes." + configSection + "." + itemId + ".receiver").equals(null)) {
					receiver = plugin.getConfig().getString("taxes." + configSection + "." + itemId + ".receiver");
				} else { // using global receiver
					receiver = plugin.getConfig().getString("receiver");
				}
				resp = economy.depositPlayer(Aliases.getReceiver(receiver), tax);
				
				// check deposit result if debug mode is on
				if(plugin.getConfig().getBoolean("debug") && resp.transactionSuccess()) {
	                player.sendMessage(String.format("%s deposited on %s's account", economy.format(resp.amount), Aliases.getReceiver(receiver)));
	            } else {
	                player.sendMessage(String.format("An error occured: %s", resp.errorMessage));
	            }
				
				// add tax to statistics
				Taxes taxes = TaxCollector.getTaxes();
				taxes.addTax(receiver, tax);
				TaxCollector.setTaxes(taxes);
			}
			
			if(plugin.getConfig().getBoolean("debug")) {
				player.sendMessage("Tax paid: " + (Double.toString(tax*-1)) + " from " + player.getName());
				player.sendMessage("Items count: " + itemsCount);
			}
		} else if(plugin.getConfig().getBoolean("debug")) {
			player.sendMessage("This action is not taxed.");
		}
	}
	
	public static void update(Player p) {
        CraftPlayer c = (CraftPlayer) p;
        for (int i = 0;i < 36;i++) {
            int nativeindex = i;
            if (i < 9) nativeindex = i + 36;
            ItemStack olditem =  c.getInventory().getItem(i);
            net.minecraft.server.ItemStack item = null;
            if (olditem != null && olditem.getType() != Material.AIR) {
                item = new net.minecraft.server.ItemStack(0, 0, 0);
                item.id = olditem.getTypeId();
                item.count = olditem.getAmount();
                item.b = olditem.getDurability();
            }
            Packet103SetSlot pack = new Packet103SetSlot(0, nativeindex, item);
            c.getHandle().netServerHandler.sendPacket(pack);
        }
    }
}
