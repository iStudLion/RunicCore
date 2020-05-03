package aw.rmjtromp.RunicCore.core.features.main.links;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Twitter extends RunicFeature implements CommandExecutor {

	@Override
	public String getName() {
		return "Twitter";
	}
	
	private RunicCommand cmd;
	
	@Override
	public void onEnable() {
		cmd = new RunicCommand("twitter")
				.setDescription("Shows the Twitter follow link")
				.setAliases(Arrays.asList("tweet"))
				.setUsage("/twitter")
				.setExecutor(this);
	}
	
	private static String twitter_handle;
	private String twitter_follow;
	@Override
	public void loadConfigurations() {
		String handle = Core.getConfig().getString("features.link.twitter-handle", "null");
		twitter_handle = handle.isEmpty() || handle.equalsIgnoreCase("null") ? null : handle.matches("^@?\\w{1,15}$") ? handle.startsWith("@") ? handle.substring(1) : handle : null;
		
		// (un)register command
		if(cmd.isRegistered()) {
			if(twitter_handle == null) cmd.unregister();
			else cmd.register();
		} else if(twitter_handle != null) cmd.register();
		
		twitter_follow = Core.getMessages().getString("features.link.twitter", "[\"\",{\"text\":\"\\u25ba\",\"color\":\"dark_gray\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"{LINK}\"}},{\"text\":\" \",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"{LINK}\"}},{\"text\":\"Click \",\"color\":\"gray\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"{LINK}\"}},{\"text\":\"here\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"{LINK}\"}},{\"text\":\" to follow us on Twitter.\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"{LINK}\"}}]");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(getHandle() != null) {
			if(sender instanceof Player) {
				RunicPlayer player = RunicPlayer.cast(sender);
				player.sendJSONMessage(Placeholder.parse(twitter_follow, player).set("{LINK}", getFollowLink()).getString());
			} else sender.sendMessage(ChatColor.GRAY+"Follow us on twitter: "+ChatColor.YELLOW+"@"+twitter_handle+ChatColor.GRAY+".");
		} else sender.sendMessage(ChatColor.RED+"Twitter account hasn't been set by the server administrator.");
		return true;
	}
	
	public static String getLink() {
		return twitter_handle != null ? "https://twitter.com/"+twitter_handle : null;
	}
	
	public static String getFollowLink() {
		return twitter_handle != null ? "https://twitter.com/intent/follow?region=follow_link&screen_name="+twitter_handle+"&tw_p=followbutton" : null;
	}
	
	public static String getHandle() {
		return twitter_handle;
	}

}
