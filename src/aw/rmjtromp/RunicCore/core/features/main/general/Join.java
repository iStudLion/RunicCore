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
import aw.rmjtromp.RunicCore.core.features.main.other.ServerSelector;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.PlayerSelector;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Join extends RunicFeature implements CommandExecutor, TabCompleter {
	
	private ServerSelector serverSelector;

	@Override
	public String getName() {
		return "Join";
	}
	
	private enum PERMISSION {
		JOIN_OTHERS("runic.join.others");
		
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
		registerCommand(new RunicCommand("join")
				.setDescription("Opens Server Selector")
				.setAliases(Arrays.asList("ss", "serverselector", "server"))
				.setUsage("/join [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}

	private String target_not_found, incorrect_usage, sender_not_a_player, no_permission, join_server_selector_open, join_server_selector_open_target;
	@Override
	public void loadConfigurations() {
		if(serverSelector != null) serverSelector = serverSelector.destroy();
		serverSelector = ServerSelector.init();
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);

		join_server_selector_open_target = Core.getMessages().getString("features.server-selector.open-target", "&7Opening server selector for &e{TARGET}&7.");
		join_server_selector_open = Core.getMessages().getString("features.server-selector.open", "&7Opening server selector...");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				RunicPlayer player = RunicPlayer.cast(sender);
				serverSelector.open(player);
			} else sender.sendMessage(Placeholder.parse(sender_not_a_player).getString());
		} else if(args.length == 1) {
			if(sender instanceof Player && args[0].equalsIgnoreCase(sender.getName())) {
				RunicPlayer player = RunicPlayer.cast(sender);
				serverSelector.open(player);
			} else {
				List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
				if(targets.size() > 0) {
					if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
						// sender is targeting self
						RunicPlayer target = RunicPlayer.cast(sender);
						serverSelector.open(target);
						target.sendMessage(Placeholder.parse(join_server_selector_open, target).getString());
					} else {
						// sender is targeting others
						if(sender.hasPermission(PERMISSION.JOIN_OTHERS.toString())) {
							for(RunicPlayer target : targets) {
								serverSelector.open(target);
								target.sendMessage(Placeholder.parse(join_server_selector_open, target).getString());
								if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(join_server_selector_open_target, sender).set("{TARGET}", target.getName()).getString());
							}
						} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
					}
				} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
			}
		} else {
			if(sender.hasPermission(PERMISSION.JOIN_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
			else sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()).getString());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(args.length == 1 && sender.hasPermission(PERMISSION.JOIN_OTHERS.toString())) {
			for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
				suggestion.add(player);
			}
		}
		
		return suggestion;
	}

}
