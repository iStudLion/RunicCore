package aw.rmjtromp.RunicCore.utilities.essential;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import aw.rmjtromp.RunicCore.core.other.extensions.RunicItemStack;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.Conditional;
import aw.rmjtromp.RunicCore.utilities.DependencyManager.Dependency;
import me.clip.placeholderapi.PlaceholderAPI;

public final class RunicGUIItem {

	private int slot;
	private String condition;
	private ItemStack item;

	public RunicGUIItem(ItemStack item, int slot, String condition) {
		this.item = item; this.slot = slot; this.condition = condition;
	}
	
	public RunicGUIItem(ItemStack item, int slot) {
		this.item = item; this.slot = slot;
	}
	
	public boolean parseCondition(RunicPlayer player) {
		if(!hasCondition()) return true;
		String cond = condition;
		if(Dependency.PLACEHOLDERAPI.isRegistered()) {
			if(PlaceholderAPI.containsPlaceholders(cond)) cond = PlaceholderAPI.setPlaceholders(player.getPlayer(), cond);
		}

		return Conditional.parse(ChatColor.stripColor(cond)).getResults();
	}
	
	public boolean hasCondition() {
		return (condition != null && !condition.isEmpty());
	}
	
	public ItemStack getPreparedItem(RunicPlayer player) {
		if(item != null && !item.getType().equals(Material.AIR)) {
			RunicItemStack RIM = new RunicItemStack(item);
			if(Dependency.PLACEHOLDERAPI.isRegistered()) {
				if(PlaceholderAPI.containsPlaceholders(RIM.getDisplayName())) RIM.setDisplayName(PlaceholderAPI.setPlaceholders(player.getPlayer(), RIM.getDisplayName()));
				if(RIM.hasLore()) {
					List<String> newLores = new ArrayList<String>();
					for(String lore : RIM.getLores()) {
						if(PlaceholderAPI.containsPlaceholders(lore)) {
							lore = PlaceholderAPI.setPlaceholders(player.getPlayer(), lore);
						}
						newLores.add(lore);
					}
					RIM.setLore(newLores);
				}
			}
			return (ItemStack) RIM;
		}
		return null;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public int getSlot() {
		return slot;
	}
	
}
