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

public final class Burn extends RunicFeature implements CommandExecutor, TabCompleter {
	
	@Override
	public String getName() {
		return "Burn";
	}

	private enum PERMISSION {
		BURN("runic.burn"),
		EXTINGUISH_SELF("runic.extinguish"),
		EXTINGUISH_OTHERS("runic.extinguish.others");
		
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
		registerCommand(new RunicCommand("burn")
				.setDescription("Set a player on fire")
				.setUsage("/burn <player> <seconds>")
				.setExecutor(this)
				.setTabCompleter(this));
		registerCommand(new RunicCommand("extinguish")
				.setDescription("Extinguish a player on fire")
				.setUsage("/extinguish [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, incorrect_usage, no_permission, not_enough_arguments, burn_extinguished, sender_not_a_player, burn_numeric_time, burn_target_extinguished, burn_target;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		not_enough_arguments = Core.getMessages().getMessage(MESSAGE.NOT_ENOUGH_ARGUMENTS);

		burn_target = Core.getMessages().getString("features.burn.target", "&e{TARGET} &7was lit on fire for &e{TIME}&7.");
		burn_target_extinguished = Core.getMessages().getString("features.burn.target-extinguished", "&e{TARGET} &7was extinguished.");
		burn_numeric_time = Core.getMessages().getString("features.burn.numeric-time", "&7Time must be numeric.");
		burn_extinguished = Core.getMessages().getString("features.burn.extinguished", "&7You were extinguished.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("burn")) {
			if(args.length < 2) {
				if(sender.hasPermission(PERMISSION.BURN.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments).set("{COMMAND}", label.toLowerCase()+" <player> <seconds>").getString());
				else sender.sendMessage(Placeholder.parse(no_permission).getString());
			} else if(args.length == 2) {
				if(sender.hasPermission(PERMISSION.BURN.toString())) {
					List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
					if(targets.size() > 0) {
						if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
							// sender is targeting self
							RunicPlayer target = RunicPlayer.cast(sender);
							if(args[1].matches("^-?\\d+$")) {
								int sec = Integer.parseInt(args[1]);
								if(sec > 0) {
									target.burn(sec);
								} else target.extinguish();
							} else sender.sendMessage(Placeholder.parse(burn_numeric_time).getString());
						} else {
							// sender is targeting others
							if(args[1].matches("^-?\\d+$")) {
								int sec = Integer.parseInt(args[1]);
								for(RunicPlayer target : targets) {
									if(sec > 0) {
										target.burn(sec);
										if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(burn_target).set("{TARGET}", target.getName()).set("{TIME}", (sec > 1 ? sec+" seconds" : sec+" second")).getString());
									} else {
										target.extinguish();
										if(target.equals(sender)) target.sendMessage(Placeholder.parse(burn_extinguished).getString());
										else sender.sendMessage(Placeholder.parse(burn_target_extinguished).set("{TARGET}", target.getName()).getString());
									}
								}
							} else sender.sendMessage(Placeholder.parse(burn_numeric_time).getString());
						}
					} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
				} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			} else {
				if(sender.hasPermission(PERMISSION.BURN.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()+" <player> <seconds>").getString());
				else sender.sendMessage(Placeholder.parse(no_permission).getString());
			}
		} else if(command.getName().equalsIgnoreCase("extinguish")) {
			if(args.length == 0) {
				// extinguish self
				if(sender.hasPermission(PERMISSION.EXTINGUISH_SELF.toString())) {
					if(sender instanceof Player) {
						RunicPlayer player = RunicPlayer.cast(sender);
						player.extinguish();
						player.sendMessage(Placeholder.parse(burn_extinguished).getString());
					} else sender.sendMessage(Placeholder.parse(sender_not_a_player).getString());
				} else sender.sendMessage(Placeholder.parse(no_permission).getString());
			} else if(args.length == 1) {
				// extinguish other or self
				if(sender.hasPermission(PERMISSION.EXTINGUISH_SELF.toString())) {
					List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
					if(targets.size() > 0) {
						if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
							// sender is targeting self
							RunicPlayer target = RunicPlayer.cast(sender);
							target.extinguish();
						} else {
							// sender is targeting others
							for(RunicPlayer target : targets) {
								target.extinguish();
								if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(burn_target_extinguished, sender).set("{TARGET}", target.getName()).getString());
								else target.sendMessage(Placeholder.parse(burn_extinguished).getString());
							}
						}
					} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
				} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			} else {
				if(sender.hasPermission(PERMISSION.EXTINGUISH_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
				else if(sender.hasPermission(PERMISSION.EXTINGUISH_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()).getString());
				else sender.sendMessage(Placeholder.parse(no_permission).getString());
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(command.getName().equalsIgnoreCase("burn")) {
			if(sender.hasPermission(PERMISSION.BURN.toString())) {
				if(args.length == 1) {
					for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
						suggestion.add(player);
					}
				} else if(args.length == 2) {
					if(args[1].isEmpty()) {
						suggestion.add("1");
					}
				}
			}
		} else if(command.getName().equalsIgnoreCase("extinguish")) {
			if(sender.hasPermission(PERMISSION.EXTINGUISH_OTHERS.toString())) {
				if(args.length == 1) {
					for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
						suggestion.add(player);
					}
				}
			}
		}
		
		return suggestion;
	}

}
