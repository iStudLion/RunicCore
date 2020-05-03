package aw.rmjtromp.RunicCore.utilities.builders;

import java.util.List;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicItemStack;
import aw.rmjtromp.RunicCore.utilities.configs.Config;

public class CustomItemBuilder {
	
	private Config config;
	private String path;

	private RunicItemStack item;
	private String name = null;
	private List<String> lore = null;
	private int amount = 1;
	
	private Set<String> enchantments = null;
	private List<String> itemflags = null;
	private Set<String> tags = null;
	
	public CustomItemBuilder(Config config, String path) {
		this.config = config; this.path = path;
		if(!config.contains(path)) return;
		

		
		amount = config.contains(path+".amount") ? config.getInt(path+".amount", 1) : 1;
		item = new RunicItemStack(config.getString(path+".item", "AIR"), amount);
		name = config.contains(path+".name") ? config.getString(path+".name") : "";
		
//		if(!plugin.kits.getConfig().contains(path+".slot") && (plugin.kits.getConfig().isString(path+".slot") || plugin.kits.getConfig().isInt(path+".slot"))) plugin.kits.set(path+".slot", -1);
//		if(plugin.kits.getConfig().isString(path+".slot")) {
//			String s = plugin.kits.getString(path+".slot");
//			if(s.toLowerCase().matches("-?[0-9]{1,2}|helmet|chest(plate)?|leggings?|boots?")) slot = s.toLowerCase();
//		} else if(plugin.kits.getConfig().isInt(path+".slot")) {
//			int s = plugin.kits.getInt(path+".slot");
//			slot = s+"";
//		}
		
		if(item.getType().equals(Material.LEATHER_HELMET) || item.getType().equals(Material.LEATHER_CHESTPLATE) || item.getType().equals(Material.LEATHER_LEGGINGS) || item.getType().equals(Material.LEATHER_BOOTS)) {
			if(config.contains(path+".color")) {
				String HEX = config.getString(path+".color");
				if(HEX.matches("#[A-Z0-9]{6}")) {
					LeatherArmorMeta meta = ((LeatherArmorMeta) item.getItemMeta());
					meta.setColor(Color.fromRGB(Integer.valueOf( HEX.substring( 1, 3 ), 16 ), Integer.valueOf( HEX.substring( 3, 5 ), 16 ), Integer.valueOf( HEX.substring( 5, 7 ), 16 )));
					item.setItemMeta(meta);
				} else {
					System.out.println("[RunicCore] "+path.split(".")[0]+" kit's \""+name+"\" item has an invalid color value. The color must be a HEX value.");
				}
			}	
		}
		
		if(config.contains(path+".lore")) lore = config.getStringList(path+".lore");
		if(config.contains(path+".flags")) itemflags = config.getStringList(path+".flags");
		if(config.contains(path+".enchantments")) enchantments = config.getKeys(path+".enchantments");
		if(config.contains(path+".tags")) tags = config.getKeys(path+".tags");
		
		if(name != null && !name.isEmpty()) item.setDisplayName(name);
		if(lore != null && lore.size() > 0) item.setLore(lore);
		if(enchantments != null && enchantments.size() > 0) processEnchantments();
		if(itemflags != null && itemflags.size() > 0) processItemFlags();
		if(tags != null && tags.size() > 0) processTags();
	}
	
	private void processEnchantments() {
		if(enchantments == null || enchantments.size() < 1) return;
		for(String query : enchantments) {
			Enchantment enchantment = RunicCore.getInstance().getLibrary().getEnchantment(query.toLowerCase().replace("[^a-zA-Z0-9_]", ""));
			if(enchantment != null) {
				int level = config.getInt(path+".enchantments."+query, 0);
				level = level < 0 ? 0 : level > 32767 ? 32767 : level;
				item.addUnsafeEnchantment(enchantment, level);
			} else {
				System.out.println("[RunicCore] Invalid or unrecognized enchantment at \""+path+".enchantments\" at '"+query+"'.");
			}
		}
	}
	
	private void processItemFlags() {
		if(itemflags == null || itemflags.size() < 1) return;
		if(itemflags.contains("unbreakable")) item.setUnbreakable(true);
		ItemMeta meta = item.getItemMeta();
		for(String flag : itemflags) {
			if(flag.equalsIgnoreCase("hide_unbreakable")) {
				meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			} else if(flag.equalsIgnoreCase("hide_enchants")) {
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			} else if(flag.equalsIgnoreCase("hide_destroys")) {
				meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			} else if(flag.equalsIgnoreCase("hide_attributes")) {
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			} else if(flag.equalsIgnoreCase("unbreakable")) {
				item.setUnbreakable(true);
			} else if(flag.equalsIgnoreCase("glow") || flag.equalsIgnoreCase("glowing")) {
				item.setGlow(true);
			}
		}
		item.setItemMeta(meta);
	}
	
	private void processTags() {
		if(tags == null || tags.size() < 1) return;
		for(String tag : tags) {
			if(config.getConfig().isString(path+".tags."+tag)) item = item.setNBTTag(tag, config.getString(path+".tags."+tag));
			else if(config.getConfig().isInt(path+".tags."+tag)) item = item.setNBTTag(tag, config.getInt(path+".tags."+tag));
			else if(config.getConfig().isBoolean(path+".tags."+tag)) item = item.setNBTTag(tag, config.getBoolean(path+".tags."+tag));
			else if(config.getConfig().isLong(path+".tags."+tag)) item = item.setNBTTag(tag, config.getConfig().getLong(path+".tags."+tag));
			else if(config.getConfig().isDouble(path+".tags."+tag)) item = item.setNBTTag(tag, config.getDouble(path+".tags."+tag));
			else System.out.println("[RunicCore] Invalid or unsupported tag type at \""+path+".tags."+tag+"\", skipping.");
		}
	}
	
	public RunicItemStack getItem() {
		return item;
	}
	
	public ItemStack getBukkitItem() {
		return (ItemStack) item;
	}
	
}
