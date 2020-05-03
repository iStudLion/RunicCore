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
import aw.rmjtromp.RunicCore.utilities.essential.Cooldown;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Feed extends RunicFeature implements CommandExecutor, TabCompleter {
	
	private Cooldown cooldown;

	private enum PERMISSION {
		FEED_SELF("runic.feed"),
		FEED_OTHERS("runic.feed.others"),
		FEED_BYPASS("runic.feed.bypass");
		
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
		return "Feed";
	}
	
	@Override
	public void onEnable() {
		cooldown = new Cooldown(this, 60);
		registerCommand(new RunicCommand("feed")
				.setDescription("Satisfy the hunger.")
				.setAliases(Arrays.asList("eat"))
				.setPermission(PERMISSION.FEED_SELF.toString())
				.setUsage("/feed [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, incorrect_usage, sender_not_a_player, no_permission, feed_feed, feed_feed_target, command_cooldown;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		command_cooldown = Core.getMessages().getMessage(MESSAGE.COMMAND_COOLDOWN);

		feed_feed = Core.getMessages().getString("features.feed.feed", "&eYour hunger has been cleared!");
		feed_feed_target = Core.getMessages().getString("features.feed.feed-target", "&eYou cleared the hunger of {TARGET}!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				RunicPlayer player = RunicPlayer.cast(sender);
				if(player.hasPermission(PERMISSION.FEED_SELF.toString())) {
					if(cooldown.containsPlayer(player) && !player.hasPermission(PERMISSION.FEED_BYPASS.toString())) {
						int time = cooldown.getTimeLeft(player);
						player.sendMessage(Placeholder.parse(command_cooldown, player).set("{TIME}", time == 60 ? "1 minute" : time > 1 ? time+" seconds" : time+ "second").getString());
					} else {
						player.feed();
						player.sendMessage(Placeholder.parse(feed_feed, player).getString());
						cooldown.addPlayer(player);
					}
				} else player.sendMessage(Placeholder.parse(no_permission, player).getString());
			} else sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
		} else if(args.length == 1) {
			if(sender.hasPermission(PERMISSION.FEED_SELF.toString())) {
				List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
				if(targets.size() > 0) {
					if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
						// sender is targeting self
						RunicPlayer target = RunicPlayer.cast(sender);
						if(cooldown.containsPlayer(target) && !target.hasPermission(PERMISSION.FEED_BYPASS.toString())) {
							int time = cooldown.getTimeLeft(target);
							target.sendMessage(Placeholder.parse(command_cooldown, target).set("{TIME}", time == 60 ? "1 minute" : time > 1 ? time+" seconds" : time+ "second").getString());
						} else {
							target.feed();
							target.sendMessage(Placeholder.parse(feed_feed, target).getString());
							cooldown.addPlayer(target);
						}
					} else {
						// sender is targeting others
						if(sender.hasPermission(PERMISSION.FEED_OTHERS.toString())) {
							for(RunicPlayer target : targets) {
								target.feed();
								target.sendMessage(Placeholder.parse(feed_feed, target).getString());
								if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(feed_feed_target, sender).set("{TARGET}", target.getName()).getString());
							}
						} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
					}
				} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else {
			if(sender.hasPermission(PERMISSION.FEED_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [player]").getString());
			else if(sender.hasPermission(PERMISSION.FEED_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()).getString());
			else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(args.length == 1 && sender.hasPermission(PERMISSION.FEED_OTHERS.toString())) {
			for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
				suggestion.add(player);
			}
		}
		
		return suggestion;
	}

}
