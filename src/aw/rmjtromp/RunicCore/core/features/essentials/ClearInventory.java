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

public final class ClearInventory extends RunicFeature implements CommandExecutor, TabCompleter {

	@Override
	public String getName() {
		return "ClearInventory";
	}
	
	private enum PERMISSION {
		CLEARINVENTORY_SELF("runic.clearinventory"),
		CLEARINVENTORY_OTHERS("runic.clearinventory.others");
		
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
		registerCommand(new RunicCommand("clearinventory")
				.setDescription("Clear all items in your inventory")
				.setAliases(Arrays.asList("clear", "ci", "cinventory", "cleari", "clearinv", "cinv", "clean", "clearinvent"))
				.setPermission(PERMISSION.CLEARINVENTORY_SELF.toString())
				.setUsage("/clearinventory [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, incorrect_usage, sender_not_a_player, no_permission, clearinventory_clear_target, clearinventory_clear;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		
		clearinventory_clear_target = Core.getMessages().getString("features.clearinventory.clear-target", "&e{TARGET}&7's inventory was cleared.");
		clearinventory_clear = Core.getMessages().getString("features.clearinventory.clear", "&7Your inventory was cleared.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				RunicPlayer player = RunicPlayer.cast(sender);
				if(player.hasPermission(PERMISSION.CLEARINVENTORY_SELF.toString())) {
					player.clearInventory();
					player.sendMessage(Placeholder.parse(clearinventory_clear).getString());
				}
				else player.sendMessage(Placeholder.parse(no_permission, player).getString());
			} else sender.sendMessage(Placeholder.parse(sender_not_a_player).getString());
		} else if(args.length == 1) {
			if(sender.hasPermission(PERMISSION.CLEARINVENTORY_SELF.toString())) {
				List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
				if(targets.size() > 0) {
					if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
						// sender is targeting self
						RunicPlayer target = RunicPlayer.cast(sender);
						target.clearInventory();
						target.sendMessage(Placeholder.parse(clearinventory_clear).getString());
					} else {
						// sender is targeting others
						if(sender.hasPermission(PERMISSION.CLEARINVENTORY_OTHERS.toString())) {
							for(RunicPlayer target : targets) {
								target.clearInventory();
								target.sendMessage(Placeholder.parse(clearinventory_clear).getString());
								if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(clearinventory_clear_target).set("{TARGET}", target.getName()).getString());
							}
						} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
					}
				} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else {
			if(sender.hasPermission(PERMISSION.CLEARINVENTORY_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
			else if(sender.hasPermission(PERMISSION.CLEARINVENTORY_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()).getString());
			else sender.sendMessage(Placeholder.parse(no_permission).getString());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(args.length == 1 && sender.hasPermission(PERMISSION.CLEARINVENTORY_OTHERS.toString())) {
			for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
				suggestion.add(player);
			}
		}
		
		return suggestion;
	}
	
}
