package aw.rmjtromp.RunicCore.core.features.main;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;

public final class RunicCore extends RunicFeature implements CommandExecutor, TabCompleter {
	
	private enum PERMISSION {
		RUNIC_ADMIN("runic.admin");
		
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
		return "RunicCore";
	}
	
	@Override
	public void onEnable() {
		plugin.getCommand("runic").setExecutor(this);
		plugin.getCommand("runic").setTabCompleter(this);;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission(PERMISSION.RUNIC_ADMIN.toString())) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7RunicCore (v"+plugin.getDescription().getVersion()+") by &eiStudLion&7."));
			} else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload")) {
					Bukkit.getPluginManager().callEvent(new RunicCoreReloadEvent(sender));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7All configs, features, and extensions have been reloaded."));
				} else if(args[0].equalsIgnoreCase("rle"))  {
					plugin.getCore().getExtensionManager().reloadExtensions();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7All extensions have been reloaded."));
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fThis feature has to yet been completed"));
				}
			} else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fThis feature has to yet been completed"));
			}
		} else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You don't have enough permissions to use this command."));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}

}
