package aw.rmjtromp.RunicCore.core.features.main.moderation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.features.main.moderation.Flagger.FlagReason;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;

public final class AntiAdvertising extends RunicFeature {

	@Override
	public String getName() {
		return "AntiAdvertising";
	}
	
	private enum PERMISSION {
		ANTIADVERTISING_BYPASS("runic.anti-advertising.bypass");
		
		private String node = "";
		
		PERMISSION(String node) {
			this.node = node;
		}
		
		@Override
		public String toString() {
			return node;
		}
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		if(!e.isCancelled() && containsAdvertisement(e.getPlayer(), e.getMessage())) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if(!e.isCancelled()) {
			if(containsAdvertisement(e.getPlayer(), String.join(" ", e.getLines()))) {
				e.setCancelled(true);
				return;
			}
		}
	}
	
	public static boolean containsAdvertisement(String message) {
		return (containsDomainIP(message) || containsNumberedIP(message));
	}
	
	public static boolean containsAdvertisement(Player player, String message) {
		return containsAdvertisement(RunicPlayer.cast(player), message);
	}
	
	public static boolean containsAdvertisement(RunicPlayer player, String message) {
		if(containsDomainIP(message) || containsNumberedIP(message)) {
			if(!player.hasPermission(PERMISSION.ANTIADVERTISING_BYPASS.toString())) {
				if(!Flagger.flagPlayer(player, FlagReason.Advertising, message)) player.sendMessage("&4Advertising other services is not allowed.");
			} else player.sendMessage("&4Advertising other services is not allowed.");
			return true;
		}
		return false;
	}
	
	private static boolean containsDomainIP(String message) {
		// group[1] = matched ip/url, group[2] matched full domain, group[3] matched domain, group[4] matched TLD, group[5] matched port
		String DomainIPPattern = "(((?:https?:\\/\\/)?(?:(?:[a-z0-9\\-]{1,63}\\.)+?)?([a-z0-9\\-]{1,63})(?:\\.([a-z]{2,18}))(?![\\.0-9a-z\\-])(?![\\.0-9a-z\\-]))(?:\\:([0-9]{5}))?)";
		Pattern pattern = Pattern.compile(DomainIPPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(message);
		
		while(matcher.find()) {
		    if(matcher.group(3).equalsIgnoreCase("runicsky") && matcher.group(4).equalsIgnoreCase("com")) continue;
		    if(Core.tlds.contains(matcher.group(4))) return true;
		}
		return false;
	}
	
	private static boolean containsNumberedIP(String message) {
		// group[1] = matched ip/url, group[2] matched ip, group[3] matched port
		String NumberedIPPattern = "((?:https?:\\/\\/)?((?:(?:[0-9]{1,63}\\.){1,7}){2,}?(?:[0-9]{1,63}))(?:(?:\\:([0-9]{5}))?(?![0-9]+)))";
		Pattern pattern = Pattern.compile(NumberedIPPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(message);
		return matcher.matches();
	}

}
