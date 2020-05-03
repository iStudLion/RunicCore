package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.events.PlayerSpawnEvent;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.PlayerSelector;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.RunicUtils;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.essential.DelayedTeleport;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Spawn extends RunicFeature implements CommandExecutor, TabCompleter, Listener {
	
	private static Location spawn;
	private int teleport_delay = 5;
	private boolean center_spawn = true;
	private boolean spawn_on_join = false;
	
	private enum PERMISSION {
		SPAWN_SELF("runic.spawn"),
		SPAWN_OTHERS("runic.spawn.others"),
		SPAWN_SET("runic.spawn.modify");
		
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
		return "Spawn";
	}
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		registerCommand(new RunicCommand("spawn")
				.setDescription("Teleport to the spawnpoint")
				.setPermission(PERMISSION.SPAWN_SELF.toString())
				.setUsage("/spawn [player]")
				.setExecutor(this)
				.setTabCompleter(this));
		
		registerCommand(new RunicCommand("setspawn")
				.setDescription("Set the spawnpoint to your current position")
				.setPermission(PERMISSION.SPAWN_SET.toString())
				.setUsage("/setspawn")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String spawn_set, target_teleported, target_teleporting, target_not_found, teleport_target_cancelled, incorrect_usage, sender_not_a_player, no_permission, eta_message, teleport_cancelled_combat, teleport_already_teleporting, teleport_cancelled_move, teleport_cancelled, teleport;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);

		center_spawn = Core.getConfig().getBoolean("features.spawn.center", true);
		spawn_on_join = Core.getConfig().getBoolean("features.spawn.spawn-on-join", false);
		teleport_delay = Core.getConfig().getInt("teleport.delay", 5);
		spawn = loadSpawnLocation();

		spawn_set = Core.getMessages().getString("spawn.spawn-set", "&7Spawn location successfully set.");
		teleport = Core.getMessages().getString("spawn.teleport", "&7You were teleported to &espawn &7safely.");
		target_teleported = Core.getMessages().getString("spawn.target-teleported", "&e{TARGET} &7was teleported to &espawn &7safely.");
		target_teleporting = Core.getMessages().getString("spawn.target-teleporting", "&7Teleporting &e{TARGET} &7to spawn...");
		eta_message = Core.getMessages().getString("teleport.eta", "&7Teleporting in &e{TIME}&7...");
		teleport_cancelled_combat = Core.getMessages().getString("teleport.cancelled.combat", "&7You were combat tagged, teleportation cancelled.");
		teleport_already_teleporting = Core.getMessages().getString("teleport.already-teleporting", "&7You're already teleporting elsewhere.");
		teleport_cancelled_move = Core.getMessages().getString("teleport.cancelled.move", "&7You have moved, teleportation cancelled.");
		teleport_cancelled = Core.getMessages().getString("teleport.cancelled.undefined", "&7Teleportation cancelled.");
		teleport_target_cancelled = Core.getMessages().getString("teleport.target-teleport-cancelled", "&e{TARGET}&7's teleportation got cancelled.");
	}
	
	private Location loadSpawnLocation() {
		Location loc = Core.getConfig().contains("features.spawn.location") ? RunicUtils.str2loc(Core.getConfig().getString("features.spawn.location"), center_spawn) : null;
		if(loc == null) {
			error("Spawn location is either invalid or not set.");
			if(Bukkit.getServer().getWorld("world") != null) loc = Bukkit.getServer().getWorld("world").getSpawnLocation();
			else loc = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
		}
		return loc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("spawn")) {
			if(args.length == 0) {
				if(sender instanceof Player) {
					if(sender.hasPermission(PERMISSION.SPAWN_SELF.toString())) {
						RunicPlayer player = RunicPlayer.cast(sender);
						if(!player.isTeleporting()) {
							int delay = player.isInsideSafeRegion() || sender.isOp() ? 0 : this.teleport_delay; // teleports player instantly if they're in safe region
							new DelayedTeleport(player, spawn, delay) {
								@Override
								public void onTeleport() {
									Bukkit.getPluginManager().callEvent(new PlayerSpawnEvent(player));
									getPlayer().sendMessage(Placeholder.parse(teleport, getPlayer()).getString());
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
									getPlayer().sendMessage(Placeholder.parse(eta_message, getPlayer()).set("{TIME}", getTimeLeft() > 1 ? getTimeLeft() + " seconds" : getTimeLeft() + " second").getString());
								}
							};
						} else player.sendMessage(Placeholder.parse(teleport_already_teleporting, player).getString());
					} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
				} else sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
			} else if(args.length == 1) {
				if(sender.hasPermission(PERMISSION.SPAWN_SELF.toString())) {
					List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
					if(targets.size() > 0) {
						if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
							// sender is targeting self
							RunicPlayer target = RunicPlayer.cast(sender);
							if(target.isTeleporting()) target.cancelTeleporting();
							int delay = target.isInsideSafeRegion() || sender.isOp() ? 0 : this.teleport_delay; // teleports player instantly if they're in safe region
							new DelayedTeleport(target, spawn, delay) {
								@Override
								public void onTeleport() {
									Bukkit.getPluginManager().callEvent(new PlayerSpawnEvent(target));
									getPlayer().sendMessage(Placeholder.parse(teleport, getPlayer()).getString());
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
									getPlayer().sendMessage(Placeholder.parse(eta_message, getPlayer()).set("{TIME}", getTimeLeft() > 1 ? getTimeLeft() + " seconds" : getTimeLeft() + " second").getString());
								}
							};
						} else {
							// sender is targeting others
							if(sender.hasPermission(PERMISSION.SPAWN_OTHERS.toString())) {
								for(RunicPlayer target : targets) {
									if(target.isTeleporting()) target.cancelTeleporting();
									int delay = target.isInsideSafeRegion() || sender.isOp() ? 0 : this.teleport_delay; // teleports player instantly if they're in safe region
									if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(target_teleporting, sender).set("{TARGET}", target.getName()).getString());
									new DelayedTeleport(target, spawn, delay) {
										@Override
										public void onTeleport() {
											Bukkit.getPluginManager().callEvent(new PlayerSpawnEvent(target));
											getPlayer().sendMessage(Placeholder.parse(teleport, getPlayer()).getString());
											if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(target_teleported, sender).set("{TARGET}", getPlayer().getName()).getString());
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
											if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(teleport_target_cancelled, sender).set("{TARGET}", getPlayer().getName()).getString());
										}
										
										@Override
										public void onInterval() {
											getPlayer().sendMessage(Placeholder.parse(eta_message, getPlayer()).set("{TIME}", getTimeLeft() > 1 ? getTimeLeft() + " seconds" : getTimeLeft() + " second").getString());
										}
									};
								}
							} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
						}
					} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
				} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			} else {
				if(sender.hasPermission(PERMISSION.SPAWN_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
				else if(sender.hasPermission(PERMISSION.SPAWN_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
				else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			}
		} else if(command.getName().equalsIgnoreCase("setspawn")) {
			if(sender instanceof Player) {
				if(sender.hasPermission(PERMISSION.SPAWN_SET.toString())) {
					if(args.length == 0) {
						RunicPlayer player = RunicPlayer.cast(sender);
						Location location = center_spawn ? RunicUtils.roundLocation(player.getLocation()) : player.getLocation();
						Core.getConfig().set("features.spawn.location", RunicUtils.loc2str(location, center_spawn));
						spawn = location;
						player.sendMessage(Placeholder.parse(spawn_set, player).getString());
					} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
				} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			} else sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(command.getName().equalsIgnoreCase("spawn")) {
			if(args.length == 1) {
				if(sender.hasPermission(PERMISSION.SPAWN_OTHERS.toString())) {
					for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
						suggestion.add(player);
					}
				}
			}
		}
		
		Collections.sort(suggestion);
		return suggestion;
	}
	
	@Deprecated
	public Location getSpawnLocation() {
		return spawn;
	}
	
//	@Override
	public Location getLocation() {
		return spawn;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(!e.getPlayer().hasPlayedBefore() || spawn_on_join) {
			RunicPlayer player = RunicPlayer.cast(e.getPlayer());
			Bukkit.getPluginManager().callEvent(new PlayerSpawnEvent(player));
			player.teleport(spawn);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		RunicPlayer player = RunicPlayer.cast(e.getPlayer());
		Bukkit.getPluginManager().callEvent(new PlayerSpawnEvent(player));
		player.teleport(spawn);
	}

}
