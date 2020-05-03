package aw.rmjtromp.RunicCore.core.features.main.moderation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.events.PlayerReportEvent;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.PlayerSelector;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.Config;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Report extends RunicFeature implements CommandExecutor, TabCompleter {

	@Override
	public String getName() {
		return "Report";
	}
	
	private enum PERMISSION {
		REPORT_RECEIVE("runic.report-receive");
		
		private String permission;
		PERMISSION(String permission) {
			this.permission = permission;
		}
		
		@Override
		public String toString() {
			return permission;
		}
	}
	
	private Config reports = null;
	
	@Override
	public void onEnable() {
		registerCommand(new RunicCommand("report")
				.setDescription("Report players for their bad behaviour")
				.setAliases(Arrays.asList("reports"))
				.setUsage("/report <player> <reason>")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, incorrect_usage, reason_too_vague, report_submitted, report_received, sender_not_a_player, report_self, report_error, report_cancelled;
	
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		
		reason_too_vague = Core.getMessages().getString("report.reason-too-vague", "&cReport reason too vague. Try being more precise or try to include more information.");
		report_submitted = Core.getMessages().getString("report.submitted", "&7Your report was submitted.");
		report_error = Core.getMessages().getString("report.error", "&cThere was an error submitting your report.");
		report_cancelled = Core.getMessages().getString("report.cancelled", "&7Your report was request was cancelled.");
		report_self = Core.getMessages().getString("report.reported-self", "&7You can not report yourself.");
		report_received = Core.getMessages().getString("report.received", "&e{TARGET} &7was reported by '&e{PLAYER}&7' for '&e{REASON}&7'.");
		
		if(Core.getMySQL() != null) {
			// check if table exists or create it
			try {
				PreparedStatement statement = Core.getMySQL().getConnection()
						.prepareStatement("SHOW TABLES LIKE 'reports';");
				ResultSet results = statement.executeQuery();
				if(results.next()==false) {
					// table doesnt exist
					PreparedStatement ps = Core.getMySQL().getConnection().prepareStatement("CREATE TABLE `reports` ( `id` INT NOT NULL AUTO_INCREMENT , `player` VARCHAR(36) NOT NULL , `target` VARCHAR(36) NOT NULL , `reason` VARCHAR(256) NOT NULL , `time` VARCHAR(10) NOT NULL , `reviewed` BOOLEAN NOT NULL DEFAULT FALSE , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
					ps.executeQuery();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(args.length == 1) {
			for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
				suggestion.add(player);
			}
		}
		
		return suggestion;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("reports")) {
			//
		} else {
			if(sender instanceof Player) {
				RunicPlayer player = RunicPlayer.cast(sender);
				if(args.length == 0 || args.length == 1) {
					player.sendMessage(Placeholder.parse(incorrect_usage, player).set("{COMMAND}", label.toLowerCase()+" <player> <reason>").getString());
				} else {
					RunicPlayer target = RunicPlayer.cast(plugin.getServer().getPlayerExact(args[0]));

					if(target != null && target.isOnline()) {
						if(!player.equals(target)) {
							// remove target name from arguments and join the string together to form the reason{}
							String reason = "";
							for(int i = 1; i < args.length; i++) {
								if(i == 1) reason += args[i];
								else reason += " "+args[i];
							}
							if(reason.matches("^(ha(ck|x)(?:ing|ers?|s)?|\\S{1,2}|[\\s\\d]+)$")) player.sendMessage(Placeholder.parse(reason_too_vague, player).getString());
							else {
								PlayerReportEvent PRE = new PlayerReportEvent(player, target, reason);
								Bukkit.getPluginManager().callEvent(PRE);
								if(!PRE.isCancelled()) {
									if(report(player, target, reason)) player.sendMessage(Placeholder.parse(report_submitted, player).getString());
									else player.sendMessage(Placeholder.parse(report_error, player).getString());
								} else player.sendMessage(Placeholder.parse(report_cancelled, player).getString());
							}
						} else player.sendMessage(Placeholder.parse(report_self, player).getString());
					} else player.sendMessage(Placeholder.parse(target_not_found, player).set("{TARGET}", args[0]).getString());
				}
			} else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(sender_not_a_player).getString()));
		}
		return true;
	}
	
	/**
	 * Reports a player
	 * @param player The player that the target is being reported by
	 * @param target The player that's getting reports
	 * @param reason The reason the target player is being reported
	 */
	private boolean report(RunicPlayer player, RunicPlayer target, String reason) {
		if(player.getPlayer().equals(target.getPlayer())) return false;
		String time = ""+System.currentTimeMillis()/1000;
		String server = plugin.getCore().getBungeeCord().getServerName();
		
		if(Core.getMySQL() != null) {
			// save report to MySQL database
			try {
				PreparedStatement insert = Core.getMySQL().getConnection()
						.prepareStatement("INSERT INTO reports (player,target,reason,time,server) VALUES (?,?,?,?,?)");
				insert.setString(1, player.getUniqueId().toString().toString());
				insert.setString(2, target.getUniqueId().toString().toString());
				insert.setString(3, reason);
				insert.setString(4, time);
				insert.setString(5, server != null && !server.isEmpty() ? server : "undefined");
				insert.executeUpdate();

				return true;
			} catch (SQLException e) {
				e.printStackTrace();
//				getDebugger().error("There was an error submitting report to MySQL database, saving to local config.", e.getMessage());
			}
		}
		
		// this part is only reached is the SQL statement didn't execute successfully

		// save report to local config
		if(reports == null) {
			reports = Config.init("reports").load();
		}
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.hasPermission(PERMISSION.REPORT_RECEIVE.toString()))
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(report_received).set("{PLAYER}", player.getName()).set("{TARGET}", target.getName()).set("{REASON}", reason).getString()));
		}
		
		int i = 0;
		while(reports.contains(i+"")) i++;
		reports.set(i+".player.uuid", player.getUniqueId().toString());
		reports.set(i+".player.name", player.getName());
		reports.set(i+".target.uuid", target.getUniqueId().toString());
		reports.set(i+".target.name", target.getName());
		reports.set(i+".reason", reason);
		reports.set(i+".timestamp", time);
		return true;
	}

	/**
	 * Reports a player
	 * @param player Th e player that the target is being reported by
	 * @param target The player that's getting reports
	 * @param reason The reason the target player is being reported
	 */
	@SuppressWarnings("unused")
	private boolean report(Player player, Player target, String reason) {
		return report(RunicPlayer.cast(player), RunicPlayer.cast(target), reason);
	}

}
