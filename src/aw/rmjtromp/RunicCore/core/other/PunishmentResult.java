package aw.rmjtromp.RunicCore.core.other;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicOfflinePlayer;
import aw.rmjtromp.RunicCore.utilities.RunicUtils;
import aw.rmjtromp.RunicCore.utilities.configs.PlayerConfig;

public class PunishmentResult implements Comparable<PunishmentResult> {

	private int id, time, expiration;
	private RunicOfflinePlayer player, executor;
	private String reason, server;
	
	public enum PunishmentType {
		MUTE,
		WARNING,
		BAN,
		BLACKLIST;
	}
	
	public static List<PunishmentResult> get(RunicOfflinePlayer player) {
		List<PunishmentResult> res = new ArrayList<>();
		
		if(Core.getMySQL() != null) {
			try {
				PreparedStatement statement = Core.getMySQL().getConnection().prepareStatement("SELECT * FROM punishments WHERE player=? ORDER BY time DESC");
				statement.setString(1, player.getUniqueId().toString());

				ResultSet results = statement.executeQuery();
				while(results.next()) {
					int id = results.getInt("id");
					UUID pid = RunicUtils.stringToUUID(results.getString("player"));
					UUID eid = RunicUtils.stringToUUID(results.getString("executor"));
					int time = results.getInt("time");
					int expiration = results.getInt("expiration");
					String reason = results.getString("reason");
					String server = results.getString("server");
					PunishmentType type = PunishmentType.valueOf(results.getString("type").toUpperCase());
					
					PunishmentResult pr = new PunishmentResult(type, id, pid, eid, time, expiration, reason, server);
					res.add(pr);
				}
			} catch(SQLException e) {
				System.out.print("[RunicCore] There was an error fetchning punishment results for "+player.getName()+": "+e.getMessage());
			}
		}
		
		if(player.playerConfigExists()) {
			PlayerConfig config = player.getPlayerConfig();
			if(config.contains("punishments")) {
				String server = RunicCore.getInstance().getCore().getBungeeCord().getServerName();
				server = server != null && !server.isEmpty() ? server : "undefined";
				UUID pid = player.getUniqueId();
				
				Set<String> keys = config.getKeys();
				for(String key : keys) {
					try {
						if(config.contains("punishments."+key+".executor") && config.contains("punishments."+key+".type") && config.contains("punishments."+key+".reason") && config.contains("punishments."+key+".time")) {
							UUID eid = RunicUtils.stringToUUID(config.getString("punishments."+key+".executor"));
							String reason = config.getString("punishments."+key+".reason");
							PunishmentType type = PunishmentType.valueOf(config.getString("punishments."+key+".type").toUpperCase());
							int time = config.getInt("punishments."+key+".time");
							int expiration = config.contains("punishments."+key+".expiration") ? config.getInt("punishments."+key+".expiration") : 0;
							int id = RunicUtils.generateRandomNumber(10000, 99999);
							
							try {
								PreparedStatement insert = Core.getMySQL().getConnection().prepareStatement("INSERT INTO `punishments` (player,executor,time,expiration,reason,server,type) VALUES (?,?,?,?,?,?,?)");
								insert.setString(1, pid.toString());
								insert.setString(2, eid.toString());
								insert.setInt(3, time);
								insert.setInt(4, expiration);
								insert.setString(5, reason);
								insert.setString(6, server);
								insert.setString(7, type.toString());
								insert.executeUpdate();
								
								// remove it if it gets uploaded successfully
								config.set("punishments."+key, null);
							} catch (SQLException e) {
								System.out.print("[RunicCore] There was an error uploading punishment for "+player.getName()+" to database: "+e.getMessage());
							}
							
							PunishmentResult pr = new PunishmentResult(type, id, pid, eid, time, expiration, reason, server);
							res.add(pr);
						}
					} catch(Exception e) {
						System.out.print("[RunicCore] There was an error grabbing punishment results for "+player.getName()+": "+e.getMessage());
					}
				}
			}
		}
		
		return res;
	}
	
	protected PunishmentResult(PunishmentType type, int id, UUID player, UUID executor, int time, int expiration, String reason, String server) {
		this.id = id; this.player = Core.getOfflinePlayer(player); this.executor = Core.getOfflinePlayer(executor); this.time = time; this.expiration = expiration; this.reason = reason; this.server = server;
	}
	
	public int getId() {
		return id;
	}
	
	public RunicOfflinePlayer getPlayer() {
		return player;
	}
	
	public RunicOfflinePlayer getExecutor() {
		return executor;
	}
	
	public int getTime() {
		return time;
	}
	
	public int getExpiration() {
		return expiration;
	}
	
	public String getReason() {
		return reason;
	}
	
	public String getServer() {
		return server;
	}
	
	public boolean isPermanent() {
		return expiration <= 0;
	}
	
	public boolean hasExpired() {
		return (!isPermanent() && Core.currentTimeSeconds() >= expiration);
	}
	
	@Deprecated
	public boolean delete() {
		// TODO
		return false;
	}

	@Override
	public int compareTo(PunishmentResult o) {
		// orders by time of execution descending (most recent comes first)
		return o.getTime() - getTime();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PunishmentResult) {
			PunishmentResult res = (PunishmentResult) obj;
			return (getId() == res.getId() && getPlayer().equals(res.getPlayer()) && getExecutor().equals(res.getExecutor()) && getTime() == res.getTime() && getExpiration() == res.getExpiration());
		}
		return false;
	}
	
}
