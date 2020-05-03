package aw.rmjtromp.RunicCore.core.features.main.moderation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicOfflinePlayer;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.RunicUtils;
import aw.rmjtromp.RunicCore.utilities.configs.Config;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Blacklist extends RunicFeature implements CommandExecutor, TabCompleter {
	
	private static Config config = null;
	
	public Blacklist() {
		super(false); // disabled
	}

	@Override
	public String getName() {
		return "Blacklist";
	}
	
	private enum PERMISSION {
		BLACKLIST("runic.blacklist"),
		BLACKLIST_EXCEMPT("runic.blacklist.excempt"),
		BLACKLIST_ALERT("runic.blacklist.alert");
		
		private String permission;
		PERMISSION(String permission) {
			this.permission = permission;
		}
		
		@Override
		public String toString() {
			return permission;
		}
	}
	
	@Override
	public void onEnable() {
		registerCommand(new RunicCommand("blacklist")
				.setDescription("Blacklists a player from the server")
				.setPermission(PERMISSION.BLACKLIST.toString())
				.setUsage("/blacklist <player> [time] <reason>")
				.setExecutor(this)
				.setTabCompleter(this));
		
		if(Core.getMySQL() != null) {
			// blacklists
			try {
				PreparedStatement statement = Core.getMySQL().getConnection()
						.prepareStatement("SHOW TABLES LIKE 'blacklists';");
				ResultSet results = statement.executeQuery();
				if(results.next()==false) {
					// table doesnt exist
					PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `blacklists` ( `id` INT NOT NULL AUTO_INCREMENT , `player` VARCHAR(36) NOT NULL , `executor` VARCHAR(36) NOT NULL , `reason` TINYTEXT NOT NULL , `time` INT(10) NOT NULL , `expiration` INT(10) NULL DEFAULT NULL , `server` VARCHAR(16) NOT NULL , PRIMARY KEY (`id`)) ENGINE = MyISAM;");
					ps.executeQuery();
				}
				
				// move all blacklists from config to database if there is any
				if(Config.init("blacklists").exists()) {
					debug("blacklists config exists, moving data to database.");
					Config conf = Config.init("blacklists").load();
					Set<String> keys = conf.getKeys();
					
					String server = plugin.getCore().getBungeeCord().getServerName();
					server = server != null && !server.isEmpty() ? server : "undefined";
					for(String key : keys) {
						try {
							String player = conf.getString(key+".player");
							String executor = conf.getString(key+".player");
							int time = conf.getInt(key+".time");
							int expiration = conf.contains(key+".expiration") ? conf.getInt(key+".expiration") : 0;
							String reason = conf.getString(key+".reason");
							
							PreparedStatement insert = Core.getMySQL().getConnection().prepareStatement("INSERT INTO `blacklists` (player,executor,time,expiration,reason,server) VALUES (?,?,?,?,?,?)");
							insert.setString(1, player);
							insert.setString(2, executor);
							insert.setInt(3, time);
							insert.setInt(4, expiration);
							insert.setString(5, reason);
							insert.setString(6, server);
							insert.executeUpdate();
							
							conf.set(key, null);
						} catch (SQLException e) {
							error("There was an error moving blacklists from config to database: "+e.getMessage());
						}
					}
				}
			} catch (SQLException e) {
				error("There was an error creating 'blacklists' table in database: "+e.getMessage());
				Core.warn("There was an error creating a new 'blacklists' table, using configs.");
				config = Config.init("blacklists").load();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static String target_blacklisted_permanently, target_blacklisted_temporarily, target_not_found, incorrect_usage, no_permission, not_enough_arguments;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		not_enough_arguments = Core.getMessages().getMessage(MESSAGE.NOT_ENOUGH_ARGUMENTS);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		
		target_blacklisted_permanently = Core.getMessages().getString("features.blacklist.alert.permanently", "&8» &e{PLAYER} &7blacklisted &e{TARGET} &7for &e{REASON}&7.");
		target_blacklisted_temporarily = Core.getMessages().getString("features.blacklist.alert.temporarily", "&8» &e{PLAYER} &7blacklisted &e{TARGET} &7for &e{DURATION} &7for &e{REASON}&7.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission(PERMISSION.BLACKLIST.toString())) {
			if(args.length == 0 || args.length == 1) sender.sendMessage(Placeholder.parse(not_enough_arguments, sender).set("{COMMAND}", label.toLowerCase()+" <player> [time] <reason>").getString());
			else if(args.length == 2) {
				
			}
		} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Blacklists the player permanently
	 * @param RunicPlayer target
	 * @param RunicPlayer executor
	 * @param String reason
	 * @return
	 * @throws Exception
	 */
	public static boolean blacklistPlayer(RunicPlayer target, RunicPlayer executor, String reason) throws Exception {
		return blacklistPlayer(target, executor, reason, (long) 0);
	}
	
	/**
	 * Blacklist the player until said time (in seconds)
	 * if expiration = 0, blacklist will be permanent
	 * @param RunicPlayer target
	 * @param RunicPlayer executor
	 * @param String reason
	 * @param Long expiration
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static boolean blacklistPlayer(RunicPlayer target, RunicPlayer executor, String reason, long expiration) throws Exception {
		if(target != null) {
			if(executor != null) {
				if(!executor.equals(target)) {
					if(target.hasPermission(PERMISSION.BLACKLIST_EXCEMPT.toString())) {
						if(executor.hasPermission(PERMISSION.BLACKLIST.toString())) {
							if(reason != null && !reason.isEmpty()) {
								String playerUUID = target.getUniqueId().toString();
								String executorUUID = executor.getUniqueId().toString();
								long timeInSec = System.currentTimeMillis()/1000;
								int time = (int) timeInSec;
								long expirationInSec = (Long.toString(expiration).length() <= 10) ? expiration : expiration/1000;
								int exp = (int) expirationInSec;
								String server = RunicCore.getInstance().getCore().getBungeeCord().getServerName();
								
								if(Core.getMySQL() != null) {
									// save report to MySQL database
									try {
										PreparedStatement insert = Core.getMySQL().getConnection()
												.prepareStatement("INSERT INTO `blacklists` (player,executor,time,expiration,reason,server) VALUES (?,?,?,?,?,?)");
										insert.setString(1, playerUUID);
										insert.setString(2, executorUUID);
										insert.setInt(3, time);
										insert.setInt(4, exp);
										insert.setString(5, reason);
										insert.setString(6, server != null && !server.isEmpty() ? server : "undefined");
										insert.executeUpdate();

										target.setBanned(true);
										target.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&8« &6&lRunic&e&lSky &8»\n&cYou were blacklisted for:\n&7"+reason+"\n&7\n&7If you believe this was a mistake,\n&7please contact our support team."));
										String duration = "a while"; // TODO make duration show the actual duration
										RunicPlayer.getOnlinePlayers().forEach((p) -> {
											if(p.hasPermission(PERMISSION.BLACKLIST_ALERT.toString())) {
												if(exp <= 0) p.sendMessage(Placeholder.parse(target_blacklisted_permanently, executor).set("{PLAYER}", executor.getName()).set("{TARGET}", target.getName()).set("{REASON}", reason).getString());
												else p.sendMessage(Placeholder.parse(target_blacklisted_temporarily, executor).set("{PLAYER}", executor.getName()).set("{TARGET}", target.getName()).set("{DURATION}", duration).set("{REASON}", reason).getString());
											}
										});
										
										return true;
									} catch (SQLException e) {}
								}
								
								// if mysql connection is null, or if there was an error executing sql statement, it will continue here
								if(config == null) config = Config.init("blacklists").load();
								
								int i = 0;
								while(config.contains(Integer.toString(i)))  i++;
								config.set(i+".player", playerUUID);
								config.set(i+".executor", executorUUID);
								config.set(i+".time", time);
								if(exp > 0) config.set(i+".expiration", exp);
								config.set(i+".reason", reason);
								
								target.setBanned(true);
								target.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&8« &6&lRunic&e&lSky &8»\n&cYou were blacklisted for:\n&7"+reason+"\n&7\n&7If you believe this was a mistake,\n&7please contact our support team."));
								String duration = "a while"; // TODO make duration show the actual duration
								RunicPlayer.getOnlinePlayers().forEach((p) -> {
									if(p.hasPermission(PERMISSION.BLACKLIST_ALERT.toString())) {
										if(exp <= 0) p.sendMessage(Placeholder.parse(target_blacklisted_permanently, executor).set("{PLAYER}", executor.getName()).set("{TARGET}", target.getName()).set("{REASON}", reason).getString());
										else p.sendMessage(Placeholder.parse(target_blacklisted_temporarily, executor).set("{PLAYER}", executor.getName()).set("{TARGET}", target.getName()).set("{DURATION}", duration).set("{REASON}", reason).getString());
									}
								});
								
								return true;
							} else throw new Exception("INVALID_REASON");
						} else throw new Exception("EXECUTOR_LACKS_PERMISSION");
					} else throw new Exception("TARGET_IS_EXCEMPT");
				} else throw new Exception("EXECUTOR_IS_TARGET");
			} else throw new Exception("INVALID_EXECUTOR");
		} else throw new Exception("INVALID_TARGET");
	}
	
	/*
	 * PreparedStatement#execute()
	 * works with any type of sql statement
	 * A true indicates that the execute method returned a result set object
	 * false indicates that the query returned an int value or void
	 * 
	 * PreparedStatement#executeQuery()
	 * execute statements that returns a result set by fetching some data from the database.
	 * It executes only select statements.
	 * 
	 * PreparedStatement#executeUpdate()
	 * execute sql statements that insert/update/delete data at the database.
	 * This method return int value representing number of records affected
	 */
	
	public static boolean isPlayerBlacklisted(RunicOfflinePlayer player) {
		String uuid = player.getUniqueId().toString();
		if(Core.getMySQL() != null) {
			try {
				PreparedStatement statement = Core.getMySQL().getConnection().prepareStatement("SELECT id, expiration FROM `blacklists` WHERE player=?");
				statement.setString(1, uuid);

				ResultSet results = statement.executeQuery();
				boolean blacklisted = false;
				while(results.next()) {
					int id = results.getInt("id");
					int expiration = results.getInt("expiration");
					
					if(expiration <= 0 || Core.currentTimeSeconds() < expiration) blacklisted |= true;
					else {
						try {
							PreparedStatement stmnt = Core.getMySQL().getConnection().prepareStatement("DELETE FROM blacklists WHERE id = ?");
							stmnt.setInt(1, id);
							stmnt.executeUpdate();
						} catch(SQLException e) {
							System.out.print("[RunicCore] There was an error removing player's expired blacklist: "+e.getMessage());
						}
					}
				}
				return blacklisted;
			} catch (SQLException e) {
				System.out.print("[RunicCore] There was an error checking if player is blacklisted: "+e.getMessage());
			}
		}
		
		if(Config.init("blacklists").exists()) {
			Config conf = Config.init("blacklists").load();
			Set<String> keys = conf.getKeys();
			for(String key : keys) {
				String id = conf.getString(key+".player");
				if(player.getUniqueId().equals(RunicUtils.stringToUUID(id))) {
					int expiration = conf.contains(key+".expiration") ? conf.getInt(key+".expiration") : 0;
					if(expiration <= 0 || Core.currentTimeSeconds() < expiration) return true;
					else {
						conf.set(key, null);
					}
				}
			}
		}
		return false;
	}
	
//	public static String getPlayerBlacklistReason(RunicOfflinePlayer player) {
//		try {
//			PreparedStatement statement = Core.getMySQL().getConnection().prepareStatement("SELECT id, expiration, reason FROM blacklists WHERE player=?");
//			statement.setString(1, uuid.toString());
//			ResultSet results = statement.executeQuery();
//			results.next();
//			
//			System.out.print(results.getInt("COINS"));
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
	public static final class BlacklistResult implements Comparable<BlacklistResult> {
		
		private int id, time, expiration;
		private RunicOfflinePlayer player, executor;
		private String reason, server;
		
		public static List<BlacklistResult> get(RunicOfflinePlayer player) {
			List<BlacklistResult> res = new ArrayList<>();
			
			if(Core.getMySQL() != null) {
				try {
					PreparedStatement statement = Core.getMySQL().getConnection().prepareStatement("SELECT * WHERE player=? ORDER BY id DESC");
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
						
						BlacklistResult br = new BlacklistResult(id, pid, eid, time, expiration, reason, server);
						res.add(br);
					}
				} catch(SQLException e) {
					System.out.print("[RunicCore] There was an error fetchning blacklist results: "+e.getMessage());
				}
			}
			
			return res;
		}
		
		private BlacklistResult(int id, UUID player, UUID executor, int time, int expiration, String reason, String server) {
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
			return Core.currentTimeSeconds() >= expiration;
		}
		
		public boolean delete() {
			return false;
		}

		@Override
		public int compareTo(BlacklistResult o) {
			// orders by time of execution descending (most recent comes first)
			return o.getTime() - getTime();
		}
		
//		@Override
//		public boolean equals(Object obj) {
//			if(obj instanceof PunishmentResult)
//		}
		
	}

}
