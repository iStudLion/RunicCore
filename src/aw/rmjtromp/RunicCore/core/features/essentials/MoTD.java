package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public class MoTD extends RunicFeature implements CommandExecutor, Listener {

	private enum PERMISSION {
		MOTD("runic.motd");
		
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
		return "MoTD";
	}
	
	@Override
	public void onEnable() {
		registerCommand(new RunicCommand("motd")
				.setDescription("Views the Message of The Day")
				.setPermission(PERMISSION.MOTD.toString())
				.setExecutor(this));
	}
	
	private List<String> motd = new ArrayList<String>();
	private String no_permission;
	@Override
	public void loadConfigurations() {
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		
		

		List<String> defaultMotd = new ArrayList<String>();
		defaultMotd.add("&7 ");
		defaultMotd.add("&7 ");
		defaultMotd.add("&7 ");
		defaultMotd.add("&7 ");
		defaultMotd.add("&7 ");
		defaultMotd.add("&7&m-------------------------------------------");
		defaultMotd.add("                                 &6Runic&eSky");
		defaultMotd.add("&7&m-------------------------------------------");
		
		motd = Core.getConfig().getStringList("features.motd.message", defaultMotd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission(PERMISSION.MOTD.toString())) {
			sender.sendMessage(Placeholder.parse(Strings.join(motd, "\n&r"), sender).getString());
		} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(motd != null && !motd.isEmpty()) {
			e.getPlayer().sendMessage(Placeholder.parse(Strings.join(motd, "\n&r"), e.getPlayer()).getString());
		}
	}

}
