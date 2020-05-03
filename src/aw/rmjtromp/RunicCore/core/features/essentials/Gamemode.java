package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

public final class Gamemode extends RunicFeature implements CommandExecutor, TabCompleter {

	private enum PERMISSION {
		GAMEMODE_SELF("runic.fly"),
		GAMEMODE_OTHERS("runic.fly.others");
		
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
		return "Gamemode";
	}
	
	@Override
	public void onEnable() {
		registerCommand(new RunicCommand("gamemode")
				.setDescription("Change player gamemode.")
				.setAliases(Arrays.asList("gm"))
				.setPermission(PERMISSION.GAMEMODE_SELF.toString())
				.setUsage("/gamemode <gamemode> [player]")
				.setExecutor(this)
				.setTabCompleter(this));
		registerCommand(new RunicCommand("adventure")
				.setDescription("Change player gamemode.")
				.setAliases(Arrays.asList("adventuremode", "gma", "gm2"))
				.setPermission(PERMISSION.GAMEMODE_SELF.toString())
				.setUsage("/adventure [player]")
				.setExecutor(this)
				.setTabCompleter(this));
		registerCommand(new RunicCommand("creative")
				.setDescription("Change player gamemode.")
				.setAliases(Arrays.asList("creativemode", "gmc", "gmt", "gm1"))
				.setPermission(PERMISSION.GAMEMODE_SELF.toString())
				.setUsage("/creative [player]")
				.setExecutor(this)
				.setTabCompleter(this));
		registerCommand(new RunicCommand("survival")
				.setDescription("Change player gamemode.")
				.setAliases(Arrays.asList("survivalmode", "gms", "gm0"))
				.setPermission(PERMISSION.GAMEMODE_SELF.toString())
				.setUsage("/survival [player]")
				.setExecutor(this)
				.setTabCompleter(this));
		registerCommand(new RunicCommand("spectator")
				.setDescription("Change player gamemode.")
				.setAliases(Arrays.asList("gmsp", "sp", "spec", "spectate", "gm3"))
				.setPermission(PERMISSION.GAMEMODE_SELF.toString())
				.setUsage("/spectator [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, not_enough_arguments, incorrect_usage, sender_not_a_player, gamemode_invalid_gamemode, no_permission, gamemode_gamemode_change, gamemode_gamemode_change_target;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		not_enough_arguments = Core.getMessages().getMessage(MESSAGE.NOT_ENOUGH_ARGUMENTS);

		gamemode_gamemode_change = Core.getMessages().getString("features.gamemode.gamemode-change", "&7Your gamemode was set to &e{GAMEMODE}&7.");
		gamemode_gamemode_change_target = Core.getMessages().getString("features.gamemode.gamemode-change-target", "&e{TARGET}&7's gamemode was set to &e{GAMEMODE}&7.");
		gamemode_invalid_gamemode = Core.getMessages().getString("features.gamemode.invalid-gamemode", "&7Invalid gamemode '&e{GAMEMODE}&7'.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("survival") || command.getName().equalsIgnoreCase("creative") || command.getName().equalsIgnoreCase("adventure") || command.getName().equalsIgnoreCase("spectator")) {
			GameMode gamemode = GameMode.SURVIVAL;
			switch (command.getName().toLowerCase()) {
			case "survival":
				gamemode = GameMode.SURVIVAL;
				break;
			case "creative":
				gamemode = GameMode.CREATIVE;
				break;
			case "adventure":
				gamemode = GameMode.ADVENTURE;
				break;
			case "spectator":
				gamemode = GameMode.SPECTATOR;
				break;
			default:
				break;
			}
			
			if(args.length == 0) {
				if(sender instanceof Player) {
					if(sender.hasPermission(PERMISSION.GAMEMODE_SELF.toString())) {
						RunicPlayer player = RunicPlayer.cast(sender);
						player.setGameMode(gamemode);
						player.sendMessage(Placeholder.parse(gamemode_gamemode_change, player).set("{GAMEMODE}", gamemode.toString().toLowerCase()).getString());
					} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
				} else sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
			} else if(args.length == 1) {
				if(sender.hasPermission(PERMISSION.GAMEMODE_SELF.toString())) {
					List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
					if(targets.size() > 0) {
						if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
							// sender is targeting self
							RunicPlayer target = RunicPlayer.cast(sender);
							target.setGameMode(gamemode);
							target.sendMessage(Placeholder.parse(gamemode_gamemode_change, target).set("{GAMEMODE}", gamemode.toString().toLowerCase()).getString());
						} else {
							// sender is targeting others
							if(sender.hasPermission(PERMISSION.GAMEMODE_OTHERS.toString())) {
								for(RunicPlayer target : targets) {
									target.setGameMode(gamemode);
									target.sendMessage(Placeholder.parse(gamemode_gamemode_change, target).set("{GAMEMODE}", gamemode.toString().toLowerCase()).getString());
									if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(gamemode_gamemode_change_target, sender).set("{TARGET}", target.getName()).set("{GAMEMODE}", gamemode.toString().toLowerCase()).getString());
								}
							} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
						}
					} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
				} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			} else {
				if(sender.hasPermission(PERMISSION.GAMEMODE_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
				else if(sender.hasPermission(PERMISSION.GAMEMODE_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
				else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			}
		} else if(command.getName().equalsIgnoreCase("gamemode")) {
			if(args.length == 0) {
				if(sender.hasPermission(PERMISSION.GAMEMODE_OTHERS.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments, sender).set("{COMMAND}", label.toLowerCase()+" <gamemode> [player]").getString());
				else if(sender.hasPermission(PERMISSION.GAMEMODE_SELF.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments, sender).set("{COMMAND}", label.toLowerCase()+" <gamemode>").getString());
				else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			} else if(args.length == 1 || (args.length == 2 && sender instanceof Player && args[1].equalsIgnoreCase(sender.getName()))) {
				if(sender instanceof Player) {
					if(sender.hasPermission(PERMISSION.GAMEMODE_SELF.toString())) {
						GameMode gamemode = null;
						if(args[0].toLowerCase().matches("survival|s|0|survive|su|surv")) gamemode = GameMode.SURVIVAL;
						else if(args[0].toLowerCase().matches("creative|c|1|create")) gamemode = GameMode.CREATIVE;
						else if(args[0].toLowerCase().matches("adventure|a|2")) gamemode = GameMode.ADVENTURE;
						else if(args[0].toLowerCase().matches("spectator|sp|3|spectate|spec")) gamemode = GameMode.SPECTATOR;
						
						if(gamemode != null) {
							RunicPlayer player = RunicPlayer.cast(sender);
							player.setGameMode(gamemode);
							player.sendMessage(Placeholder.parse(gamemode_gamemode_change, player).set("{GAMEMODE}", gamemode.toString().toLowerCase()).getString());
						} else sender.sendMessage(Placeholder.parse(gamemode_invalid_gamemode, sender).set("{GAMEMODE}", args[0]).getString());
					}
				} else {
					if(sender.hasPermission(PERMISSION.GAMEMODE_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
					else if(sender.hasPermission(PERMISSION.GAMEMODE_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
					else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
				}
			} else if(args.length == 2) {
				if(sender.hasPermission(PERMISSION.GAMEMODE_SELF.toString())) {
					GameMode gamemode = null;
					if(args[0].toLowerCase().matches("survival|s|0|survive|su|surv")) gamemode = GameMode.SURVIVAL;
					else if(args[0].toLowerCase().matches("creative|c|1|create")) gamemode = GameMode.CREATIVE;
					else if(args[0].toLowerCase().matches("adventure|a|2")) gamemode = GameMode.ADVENTURE;
					else if(args[0].toLowerCase().matches("spectator|sp|3|spectate|spec")) gamemode = GameMode.SPECTATOR;
					
					if(gamemode != null) {
						List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
						if(targets.size() > 0) {
							if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
								// sender is targeting self
								RunicPlayer target = RunicPlayer.cast(sender);
								target.setGameMode(gamemode);
								target.sendMessage(Placeholder.parse(gamemode_gamemode_change, target).set("{GAMEMODE}", gamemode.toString().toLowerCase()).getString());
							} else {
								// sender is targeting others
								if(sender.hasPermission(PERMISSION.GAMEMODE_OTHERS.toString())) {
									for(RunicPlayer target : targets) {
										target.setGameMode(gamemode);
										target.sendMessage(Placeholder.parse(gamemode_gamemode_change, target).set("{GAMEMODE}", gamemode.toString().toLowerCase()).getString());
										if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(gamemode_gamemode_change_target, sender).set("{TARGET}", target.getName()).set("{GAMEMODE}", gamemode.toString().toLowerCase()).getString());
									}
								} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
							}
						} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
					} else sender.sendMessage(Placeholder.parse(gamemode_invalid_gamemode, sender).set("{GAMEMODE}", args[0]).getString());
				} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			} else {
				if(sender.hasPermission(PERMISSION.GAMEMODE_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
				else if(sender.hasPermission(PERMISSION.GAMEMODE_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
				else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(command.getName().equalsIgnoreCase("gamemode")) {
			if(args.length == 1 && (sender.hasPermission(PERMISSION.GAMEMODE_OTHERS.toString()) || sender.hasPermission(PERMISSION.GAMEMODE_SELF.toString()))) {
				if(args[0].isEmpty()) {
					suggestion.addAll(Arrays.asList("survival", "creative", "adventure", "spectator"));
				} else {
					List<String> gamemodes = Arrays.asList("survival", "creative", "adventure", "spectator");
					for(String gamemode : gamemodes) {
						if(gamemode.startsWith(args[0].toLowerCase())) suggestion.add(gamemode);
					}
				}
			} else if(args.length == 2 && sender.hasPermission(PERMISSION.GAMEMODE_OTHERS.toString())) {
				if(args[1].isEmpty()) {
					if(sender instanceof Player) {
						for(Player p : Bukkit.getOnlinePlayers()) if(((Player) sender).canSee(p)) suggestion.add(p.getName());
					} else {
						for(Player p : Bukkit.getOnlinePlayers()) suggestion.add(p.getName());
					}
				} else {
					if(sender instanceof Player) {
						for(Player p : Bukkit.getOnlinePlayers()) if(((Player) sender).canSee(p) && p.getName().toLowerCase().startsWith(args[1].toLowerCase())) suggestion.add(p.getName());
					} else {
						for(Player p : Bukkit.getOnlinePlayers()) if(p.getName().toLowerCase().startsWith(args[1].toLowerCase())) suggestion.add(p.getName());
					}
				}
			}
		} else {
			if(args.length == 1) {
				if(sender.hasPermission(PERMISSION.GAMEMODE_OTHERS.toString())) {
					if(args[0].isEmpty()) {
						if(sender instanceof Player) {
							for(Player p : Bukkit.getOnlinePlayers()) if(((Player) sender).canSee(p)) suggestion.add(p.getName());
						} else {
							for(Player p : Bukkit.getOnlinePlayers()) suggestion.add(p.getName());
						}
					} else {
						if(sender instanceof Player) {
							for(Player p : Bukkit.getOnlinePlayers()) if(((Player) sender).canSee(p) && p.getName().toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(p.getName());
						} else {
							for(Player p : Bukkit.getOnlinePlayers()) if(p.getName().toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(p.getName());
						}
					}
				}
			}
		}

		
		Collections.sort(suggestion);
		return suggestion;
	}

}
