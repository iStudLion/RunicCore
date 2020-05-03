package aw.rmjtromp.RunicCore.core.features.main.other;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.server.PluginDisableEvent;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicItemStack;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.essential.RunicGUI;

public final class ServerSelector extends RunicGUI implements Listener {

	private static final RunicCore plugin = (RunicCore) Bukkit.getPluginManager().getPlugin("RunicCore");
	
	private ServerSelector() {
		super(Core.getConfig(), "features.server-selector.gui");
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public static ServerSelector init() {
		return new ServerSelector();
	}
	
	public void open(RunicPlayer player) {
		player.openInventory(getInventory(player));
	}
	
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e) {
		if(e.getSource().getTitle().equals(this.getTitle())) e.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getInventory().getName().equals(this.getTitle())) {
			e.setCancelled(true);

			if(!e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem() != null) {
				RunicPlayer player = RunicPlayer.cast((Player) e.getWhoClicked());
				RunicItemStack RIC = new RunicItemStack(e.getCurrentItem());
				if(RIC.hasNBTTag("server")) {
					player.sendMessage(RIC.getNBTTagAsString("server"));
					player.closeInventory();
					player.send(RIC.getNBTTagAsString("server"));
				}
			}
		}
		
	}
	
	@EventHandler
	public void onInventoryDragEvent(InventoryDragEvent e) {
		if(e.getInventory().getName().equals(this.getTitle())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onRunicCoreReload(RunicCoreReloadEvent e) {
		if(!e.isCancelled()) {
			for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				if(player.getOpenInventory() != null) {
					if(player.getOpenInventory().getTitle().equals(getTitle())) {
						player.closeInventory();
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(player.getOpenInventory() != null) {
				if(player.getOpenInventory().getTitle().equals(getTitle())) {
					player.closeInventory();
				}
			}
		}
	}
	
	public ServerSelector destroy() {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(player.getOpenInventory() != null) {
				if(player.getOpenInventory().getTitle().equals(getTitle())) {
					player.closeInventory();
				}
			}
		}
		HandlerList.unregisterAll(this);
		return null;
	}
	
}
