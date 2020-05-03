package aw.rmjtromp.RunicCore.core.features.main.general;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;

public final class Whitelist extends RunicFeature {

	@Override
	public String getName() {
		return "Whitelist";
	}
	
	private String message;
	private boolean whitelisted = false;
	@Override
	public void loadConfigurations() {
		whitelisted = Core.getConfig().getBoolean("features.whitelist.whitelisted", false);
		message = Core.getConfig().getString("features.whitelist.message", "&cServer whitelisted.");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent e) {
		if(whitelisted && !isWhitelisted(e.getPlayer().getUniqueId())) {
			e.disallow(Result.KICK_WHITELIST, ChatColor.translateAlternateColorCodes('&', message));
		}
	}
	
	private boolean isWhitelisted(UUID uuid) {
		for(OfflinePlayer op : plugin.getServer().getWhitelistedPlayers()) {
			if(op.getUniqueId().equals(uuid)) {
				return true;
			}
		}
		return false;
	}
	
}
