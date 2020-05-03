package aw.rmjtromp.RunicCore.utilities.placeholders;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.utilities.DependencyManager.Dependency;
import me.clip.placeholderapi.PlaceholderAPI;

public final class StringPlaceholder {

	private boolean papi_is_enabled = Dependency.PLACEHOLDERAPI.isRegistered();
	
	private String string;
	private Player player;
	
	protected StringPlaceholder(String string) {
		this.string = string;
	}
	
	protected StringPlaceholder(String string, Player player) {
		this.string = string; this.player = player;
	}
	
	public StringPlaceholder set(String placeholder, String value) {
		string = string.replace(placeholder, value);
		return this;
	}
	
	public StringPlaceholder set(String placeholder, int value) {
		string = string.replace(placeholder, value+"");
		return this;
	}
	
	public String getString() {
		if(papi_is_enabled && player != null) {
			string = PlaceholderAPI.setPlaceholders(player, string);
		}
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	@Deprecated
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
}
