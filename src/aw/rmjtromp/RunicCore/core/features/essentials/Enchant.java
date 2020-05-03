package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.PlayerSelector;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Enchant extends RunicFeature implements CommandExecutor, TabCompleter {

	private enum PERMISSION {
		ENCHANT_SELF("runic.enchant"),
		ENCHANT_OTHERS("runic.enchant.others"),
		ENCHANT_UNSAFE("runic.enchant.unsafe");
		
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
		return "Enchant";
	}

	@Override
	public void onEnable() {
		registerCommand(new RunicCommand("enchant")
				.setDescription("Enchants the item the user is holding.")
				.setAliases(Arrays.asList("enchantment"))
				.setPermission(PERMISSION.ENCHANT_SELF.toString())
				.setUsage("/afk [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}
	
	private String target_not_found, incorrect_usage, no_permission, enchant_enchantment_null, enchant_invalid_level, enchant_level_too_big_or_small, enchant_enchant, enchant_enchant_target, enchant_no_item, enchant_no_item_target, enchant_no_perm_unsafe_enchantment, not_enough_arguments;
	@Override
	public void loadConfigurations() {
		target_not_found = Core.getMessages().getMessage(MESSAGE.TARGET_NOT_FOUND);
		incorrect_usage = Core.getMessages().getMessage(MESSAGE.INCORRECT_USAGE);
		no_permission = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);
		not_enough_arguments = Core.getMessages().getMessage(MESSAGE.NOT_ENOUGH_ARGUMENTS);

		enchant_enchantment_null = Core.getMessages().getString("features.enchant.enchantment-doesnt-exist", "&cUnknown enchantment \"&7{ENCHANTMENT}&c\".");
		enchant_invalid_level = Core.getMessages().getString("features.enchant.invalid-level", "&cLevel must be numeric.");
		enchant_level_too_big_or_small = Core.getMessages().getString("features.enchant.level-too-big-or-small", "&cEnchantment level provided is either too big or too small.");
		enchant_enchant = Core.getMessages().getString("features.enchant.enchant", "&7The enchantment &e{ENCHANTMENT} &7has been applied to item in your hand.");
		enchant_enchant_target = Core.getMessages().getString("features.enchant.enchant-target", "&7The enchantment &e{ENCHANTMENT} &7has been applied to item in &e{TARGET}&7's hand.");
		enchant_no_item = Core.getMessages().getString("features.enchant.no-item", "&cYou're not holding anything in your hand.");
		enchant_no_item_target = Core.getMessages().getString("features.enchant.no-item-target", "&c{TARGET}'s not holding anything in their hand.");
		enchant_no_perm_unsafe_enchantment = Core.getMessages().getString("features.enchant.no-perm-unsafe-enchantment", "&cYou don't have enough permission to add unsafe enchantments.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length < 2) {
			if(sender instanceof Player) {
				if(sender.hasPermission(PERMISSION.ENCHANT_OTHERS.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments).set("{COMMAND}", label.toLowerCase()+" [player] <enchantment> <level>").getString());
				else if(sender.hasPermission(PERMISSION.ENCHANT_SELF.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments).set("{COMMAND}", label.toLowerCase()+" <enchantment> <level>").getString());
				else sender.sendMessage(Placeholder.parse(no_permission).getString());
			} else {
				if(sender.hasPermission(PERMISSION.ENCHANT_OTHERS.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments).set("{COMMAND}", label.toLowerCase()+" <player> <enchantment> <level>").getString());
				else sender.sendMessage(Placeholder.parse(no_permission).getString());
			}
		} else if(args.length == 2) {
			// /enchant <enchantment> <level>
			if(sender instanceof Player) {
				if(sender.hasPermission(PERMISSION.ENCHANT_SELF.toString())) {
					RunicPlayer player = RunicPlayer.cast(sender);
					Enchantment enchantment = library.getEnchantment(args[0]);
					if(enchantment != null) {
						if(args[1].matches("^[0-9]{1,5}$")) {
							int level = Integer.parseInt(args[1]);
							level = level < 0 ? 0 : level > 32767 ? 32767 : level;
							if(player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
								if(player.hasPermission(PERMISSION.ENCHANT_UNSAFE.toString())) {
									player.getItemInHand().addUnsafeEnchantment(enchantment, level);
									player.sendMessage(Placeholder.parse(enchant_enchant, player).set("{ENCHANTMENT}", library.getFriendlyName(enchantment).toLowerCase()).getString());
								} else {
									if(enchantment.canEnchantItem(player.getItemInHand())) {
										player.getItemInHand().addEnchantment(enchantment, level);
										player.sendMessage(Placeholder.parse(enchant_enchant, player).set("{ENCHANTMENT}", library.getFriendlyName(enchantment).toLowerCase()).getString());
									} else {
										player.sendMessage(Placeholder.parse(enchant_no_perm_unsafe_enchantment, player).getString());
									}
								}
							} else player.sendMessage(Placeholder.parse(enchant_no_item, player).getString());
						} else {
							if(args[1].matches("^-?[0-9]+$")) player.sendMessage(Placeholder.parse(enchant_level_too_big_or_small, player).getString());
							else player.sendMessage(Placeholder.parse(enchant_invalid_level, player).getString());
						}
					} else player.sendMessage(Placeholder.parse(enchant_enchantment_null, player).set("{ENCHANTMENT}", args[0]).getString());
				} else sender.sendMessage(Placeholder.parse(no_permission).getString());
			} else {
				if(sender.hasPermission(PERMISSION.ENCHANT_OTHERS.toString())) sender.sendMessage(Placeholder.parse(not_enough_arguments).set("{COMMAND}", label.toLowerCase()+" <player> <enchantment> <level>").getString());
				else sender.sendMessage(Placeholder.parse(no_permission).getString());
			}
		} else if(args.length == 3) {
			// /enchant <player> <enchantment> <level>
			if(sender.hasPermission(PERMISSION.ENCHANT_SELF.toString())) {
				List<RunicPlayer> targets = PlayerSelector.select(args[0], sender.isOp());
				if(targets.size() > 0) {
					if(sender instanceof Player && targets.size() == 1 && targets.contains(RunicPlayer.cast(sender))) {
						// sender is targeting self
						RunicPlayer target = RunicPlayer.cast(sender);
						Enchantment enchantment = library.getEnchantment(args[1]);
						if(enchantment != null) {
							if(args[2].matches("^-?\\d{1,5}$")) {
								int level = Integer.parseInt(args[2]);
								level = level < 1 ? 1 : level > 32767 ? 32767 : level;
								
								if(target.getItemInHand() != null && !target.getItemInHand().getType().equals(Material.AIR)) {
									if(target.hasPermission(PERMISSION.ENCHANT_UNSAFE.toString())) {
										target.getItemInHand().addUnsafeEnchantment(enchantment, level);
										target.sendMessage(Placeholder.parse(enchant_enchant, target).set("{ENCHANTMENT}", library.getFriendlyName(enchantment).toLowerCase()).getString());
									} else {
										if(enchantment.canEnchantItem(target.getItemInHand())) {
											level = level > enchantment.getMaxLevel() ? enchantment.getMaxLevel() : level;
											target.getItemInHand().addEnchantment(enchantment, level);
											target.sendMessage(Placeholder.parse(enchant_enchant, target).set("{ENCHANTMENT}", library.getFriendlyName(enchantment).toLowerCase()).getString());
										} else {
											target.sendMessage(Placeholder.parse(enchant_no_perm_unsafe_enchantment, target).getString());
										}
									}
								} else target.sendMessage(Placeholder.parse(enchant_no_item, target).getString());
							} else {
								if(args[2].matches("^-?[0-9]+$")) target.sendMessage(Placeholder.parse(enchant_level_too_big_or_small, target).getString());
								else target.sendMessage(Placeholder.parse(enchant_invalid_level, target).getString());
							}
						} else target.sendMessage(Placeholder.parse(enchant_enchantment_null, target).set("{ENCHANTMENT}", args[2]).getString());
					} else {
						// sender is targeting others
						if(sender.hasPermission(PERMISSION.ENCHANT_OTHERS.toString())) {
							Enchantment enchantment = library.getEnchantment(args[1]);
							if(enchantment != null) {
								if(args[2].matches("^-?\\d{1,5}$")) {
									int level = Integer.parseInt(args[2]);
									level = level < 1 ? 1 : level > 32767 ? 32767 : level;

									for(RunicPlayer target : targets) {
										if(target.getItemInHand() != null && !target.getItemInHand().getType().equals(Material.AIR)) {
											if(sender.hasPermission(PERMISSION.ENCHANT_UNSAFE.toString())) {
												target.getItemInHand().addUnsafeEnchantment(enchantment, level);
												sender.sendMessage(Placeholder.parse(enchant_enchant_target, target).set("{TARGET}", target.getName()).set("{ENCHANTMENT}", library.getFriendlyName(enchantment).toLowerCase()).getString());
											} else {
												if(enchantment.canEnchantItem(target.getItemInHand())) {
													level = level > enchantment.getMaxLevel() ? enchantment.getMaxLevel() : level;
													target.getItemInHand().addEnchantment(enchantment, level);
													sender.sendMessage(Placeholder.parse(enchant_enchant_target, sender).set("{TARGET}", target.getName()).set("{ENCHANTMENT}", library.getFriendlyName(enchantment).toLowerCase()).getString());
												} else {
													sender.sendMessage(Placeholder.parse(enchant_no_perm_unsafe_enchantment, target).getString());
												}
											}
										} else sender.sendMessage(Placeholder.parse(enchant_no_item_target, sender).set("{TARGET}", target.getName()).getString());
									}
								} else {
									if(args[2].matches("^-?[0-9]+$")) sender.sendMessage(Placeholder.parse(enchant_level_too_big_or_small, sender).getString());
									else sender.sendMessage(Placeholder.parse(enchant_invalid_level, sender).getString());
								}
							} else sender.sendMessage(Placeholder.parse(enchant_enchantment_null, sender).set("{ENCHANTMENT}", args[2]).getString());
						} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
					}
				} else sender.sendMessage(Placeholder.parse(target_not_found, sender).set("{TARGET}", args[0]).getString());
			} else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		} else {
			if(sender.hasPermission(PERMISSION.ENCHANT_OTHERS.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" [player] <enchantment> <level>").getString());
			else if(sender.hasPermission(PERMISSION.ENCHANT_SELF.toString())) sender.sendMessage(Placeholder.parse(incorrect_usage, sender).set("{COMMAND}", label.toLowerCase()+" <enchantment> <level>").getString());
			else sender.sendMessage(Placeholder.parse(no_permission, sender).getString());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestion = new ArrayList<String>();
		
		if(sender.hasPermission(PERMISSION.ENCHANT_OTHERS.toString())) {
			if(args.length == 1) {
				// add player suggestions
				for(String player : PlayerSelector.suggest(args[0])) {
					suggestion.add(player);
				}
				
				// suggest enchantments if sender is a player
				if(sender instanceof Player) {
					// if sender is a player, also suggest enchantments
					if(args[0].isEmpty()) {
						for(Enchantment e : Enchantment.values()) {
							if(!sender.hasPermission(PERMISSION.ENCHANT_UNSAFE.toString())) {
								Player target = Bukkit.getPlayerExact(args[0]);
								if(target != null && target.isOnline()) {
									if(target.getItemInHand() != null) {
										if(!e.canEnchantItem(target.getItemInHand())) continue;
									}
								}
							}
							suggestion.add(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase());
						}
					} else {
						for(Enchantment e : Enchantment.values()) {
							if(!sender.hasPermission(PERMISSION.ENCHANT_UNSAFE.toString())) {
								Player target = Bukkit.getPlayerExact(args[0]);
								if(target != null && target.isOnline()) {
									if(target.getItemInHand() != null) {
										if(!e.canEnchantItem(target.getItemInHand())) continue;
									}
								}
							}
							if(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase());
							else if(e.getName().replaceAll("[^a-zA-Z]", "").toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(e.getName().replaceAll("[^a-zA-Z]", "").toLowerCase());
						}
					}
				}
			} else if(args.length == 2) {
				if(args[1].isEmpty()) {
					Player pOne = Bukkit.getPlayerExact(args[0]);
					Enchantment eOne = library.getEnchantment(args[0]);
					
					if(pOne != null) {
						// first argument is a player
						for(Enchantment e : Enchantment.values()) {
							if(!sender.hasPermission(PERMISSION.ENCHANT_UNSAFE.toString())) {
								if(pOne.isOnline()) {
									if(pOne.getItemInHand() != null) {
										if(!e.canEnchantItem(pOne.getItemInHand())) continue;
									}
								}
							}
							suggestion.add(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase());
						}
					} else if(eOne != null) {
						// first argument was an enchantment
						for(int i = eOne.getStartLevel(); eOne.getMaxLevel() >= i; i++) suggestion.add(Integer.toString(i));
					} else {
						// couldnt identify first argument
						// just suggest enchantments
						for(Enchantment e : Enchantment.values()) {
							suggestion.add(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase());
						}
					}
				} else {
					Player pOne = Bukkit.getPlayerExact(args[0]);
					Enchantment eOne = library.getEnchantment(args[0]);
					
					if(pOne != null) {
						// first argument is a player
						for(Enchantment e : Enchantment.values()) {
							if(!sender.hasPermission(PERMISSION.ENCHANT_UNSAFE.toString())) {
								if(pOne.isOnline()) {
									if(pOne.getItemInHand() != null) {
										if(!e.canEnchantItem(pOne.getItemInHand())) continue;
									}
								}
							}
							if(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase().startsWith(args[1].toLowerCase())) suggestion.add(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase());
							else if(e.getName().replaceAll("[^a-zA-Z]", "").toLowerCase().startsWith(args[1].toLowerCase())) suggestion.add(e.getName().replaceAll("[^a-zA-Z]", "").toLowerCase());
						}
					} else if(eOne != null) {
						// first argument was an enchantment
						for(int i = eOne.getStartLevel(); eOne.getMaxLevel() >= i; i++) if(!args[1].equalsIgnoreCase(Integer.toString(i))) suggestion.add(Integer.toString(i));
					} else {
						// couldnt identify first argument
						// just suggest enchantments
						for(Enchantment e : Enchantment.values()) {
							if(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase().startsWith(args[1].toLowerCase())) suggestion.add(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase());
							else if(e.getName().replaceAll("[^a-zA-Z]", "").toLowerCase().startsWith(args[1].toLowerCase())) suggestion.add(e.getName().replaceAll("[^a-zA-Z]", "").toLowerCase());
						}
					}
				}
			} else if(args.length == 3) {
				Enchantment enchantment = library.getEnchantment(args[1]);
				if(enchantment != null) {
					for(int i = enchantment.getStartLevel(); enchantment.getMaxLevel() >= i; i++) if(!args[2].equalsIgnoreCase(Integer.toString(i))) suggestion.add(Integer.toString(i));
				} else {
					for(int i = 1; i < 6; i++) if(!args[2].equalsIgnoreCase(Integer.toString(i))) suggestion.add(Integer.toString(i));
				}
			}
		} else if(sender.hasPermission(PERMISSION.ENCHANT_SELF.toString())) {
			if(args.length == 1) {
				if(args[0].isEmpty()) {
					for(Enchantment e : Enchantment.values()) {
						if(!sender.hasPermission(PERMISSION.ENCHANT_UNSAFE.toString())) {
							Player target = Bukkit.getPlayerExact(args[0]);
							if(target != null && target.isOnline()) {
								if(target.getItemInHand() != null) {
									if(!e.canEnchantItem(target.getItemInHand())) continue;
								}
							}
						}
						suggestion.add(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase());
					}
				} else {
					for(Enchantment e : Enchantment.values()) {
						if(!sender.hasPermission(PERMISSION.ENCHANT_UNSAFE.toString()) && sender instanceof Player) {
							Player player = (Player) sender;
							if(player.getItemInHand() != null) {
								if(!e.canEnchantItem(player.getItemInHand())) continue;
							}
						}
						if(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", "").toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(library.getFriendlyName(e).replaceAll("[^a-zA-Z]", ""));
						else if(e.getName().replaceAll("[^a-zA-Z]", "").toLowerCase().startsWith(args[0].toLowerCase())) suggestion.add(e.getName().replaceAll("[^a-zA-Z]", "").toLowerCase());
					}
				}
			} else if(args.length == 2) {
				Enchantment enchantment = library.getEnchantment(args[1]);
				if(enchantment != null) {
					for(int i = enchantment.getStartLevel(); enchantment.getMaxLevel() >= i; i++) if(!args[1].equalsIgnoreCase(Integer.toString(i))) suggestion.add(Integer.toString(i));
				} else {
					for(int i = 1; i < 6; i++) if(!args[1].equalsIgnoreCase(Integer.toString(i))) suggestion.add(Integer.toString(i));
				}
			}
		}
		
		return suggestion;
	}

}
