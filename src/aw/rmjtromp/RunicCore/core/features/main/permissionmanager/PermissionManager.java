package aw.rmjtromp.RunicCore.core.features.main.permissionmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.Config;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class PermissionManager extends RunicFeature implements CommandExecutor, TabCompleter {

	private Config permissions;
	
	public PermissionManager() {
//		super(false); // disabled
	}
	
	@Override
	public String getName() {
		return "PermissionManager";
	}
	
	private enum PERMISSION {
		PERMISSIONSMANAGER_MANAGE("runic.permissionmanager.manage");
		
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
		registerCommand(new RunicCommand("permissionmanager")
				.setDescription("RunicCore permission manager")
				.setAliases(Arrays.asList("perm", "pm", "pex"))
				.setPermission(PERMISSION.PERMISSIONSMANAGER_MANAGE.toString())
				.setUsage("/permissionmanager")
				.setExecutor(this)
				.setTabCompleter(this));
		
		permissions = Config.init("permissions").loadFromResource("permissions");
		
		PermissionGroup.registerEvent();
		PermissionUser.registerEvents();
		
		Set<String> groups = permissions.getKeys("groups");
		if(groups.size() > 0) {
			for(String group : groups) PermissionGroup.getGroup(group);
		} else {
			PermissionGroup group = PermissionGroup.getGroup("default");
			group.setDefault(true);
			group.addPermission("test.permission");
		}
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			PermissionUser user = PermissionUser.getUser(player.getUniqueId());
			user.recalculatePermissions();
		}
		
		System.out.print("[RunicCore] "+PermissionGroup.getGroups().size()+" permission "+(PermissionGroup.getGroups().size() > 1 ? "groups" : "group")+" was registered");
	}
	
	private String target_not_found, incorrect_usage, pm_invalid_priority, pm_group_priority_change, pm_group_doesnt_exist, no_permission, pm_group_create_delete, pm_player_group_set, pm_player_group_add, pm_player_group_remove, pm_permission_add, pm_permission_remove, pm_group_affix_change, not_enough_arguments, pm_player_affix_change, pm_invalid_affix, pm_invalid_permission;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		not_enough_arguments = Core.getMessages().getMessage(MESSAGE.NOT_ENOUGH_ARGUMENTS);
		
		pm_player_affix_change = Core.getMessages().getString("features.permission-manager.player-affix-change", "&e{TARGET}&7's {AFFIX} was changed to \"&r{VALUE}&7\".");
		pm_group_affix_change = Core.getMessages().getString("features.permission-manager.group-affix-change", "&e{GROUP}&7 group's {AFFIX} was changed to \"&r{VALUE}&7\".");
		pm_invalid_affix = Core.getMessages().getString("features.permission-manager.invalid-affix", "&7{AFFIX} provided contains dissallowed characters.");
		pm_invalid_permission = Core.getMessages().getString("features.permission-manager.invalid-permission", "&7Permission provided contains invalid characters.");
		pm_permission_add = Core.getMessages().getString("features.permission-manager.permission-add", "&7Permission \"{PERMISSION}\" added to &e{OBJECT}&7.");
		pm_permission_remove = Core.getMessages().getString("features.permission-manager.permission-remove", "&7Permission \"{PERMISSION}\" removed from &e{OBJECT}&7.");
		pm_player_group_add = Core.getMessages().getString("features.permission-manager.added-player-to-group", "&e{TARGET} &7was added to &e{GROUP}&7.");
		pm_player_group_remove = Core.getMessages().getString("features.permission-manager.removed-player-from-group", "&e{TARGET} &7was removed from &e{GROUP}&7.");
		pm_player_group_set = Core.getMessages().getString("features.permission-manager.set-player-group", "&e{TARGET} &7group was set to &e{GROUP}&7.");
		pm_group_create_delete = Core.getMessages().getString("features.permission-manager.group-create-remove", "&7Group \"&e{GROUP}&7\" {ACTION}.");
		pm_group_doesnt_exist = Core.getMessages().getString("features.permission-manager.group-doesnt-exist", "&7Group \"&e{GROUP}&7\" doesn't exists.");
		pm_group_priority_change = Core.getMessages().getString("feature.permission-manager.group-priority-change", "&7Group \"&e{GROUP}&7\" priority was set to &e{PRIORITY}&7.");
		pm_invalid_priority = Core.getMessages().getString("features.permission-manager.invalid-priority", "&7Priority provided is invalid, priority must be numeric.");
	}
	
	/*
	 * pm user <user> prefix <prefix>
	 * pm user <user> suffix <suffix>
	 * pm user <user> group set <group>
	 * pm user <user> group add <group>
	 * pm user <user> group remove <group>
	 * pm user <user> add <permission>
	 * pm user <user> remove <permission>
	 * 
	 * pm group <group> create 3
	 * pm group <group> remove 3
	 * pm group <group> prefix <prefix> 4
	 * pm group <group> suffix <suffix> 4
	 * pm group <group> tablistprefix <prefix> 4
	 * pm group <group> tablistprefix <suffix> 4
	 * pm group <group> priority <priority> 4
	 * pm group <group> add <permission> 4
	 * pm group <group> remove <permission> 4
	 * pm group <group> parents add <group> 5
	 * pm group <group> parents remove <group> 5
	 * pm group <group> parent set <group> 5
	 * pm group <group> user add <user> 5
	 * pm group <group> user remove <user> 5
	 */

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission(PERMISSION.PERMISSIONSMANAGER_MANAGE.toString())) {
			////////////////////////////////////////////////////////////////////////////////////
			//	This part turns arguments inside quotes (") into one argument				  //
			////////////////////////////////////////////////////////////////////////////////////
			
			Pattern pattern = Pattern.compile("(\"[^\"]+\"|\\S+)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(String.join(" ", args));
	        List<String> a = new ArrayList<>();
			while(matcher.find()) {
			    for (int i = 1; i <= matcher.groupCount(); i++) {
			    	a.add(matcher.group(i));
			    }
			}
		    args = new String[a.size()];
		    for(int i = 0; i < a.size(); i++) {
		    	args[i] = a.get(i);
		    }
		    ////////////////////////////////////////////////////////////////////////////////////
			
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("user")) {
					if(args.length == 4) {
						RunicPlayer target = RunicPlayer.cast(Bukkit.getPlayerExact(args[1]));
						if(args[2].equalsIgnoreCase("prefix")) {
							String prefix = args[3];
							if(prefix.matches("^(?:\"[^\"]+\"|\\S+)$")) {
								if(target != null) {
									if(target.isOnline()) {
										PermissionUser user = PermissionUser.getUser(target.getUniqueId());
										if(prefix.charAt(0) == '"' && prefix.charAt(prefix.length() - 1) == '"') prefix = prefix.substring(1, prefix.length() - 1); 
										user.setPrefix(prefix);
										sender.sendMessage(Placeholder.parse(pm_player_affix_change, sender).set("{TARGET}", target.getName()).set("{AFFIX}", "prefix").set("{VALUE}", prefix).getString());
									} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", target.getName()).getString());
								} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[1]).getString());
							} else sender.sendMessage(Placeholder.parse(pm_invalid_affix, sender).set("{AFFIX}", "Prefix").getString());
						} else if(args[2].equalsIgnoreCase("suffix")) {
							String suffix = args[3];
							if(suffix.matches("^(?:\"[^\"]+\"|\\S+)$")) {
								if(target != null) {
									if(target.isOnline()) {
										PermissionUser user = PermissionUser.getUser(target.getUniqueId());
										if(suffix.charAt(0) == '"' && suffix.charAt(suffix.length() - 1) == '"') suffix = suffix.substring(1, suffix.length() - 1); 
										user.setSuffix(suffix);
										sender.sendMessage(Placeholder.parse(pm_player_affix_change, sender).set("{TARGET}", target.getName()).set("{AFFIX}", "suffix").set("{VALUE}", suffix).getString());
									} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", target.getName()).getString());
								} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[1]).getString());
							} else sender.sendMessage(Placeholder.parse(pm_invalid_affix, sender).set("{AFFIX}", "Suffix").getString());
						} else if(args[2].equalsIgnoreCase("add")) {
							String permission = args[3];
							if(permission.matches("^[\\w\\.-]+$")) {
								if(target != null) {
									if(target.isOnline()) {
										PermissionUser user = PermissionUser.getUser(target.getUniqueId()); 
										user.addPermission(permission);
										sender.sendMessage(Placeholder.parse(pm_permission_add, sender).set("{PERMISSION}", permission).set("{OBJECT}", target.getName()).getString());
									} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", target.getName()).getString());
								} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[1]).getString());
							} else sender.sendMessage(Placeholder.parse(pm_invalid_permission, sender).getString());
						} else if(args[2].equalsIgnoreCase("remove")) {
							String permission = args[3];
							if(permission.matches("^[\\w\\.-]+$")) {
								if(target != null) {
									if(target.isOnline()) {
										PermissionUser user = PermissionUser.getUser(target.getUniqueId()); 
										user.removePermission(permission);
										sender.sendMessage(Placeholder.parse(pm_permission_remove, sender).set("{PERMISSION}", permission).set("{OBJECT}", target.getName()).getString());
									} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", target.getName()).getString());
								} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[1]).getString());
							} else sender.sendMessage(Placeholder.parse(pm_invalid_permission, sender).getString());
						} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
					} else if(args.length == 5) {
						RunicPlayer target = RunicPlayer.cast(Bukkit.getPlayerExact(args[1]));
						if(args[2].equalsIgnoreCase("group")) {
							if(args[3].equalsIgnoreCase("set")) {
								if(target != null) {
									if(target.isOnline()) {
										if(PermissionGroup.groupExists(args[4])) {
											PermissionUser user = PermissionUser.getUser(target.getUniqueId());
											PermissionGroup group = PermissionGroup.getGroup(args[4]);
											user.setGroup(group);
											sender.sendMessage(Placeholder.parse(pm_player_group_set, sender).set("{TARGET}", target.getName()).set("{GROUP}", group.getName()).getString());
										} else sender.sendMessage(Placeholder.parse(pm_group_doesnt_exist, sender).set("{GROUP}", args[4]).getString());
									} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", target.getName()).getString());
								} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[1]).getString());
							} else if(args[3].equalsIgnoreCase("add")) {
								if(target != null) {
									if(target.isOnline()) {
										if(PermissionGroup.groupExists(args[4])) {
											PermissionUser user = PermissionUser.getUser(target.getUniqueId());
											PermissionGroup group = PermissionGroup.getGroup(args[4]);
											user.addGroup(group);
											sender.sendMessage(Placeholder.parse(pm_player_group_add, sender).set("{TARGET}", target.getName()).set("{GROUP}", group.getName()).getString());
										} else sender.sendMessage(Placeholder.parse(pm_group_doesnt_exist, sender).set("{GROUP}", args[4]).getString());
									} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", target.getName()).getString());
								} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[1]).getString());
							} else if(args[3].equalsIgnoreCase("remove")) {
								if(target != null) {
									if(target.isOnline()) {
										if(PermissionGroup.groupExists(args[4])) {
											PermissionUser user = PermissionUser.getUser(target.getUniqueId());
											PermissionGroup group = PermissionGroup.getGroup(args[4]);
											user.removeGroup(group);
											sender.sendMessage(Placeholder.parse(pm_player_group_remove, sender).set("{TARGET}", target.getName()).set("{GROUP}", group.getName()).getString());
										} else sender.sendMessage(Placeholder.parse(pm_group_doesnt_exist, sender).set("{GROUP}", args[4]).getString());
									} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", target.getName()).getString());
								} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[1]).getString());
							} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
						} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
					} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
				} else if(args[0].equalsIgnoreCase("group")) {
					if(args.length == 3) {
						if(args[2].equalsIgnoreCase("create")) {
							if(!PermissionGroup.groupExists(args[1])) {
								PermissionGroup group = PermissionGroup.getGroup(args[1]);
								sender.sendMessage(Placeholder.parse(pm_group_create_delete).set("{GROUP}", group.getName()).set("{ACTION}", "created").getString());
							} else sender.sendMessage(Placeholder.parse(pm_group_doesnt_exist, sender).set("{GROUP}", args[4]).getString());
						} else if(args[2].equalsIgnoreCase("remove")) {
							if(PermissionGroup.groupExists(args[1])) {
								PermissionGroup group = PermissionGroup.getGroup(args[1]);
								group.deleteGroup();
								sender.sendMessage(Placeholder.parse(pm_group_create_delete).set("{GROUP}", group.getName()).set("{ACTION}", "deleted").getString());
							} else sender.sendMessage(Placeholder.parse(pm_group_doesnt_exist, sender).set("{GROUP}", args[4]).getString());
						} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
					} else if(args.length == 4) {
						if(PermissionGroup.groupExists(args[1])) {
							PermissionGroup group = PermissionGroup.getGroup(args[1]);
							if(args[2].equalsIgnoreCase("prefix")) {
								String prefix = args[3];
								if(prefix.matches("^(?:\"[^\"]+\"|\\S+)$")) {
									if(prefix.charAt(0) == '"' && prefix.charAt(prefix.length() - 1) == '"') prefix = prefix.substring(1, prefix.length() - 1);
									group.setPrefix(prefix);
									sender.sendMessage(Placeholder.parse(pm_group_affix_change, sender).set("{GROUP}", group.getName()).set("{AFFIX}", "prefix").set("{VALUE}", prefix).getString());
								} else sender.sendMessage(Placeholder.parse(pm_invalid_affix, sender).set("{AFFIX}", "Prefix").getString());
							} else if(args[2].equalsIgnoreCase("suffix")) {
								String suffix = args[3];
								if(suffix.matches("^(?:\"[^\"]+\"|\\S+)$")) {
									if(suffix.charAt(0) == '"' && suffix.charAt(suffix.length() - 1) == '"') suffix = suffix.substring(1, suffix.length() - 1);
									group.setSuffix(suffix);
									sender.sendMessage(Placeholder.parse(pm_group_affix_change, sender).set("{GROUP}", group.getName()).set("{AFFIX}", "suffix").set("{VALUE}", suffix).getString());
								} else sender.sendMessage(Placeholder.parse(pm_invalid_affix, sender).set("{AFFIX}", "Suffix").getString());
							} else if(args[2].equalsIgnoreCase("add")) {
								String permission = args[3];
								if(permission.matches("^[\\w\\.-]+$")) {
									group.addPermission(permission);
									sender.sendMessage(Placeholder.parse(pm_permission_add, sender).set("{PERMISSION}", permission).set("{OBJECT}", group.getName()).getString());
								} else sender.sendMessage(Placeholder.parse(pm_invalid_permission, sender).getString());
							} else if(args[2].equalsIgnoreCase("remove")) {
								String permission = args[3];
								if(permission.matches("^[\\w\\.-]+$")) {
									group.removePermission(permission);
									sender.sendMessage(Placeholder.parse(pm_permission_remove, sender).set("{PERMISSION}", permission).set("{OBJECT}", group.getName()).getString());
								} else sender.sendMessage(Placeholder.parse(pm_invalid_permission, sender).getString());
							} else if(args[2].equalsIgnoreCase("priority")) {
								if(args[3].matches("^-?\\d+$")) {
									int priority = Integer.parseInt(args[3]);
									group.setPriority(priority);
									sender.sendMessage(Placeholder.parse(pm_group_priority_change, sender).set("{GROUP}", group.getName()).set("{PRIORITY}", group.getPriority()).getString());
								} else sender.sendMessage(Placeholder.parse(pm_invalid_priority, sender).getString());
							} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
						} else sender.sendMessage(Placeholder.parse(pm_group_doesnt_exist, sender).set("{GROUP}", args[1]).getString());
					} else if(args.length == 5) {
						if(PermissionGroup.groupExists(args[1])) {
							PermissionGroup group = PermissionGroup.getGroup(args[1]);
							if(args[2].equalsIgnoreCase("parents")) {
								if(args[3].equalsIgnoreCase("add")) {
									
								} else if(args[3].equalsIgnoreCase("remove")) {
									
								} else if(args[3].equalsIgnoreCase("set")) {
									
								}
								sender.sendMessage("Group inheritance has not yet been implemented.");
							} else if(args[2].equalsIgnoreCase("user")) {
								if(args[3].equalsIgnoreCase("add")) {
									RunicPlayer target = RunicPlayer.cast(Bukkit.getPlayerExact(args[4]));
									if(target != null && target.isOnline()) {
										PermissionUser user = PermissionUser.getUser(target.getUniqueId());
										user.addGroup(group);
										sender.sendMessage(Placeholder.parse(pm_player_group_add, sender).set("{TARGET}", target.getName()).set("{GROUP}", group.getName()).getString());
									} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[4]).getString());
								} else if(args[3].equalsIgnoreCase("remove")) {
									RunicPlayer target = RunicPlayer.cast(Bukkit.getPlayerExact(args[4]));
									if(target != null && target.isOnline()) {
										PermissionUser user = PermissionUser.getUser(target.getUniqueId());
										user.removeGroup(group);
										sender.sendMessage(Placeholder.parse(pm_player_group_remove, sender).set("{TARGET}", target.getName()).set("{GROUP}", group.getName()).getString());
									} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[4]).getString());
								} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
							} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
						} else sender.sendMessage(Placeholder.parse(pm_group_doesnt_exist, sender).set("{GROUP}", args[1]).getString());
					} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
				} else sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
			} else sender.sendMessage(Placeholder.parse(not_enough_arguments, sender).set("{COMMAND}", label.toLowerCase()+" help").getString());
		} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		return true;
	}

}
