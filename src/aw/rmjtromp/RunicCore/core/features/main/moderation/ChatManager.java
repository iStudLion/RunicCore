package aw.rmjtromp.RunicCore.core.features.main.moderation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class ChatManager extends RunicFeature implements CommandExecutor, TabCompleter {

	@Override
	public String getName() {
		return "ChatManager";
	}
	
	private enum PERMISSION {
		MUTE_CHAT("runic.chat.mute"),
		BYPASS_MUTE_CHAT("runic.chat.mute.bypass"),
		CLEAR_CHAT("runic.chat.clear"),
		BYPASS_CLEAR_CHAT("runic.chat.clear.bypass");
		
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
		registerCommand(new RunicCommand("chatmanager")
				.setDescription("RunicCore chat managing feature")
				.setAliases(Arrays.asList("chat"))
				.setUsage("/chatmanager")
				.setExecutor(this)
				.setTabCompleter(this));
		registerCommand(new RunicCommand("clearchat")
				.setDescription("RunicCore chat managing feature")
				.setAliases(Arrays.asList("cc", "emptychat"))
				.setUsage("/clearchat")
				.setExecutor(this)
				.setTabCompleter(this));
		registerCommand(new RunicCommand("unmutechat")
				.setDescription("RunicCore chat managing feature")
				.setUsage("/unmutechat")
				.setExecutor(this)
				.setTabCompleter(this));
		registerCommand(new RunicCommand("mutechat")
				.setDescription("RunicCore chat managing feature")
				.setUsage("/mutechat")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private boolean chat_is_muted;
	private String no_permission, not_enough_arguments, incorrect_usage, invalid_argument, chat_already_muted, chat_already_unmuted, chat_muted, chat_muted_by_player, chat_muted_by_annon, chat_unmuted_by_player, chat_unmuted_by_annon, chat_cleared_by_player, chat_cleared_by_annon;
	
	@Override
	public void loadConfigurations() {
		chat_is_muted = Core.getConfig().getBoolean("features.chat.muted", false);
		
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		not_enough_arguments = Core.getMessages().getMessage(MESSAGE.NOT_ENOUGH_ARGUMENTS);
		invalid_argument = Core.getMessages().getMessage(MESSAGE.INVALID_ARGUMENT);

		chat_muted = Core.getMessages().getString("chat.mute_message", "&cYou may not talk whilst the chat is muted.");
		chat_muted_by_player = Core.getMessages().getString("chat.muted_message", "&8(&6!&8) &7Chat was muted by &e{PLAYER}&7.");
		chat_muted_by_annon = Core.getMessages().getString("chat.muted_annonymous_message", "&8(&6!&8) &7Chat was muted.");
		chat_unmuted_by_player = Core.getMessages().getString("chat.unmuted_message", "&8(&6!&8) &7Chat was unmuted by &e{PLAYER}&7.");
		chat_unmuted_by_annon = Core.getMessages().getString("chat.unmuted_annonymous_message", "&8(&6!&8) &7Chat was unmuted.");
		chat_cleared_by_player = Core.getMessages().getString("chat.cleared_message", "&8(&6!&8) &7Chat was cleared by &e{PLAYER}&7.");
		chat_cleared_by_annon = Core.getMessages().getString("chat.cleared_annonymous_message", "&8(&6!&8) &7Chat was cleared.");
		chat_already_muted = Core.getMessages().getString("chat.already_muted", "&cChat is already muted.");
		chat_already_unmuted = Core.getMessages().getString("chat.already_unmuted", "&cChat is already unmuted.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equals("mutechat")) {
			if(sender.hasPermission(PERMISSION.MUTE_CHAT.toString())) {
				if(args.length == 0) {
					if(!chat_is_muted) {
						if(sender instanceof Player && !RunicPlayer.cast(sender).isVanished()) Bukkit.broadcastMessage(Placeholder.parse(chat_muted_by_player, sender).set("{PLAYER}", sender.getName()).getString());
						else Bukkit.broadcastMessage(Placeholder.parse(chat_muted_by_annon).getString());
						chat_is_muted = true;
						Core.getConfig().set("features.chat.muted", true);
					} else {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(chat_already_muted, sender).getString()));
					}
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(invalid_argument).set("{COMMAND}", label.toLowerCase()+" help").getString()));
				}
			} else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(no_permission).getString()));
			}
		} else if(command.getName().equals("unmutechat")) {
			if(sender.hasPermission(PERMISSION.MUTE_CHAT.toString())) {
				if(args.length == 0) {
					if(chat_is_muted) {
						if(sender instanceof Player && !RunicPlayer.cast(sender).isVanished()) Bukkit.broadcastMessage(Placeholder.parse(chat_unmuted_by_player).set("{PLAYER}", sender.getName()).getString());
						else Bukkit.broadcastMessage(Placeholder.parse(chat_unmuted_by_annon).getString());
						chat_is_muted = false;
						Core.getConfig().set("features.chat.muted", false);
					} else {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(chat_already_unmuted).getString()));
					}
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(invalid_argument).set("{COMMAND}", label.toLowerCase()+" help").getString()));
				}
			} else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(no_permission).getString()));
			}
		} else if(command.getName().equals("clearchat")) {
			if(sender.hasPermission(PERMISSION.CLEAR_CHAT.toString())) {
				if(args.length == 0) {
					clearChat();
					if(sender instanceof Player && !RunicPlayer.cast(sender).isVanished()) Bukkit.broadcastMessage(Placeholder.parse(chat_cleared_by_player).set("{PLAYER}", sender.getName()).getString());
					else Bukkit.broadcastMessage(Placeholder.parse(chat_cleared_by_annon).getString());
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(invalid_argument).set("{COMMAND}", label.toLowerCase()+" help").getString()));
				}
			} else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(no_permission).getString()));
			}
		} else {
			if(args.length == 0) {
				if(sender.hasPermission(PERMISSION.CLEAR_CHAT.toString()) || sender.hasPermission(PERMISSION.MUTE_CHAT.toString())) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(not_enough_arguments).set("{COMMAND}", command.getName().toLowerCase()+" help").getString()));
				else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(no_permission).getString()));
			} else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("help")) {
					if(sender.hasPermission(PERMISSION.CLEAR_CHAT.toString()) || sender.hasPermission(PERMISSION.MUTE_CHAT.toString() )) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eChatManager &7(RunicCore)"
							+ "\n&e/"+command.getName().toLowerCase()+" clear &6- &7Clears chat"
							+ "\n&e/"+command.getName().toLowerCase()+" mute &6- &7Mutes chat"
							+ "\n&e/"+command.getName().toLowerCase()+" unmute &6- &7Unmutes chat"
							+ "\n&e/"+command.getName().toLowerCase()+" help &6- &7Show this list of command"));
					} else sender.sendMessage(Placeholder.parse(no_permission).getString());
				} else if(args[0].equalsIgnoreCase("clear")) {
					if(sender.hasPermission(PERMISSION.CLEAR_CHAT.toString())) {
						clearChat();
						if(sender instanceof Player && !RunicPlayer.cast(sender).isVanished()) Bukkit.broadcastMessage(Placeholder.parse(chat_cleared_by_player).set("{PLAYER}", sender.getName()).getString());
						else Bukkit.broadcastMessage(Placeholder.parse(chat_cleared_by_annon).getString());
					} else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(no_permission).getString()));
				} else if(args[0].equalsIgnoreCase("mute")) {
					if(sender.hasPermission(PERMISSION.MUTE_CHAT.toString())) {
						if(!chat_is_muted) {
							if(sender instanceof Player && !RunicPlayer.cast(sender).isVanished()) Bukkit.broadcastMessage(Placeholder.parse(chat_muted_by_player).set("{PLAYER}", sender.getName()).getString());
							else Bukkit.broadcastMessage(Placeholder.parse(chat_muted_by_annon).getString());
							chat_is_muted = true;
							Core.getConfig().set("features.chat.muted", true);							
						} else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(chat_already_muted).getString()));
					} else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(no_permission).getString()));
				} else if(args[0].equalsIgnoreCase("unmute")) {
					if(sender.hasPermission(PERMISSION.MUTE_CHAT.toString())) {
						if(chat_is_muted) {
							if(sender instanceof Player && !RunicPlayer.cast(sender).isVanished()) Bukkit.broadcastMessage(Placeholder.parse(chat_unmuted_by_player).set("{PLAYER}", sender.getName()).getString());
							else Bukkit.broadcastMessage(Placeholder.parse(chat_unmuted_by_annon).getString());
							chat_is_muted = false;
							Core.getConfig().set("features.chat.muted", false);
						} else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(chat_already_unmuted).getString()));
					} else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(no_permission).getString()));
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()+" help").getString()));
				}
			} else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Placeholder.parse(incorrect_usage).set("{COMMAND}", label.toLowerCase()+" help").getString()));
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(args.length == 1) {
			if(args[0].isEmpty()) {
				if(command.getName().equalsIgnoreCase("chatmanager")) {
					if(sender.hasPermission(PERMISSION.CLEAR_CHAT.toString())) suggestion.add("clear");
					if(sender.hasPermission(PERMISSION.MUTE_CHAT.toString())) {
						suggestion.add("mute");
						suggestion.add("unmute");
					}
					suggestion.add("help");
				}
			} else {
				if(command.getName().equalsIgnoreCase("chatmanager")) {
					if(sender.hasPermission(PERMISSION.CLEAR_CHAT.toString()) && "clear".startsWith(args[0].toLowerCase())) suggestion.add("clear");
					if(sender.hasPermission(PERMISSION.MUTE_CHAT.toString()) && ( "mute".startsWith(args[0].toLowerCase()) ||  "unmute".startsWith(args[0].toLowerCase()) )) {
						suggestion.add("mute");
						suggestion.add("unmute");
					}
					if("help".startsWith(args[0].toLowerCase())) suggestion.add("help");
				}
			}
		}
		
		Collections.sort(suggestion);
		return suggestion;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		RunicPlayer player = RunicPlayer.cast(e.getPlayer());
		if(chat_is_muted && !player.isOp() && !player.isVanished() && !player.hasPermission(PERMISSION.BYPASS_MUTE_CHAT.toString())) {
			e.setCancelled(true);
			player.sendMessage(Placeholder.parse(chat_muted , player).getString());
		} else {
			if(AntiAdvertising.containsAdvertisement(player, e.getMessage())) {
				// Automatically flags player for advertising
				// but will not flag staff or opped players
				e.setCancelled(true);
			} else e.setMessage(AntiSwear.steralize(e.getMessage()));
		}
	}
	
	public static void clearChat() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.hasPermission(PERMISSION.BYPASS_CLEAR_CHAT.toString())) continue;
			// sends message 200 times
			for(int i = 0; i < 200; i++) {
				// sends a message with 5 rows of empty lines
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7\n&7\n&7\n&7\n&7"));
			}
			// total empty lines = 1000
		}
	}

}
