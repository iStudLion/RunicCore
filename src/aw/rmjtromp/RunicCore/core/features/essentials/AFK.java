package aw.rmjtromp.RunicCore.core.features.essentials;

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

public final class AFK extends RunicFeature implements CommandExecutor, TabCompleter {

	private enum PERMISSION {
		AFK_SELF("runic.afk"),
		AFK_OTHERS("runic.afk.others");
		
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
		return "AFK";
	}
	
	@Override
	public void onEnable() {
		registerCommand(new RunicCommand("afk")
				.setDescription("Marks you as away-from-keyboard")
				.setAliases(Arrays.asList("away"))
				.setPermission(PERMISSION.AFK_SELF.toString())
				.setUsage("/afk [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, incorrect_usage, sender_not_a_player, no_permission, afk_target_enter, afk_target_leave;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);

		afk_target_enter = Core.getMessages().getString("features.afk.target-enter", "&e{TARGET} &7is now AFK!");
		afk_target_leave = Core.getMessages().getString("features.afk.target-leave", "&e{TARGET} &7is no longer AFK!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				RunicPlayer target = RunicPlayer.cast(sender);
				if(target.hasPermission(PERMISSION.AFK_SELF.toString())) target.setAFK(!target.isAFK());
				else target.sendMessage(Placeholder.parse(no_permission, target).getString());
			} else sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
		} else if(args.length == 1) {
			if(sender.hasPermission(PERMISSION.AFK_SELF.toString())) {
				List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
				if(targets.size() > 0) {
					if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
						// sender is targeting self
						RunicPlayer target = RunicPlayer.cast(sender);
						target.setAFK(!target.isAFK());
					} else {
						// sender is targeting others
						if(sender.hasPermission(PERMISSION.AFK_OTHERS.toString())) {
							for(RunicPlayer target : targets) {
								target.setAFK(!target.isAFK());
								if(!target.equals(sender)) {
									if(target.isAFK()) sender.sendMessage(Placeholder.parse(afk_target_enter, sender).set("{TARGET}", target.getName()).getString());
									else sender.sendMessage(Placeholder.parse(afk_target_leave, sender).set("{TARGET}", target.getName()).getString());
								}
							}
						} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
					}
				} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else {
			if(sender.hasPermission(PERMISSION.AFK_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
			else if(sender.hasPermission(PERMISSION.AFK_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
			else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(args.length == 1 && sender.hasPermission(PERMISSION.AFK_OTHERS.toString())) {
			for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
				suggestion.add(player);
			}
		}
		
		return suggestion;
	}
	
}
