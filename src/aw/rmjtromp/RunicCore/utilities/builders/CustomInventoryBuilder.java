package aw.rmjtromp.RunicCore.utilities.builders;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import aw.rmjtromp.RunicCore.core.other.extensions.RunicItemStack;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.configs.Config;

public class CustomInventoryBuilder {
	
	private Inventory i;

	public CustomInventoryBuilder(RunicPlayer player, int size) {
		i = Bukkit.createInventory(player.getPlayer(), size);
	}

	public CustomInventoryBuilder(RunicPlayer player, int size, String title) {
		i = Bukkit.createInventory(player.getPlayer(), size, ChatColor.translateAlternateColorCodes('&', title));
	}
	
	public CustomInventoryBuilder(Config config, String path, RunicPlayer player) {
		int size = config.getInt(path+".size", 54);
		if(size != 9 && size != 18 && size != 27 && size != 36 && size != 45 && size != 54) size = 54;
		config.set(path+".size", 54);
		if(config.contains(path+".title")) i = Bukkit.createInventory(player.getPlayer(), size, ChatColor.translateAlternateColorCodes('&', config.getString(path+".title", "&8Inventory")));
		else i = Bukkit.createInventory(player.getPlayer(), size);
	}
	
	public CustomInventoryBuilder(Config config, String path, Player player) {
		int size = config.getInt(path+".size", 54);
		if(size != 9 && size != 18 && size != 27 && size != 36 && size != 45 && size != 54) size = 54;
		config.set(path+".size", 54);
		if(config.contains(path+".title")) i = Bukkit.createInventory(player.getPlayer(), size, ChatColor.translateAlternateColorCodes('&', config.getString(path+".title", "&8Inventory")));
		else i = Bukkit.createInventory(player, size);
	}
	
	public void setItem(int slot, CustomItemBuilder item) {
		if(slot < 0 || slot > i.getSize()-1) return;
		i.setItem(slot, item.getBukkitItem());
	}
	
	public void setItem(int slot, RunicItemStack item) {
		if(slot < 0 || slot > i.getSize()-1) return;
		i.setItem(slot, (ItemStack) item);
	}
	
	public void setItem(int slot, ItemStack item) {
		if(slot < 0 || slot > i.getSize()-1) return;
		i.setItem(slot, item);
	}
	
	public void addItem(CustomItemBuilder item) {
		i.addItem(item.getBukkitItem());
	}
	
	public void addItem(RunicItemStack item) {
		i.addItem((ItemStack) item);
	}
	
	public void addItem(ItemStack item) {
		i.addItem(item);
	}
	
	@SuppressWarnings("deprecation")
	public boolean contains(int slot) {
		return i.contains(slot);
	}
	
	public boolean contains(CustomItemBuilder item) {
		return i.contains(item.getBukkitItem());
	}
	
	public boolean contains(RunicItemStack item) {
		return i.contains((ItemStack) item);
	}
	
	public boolean contains(ItemStack item) {
		return i.contains(item);
	}
	
	public Inventory getInventory() {
		return i;
	}
	
}
