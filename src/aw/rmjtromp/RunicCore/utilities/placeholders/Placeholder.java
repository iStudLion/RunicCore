package aw.rmjtromp.RunicCore.utilities.placeholders;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import aw.rmjtromp.RunicCore.core.other.extensions.BukkitPlayer;

public class Placeholder {
	
	public static StringPlaceholder parse(String string) {
		return new StringPlaceholder(string);
	}
	
	public static StringPlaceholder parse(String string, Object player) {
		if(string.isEmpty() || string == null) return new StringPlaceholder("null");
		else if(player instanceof BukkitPlayer) return new StringPlaceholder(string, ((BukkitPlayer) player).getPlayer());
		else if(player instanceof Player) return new StringPlaceholder(string, (Player) player);
		else return new StringPlaceholder(string);
	}
	
	public static ItemPlaceholder parse(ItemStack item) {
		return new ItemPlaceholder(item);
	}
	
	public static ItemPlaceholder parse(ItemStack item, Object player) {
		if(item == null) return null;
		else if(player instanceof BukkitPlayer) return new ItemPlaceholder(item, ((BukkitPlayer) player).getPlayer());
		else if(player instanceof Player) return new ItemPlaceholder(item, (Player) player);
		else return new ItemPlaceholder(item);
	}
	
}
