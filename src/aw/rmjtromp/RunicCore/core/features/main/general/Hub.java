package aw.rmjtromp.RunicCore.core.features.main.general;

import java.util.ArrayList;
import java.util.Arrays;
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

public final class Hub extends RunicFeature implements CommandExecutor, TabCompleter {

	@Override
	public String getName() {
		return "Hub";
	}
	
	private enum PERMISSION {
		SERVER_HUB_SELF("runic.server.hub"),
		SERVER_HUB_OTHERS("runic.server.others");
		
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
		registerCommand(new RunicCommand("hub")
				.setDescription("Fast and short command to teleport to server lobby")
				.setAliases(Arrays.asList("lobby"))
				.setUsage("/hub [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, incorrect_usage, sender_not_a_player, no_permission, hub_sending_target;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);

		hub_sending_target = Core.getMessages().getString("feature.server.sending-target", "&7Sending &e{TARGET}&7 to &e{SERVER}&7.");
//		hub_server_nonexistance = Core.getMessages().getString("feature.server.nonexistance", "&7Server '&e{0}&7' doesn't exist.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				if(sender.hasPermission(PERMISSION.SERVER_HUB_SELF.toString())) {
					RunicPlayer player = RunicPlayer.cast(sender);
					player.send("Hub");
				} else sender.sendMessage(Placeholder.parse(no_permission).getString());
			} else sender.sendMessage(Placeholder.parse(sender_not_a_player).getString());
		} else if(args.length == 1) {
			if(sender.hasPermission(PERMISSION.SERVER_HUB_SELF.toString())) {
				List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
				if(targets.size() > 0) {
					if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
						// sender is targeting self
						RunicPlayer target = RunicPlayer.cast(sender);
						target.send("Hub");
					} else {
						// sender is targeting others
						if(sender.hasPermission(PERMISSION.SERVER_HUB_OTHERS.toString())) {
							for(RunicPlayer target : targets) {
								target.send("Hub");
								if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(hub_sending_target).set("{TARGET}", target.getName()).set("{SERVER}", "Hub").getString());
							}
						} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
					}
				} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else {
			if(sender.hasPermission(PERMISSION.SERVER_HUB_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
			else if(sender.hasPermission(PERMISSION.SERVER_HUB_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()).getString());
			else sender.sendMessage(Placeholder.parse(no_permission).getString());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(args.length == 1 && sender.hasPermission(PERMISSION.SERVER_HUB_OTHERS.toString())) {
			for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
				suggestion.add(player);
			}
		}
		
		return suggestion;
	}
	
}
