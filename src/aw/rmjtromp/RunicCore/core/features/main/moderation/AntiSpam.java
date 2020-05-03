package aw.rmjtromp.RunicCore.core.features.main.moderation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class AntiSpam extends RunicFeature {

	@Override
	public String getName() {
		return "AntiSpam";
	}
	
	private HashMap<RunicPlayer, Long> chat_cooldown = new HashMap<RunicPlayer, Long>();
	private HashMap<RunicPlayer, HashMap<String, Long>> command_cooldown = new HashMap<RunicPlayer, HashMap<String, Long>>();
	
	private String no_spamming, no_spamming_commands;
	
	@Override
	public void loadConfigurations() {
		no_spamming = Core.getMessages().getString("chat.anti-spam.chat", "&7Please don't spam in chat.");
		no_spamming_commands = Core.getMessages().getString("chat.anti-spam.command", "&7Please don't spam commands.");

		if(chat_cooldown == null) chat_cooldown = new HashMap<RunicPlayer, Long>();
		if(chat_cooldown.size() > 0) {
			for(RunicPlayer player : chat_cooldown.keySet()) {
				long now = System.currentTimeMillis();
				long last_time = chat_cooldown.get(player);
				if(now - last_time >= 500) chat_cooldown.remove(player);
			}
		}
		
		if(command_cooldown == null) command_cooldown = new HashMap<RunicPlayer, HashMap<String, Long>>();
		if(command_cooldown.size() > 0) {
			for(RunicPlayer player : command_cooldown.keySet()) {
				long now = System.currentTimeMillis();
				
				HashMap<String, Long> commands = command_cooldown.get(player);
				if(commands.size() > 0) {
					for(String command : commands.keySet()) {
						long last_time = commands.get(command);
						if(now - last_time >= 500) commands.remove(command);
					}
				}
				
				if(commands.size() > 0) command_cooldown.replace(player, commands);
				else command_cooldown.remove(player);
			}
		}
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		String command = e.getMessage().split(" ")[0].toLowerCase().substring(1);
		long now = System.currentTimeMillis();
		RunicPlayer player = RunicPlayer.cast(e.getPlayer());
		
		if(command_cooldown.containsKey(player)) {
			HashMap<String, Long> commands = command_cooldown.get(player);
			if(commands.containsKey(command)) {
				long last_time = commands.get(command);
				if(now - last_time < 500) {
					e.setCancelled(true);
					player.sendMessage(no_spamming_commands);
				}
			} else {
				commands.put(command, now);
				command_cooldown.replace(player, commands);
			}
			
			// cannot use #remove() inside for() loop
			List<String> cmdtoremove = new ArrayList<String>();
			
			for(String cmd : commands.keySet()) {
				long last_time = commands.get(cmd);
				if(now - last_time > 1000) cmdtoremove.add(cmd);
			}
			
			for(String cmd : cmdtoremove) commands.remove(cmd);
		} else {
			HashMap<String, Long> commands = new HashMap<String, Long>();
			commands.put(command, now);
			command_cooldown.put(player, commands);
		}
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		RunicPlayer player = RunicPlayer.cast(e.getPlayer());
		long now = System.currentTimeMillis();
		if(chat_cooldown.containsKey(player)) {
			long last_time = chat_cooldown.get(player);
			
			if(now - last_time < 500) {
				e.setCancelled(true);
				player.sendMessage(Placeholder.parse(no_spamming, player).getString());
			}
			chat_cooldown.replace(player, now);
		} else chat_cooldown.put(player, now);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(chat_cooldown.containsKey(e.getPlayer())) chat_cooldown.remove(e.getPlayer());
		if(command_cooldown.containsKey(e.getPlayer())) command_cooldown.remove(e.getPlayer());
	}

}
