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

public final class Enderchest extends RunicFeature implements CommandExecutor, TabCompleter {

	private enum PERMISSION {
		ENDERCHEST_SELF("runic.enderchest"),
		ENDERCHEST_OTHERS("runic.enderchest.others");
		
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
		return "Enderchest";
	}
	
	@Override
	public void onEnable() {
		registerCommand(new RunicCommand("enderchest")
				.setDescription("Lets you see inside an enderchest")
				.setAliases(Arrays.asList("echest", "chest", "endersee", "ec"))
				.setPermission(PERMISSION.ENDERCHEST_SELF.toString())
				.setUsage("/enderchest [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, incorrect_usage, sender_not_a_player, no_permission, target_selection_too_big, enderchest_open, enderchest_open_target;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		target_selection_too_big = Core.getMessages().getMessage(MESSAGE.TARGET_SELECTION_TOO_BIG);

		enderchest_open = Core.getMessages().getString("features.enderchest.open", "&7Opening enderchest...");
		enderchest_open_target = Core.getMessages().getString("features.enderchest.open", "&7Opening {TARGET}'s enderchest...");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			RunicPlayer player = RunicPlayer.cast((Player) sender);
			if(args.length == 0) {
				if(player.hasPermission(PERMISSION.ENDERCHEST_SELF.toString())) {
					player.openInventory(player.getEnderChest());
					player.sendMessage(Placeholder.parse(enderchest_open, player).getString());
				} else player.sendMessage(Placeholder.parse(no_permission, player).getString());
			} else if(args.length == 1) {
				if(sender.hasPermission(PERMISSION.ENDERCHEST_SELF.toString())) {
					List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
					if(targets.size() > 0) {
						if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
							// sender is targeting self
							player.openInventory(player.getEnderChest());
							player.sendMessage(Placeholder.parse(enderchest_open, player).getString());
						} else {
							// sender is targeting others
							if(sender.hasPermission(PERMISSION.ENDERCHEST_OTHERS.toString())) {
								if(targets.size() == 1) {
									RunicPlayer target = targets.get(0);
									player.openInventory(target.getEnderChest());
									player.sendMessage(Placeholder.parse(enderchest_open_target, player).set("{TARGET}", target.getName()).getString());
								} else {
									// size too big, can't open all
									player.sendMessage(Placeholder.parse(target_selection_too_big, player).getString());
								}
							} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
						}
					} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
				} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
			} else {
				if(player.hasPermission(PERMISSION.ENDERCHEST_OTHERS.toString())) player.sendMessage(Placeholder.parse(incorrect_usage, player).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
				else if(player.hasPermission(PERMISSION.ENDERCHEST_SELF.toString())) player.sendMessage(Placeholder.parse(incorrect_usage, player).set("{COMMAND}", label.toLowerCase()).getString());
				else player.sendMessage(Placeholder.parse(no_permission, player).getString());
			}
		} else sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(args.length == 1 && sender.hasPermission(PERMISSION.ENDERCHEST_OTHERS.toString())) {
			for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
				suggestion.add(player);
			}
		}
		
		return suggestion;
	}
}
