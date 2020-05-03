package aw.rmjtromp.RunicCore.core.features.main.permissionmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.permissions.PermissionAttachment;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;
import aw.rmjtromp.RunicCore.utilities.configs.Config;

public final class PermissionUser implements Listener {
	
	private final static Config config = Config.init("permissions").loadFromResource("permissions");
	private static HashMap<UUID, PermissionUser> users = new HashMap<>();
	
	private UUID uuid;
	private String prefix, suffix, tablist_prefix, tablist_suffix = null;
	private List<String> permissions = new ArrayList<>();
	private List<PermissionGroup> groups = new ArrayList<>();
	private PermissionAttachment attachment;
	
	private PermissionUser() {
		Bukkit.getPluginManager().registerEvents(this, RunicCore.getInstance());
	}
	
	private PermissionUser(UUID uuid) {
		this.uuid = uuid;
		// add default groups
		
		Player player = Bukkit.getPlayer(uuid);
		if(player != null && player.isOnline()) {
			attachment = player.addAttachment(RunicCore.getInstance());
		}
		
//		loadEverything(); /* #recalculatePermission() already loads everything */
		recalculatePermissions();
		
		users.put(uuid, this);
	}
	
	public static void registerEvents() {
		new PermissionUser();
	}
	
	/**
	 * Loads every thing for the user<br>
	 * personal prefix, suffix, permissions and groups the user is member of.
	 */
	private void loadEverything() {
		if(config.contains("users."+uuid.toString())) {
			if(config.contains("users."+uuid.toString()+".options")) {
				// load prefix and suffix
				prefix = config.contains("users."+uuid.toString()+".options.prefix") ? config.getString("users."+uuid.toString()+".options.prefix") : null;
				suffix = config.contains("users."+uuid.toString()+".options.suffix") ? config.getString("users."+uuid.toString()+".options.suffix") : null;
				
				tablist_prefix = config.contains("users."+uuid.toString()+".options.tablist.prefix") ? config.getString("users."+uuid.toString()+".options.tablist.prefix") : null;
				tablist_suffix = config.contains("users."+uuid.toString()+".options.tablist.suffix") ? config.getString("users."+uuid.toString()+".options.tablist.suffix") : null;
				
				if(prefix != null && prefix.isEmpty()) prefix = null;
				if(suffix != null && suffix.isEmpty()) suffix = null;
				
				// load all permissions
				if(config.contains("users."+uuid.toString()+".permissions")) permissions = config.isList("users."+uuid.toString()+".permissions") ? config.getStringList("users."+uuid.toString()+".permissions") : new ArrayList<>();			
				
				// load all groups
				if(config.contains("users."+uuid.toString()+".groups")) {
					List<String> groups = config.isList("users."+uuid.toString()+".groups") ? config.getStringList("users."+uuid.toString()+".groups") : new ArrayList<String>();
					
					groups.forEach((group) -> {
						if(PermissionGroup.groupExists(group)) this.groups.add(PermissionGroup.getGroup(group));
					});
				}
			}
		} // don't create the user if the user doesn't exist, to save space
	}

	/**
	 * Sets the user's personal prefix
	 * @param String prefix
	 */
	public void setPrefix(String string) {
		if(string == null || string.isEmpty()) {
			prefix = null;
			config.set("users."+uuid.toString()+".options.prefix", null);
		} else {
			prefix = string;
			config.set("users."+uuid.toString()+".options.prefix", string);
		}
		// update prefix of player (depending on priority)
	}
	
	/**
	 * Returns the user's personal prefix
	 * @return String prefix || null
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Gets the user's personal suffix
	 * @return String prefix || null
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * Sets the user's personal suffix
	 * @param String prefix
	 */
	public void setSuffix(String string) {
		if(string == null || string.isEmpty()) {
			suffix = null;
			config.set("users."+uuid.toString()+".options.suffix", null);
		} else {
			suffix = string;
			config.set("users."+uuid.toString()+".options.suffix", string);
		}
	}

	/**
	 * Sets the user's personal tablist prefix
	 * @param String prefix
	 */
	public void setTablistPrefix(String string) {
		if(string == null || string.isEmpty()) {
			tablist_prefix = null;
			config.set("users."+uuid.toString()+".options.tablist.prefix", null);
		} else {
			tablist_prefix = string;
			config.set("users."+uuid.toString()+".options.tablist.prefix", string);
		}
		// update prefix of player (depending on priority)
	}
	
	/**
	 * Returns the user's personal tablist prefix
	 * @return String prefix || null
	 */
	public String getTablistPrefix() {
		return tablist_prefix;
	}
	
	/**
	 * Gets the user's personal tablist suffix
	 * @return String prefix || null
	 */
	public String getTablistSuffix() {
		return tablist_suffix;
	}

	/**
	 * Sets the user's personal tablist suffix
	 * @param String prefix
	 */
	public void setTablistSuffix(String string) {
		if(string == null || string.isEmpty()) {
			tablist_suffix = null;
			config.set("users."+uuid.toString()+".options.tablist.suffix", null);
		} else {
			tablist_suffix = string;
			config.set("users."+uuid.toString()+".options.tablist.suffix", string);
		}
	}

	/**
	 * Adds permission to personal user
	 * @param String permission
	 */
	public void addPermission(String permission) {
		if(permission != null && !permission.isEmpty()) {
			if(!hasPermission(permission)) {
				permissions.add(permission);
				recalculatePermissions();
				
				config.set("users."+uuid.toString()+".permissions", permissions);
			}
		}
	}

	/**
	 * Removes permission from personal user
	 * @param String permission
	 */
	public void removePermission(String permission) {
		if(permission != null && !permission.isEmpty()) {
			if(hasPermission(permission)) {
				permissions.remove(permission);
				recalculatePermissions();
				
				config.set("users."+uuid.toString()+".permissions", permissions);
			}
		}
	}
	
	/**
	 * Returns all groups (that user is member of)'s name
	 * @return StringList Group names 
	 */
	private List<String> getListGroupNames() {
		List<String> groups = new ArrayList<String>();
		for(PermissionGroup group : this.groups) {
			groups.add(group.getName());
		}
		return groups;
	}

	/**
	 * Adds user to a group
	 * @param PermissionGroup group
	 */
	public void addGroup(PermissionGroup group) {
		if(group != null) {
			groups.add(group);
			if(groups.size() < 1) config.set("users."+uuid.toString()+".groups", null);
			else config.set("users."+uuid.toString()+".groups", getListGroupNames());
			recalculatePermissions();
		}
	}

	/**
	 * Remove user from a group
	 * @param PermissionGroup group
	 */
	public void removeGroup(PermissionGroup group) {
		if(group != null && groups.contains(group)) {
			groups.remove(group);
			if(groups.size() < 1) config.set("users."+uuid.toString()+".groups", null);
			else config.set("users."+uuid.toString()+".groups", getListGroupNames());
			recalculatePermissions();
		}
	}

	/**
	 * Sets the user's group
	 * <p style="color:red;"><b>WARNING:</b> this removes the user's previous groups</p>
	 * @param group
	 */
	public void setGroup(PermissionGroup group) {
		if(group != null) {
			groups.clear();
			groups.add(group);
			config.set("users."+uuid.toString()+".groups", getListGroupNames());
			recalculatePermissions();
		}
	}
	
	/**
	 * Returns whether or not user is member of a group
	 * <br><b>This does not work with default groups</b>
	 * @param PermissionGroup group
	 * @return Boolean
	 */
	public boolean hasGroup(PermissionGroup group) {
		return groups.contains(group);
	}

	/**
	 * Returns whether or not the personal user has a specific permission
	 * @param String permission
	 * @return Boolean
	 */
	public boolean hasPermission(String permission) {
		if(permission != null && !permission.isEmpty()) {
			return permissions.contains(permission);
		}
		return false;
	}
	
	/**
	 * Returns all users loaded
	 * @return PermissionUser Collection
	 */
	public static Collection<PermissionUser> getUsers() {
		return users.values();
	}

	// TODO load users that are offline?
	/**
	 * Returns PermissionUser for UUID
	 * @param UUID uuid
	 * @return PermissionUser
	 */
	public static PermissionUser getUser(UUID uuid) {
		if(uuid != null) {
			if(users.containsKey(uuid)) return users.get(uuid);
			else return new PermissionUser(uuid);
		}
		return null;
	}

	/**
	 * Returns whether or not user is member of a group
	 * <br><b>This works with default groups</b>
	 * @param PermissionGroup group
	 * @return Boolean
	 */
	public boolean isMemberOf(PermissionGroup group) {
		return (group.isDefault() || groups.contains(group));
	}

	/**
	 * Returns whether or not user is member of a group
	 * <br><b>This works with default groups</b>
	 * @param String group name
	 * @return Boolean
	 */
	@Deprecated
	public boolean isMemberOf(String groupName) {
		if(PermissionGroup.groupExists(groupName)) {
			PermissionGroup group = PermissionGroup.getGroup(groupName);
			return (group.isDefault() || groups.contains(group));
		}
		return false;
	}
	
	/**
	 * Returns the user's unique ID
	 * @return UUID
	 */
	public UUID getUniqueId() {
		return uuid;
	}
	
	/**
	 * Returns the groups that the users is member of
	 * @return
	 */
	public List<PermissionGroup> getGroups() {
		List<PermissionGroup> g = new ArrayList<>();
		for(PermissionGroup group : groups) {
			if(isMemberOf(group)) g.add(group);
		}
		return g;
	}
	
	/**
	 * Recalculates user's permissions
	 */
	public void recalculatePermissions() {
		Player player = Bukkit.getPlayer(uuid);
		if(player != null && player.isOnline()) {
			loadEverything();
			
			List<String> finalPermissions = new ArrayList<String>();
			finalPermissions.addAll(permissions);
			for(PermissionGroup group : PermissionGroup.getGroups()) {
				if(isMemberOf(group)) finalPermissions.addAll(group.getPermissions());
			}
			
			if(attachment != null) {
				player.removeAttachment(attachment);
				attachment.remove();
				attachment = null;
			}
			
			attachment = player.addAttachment(RunicCore.getInstance());
			for(String permission : finalPermissions) {
				attachment.setPermission(permission, true);
			}
		}
	}
	
	/**
	 * Removes permission attachment from player
	 */
	private void removeAttachment() {
		Player player = Bukkit.getPlayer(uuid);
		if(player != null && player.isOnline()) {
			if(attachment != null) {
				player.removeAttachment(attachment);
				attachment.remove();
				attachment = null;
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PermissionUser) {
			return uuid.equals(((PermissionUser) obj).uuid);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "PermissionUser::"+uuid.toString();
	}
	
	@EventHandler
	public static void onPlayerJoin(PlayerJoinEvent e) {
		PermissionUser user = PermissionUser.getUser(e.getPlayer().getUniqueId());
		user.recalculatePermissions();
	}
	
	@EventHandler
	public static void onPlayerQuit(PlayerQuitEvent e) {
		if(users.containsKey(e.getPlayer().getUniqueId())) {
			PermissionUser user = users.get(e.getPlayer().getUniqueId());
			user.removeAttachment();
		}
	}
	
	@EventHandler
	public static void onRunicCoreReloadEvent(RunicCoreReloadEvent e) {
		for(PermissionUser user : users.values()) {
			user.recalculatePermissions();
		}
	}
	
	@EventHandler
	public static void onPluginDisableEvent(PluginDisableEvent e) {
		for(PermissionUser user : users.values()) {
			user.removeAttachment();
		}
	}
	
}
