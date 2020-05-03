package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.events.PrePlayerHomeCreateEvent;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer.PlayerHome;
import aw.rmjtromp.RunicCore.utilities.PlayerSelector;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Home extends RunicFeature implements CommandExecutor, TabCompleter {

	@Override
	public String getName() {
		return "Home";
	}

	private enum PERMISSION {
		HOME_SELF("runic.home"),
		HOME_OTHERS("runic.home.others"),
		HOME_MODIFY("runic.home.modify");
		// runic.home.<count> for home amount (default 0)
		
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
		registerCommand(new RunicCommand("home")
				.setDescription("Teleport to your home")
				.setAliases(Arrays.asList("homes"))
				.setPermission(PERMISSION.HOME_SELF.toString())
				.setUsage("/home [[player] home]")
				.setExecutor(this)
				.setTabCompleter(this));
		registerCommand(new RunicCommand("sethome")
				.setDescription("Set a new home at your current location")
				.setAliases(Arrays.asList("createhome"))
				.setPermission(PERMISSION.HOME_SELF.toString())
				.setUsage("/sethome [[player] home]")
				.setExecutor(this)
				.setTabCompleter(this));
		registerCommand(new RunicCommand("delhome")
				.setDescription("Deletes an existing home")
				.setAliases(Arrays.asList("deletehome", "remhome", "removehome", "rmhome"))
				.setPermission(PERMISSION.HOME_SELF.toString())
				.setUsage("/delhome [[player] home]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private int teleport_delay = 5;
	private String target_not_found, incorrect_usage, sender_not_a_player, no_permission, home_no_home, home_homes, teleport_eta, teleport_cancelled_combat,
	teleport_already_teleporting, teleport_cancelled_move, teleport_cancelled, home_teleported,
	home_invalid_location, home_doesnt_exist, target_selection_too_big, not_enough_arguments, home_home_created, home_home_deleted,
	home_creation_error, home_deletion_error;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		target_selection_too_big = Core.getMessages().getMessage(MESSAGE.TARGET_SELECTION_TOO_BIG);
		not_enough_arguments = Core.getMessages().getMessage(MESSAGE.NOT_ENOUGH_ARGUMENTS);
		
		home_no_home = Core.getMessages().getString("features.home.no-home", "&cYou don't have any homes. Try setting one with /sethome <name>.");
		home_homes = Core.getMessages().getString("features.home.homes", "&eHomes: &7{HOMES}");
		home_invalid_location = Core.getMessages().getString("features.home.invalid-location", "&cInvalid home location.");
		home_doesnt_exist = Core.getMessages().getString("features.home.doesnt-exist", "&cHome '&7{HOME}&c' doesn't exist.");
		home_home_created = Core.getMessages().getString("features.home.home-created", "&eNew home created at your current location.");
		home_home_deleted = Core.getMessages().getString("features.home.home-deleted", "&cYou deleted your home: &7{HOME}&c.");
		home_creation_error = Core.getMessages().getString("features.home.creation-error", "&cThere was an error creating your home: &7{REASON}&c.");
		home_deletion_error = Core.getMessages().getString("features.home.deletion-error", "&cThere was an error deleting your home: &7{REASON}&c.");

		home_teleported = Core.getMessages().getString("features.home.teleported", "&7You were teleported to your home: &e{HOME}.");
		
		teleport_delay = Core.getConfig().getInt("teleport.delay", 5);
		teleport_eta = Core.getMessages().getString("teleport.eta", "&7Teleporting in &e{TIME}&7...");
		teleport_cancelled_combat = Core.getMessages().getString("teleport.cancelled.combat", "&7You were combat tagged, teleportation cancelled.");
		teleport_already_teleporting = Core.getMessages().getString("teleport.already-teleporting", "&7You're already teleporting elsewhere.");
		teleport_cancelled_move = Core.getMessages().getString("teleport.cancelled.move", "&7You have moved, teleportation cancelled.");
		teleport_cancelled = Core.getMessages().getString("teleport.cancelled.undefined", "&7Teleportation cancelled.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("home")) {
			if(sender.hasPermission(PERMISSION.HOME_SELF.toString())) {
				if(sender instanceof Player) {
					if(args.length == 0) {
						RunicPlayer target = RunicPlayer.cast(sender);
						if(!target.isTeleporting()) {
							if(target.getHomes().size() == 0) {
								target.sendMessage(Placeholder.parse(home_no_home, target).getString());
							} else if(target.getHomes().size() == 1) {
								PlayerHome home = target.getHomes().get(0);
								if(home != null) {
									/*new DelayedTeleport(target, home, (target.isOp() || target.isInsideSafeRegion()) ? 0 : teleport_delay) {
										@Override
										public void onTeleport() {
											getPlayer().sendMessage(Placeholder.parse(home_teleported, getPlayer()).set("{HOME}", home.getName()).getString());
										}
										
										@Override
										public void onCancelled() {
											if(getCancelCause() == TeleportCancelCause.COMBAT_TAGGED) {
												getPlayer().sendMessage(Placeholder.parse(teleport_cancelled_combat, getPlayer()).getString());
											} else if(getCancelCause() == TeleportCancelCause.PLAYER_MOVE) {
												getPlayer().sendMessage(Placeholder.parse(teleport_cancelled_move, getPlayer()).getString());
											} else {
												if(getPlayer().isOnline()) {
													getPlayer().sendMessage(Placeholder.parse(teleport_cancelled, getPlayer()).getString());
												}
											}
										}
										
										@Override
										public void onInterval() {
											getPlayer().sendMessage(Placeholder.parse(teleport_eta, getPlayer()).set("{TIME}", getTimeLeft() > 1 ? getTimeLeft() + " seconds" : getTimeLeft() + " second").getString());
										}
									};*/
								} else target.sendMessage(Placeholder.parse(home_invalid_location, target).getString());
							} else  {
								List<String> homes = new ArrayList<String>();
								target.getHomes().forEach((home) -> homes.add(home.getName()));
								target.sendMessage(Placeholder.parse(home_homes, target).set("{HOMES}", Strings.join(homes, ", ")).getString());
							}
						} else target.sendMessage(Placeholder.parse(teleport_already_teleporting, target).getString());
					} else if(args.length == 1) {
						RunicPlayer target = RunicPlayer.cast(sender);
						if(!target.isTeleporting()) {
							if(target.hasHome(args[0])) {
								PlayerHome home = target.getHome(args[0]);
								if(home != null) {
									/*new DelayedTeleport(target, home, (target.isOp() || target.isInsideSafeRegion()) ? 0 : teleport_delay) {
										@Override
										public void onTeleport() {
											getPlayer().sendMessage(Placeholder.parse(home_teleported, getPlayer()).set("{HOME}", home.getName()).getString());
										}
										
										@Override
										public void onCancelled() {
											if(getCancelCause() == TeleportCancelCause.COMBAT_TAGGED) {
												getPlayer().sendMessage(Placeholder.parse(teleport_cancelled_combat, getPlayer()).getString());
											} else if(getCancelCause() == TeleportCancelCause.PLAYER_MOVE) {
												getPlayer().sendMessage(Placeholder.parse(teleport_cancelled_move, getPlayer()).getString());
											} else {
												if(getPlayer().isOnline()) {
													getPlayer().sendMessage(Placeholder.parse(teleport_cancelled, getPlayer()).getString());
												}
											}
										}
										
										@Override
										public void onInterval() {
											getPlayer().sendMessage(Placeholder.parse(teleport_eta, getPlayer()).set("{TIME}", getTimeLeft() > 1 ? getTimeLeft() + " seconds" : getTimeLeft() + " second").getString());
										}
									};*/
								} else target.sendMessage(Placeholder.parse(home_invalid_location, target).getString());
							} else target.sendMessage(Placeholder.parse(home_doesnt_exist, target).set("{HOME}", args[0]).getString());
						} else target.sendMessage(Placeholder.parse(teleport_already_teleporting, target).getString());
					} else if(args.length == 2) {
						// /home <player> <home> (sender wants to teleport to a player's home)
						RunicPlayer player = RunicPlayer.cast(sender);
						if(!player.isTeleporting()) {
							List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
							if(targets.size() > 0) {
								if(targets.size() == 1) {
									if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
										// sender is targeting self
										if(player.hasHome(args[1])) {
											PlayerHome home = player.getHome(args[1]);
											if(home != null) {
												/*new DelayedTeleport(player, home, (player.isOp() || player.isInsideSafeRegion()) ? 0 : teleport_delay) {
													@Override
													public void onTeleport() {
														getPlayer().sendMessage(Placeholder.parse(home_teleported, getPlayer()).set("{HOME}", home.getName()).getString());
													}
													
													@Override
													public void onCancelled() {
														if(getCancelCause() == TeleportCancelCause.COMBAT_TAGGED) {
															getPlayer().sendMessage(Placeholder.parse(teleport_cancelled_combat, getPlayer()).getString());
														} else if(getCancelCause() == TeleportCancelCause.PLAYER_MOVE) {
															getPlayer().sendMessage(Placeholder.parse(teleport_cancelled_move, getPlayer()).getString());
														} else {
															if(getPlayer().isOnline()) {
																getPlayer().sendMessage(Placeholder.parse(teleport_cancelled, getPlayer()).getString());
															}
														}
													}
													
													@Override
													public void onInterval() {
														getPlayer().sendMessage(Placeholder.parse(teleport_eta, getPlayer()).set("{TIME}", getTimeLeft() > 1 ? getTimeLeft() + " seconds" : getTimeLeft() + " second").getString());
													}
												};*/
											} else player.sendMessage(Placeholder.parse(home_invalid_location, player).getString());
										} else player.sendMessage(Placeholder.parse(home_doesnt_exist, player).set("{HOME}", args[1]).getString());
									} else {
										// sender is targeting others
										if(sender.hasPermission(PERMISSION.HOME_OTHERS.toString())) {
											RunicPlayer target = targets.get(0);
											if(target.hasHome(args[1])) {
												PlayerHome home = target.getHome(args[1]);
												if(home != null) {
													/*new DelayedTeleport(player, home, (target.isOp() || target.isInsideSafeRegion()) ? 0 : teleport_delay) {
														@Override
														public void onTeleport() {
															getPlayer().sendMessage(Placeholder.parse(home_teleported, getPlayer()).set("{HOME}", home.getName()).getString());
														}
														
														@Override
														public void onCancelled() {
															if(getCancelCause() == TeleportCancelCause.COMBAT_TAGGED) {
																getPlayer().sendMessage(Placeholder.parse(teleport_cancelled_combat, getPlayer()).getString());
															} else if(getCancelCause() == TeleportCancelCause.PLAYER_MOVE) {
																getPlayer().sendMessage(Placeholder.parse(teleport_cancelled_move, getPlayer()).getString());
															} else {
																if(getPlayer().isOnline()) {
																	getPlayer().sendMessage(Placeholder.parse(teleport_cancelled, getPlayer()).getString());
																}
															}
														}
														
														@Override
														public void onInterval() {
															getPlayer().sendMessage(Placeholder.parse(teleport_eta, getPlayer()).set("{TIME}", getTimeLeft() > 1 ? getTimeLeft() + " seconds" : getTimeLeft() + " second").getString());
														}
													};*/
												} else player.sendMessage(Placeholder.parse(home_invalid_location, player).getString());
											} else player.sendMessage(Placeholder.parse(home_doesnt_exist, player).set("{HOME}", args[1]).getString());
										} else player.sendMessage(Placeholder.parse(no_permission, player).getString());
									}
								} else player.sendMessage(Placeholder.parse(target_selection_too_big, player).getString());
							} else player.sendMessage(Placeholder.parse(target_not_found, player).set("{TARGET}", args[0]).getString());
						} else player.sendMessage(Placeholder.parse(teleport_already_teleporting, player).getString());
					} else {
						if(sender.hasPermission(PERMISSION.HOME_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [[player] home]").getString());
						else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [home]").getString());
					}
				} else sender.sendMessage(Placeholder.parse(sender_not_a_player).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else if(command.getName().equalsIgnoreCase("sethome")) {
			if(sender.hasPermission(PERMISSION.HOME_SELF.toString()) && sender.hasPermission(PERMISSION.HOME_MODIFY.toString())) {
				if(sender instanceof Player) {
					RunicPlayer player = RunicPlayer.cast(sender);
					if(args.length == 0) {
						player.sendMessage(Placeholder.parse(not_enough_arguments, player).set("{COMMAND}", label.toLowerCase()+" <name>").getString());
					} else if(args.length == 1) {
						if(!player.hasHome(args[0])) {
							PrePlayerHomeCreateEvent PPHCE = new PrePlayerHomeCreateEvent(player);
							Bukkit.getPluginManager().callEvent(PPHCE);
							if(!PPHCE.isCancelled()) {
								try {
									player.createHome(args[0]);
									player.sendMessage(Placeholder.parse(home_home_created, player).getString());
								} catch (Exception e) {
									player.sendMessage(Placeholder.parse(home_creation_error, player).set("{REASON}", e.getMessage()).getString());
								}
							} else player.sendMessage(Placeholder.parse(home_creation_error, player).set("{REASON}", (PPHCE.getReason() != null && !PPHCE.getReason().isEmpty()) ? PPHCE.getReason() : "No reason provided").getString());
						} else player.sendMessage(Placeholder.parse(home_creation_error, player).set("{REASON}", "Home with that name already exists").getString());
					} else player.sendMessage(Placeholder.parse(incorrect_usage, player).set("{COMMAND}", label.toLowerCase()+" <name>").getString()); 
				} else sender.sendMessage(Placeholder.parse(sender_not_a_player).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else if(command.getName().equalsIgnoreCase("delhome")) {
			if(sender.hasPermission(PERMISSION.HOME_SELF.toString()) && sender.hasPermission(PERMISSION.HOME_MODIFY.toString())) {
				if(sender instanceof Player) {
					RunicPlayer player = RunicPlayer.cast(sender);
					if(args.length == 0) {
						player.sendMessage(Placeholder.parse(not_enough_arguments, player).set("{COMMAND}", label.toLowerCase()+" <name>").getString());
					} else if(args.length == 1) {
						if(player.hasHome(args[0])) {
							try {
								PlayerHome home = player.getHome(args[0]);
								home.delete();
								player.sendMessage(Placeholder.parse(home_home_deleted, player).set("{HOME}", home.getName()).getString());
							} catch (Exception e) {
								player.sendMessage(Placeholder.parse(home_creation_error, player).set("{REASON}", e.getMessage()).getString());
							}
						} else player.sendMessage(Placeholder.parse(home_deletion_error, player).set("{REASON}", "Home with that name doesn't exist").getString());
					} else player.sendMessage(Placeholder.parse(incorrect_usage, player).set("{COMMAND}", label.toLowerCase()+" <name>").getString()); 
				} else sender.sendMessage(Placeholder.parse(sender_not_a_player).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<>();
		
		if(command.getName().equalsIgnoreCase("home") || command.getName().equalsIgnoreCase("delhome")) {
			if(sender instanceof Player) {
				RunicPlayer player = RunicPlayer.cast(sender);
				List<String> homes = new ArrayList<>();
				player.getHomes().forEach((home) -> homes.add(home.getName()));
				
				if(!args[0].isEmpty()) {
					for(String home : homes) {
						if(home.toLowerCase().startsWith(args[0].toLowerCase()) || home.toLowerCase().contains(args[0].toLowerCase())) suggestion.add(home);
					}
					Collections.sort(suggestion);
					
					if(command.getName().equalsIgnoreCase("home") && suggestion.isEmpty() && sender.hasPermission(PERMISSION.HOME_OTHERS.toString())) {
						for(String p : PlayerSelector.suggest(args[0], sender.isOp())) {
							suggestion.add(p);
						}
					}
				} else {
					for(String home : homes) suggestion.add(home);
					Collections.sort(suggestion);
				}
			}
		}
		
		return suggestion;
	}

}
