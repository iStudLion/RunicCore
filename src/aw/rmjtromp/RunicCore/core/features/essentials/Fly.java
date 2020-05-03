package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

public final class Fly extends RunicFeature implements CommandExecutor, TabCompleter {

	private enum PERMISSION {
		FLY_SELF("runic.fly"),
		FLY_OTHERS("runic.fly.others");
		
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
	public String getName() {
		return "Fly";
	}
	
	@Override
	public void onEnable() {
		registerCommand(new RunicCommand("fly")
				.setDescription("Toggles your flight")
				.setAliases(Arrays.asList("flight"))
				.setPermission(PERMISSION.FLY_SELF.toString())
				.setUsage("/fly [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, incorrect_usage, sender_not_a_player, no_permission, fly_flight_toggle, fly_flight_toggle_target;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);

		fly_flight_toggle = Core.getMessages().getString("features.fly.flight-toggle", "&7Your flight mode has been &e{TOGGLE}&7.");
		fly_flight_toggle_target = Core.getMessages().getString("features.fly.flight-toggle-target", "&e{TARGET}&7's flight mode has been &e{TOGGLE}&7.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				RunicPlayer player = RunicPlayer.cast(sender);
				if(player.hasPermission(PERMISSION.FLY_SELF.toString())) {
					player.toggleFlight();
					if(player.getAllowFlight()) player.sendMessage(Placeholder.parse(fly_flight_toggle, player).set("{TOGGLE}", "enabled").getString());
					else player.sendMessage(Placeholder.parse(fly_flight_toggle, player).set("{TOGGLE}", "disabled").getString());
				} else player.sendMessage(Placeholder.parse(no_permission, player).getString());
			} else sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
		} else if(args.length == 1) {
			if(sender.hasPermission(PERMISSION.FLY_SELF.toString())) {
				if(sender instanceof Player && args[0].toLowerCase().matches("(?:enabled?|true|on|yes|1|activate|disabled?|false|off|no|0|deactivate|toggle)")) {
					RunicPlayer target = RunicPlayer.cast(sender);
					boolean toggle = args[0].toLowerCase().matches("(?:enabled?|true|on|yes|1|activate)") ? true : args[0].toLowerCase().matches("(?:disabled?|false|off|no|0|deactivate)") ? false : args[0].toLowerCase().matches("toggle") ? !target.getAllowFlight() : false;
					target.setAllowFlight(toggle);
					target.setFlying(toggle);
					if(target.getAllowFlight())	target.sendMessage(Placeholder.parse(fly_flight_toggle, target).set("{TOGGLE}", "enabled").getString());
					else target.sendMessage(Placeholder.parse(fly_flight_toggle, target).set("{TOGGLE}", "disabled").getString());
				} else {
					List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
					if(targets.size() > 0) {
						if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
							// sender is targeting self
							RunicPlayer target = RunicPlayer.cast(sender);
							target.toggleFlight();
							if(target.getAllowFlight())	target.sendMessage(Placeholder.parse(fly_flight_toggle, target).set("{TOGGLE}", "enabled").getString());
							else target.sendMessage(Placeholder.parse(fly_flight_toggle, target).set("{TOGGLE}", "disabled").getString());
						} else {
							// sender is targeting others
							if(sender.hasPermission(PERMISSION.FLY_OTHERS.toString())) {
								for(RunicPlayer target : targets) {
									target.toggleFlight();
									if(target.getAllowFlight()) {
										target.sendMessage(Placeholder.parse(fly_flight_toggle, target).set("{TOGGLE}", "enabled").getString());
										if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(fly_flight_toggle_target, sender).set("{TOGGLE}", "enabled").set("{TARGET}", target.getName()).getString());
									} else {
										target.sendMessage(Placeholder.parse(fly_flight_toggle, target).set("{TOGGLE}", "disabled").getString());
										if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(fly_flight_toggle_target, sender).set("{TOGGLE}", "disabled").set("{TARGET}", target.getName()).getString());
									}
								}
							} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
						}
					} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
				}
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else if(args.length == 2) {
			if(sender.hasPermission(PERMISSION.FLY_SELF.toString())) {
				List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
				if(targets.size() > 0) {
					if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
						// sender is targeting self
						RunicPlayer target = RunicPlayer.cast(sender);
						boolean toggle = args[1].toLowerCase().matches("(?:enabled?|true|on|yes|1|activate)") ? true : args[1].toLowerCase().matches("(?:disabled?|false|off|no|0|deactivate)") ? false : args[1].toLowerCase().matches("toggle") ? !target.getAllowFlight() : false;
						target.setAllowFlight(toggle);
						target.setFlying(toggle);
						if(target.getAllowFlight())	target.sendMessage(Placeholder.parse(fly_flight_toggle, target).set("{TOGGLE}", "enabled").getString());
						else target.sendMessage(Placeholder.parse(fly_flight_toggle, target).set("{TOGGLE}", "disabled").getString());
					} else {
						// sender is targeting others
						if(sender.hasPermission(PERMISSION.FLY_OTHERS.toString())) {
							for(RunicPlayer target : targets) {
								boolean toggle = args[1].toLowerCase().matches("(?:enabled?|true|on|yes|1|activate|allow)") ? true : args[1].toLowerCase().matches("(?:disabled?|false|off|no|0|deactivate|disallow)") ? false : args[1].toLowerCase().matches("toggle") ? !target.getAllowFlight() : false;
								boolean beforeToggle = target.getAllowFlight(); // will not send a message to target (unless sender is target) if nothing happened
								target.setAllowFlight(toggle);
								target.setFlying(toggle);
								if(target.getAllowFlight()) {
									if(beforeToggle != toggle || target.equals(sender)) target.sendMessage(Placeholder.parse(fly_flight_toggle, target).set("{TOGGLE}", "enabled").getString());
									if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(fly_flight_toggle_target, sender).set("{TOGGLE}", "enabled").set("{TARGET}", target.getName()).getString());
								} else {
									if(beforeToggle != toggle || target.equals(sender)) target.sendMessage(Placeholder.parse(fly_flight_toggle, target).set("{TOGGLE}", "disabled").getString());
									if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(fly_flight_toggle_target, sender).set("{TOGGLE}", "disabled").set("{TARGET}", target.getName()).getString());
								}
							}
						} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
					}
				} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else {
			if(sender.hasPermission(PERMISSION.FLY_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
			else if(sender.hasPermission(PERMISSION.FLY_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
			else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		}
		return true;
	}

	private List<String> toggles = Arrays.asList("disable", "enable", "true", "false", "on", "off", "yes", "no", "1", "0", "deactivate", "activate", "allow", "disallow", "toggle");
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
			if(args.length == 1) {
				if(sender.hasPermission(PERMISSION.FLY_SELF.toString())) {
					if(sender.hasPermission(PERMISSION.FLY_OTHERS.toString())) {
						for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
							suggestion.add(player);
						}
					}
					
					if(sender instanceof Player) {
						if(!suggestion.contains(sender.getName())) suggestion.add(sender.getName());
						for(String toggle : toggles) {
							if(toggle.startsWith(args[0].toLowerCase())) suggestion.add(toggle);
						}
					}
				}
			} else if(args.length == 2) {
				if(sender.hasPermission(PERMISSION.FLY_SELF.toString())) {
					for(String toggle : toggles) {
						if(toggle.startsWith(args[1].toLowerCase())) suggestion.add(toggle);
					}
					Collections.sort(suggestion);
				}
			}
		
		return suggestion;
	}

}
