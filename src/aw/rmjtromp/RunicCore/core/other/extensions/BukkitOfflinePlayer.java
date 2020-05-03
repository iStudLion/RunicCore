package aw.rmjtromp.RunicCore.core.other.extensions;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BukkitOfflinePlayer implements OfflinePlayer {
	
	protected OfflinePlayer player;
	
	public BukkitOfflinePlayer(OfflinePlayer player) {
		this.player = player;
	}
	
	public BukkitOfflinePlayer(UUID uuid) {
		this.player = Bukkit.getOfflinePlayer(uuid);
	}

	@Override
	public boolean isOp() {
		return player.isOp();
	}

	@Override
	public void setOp(boolean arg0) {
		player.setOp(arg0);
	}

	@Override
	public Map<String, Object> serialize() {
		return player.serialize();
	}

	@Override
	public Location getBedSpawnLocation() {
		return player.getBedSpawnLocation();
	}

	@Override
	public long getFirstPlayed() {
		return player.getFirstPlayed();
	}

	@Override
	public long getLastPlayed() {
		return player.getLastPlayed();
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public Player getPlayer() {
		return player.getPlayer();
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public boolean hasPlayedBefore() {
		return player.hasPlayedBefore();
	}

	@Override
	public boolean isBanned() {
		return player.isBanned();
	}

	@Override
	public boolean isOnline() {
		return player.isOnline();
	}

	@Override
	public boolean isWhitelisted() {
		return player.isWhitelisted();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setBanned(boolean arg0) {
		player.setBanned(arg0);
	}

	@Override
	public void setWhitelisted(boolean arg0) {
		player.setWhitelisted(arg0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BukkitOfflinePlayer) {
			return player.equals(((BukkitOfflinePlayer) obj).player);
		} else {
			return player.equals(obj);
		}
	}

}
