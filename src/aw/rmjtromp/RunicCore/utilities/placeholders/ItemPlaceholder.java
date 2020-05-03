package aw.rmjtromp.RunicCore.utilities.placeholders;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import aw.rmjtromp.RunicCore.core.other.extensions.RunicItemStack;
import aw.rmjtromp.RunicCore.utilities.DependencyManager.Dependency;
import me.clip.placeholderapi.PlaceholderAPI;

public final class ItemPlaceholder {

	// do not make it static, this is to re-check everytime a new instance is created
	private boolean papi_is_enabled = Dependency.PLACEHOLDERAPI.isRegistered();
	
	private RunicItemStack item;
	private Player player;
	
	public ItemPlaceholder(ItemStack item) {
		this.item = new RunicItemStack(item);
	}
	
	public ItemPlaceholder(ItemStack item, Player player) {
		this.item = new RunicItemStack(item);
		this.player = player;
	}
	
	public ItemPlaceholder set(String placeholder, String value) {
		item.getDisplayName().replace(placeholder, value);
		if(item.hasLore()) {
			List<String> newLores = new ArrayList<String>();
			for(String lore : item.getLores()) {
				newLores.add(lore.replace(placeholder, value));
			}
			item.setLore(newLores);
		}
		return this;
	}
	
	public ItemPlaceholder set(String placeholder, int value) {
		return set(placeholder, value+"");
	}
	
	public RunicItemStack getItem() {
		if(papi_is_enabled && player != null) {
			if(PlaceholderAPI.containsPlaceholders(item.getDisplayName())) item.setDisplayName(PlaceholderAPI.setPlaceholders(player, item.getDisplayName()));
			if(item.hasLore()) {
				List<String> newLores = new ArrayList<String>();
				for(String lore : item.getLores()) {
					if(PlaceholderAPI.containsPlaceholders(lore)) {
						newLores.add(PlaceholderAPI.setPlaceholders(player, lore));
					} else newLores.add(lore);
				}
				item.setLore(newLores);
			}
		}
		return item;
	}
	
}