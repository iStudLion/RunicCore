package aw.rmjtromp.RunicCore.core.features.main.moderation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.events.PlayerFlagEvent;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.configs.Config;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Flagger extends RunicFeature {

	@Override
	public String getName() {
		return "Flagger";
	}
	
	private enum PERMISSION {
		FLAGGER_BYPASS("runic.flagger.bypass"),
		FLAGGER_RECEIVE("runic.flagger.flags-receive");
		
		private String permission;
		PERMISSION(String permission) {
			this.permission = permission;
		}
		
		@Override
		public String toString() {
			return permission;
		}
	}

	private static Config flags;
	
	public enum FlagReason { Advertising, Cheating, Spamming, AutoMining };

	@Override
	public void onEnable() {
		loadConfigurations();
	}
	
	private static String account_flagged, flag_received;
	
	@Override
	public void loadConfigurations() {
		account_flagged = Core.getMessages().getString("flags.player-flagged", "&7Your account was flagged for '&c{REASON}&7'.");
		flag_received = Core.getMessages().getString("flag.flag-received", "&e{TARGET} &7was flagged for &e{REASON}&7.");
		
		if(Core.getMySQL() != null) {
			// check if table exists or create it
			try {
				PreparedStatement statement = Core.getMySQL().getConnection().prepareStatement("SHOW TABLES LIKE 'flags';");
				ResultSet results = statement.executeQuery();
				if(results.next()==false) {
					// table doesnt exist
					PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `flags` ( `id` INT NOT NULL AUTO_INCREMENT , `player` VARCHAR(36) NOT NULL , `reason` VARCHAR(256) NOT NULL , `message` VARCHAR(256) NOT NULL , `time` VARCHAR(10) NOT NULL , `server` VARCHAR(30) NOT NULL , `reviewed` BOOLEAN NOT NULL DEFAULT FALSE , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
					ps.executeQuery();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reports a player
	 * @param player The player that is being flagged
	 * @param reason The reason the player is being flagged
	 * @param message extra information about flag
	 */
	public static boolean flagPlayer(RunicPlayer player, FlagReason reason) {
		return flagPlayer(player, "", reason);
	}
	
	public static boolean flagPlayer(RunicPlayer player, FlagReason reason, String message) {
		boolean res = flagPlayer(player, message, reason);
		if(res) player.sendMessage(Placeholder.parse(account_flagged, player).set("{REASON}", reason.toString()).getString());
		return res;
	}
	
	/**
	 * Reports a player
	 * @param player The player that is being flagged
	 * @param reason The reason the player is being flagged
	 */
	private static boolean flagPlayer(RunicPlayer player,String message, FlagReason reason) {
		if(player.hasPermission(PERMISSION.FLAGGER_BYPASS.toString())) return false;
		String time = System.currentTimeMillis()/1000+"";
		String server = plugin.getCore().getBungeeCord().getServerName();
		
		PlayerFlagEvent PFE = new PlayerFlagEvent(player, reason, message);
		Bukkit.getPluginManager().callEvent(PFE);
		if(!PFE.isCancelled()) {
			if(Core.getMySQL() != null) {
				// save report to MySQL database
				try {
					PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("INSERT INTO flags (player,reason,message,time,server) VALUES (?,?,?,?,?)");
					ps.setString(1, player.getUniqueId().toString());
					ps.setString(2, reason != null ? reason.toString() : "undefined");
					ps.setString(3, message);
					ps.setString(4, time);
					ps.setString(5, server != null && !server.isEmpty() ? server : "undefined");
					ps.executeUpdate();

					return true;
				} catch (SQLException e) {
					e.printStackTrace();
//					getDebugger().error("There was an error submitting flag to MySQL database, saving to local config.", e.getMessage());
				}
			}
			
			// this part is only reached if the MySQL statement is not successfully executed

			// save report to local config
			if(flags == null) {
				flags = Config.init("flags").load();
			}
			
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.hasPermission(PERMISSION.FLAGGER_RECEIVE.toString())) 
					p.sendMessage(Placeholder.parse(flag_received).set("{TARGET}", player.getName()).set("{REASON}", reason.toString().toLowerCase()).getString());
			}
			
			int i = 0;
			while(flags.contains(i+"")) i++;
			flags.set(i+".player.uuid", player.getUniqueId().toString());
			flags.set(i+".player.name", player.getName());
			flags.set(i+".reason", reason != null ? reason.toString() : "undefined");
			flags.set(i+".message", message);
			flags.set(i+".timestamp", time);
			return true;
		}
		return false;
	}

}
