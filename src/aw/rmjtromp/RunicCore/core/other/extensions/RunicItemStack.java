package aw.rmjtromp.RunicCore.core.other.extensions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.Property;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.utilities.placeholders.ItemPlaceholder;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagFloat;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagLong;
import net.minecraft.server.v1_8_R3.NBTTagShort;
import net.minecraft.server.v1_8_R3.NBTTagString;

public final class RunicItemStack extends ItemStack {
	
	private static final RunicCore plugin = RunicCore.getInstance();
	
	public RunicItemStack(ItemStack itemStack) {
		super(itemStack == null ? new ItemStack(Material.AIR, 1) : itemStack);
	}
	
	public RunicItemStack(Material material) {
		super(material);
	}
	
	public RunicItemStack(Material material, int amount) {
		super(material, (amount>64?64:amount<1?1:amount));
	}
	
	public RunicItemStack(Material material, int amount, int durability) {
		super(material, (amount>64?64:amount<1?1:amount), (short) durability);
	}
	
	public RunicItemStack(String material) {
		this(material, 1);
	}
	
	public RunicItemStack(String material, int amount) {
		super(plugin.getLibrary().getItem(material));
		this.setAmount((amount>64?64:amount<1?1:amount));
	}

	public RunicItemStack setNBTTag(String key, String value) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		compound.set(key, new NBTTagString(value));
		craftItem.setTag(compound);
		CraftItemStack.asBukkitCopy(craftItem);
		this.setItemMeta(CraftItemStack.getItemMeta(craftItem));
		return this;
	}

	public RunicItemStack setNBTTag(String key, Boolean value) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		compound.setBoolean(key, value);
		craftItem.setTag(compound);
		CraftItemStack.asBukkitCopy(craftItem);
		this.setItemMeta(CraftItemStack.getItemMeta(craftItem));
		return this;
	}

	public RunicItemStack setNBTTag(String key, int value) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		compound.set(key, new NBTTagInt(value));
		craftItem.setTag(compound);
		CraftItemStack.asBukkitCopy(craftItem);
		this.setItemMeta(CraftItemStack.getItemMeta(craftItem));
		return this;
	}

	public RunicItemStack setNBTTag(String key, double value) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		compound.set(key, new NBTTagDouble(value));
		craftItem.setTag(compound);
		CraftItemStack.asBukkitCopy(craftItem);
		this.setItemMeta(CraftItemStack.getItemMeta(craftItem));
		return this;
	}

	public RunicItemStack setNBTTag(String key, float value) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		compound.set(key, new NBTTagFloat(value));
		craftItem.setTag(compound);
		CraftItemStack.asBukkitCopy(craftItem);
		this.setItemMeta(CraftItemStack.getItemMeta(craftItem));
		return this;
	}

	public RunicItemStack setNBTTag(String key, short value) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		compound.set(key, new NBTTagShort(value));
		craftItem.setTag(compound);
		CraftItemStack.asBukkitCopy(craftItem);
		this.setItemMeta(CraftItemStack.getItemMeta(craftItem));
		return this;
	}

	public RunicItemStack setNBTTag(String key, long value) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		compound.set(key, new NBTTagLong(value));
		craftItem.setTag(compound);
		CraftItemStack.asBukkitCopy(craftItem);
		this.setItemMeta(CraftItemStack.getItemMeta(craftItem));
		return this;
	}

	public String getNBTTagAsString(String key) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		return compound.getString(key);
	}

	public Boolean getNBTTagAsBoolean(String key) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		return compound.getBoolean(key);
	}

	public int getNBTTagAsInt(String key) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		return compound.getInt(key);
	}

	public double getNBTTagAsDouble(String key) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		return compound.getDouble(key);
	}

	public float getNBTTagAsFloat(String key) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		return compound.getFloat(key);
	}

	public short getNBTTagAsShort(String key) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		return compound.getShort(key);
	}

	public long getNBTTagAsLong(String key) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		return compound.getLong(key);
	}
	
	public boolean hasNBTTag(String key) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		return compound.hasKey(key);
	}
	
	public RunicItemStack setDisplayName(String name) {
		ItemMeta meta = this.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		this.setItemMeta(meta);
		return this;
	}
	
	public String getDisplayName() {
		return this.getItemMeta().getDisplayName();
	}
	
	public RunicItemStack setLore(List<String> lores) {
		ItemMeta meta = this.getItemMeta();
		List<String> finalLores = new ArrayList<String>();
		for(String lore : lores) {
			finalLores.add(ChatColor.translateAlternateColorCodes('&', lore));
		}
		meta.setLore(finalLores);
		this.setItemMeta(meta);
		return this;
	}
	
	public List<String> getLores() {
		ItemMeta meta = this.getItemMeta();
		return meta.getLore();
	}
	
	public boolean hasLore() {
		ItemMeta meta = this.getItemMeta();
		return meta.hasLore();
	}
	
	public RunicItemStack setGlow(boolean glow) {
		ItemMeta meta = this.getItemMeta();
		if(glow) {
			if(!meta.hasEnchant(Enchantment.LURE)) meta.addEnchant(Enchantment.LURE, 1, false);
			if(!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		} else {
			if(meta.hasEnchant(Enchantment.LURE)) meta.removeEnchant(Enchantment.LURE);
			if(meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		this.setItemMeta(meta);
		return this;
	}
	
	public boolean hasGlow() {
		ItemMeta meta = this.getItemMeta();
		if(meta.hasEnchant(Enchantment.LURE) && meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) return true;
		return false;
	}
	
	public RunicItemStack setUnbreakable(boolean unbreakable) {
		net.minecraft.server.v1_8_R3.ItemStack craftItem = CraftItemStack.asNMSCopy(this);
		NBTTagCompound compound = (craftItem.hasTag()) ? craftItem.getTag() : new NBTTagCompound();
		compound.setBoolean("Unbreakable", unbreakable);
		craftItem.setTag(compound);
		CraftItemStack.asBukkitCopy(craftItem);
		this.setItemMeta(CraftItemStack.getItemMeta(craftItem));
		return this;
	}
	
	public RunicItemStack parse(RunicPlayer player) {
		ItemPlaceholder p = Placeholder.parse(this, player);
		return p != null ? p.getItem() : this;
	}
	
	public static RunicItemStack getSkull(String b64stringtexture) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
	    PropertyMap propertyMap = profile.getProperties();
	    if (propertyMap == null) {
	        throw new IllegalStateException("Profile doesn't contain a property map");
	    }
	    propertyMap.put("textures", new Property("textures", b64stringtexture));
	    RunicItemStack head = new RunicItemStack(Material.SKULL_ITEM, 1, (short) 3);
	    ItemMeta headMeta = head.getItemMeta();
	    Class<?> headMetaClass = headMeta.getClass();
	    try {
			getField(headMetaClass, "profile", GameProfile.class, 0).set(headMeta, profile);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	    head.setItemMeta(headMeta);
	    return head;
	}

	private static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
	    for (final Field field : target.getDeclaredFields()) {
	        if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
	            field.setAccessible(true);
	            return field;
	        }
	    }

	    // Search in parent classes
	    if (target.getSuperclass() != null)
	        return getField(target.getSuperclass(), name, fieldType, index);
	    throw new IllegalArgumentException("Cannot find field with type " + fieldType);
	}

}
