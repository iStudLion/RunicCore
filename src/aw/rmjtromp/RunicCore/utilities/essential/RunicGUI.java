package aw.rmjtromp.RunicCore.utilities.essential;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.builders.CustomInventoryBuilder;
import aw.rmjtromp.RunicCore.utilities.builders.CustomItemBuilder;
import aw.rmjtromp.RunicCore.utilities.configs.Config;

public class RunicGUI {

	private String title;
	private int size;
//	private HashMap<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
	private HashMap<String, RunicGUIItem> contents = new HashMap<String, RunicGUIItem>();
	
	protected RunicGUI(CustomInventoryBuilder inventory) {
		this(inventory.getInventory());
	}
	
	protected RunicGUI(Inventory inventory) {
		setTitle(inventory.getTitle());
		setSize(inventory.getSize());
		
		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if(item != null && !item.getType().equals(Material.AIR)) {
				addItem("item-"+i, new RunicGUIItem(item, i));
			}
		}
	}
	
	protected RunicGUI(Config config) {
		setTitle(config.getString("title", "&8Inventory"));
		
		int size = config.getInt("size", 54);
		if(size != 9 && size != 18 && size != 27 && size != 36 && size != 45 && size != 54) {
			size = 54;
			config.set("size", 54);
		}
		setSize(size);
		
		if(config.contains("contents")) {
			Set<String> keys = config.getKeys("contents");
			if(keys.size() < 1) {
				config.set("contents.myCustomItem.item", "diamondsword");
				config.set("contents.myCustomItem.amount", 1);
				config.set("contents.myCustomItem.name", "&eMy Custom Item");
				config.set("contents.myCustomItem.lore", Arrays.asList("&7Custom items even supports lores", "&7", " &8� &cEdit me in config!", "&7 at "+config.getConfigName()+".yml"));
				config.set("contents.myCustomItem.enchants.unbreaking", 1);
				config.set("contents.myCustomItem.flags", Arrays.asList("hide_attributes", "unbreakable", "hide_unbreakable"));
				keys = config.getKeys("contents");
			}
			for(String key : keys) {
				int slot = config.getInt("contents."+key+".slot", -1);
				String condition = config.contains("contents."+key+".condition") ? config.get("contents."+key+".condition").toString() : null;
				
				ItemStack item = new CustomItemBuilder(config, "contents."+key).getBukkitItem();
				if(condition != null && !condition.isEmpty()) {
					addItem(key, new RunicGUIItem(item, slot, condition));
				} else {
					addItem(key, new RunicGUIItem(item, slot));
				}
			}
		}
	}
	
	protected RunicGUI(Config config, String path) {
		setTitle(config.getString(path+".title", "&8Inventory"));
		setSize(config.getInt(path+".size", 54));
		
		if(config.contains(path+".contents")) {
			Set<String> keys = config.getKeys(path+".contents");
			if(keys.size() < 1) {
				config.set(path+".contents.myCustomItem.item", "diamondsword");
				config.set(path+".contents.myCustomItem.amount", 1);
				config.set(path+".contents.myCustomItem.name", "&eMy Custom Item");
				config.set(path+".contents.myCustomItem.lore", Arrays.asList("&7Custom items even supports lores", "&7", " &8� &cEdit me in config!", "&7 at "+config.getConfigName()+".yml"));
				config.set(path+".contents.myCustomItem.enchants.unbreaking", 1);
				config.set(path+".contents.myCustomItem.flags", Arrays.asList("hide_attributes", "unbreakable", "hide_unbreakable"));
				keys = config.getKeys(path+".contents");
			}
			for(String key : keys) {
				int slot = config.getInt(path+".contents."+key+".slot", -1);
				String condition = config.contains(path+".contents."+key+".condition") ? config.get(path+".contents."+key+".condition").toString() : null;
				ItemStack item = new CustomItemBuilder(config, path+".contents."+key).getBukkitItem();
				if(condition != null && !condition.isEmpty()) {
					addItem(key, new RunicGUIItem(item, slot, condition));
				} else {
					addItem(key, new RunicGUIItem(item, slot));
				}
			}
		}
	}
	
	protected String getTitle() {
		return title;
	}
	
	private void setTitle(String title) {
		this.title = ChatColor.translateAlternateColorCodes('&', title);
	}
	
	protected int getSize() {
		return size;
	}
	
	private void setSize(int size) {
		this.size = (size != 9 && size != 18 && size != 27 && size != 36 && size != 45 && size != 54) ? 54 : size;
	}
	
	public Collection<RunicGUIItem> getContents() {
		return contents.values();
	}
	
	private void addItem(String id, RunicGUIItem item) {
		if(id.isEmpty() || item == null || item.getItem().getType().equals(Material.AIR)) return;
		if(contents.containsKey(id)) contents.replace(id, item);
		else contents.put(id, item);
	}
	
	public void addItem(String id, ItemStack item, int slot) {
		if(id.isEmpty() || item == null || item.getType().equals(Material.AIR)) return;
		if(contents.containsKey(id)) contents.replace(id, new RunicGUIItem(item, slot));
		else contents.put(id, new RunicGUIItem(item, slot));
	}
	
	public boolean containsItem(String id) {
		return contents.containsKey(id);
	}
	
	public boolean hasItem(int slot) {
		for(RunicGUIItem i : getContents()) {
			if(i.getSlot() == slot) {
				return true;
			}
		}
		return false;
	}
	
	public ItemStack getItem(int slot) {
		ItemStack item = null;
		for(RunicGUIItem i : getContents()) {
			if(i.getSlot() == slot) {
				item = i.getItem();
			}
		}
		return item;
	}
	
	public Inventory getInventory(RunicPlayer player) {
		Inventory i = Bukkit.createInventory(player.getPlayer(), size, ChatColor.translateAlternateColorCodes('&', title));
		for(RunicGUIItem item : contents.values()) {
			if(item.hasCondition()) {
				if(!item.parseCondition(player)) continue;
			}
			i.setItem(item.getSlot(), item.getPreparedItem(player));
		}
		return i;
	}
	
	/**
	 * Can compare with Inventory|RunicGUI|CustomInventoryBuilder
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RunicGUI) {
			RunicGUI gui = (RunicGUI) obj;
			if(title.equals(gui.title) && size == gui.size && contents.equals(gui.contents)) return true;
			else return false;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "RunicGUI::"+(title != null && !title.isEmpty() ? title : "null");
	}
	
}
