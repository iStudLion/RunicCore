package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.PlayerSelector;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Ping extends RunicFeature implements CommandExecutor, TabCompleter {

	@Override
	public String getName() {
		return "Ping";
	}
	
	private enum PERMISSION {
		PING_SELF("runic.ping"),
		PING_OTHERS("runic.ping.others");
		
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
		registerCommand(new RunicCommand("ping")
				.setDescription("Shows a player's ping")
				.setPermission(PERMISSION.PING_SELF.toString())
				.setUsage("/ping [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	String ping_ping, ping_ping_target, no_permission, sender_not_a_player, target_not_found, incorrect_usage;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);

		ping_ping = Core.getMessages().getString("features.ping.ping", "&7You have a ping of {PING}ms&7.");
		ping_ping_target = Core.getMessages().getString("features.ping.ping-target", "&e{TARGET} &7has a ping of {PING}ms&7.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				if(sender.hasPermission(PERMISSION.PING_SELF.toString())) {
					RunicPlayer player = RunicPlayer.cast(sender);
					int ping = player.getCraftPlayer().getHandle().ping;
					player.sendMessage(Placeholder.parse(ping_ping, player).set("{PING}", (ping <= 151 ? "&a"+ping : ping <= 300 ? "&6"+ping : ping > 300 ? "&c"+ping : "&e"+ping)).getString());
				} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			} else sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
		} else if(args.length == 1) {
			if(sender.hasPermission(PERMISSION.PING_SELF.toString())) {
				List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
				if(targets.size() > 0) {
					if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
						// sender is targeting self
						RunicPlayer target = RunicPlayer.cast(sender);
						int ping = target.getCraftPlayer().getHandle().ping;
						target.sendMessage(Placeholder.parse(ping_ping, target).set("{PING}", (ping <= 151 ? "&a"+ping : ping <= 300 ? "&6"+ping : ping > 300 ? "&c"+ping : "&e"+ping)).getString());
					} else {
						// sender is targeting others
						if(sender.hasPermission(PERMISSION.PING_OTHERS.toString())) {
							for(RunicPlayer target : targets) {
								int ping = target.getCraftPlayer().getHandle().ping;
								if(target.equals(sender)) target.sendMessage(Placeholder.parse(ping_ping, target).set("{PING}", (ping <= 151 ? "&a"+ping : ping <= 300 ? "&6"+ping : ping > 300 ? "&c"+ping : "&e"+ping)).getString());
								else sender.sendMessage(Placeholder.parse(ping_ping_target, sender).set("{TARGET}", target.getName()).set("{PING}", (ping <= 151 ? "&a"+ping : ping <= 300 ? "&6"+ping : ping > 300 ? "&c"+ping : "&e"+ping)).getString());
							}
						} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
					}
				} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else {
			if(sender.hasPermission(PERMISSION.PING_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
			else if(sender.hasPermission(PERMISSION.PING_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
			else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		}
		
		// using reflection
		/*try {
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			int ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}*/
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(args.length == 1 && sender.hasPermission(PERMISSION.PING_OTHERS.toString())) {
			for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
				suggestion.add(player);
			}
		}
		
		return suggestion;
	}
	
}
