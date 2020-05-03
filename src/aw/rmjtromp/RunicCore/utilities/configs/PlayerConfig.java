package aw.rmjtromp.RunicCore.utilities.configs;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.other.extensions.BukkitPlayer;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicOfflinePlayer;

public class PlayerConfig extends Config {
	
	@Override
	public String getName() {
		return "PlayerConfig";
	}

	private PlayerConfig(Player player) {
		super("players", player.getUniqueId().toString());
		load();
	}
	
	public static PlayerConfig getPlayerConfig(BukkitPlayer player) {
		return new PlayerConfig(player.getPlayer());
	}
	
	public static PlayerConfig getPlayerConfig(Player player) {
		return new PlayerConfig(player);
	}
	
	public static PlayerConfig getPlayerConfig(OfflinePlayer player) {
		return new PlayerConfig(player.getPlayer());
	}
	
	public static PlayerConfig getPlayerConfig(RunicOfflinePlayer player) {
		return new PlayerConfig(player.getPlayer());
	}
	
}
