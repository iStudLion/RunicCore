package aw.rmjtromp.RunicCore.core.other.extensions;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import aw.rmjtromp.RunicCore.core.other.PunishmentResult;
import aw.rmjtromp.RunicCore.utilities.configs.Config;
import aw.rmjtromp.RunicCore.utilities.configs.PlayerConfig;

public class RunicOfflinePlayer extends BukkitOfflinePlayer {
	
	private static HashMap<UUID, RunicOfflinePlayer> players = new HashMap<>();

	private PlayerConfig config = null;

	protected RunicOfflinePlayer(UUID uuid) {
		super(uuid);
		players.put(uuid, this);
	}
	
	public static RunicOfflinePlayer c(OfflinePlayer p) {
		if(players.containsKey(p.getUniqueId())) return players.get(p.getUniqueId());
		return new RunicOfflinePlayer(p.getUniqueId());
	}
	
	// works with all runic extensions
	public static RunicOfflinePlayer c(RunicOfflinePlayer p) {
		if(players.containsKey(p.getUniqueId())) return players.get(p.getUniqueId());
		return new RunicOfflinePlayer(p.getUniqueId());
	}
	
	public PlayerConfig getPlayerConfig() {
		if(config == null) config = PlayerConfig.getPlayerConfig(this);
		return config;
	}
	
	public boolean playerConfigExists() {
		return Config.init("players", getUniqueId().toString()).exists();
	}
	
	public List<PunishmentResult> getPunishments() {
		return PunishmentResult.get(this);
	}
	
	public boolean isBlacklisted() {
		return false;
	}

}
