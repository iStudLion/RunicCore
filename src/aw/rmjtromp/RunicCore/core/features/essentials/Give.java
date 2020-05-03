package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.PlayerSelector;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Give extends RunicFeature implements CommandExecutor, TabCompleter {

	private enum PERMISSION {
		GIVE_SELF("runic.give"),
		GIVE_OTHERS("runic.give.others");
		
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
		return "Give";
	}
	
	@Override
	public void onEnable() {
		registerCommand(new RunicCommand("give")
				.setDescription("Give a player an item")
				.setPermission(PERMISSION.GIVE_SELF.toString())
				.setUsage("/give <player> <item> [amount]")
				.setExecutor(this)
				.setTabCompleter(this));
				
		registerCommand(new RunicCommand("item")
				.setDescription("Spawn an item")
				.setAliases(Arrays.asList("i"))
				.setPermission(PERMISSION.GIVE_SELF.toString())
				.setUsage("/i <item> [amount]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String empty_hand, this_instance_not_player, invalid_argument, target_not_found, invalid_amount, amount_too_small, invalid_player_or_item, sender_not_a_player, no_permission, item_added, item_added_target, no_space, no_space_target, unknown_item;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		sender_not_a_player = Core.getMessages().getMessage(MESSAGE.SENDER_NOT_A_PLAYER);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		invalid_argument = Core.getMessages().getMessage(MESSAGE.INVALID_ARGUMENT);

		item_added = Core.getMessages().getString("features.give.give", "&e{AMOUNT}&7x &e{ITEM} &7was added to your inventory.");
		item_added_target = Core.getMessages().getString("features.give.give-target", "&e{AMOUNT}&7x &e{ITEM} &7was added to &e{TARGET}&7's inventory.");
		no_space = Core.getMessages().getString("features.give.no-space", "&cYou don't have enough open inventory slots.");
		no_space_target = Core.getMessages().getString("features.give.no-space-target", "&c{TARGET} doesn't have enough open inventory slots.");
		unknown_item = Core.getMessages().getString("features.give.unknown-item", "&cInvalid item '&7{ITEM}&c'.");
		empty_hand = Core.getMessages().getString("features.give.empty-hand", "&cYou're not holding anything in your hand.");
		this_instance_not_player = Core.getMessages().getString("features.give.this-instance-not-player", "&cYou must be a player to use 'this' instance.");
		invalid_amount = Core.getMessages().getString("features.give.invalid-amount", "&cAmount must be numeric.");
		amount_too_small = Core.getMessages().getString("features.give.amount-too-small", "&cItem amount must be bigger than 0.");
		invalid_player_or_item = Core.getMessages().getString("features.give.invalid-player-or-item", "&cNo player or item with such name was found.");
	}
	
	/*
	 * TODO check if item is actually obtainable
	 * TODO create a new method to check how many slots for new items they have (also accounting for how much they currently have and if they can have more)
	 */

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission(PERMISSION.GIVE_SELF.toString())) {
			if(command.getName().equalsIgnoreCase("give")) {
				if(args.length == 1) {
					// /give <item>
					if(sender instanceof Player) {
						RunicPlayer player = RunicPlayer.cast(sender);
						ItemStack item = plugin.getLibrary().getItem(args[0]);
						if(item != null) {
							if(player.hasEmptySlot(1)) {
								player.getInventory().addItem(item);
								player.sendMessage(Placeholder.parse(item_added, player).set("{AMOUNT}", 1).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).getString());
							} else player.sendMessage(Placeholder.parse(no_space, player).getString());
						} else player.sendMessage(Placeholder.parse(unknown_item, player).set("{ITEM}", args[0]).getString()); 
					} else sender.sendMessage(Placeholder.parse(sender_not_a_player).getString());
				} else if(args.length == 2) {
					// /give [player] <item> [amount]
					
					if(sender.hasPermission(PERMISSION.GIVE_SELF.toString())) {
						List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
						if(targets.size() > 0) {
							ItemStack item;
							if(args[1].equalsIgnoreCase("this")) {
								if(sender instanceof Player) {
									RunicPlayer player = RunicPlayer.cast(sender);
									if(player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) item = player.getItemInHand().clone();
									else {
										player.sendMessage(Placeholder.parse(empty_hand, player).getString());
										return true;
									}
								} else {
									sender.sendMessage(Placeholder.parse(this_instance_not_player, sender).getString());
									return true;
								}
							} else item = library.getItem(args[1]);
							
							if(item != null && !item.getType().equals(Material.AIR)) {
								int amount = 1;
								amount = amount > 36*item.getMaxStackSize() ? 36*item.getMaxStackSize() : amount;
								/* counts how many open slots is required for said amount (depends on max stack size) */
								int requiredslots = ((amount - (amount%item.getMaxStackSize())) / item.getMaxStackSize()) + (amount%item.getMaxStackSize()!=0 ? 1 : 0);
								
								if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
									// sender is targeting self
									RunicPlayer target = RunicPlayer.cast(sender);
									if(target.hasEmptySlot(requiredslots)) {
										int leftToGive = amount;
										while(leftToGive > 0) {
											ItemStack i = item;
											int a = leftToGive > i.getMaxStackSize() ? i.getMaxStackSize() : leftToGive;
											i.setAmount(a);
											leftToGive -= a;
											target.getInventory().addItem(i);
										} 
										target.updateInventory();
										target.sendMessage(Placeholder.parse(item_added, target).set("{AMOUNT}", amount).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).getString());
									} else target.sendMessage(Placeholder.parse(no_space, target).getString());
								} else {
									// sender is targeting others
									if(sender.hasPermission(PERMISSION.GIVE_OTHERS.toString())) {
										for(RunicPlayer target : targets) {
											if(target.hasEmptySlot(requiredslots)) {
												int leftToGive = amount;
												while(leftToGive > 0) {
													ItemStack i = item;
													int a = leftToGive > i.getMaxStackSize() ? i.getMaxStackSize() : leftToGive;
													i.setAmount(a);
													leftToGive -= a;
													target.getInventory().addItem(i);
												} 
												target.updateInventory();
												target.sendMessage(Placeholder.parse(item_added, target).set("{AMOUNT}", amount).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).getString());
												if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(item_added_target, sender).set("{AMOUNT}", 1).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).set("{TARGET}", target.getName()).getString());
											} else sender.sendMessage(Placeholder.parse(no_space_target, sender).set("{TARGET}", target.getName()).getString());
											
										}
									} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
								}
							} else sender.sendMessage(Placeholder.parse(unknown_item, sender).set("{ITEM}", args[1]).getString());
						} else if(sender instanceof Player && library.getItem(args[0]) != null) {
							RunicPlayer target = RunicPlayer.cast(sender);
							ItemStack item = library.getItem(args[0]);
							if(NumberUtils.isNumber(args[1])) {
								int amount = Integer.parseInt(args[1]);
								if(amount > 0) {
									amount = amount > 36*item.getMaxStackSize() ? 36*item.getMaxStackSize() : amount;
									/* counts how many open slots is required for said amount (depends on max stack size) */
									int requiredslots = ((amount - (amount%item.getMaxStackSize())) / item.getMaxStackSize()) + (amount%item.getMaxStackSize()!=0 ? 1 : 0);
									if(target.hasEmptySlot(requiredslots)) {
										int leftToGive = amount;
										while(leftToGive > 0) {
											ItemStack i = item;
											int a = leftToGive > i.getMaxStackSize() ? i.getMaxStackSize() : leftToGive;
											i.setAmount(a);
											leftToGive -= a;
											target.getInventory().addItem(i);
										}
										target.sendMessage(Placeholder.parse(item_added, target).set("{AMOUNT}", amount).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).getString());
									} else target.sendMessage(Placeholder.parse(no_space, target).getString()); 
								} else target.sendMessage(Placeholder.parse(amount_too_small, target).getString());
							} else target.sendMessage(Placeholder.parse(invalid_amount, target).getString());
						} else {
							if(sender instanceof Player) {
								if(sender.hasPermission(PERMISSION.GIVE_OTHERS.toString())) sender.sendMessage(Placeholder.parse(invalid_player_or_item, sender).getString());
								else sender.sendMessage(Placeholder.parse(unknown_item, sender).set("{ITEM}", args[0]).getString());
							} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
						}
					} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
				} else if(args.length == 3) {
					// /give <player> <item> <amount>
					if(sender.hasPermission(PERMISSION.GIVE_SELF.toString())) {
						List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
						if(targets.size() > 0) {
							ItemStack item;
							if(args[1].equalsIgnoreCase("this")) {
								if(sender instanceof Player) {
									RunicPlayer player = RunicPlayer.cast(sender);
									if(player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) item = player.getItemInHand().clone();
									else {
										player.sendMessage(Placeholder.parse(empty_hand, player).getString());
										return true;
									}
								} else {
									sender.sendMessage(Placeholder.parse(this_instance_not_player, sender).getString());
									return true;
								}
							} else item = library.getItem(args[1]);
							
							if(item != null && !item.getType().equals(Material.AIR)) {
								if(NumberUtils.isNumber(args[2])) {
									int amount = Integer.parseInt(args[2]);
									if(amount > 0) {
										amount = amount > 36*item.getMaxStackSize() ? 36*item.getMaxStackSize() : amount;
										/* counts how many open slots is required for said amount (depends on max stack size) */
										int requiredslots = ((amount - (amount%item.getMaxStackSize())) / item.getMaxStackSize()) + (amount%item.getMaxStackSize()!=0 ? 1 : 0);
										
										if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
											// sender is targeting self
											RunicPlayer target = RunicPlayer.cast(sender);
											if(target.hasEmptySlot(requiredslots)) {
												int leftToGive = amount;
												while(leftToGive > 0) {
													ItemStack i = item;
													int a = leftToGive > i.getMaxStackSize() ? i.getMaxStackSize() : leftToGive;
													i.setAmount(a);
													leftToGive -= a;
													target.getInventory().addItem(i);
												}
												target.sendMessage(Placeholder.parse(item_added, target).set("{AMOUNT}", amount).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).getString());
											} else sender.sendMessage(Placeholder.parse(no_space, sender).getString());
										} else {
											// sender is targeting others
											if(sender.hasPermission(PERMISSION.GIVE_OTHERS.toString())) {
												for(RunicPlayer target : targets) {
													if(target.hasEmptySlot(requiredslots)) {
														int leftToGive = amount;
														while(leftToGive > 0) {
															ItemStack i = item;
															int a = leftToGive > i.getMaxStackSize() ? i.getMaxStackSize() : leftToGive;
															i.setAmount(a);
															leftToGive -= a;
															target.getInventory().addItem(i);
														}
														target.sendMessage(Placeholder.parse(item_added, target).set("{AMOUNT}", amount).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).getString());
														if(!target.equals(sender)) sender.sendMessage(Placeholder.parse(item_added_target, sender).set("{TARGET}", target.getName()).set("{AMOUNT}", amount).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).getString());
													} else sender.sendMessage(Placeholder.parse(no_space_target, sender).set("{TARGET}", target.getName()).getString());
												}
											} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
										} 
									} else sender.sendMessage(Placeholder.parse(amount_too_small, sender).getString());
								} else sender.sendMessage(Placeholder.parse(invalid_amount, sender).getString());
							} else sender.sendMessage(Placeholder.parse(unknown_item, sender).set("{ITEM}", args[1]).getString());
						} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
					} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
					
					/*RunicPlayer target = null;
					if(args[0].equalsIgnoreCase(sender.getName())) {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
							return true;
						} else target = RunicPlayer.cast(sender);
					}
					
					if(target == null) target = RunicPlayer.cast(Bukkit.getPlayerExact(args[0]));
					if(target != null) {
						if(target.isOnline()) {
							ItemStack item = plugin.getLibrary().getItem(args[1]);
							if(item != null) {
								if(args[2].matches("^-?\\d+$")) {
									int amount = Integer.parseInt(args[2]);
									if(amount > 0) {
										amount = amount > 36*item.getMaxStackSize() ? 36*item.getMaxStackSize() : amount;
										// counts how many open slots is required for said amount (depends on max stack size) 
										int requiredslots = ((amount - (amount%item.getMaxStackSize())) / item.getMaxStackSize()) + (amount%item.getMaxStackSize()!=0 ? 1 : 0);
										if(target.hasEmptySlot(requiredslots)) {
											int leftToGive = amount;
											while(leftToGive > 0) {
												ItemStack i = item;
												int a = leftToGive > i.getMaxStackSize() ? i.getMaxStackSize() : leftToGive;
												i.setAmount(a);
												leftToGive -= a;
												target.getInventory().addItem(i);
											}
											target.sendMessage(Placeholder.parse(item_added, target).set("{AMOUNT}", amount).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).getString());
											if(!target.getName().equals(sender.getName())) sender.sendMessage(Placeholder.parse(item_added_target, sender).set("{AMOUNT}", 1).set("{ITEM}", plugin.getLibrary().getFriendlyName(item)).set("{TARGET}", target.getName()).getString());
										} else {
											if(sender.getName().equals(target.getName())) sender.sendMessage(Placeholder.parse(no_space, sender).getString());
											else sender.sendMessage(Placeholder.parse(no_space_target, sender).set("{TARGET}", target.getName()).getString()); 
										}
									} else sender.sendMessage(Placeholder.parse(amount_too_small, sender).getString());
								} else sender.sendMessage(Placeholder.parse(invalid_amount, sender).getString());
							} else sender.sendMessage(Placeholder.parse(unknown_item, sender).set("{ITEM}", args[1]).getString());
						} else sender.sendMessage(Placeholder.parse(target_not_online, sender).set("{TARGET}", target.getName()).getString());
					} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());*/
				} else {
					if(sender.hasPermission(PERMISSION.GIVE_OTHERS.toString())) {
						if(sender instanceof Player) sender.sendMessage(Placeholder.parse(invalid_argument, sender).set("{COMMAND}", label.toLowerCase()+" [player] <item> [amount]").getString());
						else sender.sendMessage(Placeholder.parse(invalid_argument, sender).set("{COMMAND}", label.toLowerCase()+" <player> <item> [amount]").getString());
					} else sender.sendMessage(Placeholder.parse(invalid_argument, sender).set("{COMMAND}", label.toLowerCase()+" <item> [amount]").getString());
				}
			} else if (command.getName().equalsIgnoreCase("item")) {
				if(sender instanceof Player) {
					RunicPlayer player = RunicPlayer.cast(sender);
					if(args.length == 1) {
						ItemStack item = plugin.getLibrary().getItem(args[0]);
						if(item != null) {
							if(player.hasEmptySlot(1)) {
								player.getInventory().addItem(item);
								player.sendMessage(Placeholder.parse(item_added, player).set("{AMOUNT}", 1).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).getString());
							} else player.sendMessage(Placeholder.parse(no_space, player).getString());
						} else player.sendMessage(Placeholder.parse(unknown_item, player).set("{ITEM}", args[0]).getString());
					} else if(args.length == 2) {
						ItemStack item = plugin.getLibrary().getItem(args[0]);
						if(item != null) {
							if(NumberUtils.isNumber(args[1])) {
								int amount = Integer.parseInt(args[1]);
								if(amount > 0) {
									amount = amount > 36*item.getMaxStackSize() ? 36*item.getMaxStackSize() : amount;
									/* counts how many open slots is required for said amount (depends on max stack size) */
									int requiredslots = ((amount - (amount%item.getMaxStackSize())) / item.getMaxStackSize()) + (amount%item.getMaxStackSize()!=0 ? 1 : 0);
									if(player.hasEmptySlot(requiredslots)) {
										int leftToGive = amount;
										while(leftToGive > 0) {
											ItemStack i = item;
											int a = leftToGive > i.getMaxStackSize() ? i.getMaxStackSize() : leftToGive;
											i.setAmount(a);
											leftToGive -= a;
											player.getInventory().addItem(i);
										}
										player.sendMessage(Placeholder.parse(item_added, player).set("{AMOUNT}", amount).set("{ITEM}", plugin.getLibrary().getFriendlyName(item).toLowerCase()).getString());
									} else sender.sendMessage(Placeholder.parse(no_space, sender).getString()); 
								} else sender.sendMessage(Placeholder.parse(amount_too_small, sender).getString());
							} else player.sendMessage(Placeholder.parse(amount_too_small, player).getString());
						} else player.sendMessage(Placeholder.parse(unknown_item, player).set("{ITEM}", args[0]).getString());
					} else sender.sendMessage(Placeholder.parse(invalid_argument, sender).set("{COMMAND}", label.toLowerCase()+" <item> [amount]").getString());
				} else sender.sendMessage(Placeholder.parse(sender_not_a_player, sender).getString());
			} else return false;
		} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(command.getName().equalsIgnoreCase("give")) {
			if(sender.hasPermission(PERMISSION.GIVE_SELF.toString())) {
				if(args.length == 1) {
					if(sender.hasPermission(PERMISSION.GIVE_OTHERS.toString())) {
						for(String player : PlayerSelector.suggest(args[0], sender.isOp())) {
							suggestion.add(player);
						}
					}
					
					if(sender instanceof Player) {
						// only suggest sender's name if sender is a player
						// only suggest items if sender is player
						if(args[0].isEmpty()) {
							if(!suggestion.contains(sender.getName())) suggestion.add(sender.getName());
							for(String item : library.getItemNameList()) suggestion.add(item);
						} else {
							if(!suggestion.contains(sender.getName()) && sender.getName().toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(sender.getName());
							for(String item : library.getItemNameList()) {
								if(item.toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(item);
							}
						}
					}
				} else if(args.length == 2) {
					if(PlayerSelector.select(args[0], sender.isOp()).size() > 0) {
						// first argument is a player, now suggest an item
						if(args[1].isEmpty()) {
							for(String item : library.getItemNameList()) suggestion.add(item);
						} else {
							for(String item : library.getItemNameList()) {
								if(item.toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(item);
							}
						}
					} else if(library.getItemNameList().contains(args[0].toLowerCase())) {
						// first argument is an item, now suggest an amount
						ItemStack item = library.getItem(args[0]);
						int max = item != null ? item.getType().getMaxStackSize() : 64;
						
						suggestion.add("1");
						int x = 1;
						// keep suggesting all numbers to the power of two up until max amount or 64
						while(Math.pow(2, x) <= max) {
							suggestion.add(Math.round(Math.pow(2, x))+"");
							x++;
						}
					}
				} else if(args.length == 3) {
					// judging by the argument length, now shall i suggest an amount
					ItemStack item = library.getItem(args[2]);
					int max = item != null ? item.getType().getMaxStackSize() : 64;
					
					suggestion.add("1");
					int x = 1;
					// keep suggesting all numbers to the power of two up until max amount or 64
					while(Math.pow(2, x) <= max) {
						suggestion.add(Math.round(Math.pow(2, x))+"");
						x++;
					}
				}
			}
		} else if(command.getName().equalsIgnoreCase("item")) {
			if(sender.hasPermission(PERMISSION.GIVE_SELF.toString())) {
				if(sender instanceof Player) {
					// only return suggestion is sender is a player
					if(args.length == 1) {
						if(args[0].isEmpty()) {
							if(!suggestion.contains(sender.getName())) suggestion.add(sender.getName());
							for(String item : library.getItemNameList()) suggestion.add(item);
						} else {
							if(!suggestion.contains(sender.getName()) && sender.getName().toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(sender.getName());
							for(String item : library.getItemNameList()) {
								if(item.toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(item);
							}
						}
					} else if(args.length == 2) {
						// first argument is supposed to be an item, now suggest an amount
						ItemStack item = library.getItem(args[0]);
						int max = item != null ? item.getType().getMaxStackSize() : 64;
						
						suggestion.add("1");
						int x = 1;
						// keep suggesting all numbers to the power of two up until max amount or 64
						while(Math.pow(2, x) <= max) {
							suggestion.add(Math.round(Math.pow(2, x))+"");
							x++;
						}
					}
				}
			}
		}
	
		return suggestion;
	}

}
