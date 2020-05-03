package aw.rmjtromp.RunicCore.core.features.main.permissionmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import com.avaje.ebean.validation.NotNull;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.utilities.configs.Config;

public final class PermissionGroup implements Listener, Comparable<PermissionGroup> {
	
	private static HashMap<String, PermissionGroup> groups = new HashMap<>();
	private final static Config config = Config.init("permissions").loadFromResource("permissions");

	private String name, prefix, suffix, tablist_prefix, tablist_suffix = null;
	private int priority = 0;
	private boolean Default = false;
	private List<String> permissions = new ArrayList<>();
	private ChatColor chatColor = ChatColor.GRAY;
	
	private PermissionGroup() {
		Bukkit.getPluginManager().registerEvents(this, RunicCore.getInstance());
	}
	
	public static void registerEvent() {
		new PermissionGroup();
	}

	private PermissionGroup(String name) {
		this.name = name;
		loadEverything();
		groups.put(getName().toLowerCase(), this);
	}

	@NotNull
	public static PermissionGroup getGroup(String group) {
		if(group != null && !group.isEmpty()) {
			if(groups.containsKey(group.toLowerCase())) return groups.get(group.toLowerCase());
			else return new PermissionGroup(group);
		}
		return null;
	}
	
	private void loadEverything() {
		if(!config.contains("groups."+name+".options")) config.set("groups."+name+".options", new String[] {});
		else {
			prefix = config.contains("groups."+name+".options.prefix") ? config.getString("groups."+name+".options.prefix") : null;
			suffix = config.contains("groups."+name+".options.suffix") ? config.getString("groups."+name+".options.suffix") : null;
			priority = config.contains("groups."+name+".options.priority") ? config.getInt("groups."+name+".options.priority") : 0;

			tablist_prefix = config.contains("groups."+name+".options.tablist.prefix") ? config.getString("groups."+name+".options.tablist.prefix") : null;
			tablist_suffix = config.contains("groups."+name+".options.tablist.suffix") ? config.getString("groups."+name+".options.tablist.suffix") : null;
			
			if(prefix != null && prefix.isEmpty()) prefix = null;
			if(suffix != null && suffix.isEmpty()) suffix = null;
		}
		if(!config.contains("groups."+name+".permissions")) config.set("groups."+name+".permissions", new ArrayList<String>());
		else permissions = config.getStringList("groups."+name+".permissions");
	}
	
	/**
	 * Returns the group's name
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the group's prefix
	 * @return String || null
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Returns the group's suffix
	 * @return String || null
	 */
	public String getSuffix() {
		return suffix;
	}
	
	/**
	 * Returns the group's tablist prefix
	 * @return String || null
	 */
	public String getTablistPrefix() {
		return tablist_prefix;
	}
	
	/**
	 * Returns the group's tablist suffix
	 * @return String || null
	 */
	public String getTablistSuffix() {
		return tablist_suffix;
	}

	/**
	 * Sets the groups prefix
	 * @param String prefix
	 */
	public void setPrefix(String string) {
		if(string == null || string.isEmpty()) {
			prefix = null;
			config.set("groups."+getName()+".options.prefix", null);
		} else {
			prefix = string;
			config.set("groups."+getName()+".options.prefix", string);
		}
	}

	/**
	 * Sets the groups suffix
	 * @param String suffix
	 */
	public void setSuffix(String string) {
		if(string == null || string.isEmpty()) {
			suffix = null;
			config.set("groups."+getName()+".options.suffix", null);
		} else {
			suffix = string;
			config.set("groups."+getName()+".options.suffix", string);
		}
	}

	/**
	 * Sets the groups tablist prefix
	 * @param String prefix
	 */
	public void setTablistPrefix(String string) {
		if(string == null || string.isEmpty()) {
			tablist_prefix = null;
			config.set("groups."+getName()+".options.tablist.prefix", null);
		} else {
			tablist_prefix = string;
			config.set("groups."+getName()+".options.tablist.prefix", string);
		}
	}

	/**
	 * Sets the groups tablist suffix
	 * @param String suffix
	 */
	public void setTablistSuffix(String string) {
		if(string == null || string.isEmpty()) {
			tablist_suffix = null;
			config.set("groups."+getName()+".options.tablist.suffix", null);
		} else {
			tablist_suffix = string;
			config.set("groups."+getName()+".options.tablist.suffix", string);
		}
	}

	/**
	 * Makes the group a default group
	 * @param Boolean toggle
	 */
	public void setDefault(boolean bool) {
		// only continue if the toggle is different than current toggle
		if(Default != bool) {
			Default = bool;
			config.set("groups."+name+".options.default", bool);
			recalculatePermissions();
		}
	}
	
	/**
	 * Returns whether or not the group is default
	 * @return Boolean
	 */
	public boolean isDefault() {
		return Default;
	}

	/**
	 * Adds a permission to group
	 * @param String permission
	 */
	public void addPermission(String permission) {
		if(permission != null && !permission.isEmpty()) {
			if(!hasPermission(permission)) {
				permissions.add(permission);
				recalculatePermissions();
				
				config.set("groups."+name+".permissions", permissions);
			}
		}
	}
	
	/**
	 * Returns the group's chat color
	 * @return ChatColor
	 */
	public ChatColor getChatColor() {
		return chatColor;
	}
	
	/**
	 * Sets the group's chat color
	 * @param ChatColor color
	 */
	@Deprecated
	public void setChatColor(ChatColor color) {
		// TODO
	}
	
	/**
	 * Returns all permissions of the group
	 * @return StringList
	 */
	public List<String> getPermissions() {
		// return a clone
		return new ArrayList<String>(permissions);
	}

	/**
	 * Removes a specific permission from the group
	 * @param String permission
	 */
	public void removePermission(String permission) {
		if(permission != null && !permission.isEmpty()) {
			if(hasPermission(permission)) {
				permissions.remove(permission);
				recalculatePermissions();
				
				config.set("groups."+name+".permissions", permissions);
			}
		}
	}

	/**
	 * Returns whether or not a group has a permission node
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
	 * Returns all registered groups
	 * @return PermissionGroup Collection
	 */
	public static Collection<PermissionGroup> getGroups() {
		return groups.values();
	}

	/**
	 * Return whether or not a group exists
	 * @param String group name
	 * @return Boolean
	 */
	public static boolean groupExists(String groupName) {
		if(groupName != null && !groupName.isEmpty()) {
			return groups.containsKey(groupName.toLowerCase());
		}
		return false;
	}

	/**
	 * Adds a user to a group
	 * @param PermissionUser user
	 */
	public void addPlayer(PermissionUser user) {
		if(!user.hasGroup(this)) user.addGroup(this);
	}

	/**
	 * Sets the listing priority of the group
	 * @param Integer priority
	 */
	public void setPriority(int priority) {
		if(this.priority != priority) {
			this.priority = priority;
			config.set("groups."+name+".options.priority", priority);
			recalculatePermissions();
		}
	}
	
	/**
	 * Returns the group's listing priority
	 * @return
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Removes player from a group
	 * @param PermissionUser user
	 */
	public void removePlayer(PermissionUser user) {
		if(user.hasGroup(this)) user.removeGroup(this);
	}

	/**
	 * Returns whether or not a user is member of the group
	 * @param PermissionUser user
	 * @return Boolean
	 */
	public boolean containsPlayer(PermissionUser user) {
		return user.hasGroup(this);
	}
	
	/**
	 * Recalculates the permission of all the users in the group
	 */
	private void recalculatePermissions() {
		loadEverything();
		for(Player player : Bukkit.getOnlinePlayers()) {
			PermissionUser user = PermissionUser.getUser(player.getUniqueId());
			if(user.isMemberOf(this)) user.recalculatePermissions();
		}
	}
	
	/**
	 * Deletes a group completely
	 */
	public void deleteGroup() {
		config.set("groups."+getName(), null);
		for(Player player : Bukkit.getOnlinePlayers()) {
			PermissionUser user = PermissionUser.getUser(player.getUniqueId());
			if(user.isMemberOf(this)) {
				/* check if user actually HAS a group, and is not assigned bc of its default */
				if(user.hasGroup(this)) user.removeGroup(this); /* PermissionUser#removeGroup() already recalculates permissions*/
				else user.recalculatePermissions();
			}
		}
		groups.remove(getName().toLowerCase());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PermissionGroup) {
			return getName().equals(((PermissionGroup) obj).getName());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "PermissionGroup::"+getName();
	}
	
	@EventHandler
	public static void onPluginDisableEvent(PluginDisableEvent e) {
		groups.clear();
	}

	@Override
	public int compareTo(PermissionGroup o) {
		return getPriority() - o.getPriority();
	}
	
}
