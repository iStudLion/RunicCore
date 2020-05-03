package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Broadcast extends RunicFeature implements CommandExecutor {

	@Override
	public String getName() {
		return "Broadcast";
	}
	
	private enum PERMISSION {
		BROADCAST("runic.broadcast");
		
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
		registerCommand(new RunicCommand("broadcast")
				.setDescription("Broadcasts a message to the entire server")
				.setAliases(Arrays.asList("bc", "bcast", "shout"))
				.setPermission(PERMISSION.BROADCAST.toString())
				.setUsage("/broadcast <message>")
				.setExecutor(this));
	}
	
	private String no_permission, broadcast_empty;
	@Override
	public void loadConfigurations() {
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		
		broadcast_empty = Core.getMessages().getString("features.broadcast.empty", "&cBroadcast message can not be empty.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission(PERMISSION.BROADCAST.toString())) {
			String message = String.join(" ", args);
			if(!message.isEmpty()) {
				System.out.print(Placeholder.parse(message).getString());
				for(Player player : Bukkit.getOnlinePlayers()) {
					player.sendMessage(Placeholder.parse(message, player).getString());
				}
			} else sender.sendMessage(Placeholder.parse(broadcast_empty).getString());
		} else sender.sendMessage(Placeholder.parse(no_permission).getString());
		return true;
	}

}
