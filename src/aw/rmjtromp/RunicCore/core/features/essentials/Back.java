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

public final class Back extends RunicFeature implements CommandExecutor, TabCompleter {

	@Override
	public String getName() {
		return "Back";
	}
	
	private enum PERMISSION {
		BACK_SELF("runic.back"),
		BACK_OTHERS("runic.back.others");
		
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
		registerCommand(new RunicCommand("back")
				.setDescription("Teleports you to your location prior to tp/spawn/warp")
				.setAliases(Arrays.asList("return"))
				.setPermission(PERMISSION.BACK_SELF.toString())
				.setUsage("/back [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, incorrect_usage, sender_not_a_player, no_permission, back_return_target;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		
		back_return_target = Core.getMessages().getString("features.back.return-target", "&7Returning &e{TARGET} &7to their previous location.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				RunicPlayer target = RunicPlayer.cast(sender);
				if(target.hasPermission(PERMISSION.BACK_SELF.toString())) target.sendBack();
				else target.sendMessage(Placeholder.parse(no_permission, target).getString());
			} else sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
		} else if(args.length == 1) {
			if(sender.hasPermission(PERMISSION.BACK_SELF.toString())) {
				List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
				if(targets.size() > 0) {
					if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
						// sender is targeting self
						RunicPlayer target = RunicPlayer.cast(sender);
						target.sendBack();
					} else {
						// sender is targeting others
						if(sender.hasPermission(PERMISSION.BACK_OTHERS.toString())) {
							for(RunicPlayer target : targets) {
								target.setAFK(!target.isAFK());
								if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(back_return_target, sender).set("{TARGET}", target.getName()).getString());
							}
						} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
					}
				} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else {
			if(sender.hasPermission(PERMISSION.BACK_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
			else if(sender.hasPermission(PERMISSION.BACK_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()).getString());
			else sender.sendMessage(Placeholder.parse(no_permission).getString());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(args.length == 1 && sender.hasPermission(PERMISSION.BACK_OTHERS.toString())) {
			for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
				suggestion.add(player);
			}
		}
		
		return suggestion;
	}
	
}
