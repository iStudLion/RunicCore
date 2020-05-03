package aw.rmjtromp.RunicCore.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Library {

	protected HashMap<String, ItemStack> items = new HashMap<>();
	protected HashMap<String, Material> materials = new HashMap<>();
	protected HashMap<String, EntityType> mobs = new HashMap<>();
	protected HashMap<String, Enchantment> enchantments =  new HashMap<>();
	private HashMap<String, PotionEffectType> potionEffects = new HashMap<>();
	
	public Library() {
		this.registerLibraries();
	}
	
//	private String readStream(InputStream stream) {
//		try {
//			return IOUtils.toString(stream);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return "{}";
//	}

	private void registerLibraries() {
		try {
			InputStream input = getClass().getResourceAsStream("/items.json");
			
//			File file = new File(RunicCore.getInstance().getDataFolder()+ File.separator +"items.json");
//			if(!file.exists()) file.createNewFile();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//			
//			String line = null;
//		    while ((line = reader.readLine()) != null) {
//		    	writer.append(line);
//		    	writer.newLine();
//		    }
//		    reader.close();
//		    writer.close();

//		    String content = new String(Files.readAllBytes(Paths.get("readMe.txt")), StandardCharsets.UTF_8);
			try {
				JSONObject items = (JSONObject) new JSONParser().parse(IOUtils.toString(input));
				for(Object i : items.keySet()) {
					String key = (String) i;
					ItemStack item;
					Material material = Material.getMaterial(((JSONObject) items.get(key)).get("m").toString());
					if(material != null) {
						this.materials.put(key, material);
						if(((JSONObject) items.get(key)).containsKey("d")) {
							int damage = Integer.parseInt(((JSONObject) items.get(key)).get("d").toString());
							item = new ItemStack(material, 1, (short) damage);
						} else item = new ItemStack(material, 1);
						if(item != null) this.items.put(key, item);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject mobs = (JSONObject) new JSONParser().parse("{\"c\":{\"entityType\":\"CREEPER\"},\"creep\":{\"entityType\":\"CREEPER\"},\"cataclysm\":{\"entityType\":\"CREEPER\"},\"creeper\":{\"entityType\":\"CREEPER\"},\"skeleton\":{\"entityType\":\"SKELETON\"},\"s\":{\"entityType\":\"SKELETON\"},\"sk\":{\"entityType\":\"SKELETON\"},\"skelly\":{\"entityType\":\"SKELETON\"},\"skellington\":{\"entityType\":\"SKELETON\"},\"sp\":{\"entityType\":\"SPIDER\"},\"bug\":{\"entityType\":\"SPIDER\"},\"spider\":{\"entityType\":\"SPIDER\"},\"giant\":{\"entityType\":\"GIANT\"},\"giantzombie\":{\"entityType\":\"GIANT\"},\"bigzombie\":{\"entityType\":\"GIANT\"},\"zombie\":{\"entityType\":\"ZOMBIE\"},\"z\":{\"entityType\":\"ZOMBIE\"},\"zed\":{\"entityType\":\"ZOMBIE\"},\"monster\":{\"entityType\":\"ZOMBIE\"},\"slime\":{\"entityType\":\"SLIME\"},\"sl\":{\"entityType\":\"SLIME\"},\"ghast\":{\"entityType\":\"GHAST\"},\"g\":{\"entityType\":\"GHAST\"},\"ghost\":{\"entityType\":\"GHAST\"},\"pigzombie\":{\"entityType\":\"PIG_ZOMBIE\"},\"pigman\":{\"entityType\":\"PIG_ZOMBIE\"},\"pigmen\":{\"entityType\":\"PIG_ZOMBIE\"},\"zombiepigman\":{\"entityType\":\"PIG_ZOMBIE\"},\"zombiepigmen\":{\"entityType\":\"PIG_ZOMBIE\"},\"pg\":{\"entityType\":\"PIG_ZOMBIE\"},\"zp\":{\"entityType\":\"PIG_ZOMBIE\"},\"zombiepig\":{\"entityType\":\"PIG_ZOMBIE\"},\"enderman\":{\"entityType\":\"ENDERMAN\"},\"e\":{\"entityType\":\"ENDERMAN\"},\"em\":{\"entityType\":\"ENDERMAN\"},\"ender\":{\"entityType\":\"ENDERMAN\"},\"endermen\":{\"entityType\":\"ENDERMAN\"},\"slendermen\":{\"entityType\":\"ENDERMAN\"},\"slenderman\":{\"entityType\":\"ENDERMAN\"},\"slender\":{\"entityType\":\"ENDERMAN\"},\"cs\":{\"entityType\":\"CAVE_SPIDER\"},\"cavespider\":{\"entityType\":\"CAVE_SPIDER\"},\"cspider\":{\"entityType\":\"CAVE_SPIDER\"},\"bluespider\":{\"entityType\":\"CAVE_SPIDER\"},\"silverfish\":{\"entityType\":\"SILVERFISH\"},\"sf\":{\"entityType\":\"SILVERFISH\"},\"sfish\":{\"entityType\":\"SILVERFISH\"},\"rat\":{\"entityType\":\"SILVERFISH\"},\"blaze\":{\"entityType\":\"BLAZE\"},\"bl\":{\"entityType\":\"BLAZE\"},\"b\":{\"entityType\":\"BLAZE\"},\"magmacube\":{\"entityType\":\"MAGMA_CUBE\"},\"lavaslime\":{\"entityType\":\"MAGMA_CUBE\"},\"mcube\":{\"entityType\":\"MAGMA_CUBE\"},\"magma\":{\"entityType\":\"MAGMA_CUBE\"},\"m\":{\"entityType\":\"MAGMA_CUBE\"},\"mc\":{\"entityType\":\"MAGMA_CUBE\"},\"enderdragon\":{\"entityType\":\"ENDER_DRAGON\"},\"dragon\":{\"entityType\":\"ENDER_DRAGON\"},\"raqreqentba\":{\"entityType\":\"ENDER_DRAGON\"},\"wither\":{\"entityType\":\"WITHER\"},\"witherboss\":{\"entityType\":\"WITHER\"},\"wboss\":{\"entityType\":\"WITHER\"},\"bat\":{\"entityType\":\"BAT\"},\"batman\":{\"entityType\":\"BAT\"},\"witch\":{\"entityType\":\"WITCH\"},\"hag\":{\"entityType\":\"WITCH\"},\"sibly\":{\"entityType\":\"WITCH\"},\"sorceress\":{\"entityType\":\"WITCH\"},\"endermite\":{\"entityType\":\"ENDERMITE\"},\"mite\":{\"entityType\":\"ENDERMITE\"},\"purplerat\":{\"entityType\":\"ENDERMITE\"},\"pr\":{\"entityType\":\"ENDERMITE\"},\"acarid\":{\"entityType\":\"ENDERMITE\"},\"acarian\":{\"entityType\":\"ENDERMITE\"},\"acarine\":{\"entityType\":\"ENDERMITE\"},\"guardian\":{\"entityType\":\"GUARDIAN\"},\"keeper\":{\"entityType\":\"GUARDIAN\"},\"guard\":{\"entityType\":\"GUARDIAN\"},\"watcher\":{\"entityType\":\"GUARDIAN\"},\"pig\":{\"entityType\":\"PIG\"},\"p\":{\"entityType\":\"PIG\"},\"sheep\":{\"entityType\":\"SHEEP\"},\"sh\":{\"entityType\":\"SHEEP\"},\"cow\":{\"entityType\":\"COW\"},\"bovine\":{\"entityType\":\"COW\"},\"ch\":{\"entityType\":\"CHICKEN\"},\"chick\":{\"entityType\":\"CHICKEN\"},\"chicken\":{\"entityType\":\"CHICKEN\"},\"bird\":{\"entityType\":\"CHICKEN\"},\"squid\":{\"entityType\":\"SQUID\"},\"sq\":{\"entityType\":\"SQUID\"},\"octupus\":{\"entityType\":\"SQUID\"},\"wolf\":{\"entityType\":\"WOLF\"},\"w\":{\"entityType\":\"WOLF\"},\"dog\":{\"entityType\":\"WOLF\"},\"mushroom\":{\"entityType\":\"MUSHROOM_COW\"},\"mooshroom\":{\"entityType\":\"MUSHROOM_COW\"},\"mcow\":{\"entityType\":\"MUSHROOM_COW\"},\"redcow\":{\"entityType\":\"MUSHROOM_COW\"},\"mushroomcow\":{\"entityType\":\"MUSHROOM_COW\"},\"shroom\":{\"entityType\":\"MUSHROOM_COW\"},\"snowman\":{\"entityType\":\"SNOWMAN\"},\"snowgolem\":{\"entityType\":\"SNOWMAN\"},\"sgolem\":{\"entityType\":\"SNOWMAN\"},\"sm\":{\"entityType\":\"SNOWMAN\"},\"sg\":{\"entityType\":\"SNOWMAN\"},\"snowmen\":{\"entityType\":\"SNOWMAN\"},\"ocelot\":{\"entityType\":\"OCELOT\"},\"cat\":{\"entityType\":\"OCELOT\"},\"oce\":{\"entityType\":\"OCELOT\"},\"o\":{\"entityType\":\"OCELOT\"},\"kitty\":{\"entityType\":\"OCELOT\"},\"villagergolem\":{\"entityType\":\"IRON_GOLEM\"},\"igolem\":{\"entityType\":\"IRON_GOLEM\"},\"ironman\":{\"entityType\":\"IRON_GOLEM\"},\"ironmen\":{\"entityType\":\"IRON_GOLEM\"},\"iron\":{\"entityType\":\"IRON_GOLEM\"},\"ig\":{\"entityType\":\"IRON_GOLEM\"},\"irongolem\":{\"entityType\":\"IRON_GOLEM\"},\"horse\":{\"entityType\":\"HORSE\"},\"h\":{\"entityType\":\"HORSE\"},\"bronco\":{\"entityType\":\"HORSE\"},\"pony\":{\"entityType\":\"HORSE\"},\"rabbit\":{\"entityType\":\"RABBIT\"},\"r\":{\"entityType\":\"RABBIT\"},\"rab\":{\"entityType\":\"RABBIT\"},\"bunny\":{\"entityType\":\"RABBIT\"},\"hare\":{\"entityType\":\"RABBIT\"},\"cony\":{\"entityType\":\"RABBIT\"},\"coney\":{\"entityType\":\"RABBIT\"},\"v\":{\"entityType\":\"VILLAGER\"},\"villager\":{\"entityType\":\"VILLAGER\"},\"npc\":{\"entityType\":\"VILLAGER\"}}");
			for(Object m : mobs.keySet()) {
				String key = (String) m;
				EntityType entity = EntityType.valueOf(((JSONObject) mobs.get(key)).get("entityType").toString());
				if(entity != null) this.mobs.put(key, entity);
			}
		} catch(ParseException e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject enchantments = (JSONObject) new JSONParser().parse("{\"alldamage\":{\"enchantment\":\"DAMAGE_ALL\"},\"alldmg\":{\"enchantment\":\"DAMAGE_ALL\"},\"sharp\":{\"enchantment\":\"DAMAGE_ALL\"},\"sharpness\":{\"enchantment\":\"DAMAGE_ALL\"},\"arthropodsdamage\":{\"enchantment\":\"DAMAGE_ARTHROPODS\"},\"ardmg\":{\"enchantment\":\"DAMAGE_ARTHROPODS\"},\"baneofarthropods\":{\"enchantment\":\"DAMAGE_ARTHROPODS\"},\"undeaddamage\":{\"enchantment\":\"DAMAGE_UNDEAD\"},\"smite\":{\"enchantment\":\"DAMAGE_UNDEAD\"},\"efficieny\":{\"enchantment\":\"DIG_SPEED\"},\"eff\":{\"enchantment\":\"DIG_SPEED\"},\"digspeed\":{\"enchantment\":\"DIG_SPEED\"},\"unb\":{\"enchantment\":\"DURABILITY\"},\"unbreaking\":{\"enchantment\":\"DURABILITY\"},\"unbreak\":{\"enchantment\":\"DURABILITY\"},\"dura\":{\"enchantment\":\"DURABILITY\"},\"durability\":{\"enchantment\":\"DURABILITY\"},\"fireaspect\":{\"enchantment\":\"FIRE_ASPECT\"},\"fire\":{\"enchantment\":\"FIRE_ASPECT\"},\"knockback\":{\"enchantment\":\"KNOCKBACK\"},\"kb\":{\"enchantment\":\"KNOCKBACK\"},\"blockslootbonus\":{\"enchantment\":\"LOOT_BONUS_BLOCKS\"},\"fortune\":{\"enchantment\":\"LOOT_BONUS_BLOCKS\"},\"fort\":{\"enchantment\":\"LOOT_BONUS_BLOCKS\"},\"loot\":{\"enchantment\":\"LOOT_BONUS_MOBS\"},\"looting\":{\"enchantment\":\"LOOT_BONUS_MOBS\"},\"mobslootbonus\":{\"enchantment\":\"LOOT_BONUS_MOBS\"},\"mobloot\":{\"enchantment\":\"LOOT_BONUS_MOBS\"},\"oxygen\":{\"enchantment\":\"OXYGEN\"},\"resp\":{\"enchantment\":\"OXYGEN\"},\"respiration\":{\"enchantment\":\"OXYGEN\"},\"protection\":{\"enchantment\":\"PROTECTION_ENVIRONMENTAL\"},\"prot\":{\"enchantment\":\"PROTECTION_ENVIRONMENTAL\"},\"explosionsprotection\":{\"enchantment\":\"PROTECTION_EXPLOSIONS\"},\"blastprotection\":{\"enchantment\":\"PROTECTION_EXPLOSIONS\"},\"blastprot\":{\"enchantment\":\"PROTECTION_EXPLOSIONS\"},\"expprot\":{\"enchantment\":\"PROTECTION_EXPLOSIONS\"},\"fallprotection\":{\"enchantment\":\"PROTECTION_FALL\"},\"fallprot\":{\"enchantment\":\"PROTECTION_FALL\"},\"featherfall\":{\"enchantment\":\"PROTECTION_FALL\"},\"featherfalling\":{\"enchantment\":\"PROTECTION_FALL\"},\"fireprotection\":{\"enchantment\":\"PROTECTION_FIRE\"},\"fireprot\":{\"enchantment\":\"PROTECTION_FIRE\"},\"projectileprotection\":{\"enchantment\":\"PROTECTION_PROJECTILE\"},\"projprot\":{\"enchantment\":\"PROTECTION_PROJECTILE\"},\"silktouch\":{\"enchantment\":\"SILK_TOUCH\"},\"silk\":{\"enchantment\":\"SILK_TOUCH\"},\"waterworker\":{\"enchantment\":\"WATER_WORKER\"},\"aquaaffinity\":{\"enchantment\":\"WATER_WORKER\"},\"firearrow\":{\"enchantment\":\"ARROW_FIRE\"},\"flame\":{\"enchantment\":\"ARROW_FIRE\"},\"arrowdamage\":{\"enchantment\":\"ARROW_DAMAGE\"},\"power\":{\"enchantment\":\"ARROW_DAMAGE\"},\"arrowknockback\":{\"enchantment\":\"ARROW_KNOCKBACK\"},\"arrowkb\":{\"enchantment\":\"ARROW_KNOCKBACK\"},\"punch\":{\"enchantment\":\"ARROW_KNOCKBACK\"},\"infinitearrows\":{\"enchantment\":\"ARROW_INFINITE\"},\"infarrows\":{\"enchantment\":\"ARROW_INFINITE\"},\"infinity\":{\"enchantment\":\"ARROW_INFINITE\"},\"infinite\":{\"enchantment\":\"ARROW_INFINITE\"},\"depthstrider\":{\"enchantment\":\"DEPTH_STRIDER\"},\"luck\":{\"enchantment\":\"LUCK\"},\"sealuck\":{\"enchantment\":\"LUCK\"},\"luckofthesea\":{\"enchantment\":\"LUCK\"},\"lure\":{\"enchantment\":\"LURE\"},\"thorns\":{\"enchantment\":\"THORNS\"},\"cactus\":{\"enchantment\":\"THORNS\"},\"prick\":{\"enchantment\":\"THORNS\"}}");
			for(Object e : enchantments.keySet()) {
				String key = (String) e;
				Enchantment enchantment = Enchantment.getByName(((JSONObject) enchantments.get(key)).get("enchantment").toString());
				if(enchantment != null) this.enchantments.put(key, enchantment);
			}
		} catch(ParseException e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject potionEffects = (JSONObject) new JSONParser().parse("{\"speed\":{\"potionEffect\":\"SPEED\"},\"velocity\":{\"potionEffect\":\"SPEED\"},\"swift\":{\"potionEffect\":\"SPEED\"},\"swiftness\":{\"potionEffect\":\"SPEED\"},\"slow\":{\"potionEffect\":\"SLOW\"},\"slowness\":{\"potionEffect\":\"SLOW\"},\"haste\":{\"potionEffect\":\"FAST_DIGGING\"},\"fastdigging\":{\"potionEffect\":\"FAST_DIGGING\"},\"fastdig\":{\"potionEffect\":\"FAST_DIGGING\"},\"miningfatigue\":{\"potionEffect\":\"SLOW_DIGGING\"},\"minerfatigue\":{\"potionEffect\":\"SLOW_DIGGING\"},\"minersfatigue\":{\"potionEffect\":\"SLOW_DIGGING\"},\"fatigue\":{\"potionEffect\":\"SLOW_DIGGING\"},\"slowdig\":{\"potionEffect\":\"SLOW_DIGGING\"},\"slowdigging\":{\"potionEffect\":\"SLOW_DIGGING\"},\"increasedamage\":{\"potionEffect\":\"INCREASE_DAMAGE\"},\"increasedmg\":{\"potionEffect\":\"INCREASE_DAMAGE\"},\"strength\":{\"potionEffect\":\"INCREASE_DAMAGE\"},\"strong\":{\"potionEffect\":\"INCREASE_DAMAGE\"},\"heal\":{\"potionEffect\":\"HEAL\"},\"healing\":{\"potionEffect\":\"HEAL\"},\"health\":{\"potionEffect\":\"HEAL\"},\"instanthealing\":{\"potionEffect\":\"HEAL\"},\"instanthealth\":{\"potionEffect\":\"HEAL\"},\"harm\":{\"potionEffect\":\"HARM\"},\"harming\":{\"potionEffect\":\"HARM\"},\"damage\":{\"potionEffect\":\"HARM\"},\"instantdamage\":{\"potionEffect\":\"HARM\"},\"dmg\":{\"potionEffect\":\"HARM\"},\"jump\":{\"potionEffect\":\"JUMP\"},\"jumpboost\":{\"potionEffect\":\"JUMP\"},\"leap\":{\"potionEffect\":\"JUMP\"},\"leaping\":{\"potionEffect\":\"JUMP\"},\"confusion\":{\"potionEffect\":\"CONFUSION\"},\"naussea\":{\"potionEffect\":\"CONFUSION\"},\"nausea\":{\"potionEffect\":\"CONFUSION\"},\"regen\":{\"potionEffect\":\"REGENERATION\"},\"regeneration\":{\"potionEffect\":\"REGENERATION\"},\"resistance\":{\"potionEffect\":\"DAMAGE_RESISTANCE\"},\"res\":{\"potionEffect\":\"DAMAGE_RESISTANCE\"},\"damageresistance\":{\"potionEffect\":\"DAMAGE_RESISTANCE\"},\"damageres\":{\"potionEffect\":\"DAMAGE_RESISTANCE\"},\"dmgres\":{\"potionEffect\":\"DAMAGE_RESISTANCE\"},\"fireresistance\":{\"potionEffect\":\"FIRE_RESISTANCE\"},\"fireres\":{\"potionEffect\":\"FIRE_RESISTANCE\"},\"waterbreathing\":{\"potionEffect\":\"WATER_BREATHING\"},\"waterbreath\":{\"potionEffect\":\"WATER_BREATHING\"},\"invis\":{\"potionEffect\":\"INVISIBILITY\"},\"invisibility\":{\"potionEffect\":\"INVISIBILITY\"},\"blind\":{\"potionEffect\":\"BLINDNESS\"},\"blindness\":{\"potionEffect\":\"BLINDNESS\"},\"nightvision\":{\"potionEffect\":\"NIGHT_VISION\"},\"hunger\":{\"potionEffect\":\"HUNGER\"},\"hungry\":{\"potionEffect\":\"HUNGER\"},\"weak\":{\"potionEffect\":\"WEAKNESS\"},\"weakness\":{\"potionEffect\":\"WEAKNESS\"},\"poison\":{\"potionEffect\":\"POISON\"},\"wither\":{\"potionEffect\":\"WITHER\"}}");
			for(Object a : potionEffects.keySet()) {
				String key = (String) a;
				PotionEffectType effect = PotionEffectType.getByName(((JSONObject) potionEffects.get(key)).get("potionEffect").toString());
				if(effect != null) this.potionEffects.put(key, effect);
			}
		} catch(ParseException e) {
			e.printStackTrace();
		}
	}
	
	public ItemStack getItem(String arg0) {
		if(arg0.contains(":")) {
			ItemStack item;
			if(arg0.split(":", 2)[0].matches("^[0-9]+$")) {
				int id = Integer.parseInt(arg0.split(":")[0]);
				item = getItem(id);
			} else item = getItem(arg0.split(":", 2)[0]);
			
			if(item != null) {
				if(arg0.split(":", 2)[1].matches("^[0-9]+$")) {
					int durability = Integer.parseInt(arg0.split(":")[1]);
					item.setDurability((short) durability);
				}
			}
			return item;
		}
		return items.containsKey(arg0.toLowerCase().replaceAll("[^a-z]", "")) ? items.get(arg0.toLowerCase().replaceAll("[^a-z]", "")) : null;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItem(int arg0) {
		return new ItemStack(Material.getMaterial(arg0), 1);
	}
	
	public Set<String> getItemNameList() {
		return items.keySet();
	}
	
	public Material getMaterial(String arg0) {
		return materials.containsKey(arg0.toLowerCase().replaceAll("[^a-z]", "")) ? materials.get(arg0.toLowerCase().replaceAll("[^a-z]", "")) : null;
	}
	
	@SuppressWarnings("deprecation")
	public Material getMaterial(int arg0) {
		return Material.getMaterial(arg0);
	}

	public EntityType getEntityType(String arg0) {
		return mobs.containsKey(arg0.toLowerCase().replaceAll("[^a-z]", "")) ? mobs.get(arg0.toLowerCase().replaceAll("[^a-z]", "")) : null;
	}

	@SuppressWarnings("deprecation")
	public EntityType getEntityType(int arg0) {
		return EntityType.fromId(arg0);
	}

	public Enchantment getEnchantment(String arg0) {
		return enchantments.containsKey(arg0.toLowerCase().replaceAll("[^a-z]", "")) ? enchantments.get(arg0.toLowerCase().replaceAll("[^a-z]", "")) : null;
	}

	@SuppressWarnings("deprecation")
	public Enchantment getEnchantment(int arg0) {
		return Enchantment.getById(arg0);
	}

	public PotionEffectType getPotionEffect(String arg0) {
		return potionEffects.containsKey(arg0.toLowerCase().replaceAll("[^a-z]", "")) ? potionEffects.get(arg0.toLowerCase().replaceAll("[^a-z]", "")) : null;
	}
	
	@SuppressWarnings("deprecation")
	public PotionEffectType getPotionEffect(int arg0) {
		return PotionEffectType.getById(arg0);
	}
	
	public String getFriendlyName(Object arg0) {
		if(arg0 instanceof Enchantment) {
			Enchantment enchantment = (Enchantment) arg0;
			if(enchantment.equals(Enchantment.ARROW_DAMAGE)) return "Power";
			else if(enchantment.equals(Enchantment.ARROW_FIRE)) return "Flame";
			else if(enchantment.equals(Enchantment.ARROW_INFINITE)) return "Infinity";
			else if(enchantment.equals(Enchantment.ARROW_KNOCKBACK)) return "Punch";
			else if(enchantment.equals(Enchantment.DAMAGE_ALL)) return "Sharpness";
			else if(enchantment.equals(Enchantment.DAMAGE_ARTHROPODS)) return "Bane of Arthopods";
			else if(enchantment.equals(Enchantment.DAMAGE_UNDEAD)) return "Smite";
			else if(enchantment.equals(Enchantment.DEPTH_STRIDER)) return "Depth Strider";
			else if(enchantment.equals(Enchantment.DIG_SPEED)) return "Efficiency";
			else if(enchantment.equals(Enchantment.DURABILITY)) return "Unbreaking";
			else if(enchantment.equals(Enchantment.FIRE_ASPECT)) return "Fire Aspect";
			else if(enchantment.equals(Enchantment.KNOCKBACK)) return "Knockback";
			else if(enchantment.equals(Enchantment.LOOT_BONUS_BLOCKS)) return "Fortune";
			else if(enchantment.equals(Enchantment.LOOT_BONUS_MOBS)) return "Looting";
			else if(enchantment.equals(Enchantment.LUCK)) return "Luck of the Sea";
			else if(enchantment.equals(Enchantment.LURE)) return "Lure";
			else if(enchantment.equals(Enchantment.OXYGEN)) return "Respiration";
			else if(enchantment.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) return "Protection";
			else if(enchantment.equals(Enchantment.PROTECTION_EXPLOSIONS)) return "Blast Protection";
			else if(enchantment.equals(Enchantment.PROTECTION_FALL)) return "Feather Falling";
			else if(enchantment.equals(Enchantment.PROTECTION_FIRE)) return "Fire Protection";
			else if(enchantment.equals(Enchantment.PROTECTION_PROJECTILE)) return "Projectile Protection";
			else if(enchantment.equals(Enchantment.SILK_TOUCH)) return "Silk Touch";
			else if(enchantment.equals(Enchantment.THORNS)) return "Thorns";
			else if(enchantment.equals(Enchantment.WATER_WORKER)) return "Aqua Affinity";
			else return "Unknown";
		} else if(arg0 instanceof EntityType || arg0 instanceof Entity) {
			EntityType type = arg0 instanceof EntityType ? (EntityType) arg0 : ((Entity) arg0).getType();
			if(type.equals(EntityType.CREEPER)) return "Creeper";
			else if(type.equals(EntityType.SKELETON)) return "Skeleton";
			else if(type.equals(EntityType.SPIDER)) return "Spider";
			else if(type.equals(EntityType.GIANT)) return "Giant";
			else if(type.equals(EntityType.ZOMBIE)) return "Zombie";
			else if(type.equals(EntityType.SLIME)) return "Slime";
			else if(type.equals(EntityType.GHAST)) return "Ghast";
			else if(type.equals(EntityType.PIG_ZOMBIE)) return "Zombie Pigman";
			else if(type.equals(EntityType.ENDERMAN)) return "Enderman";
			else if(type.equals(EntityType.CAVE_SPIDER)) return "Cave Spider";
			else if(type.equals(EntityType.SILVERFISH)) return "Silverfish";
			else if(type.equals(EntityType.BLAZE)) return "Blaze";
			else if(type.equals(EntityType.MAGMA_CUBE)) return "Magma Cube";
			else if(type.equals(EntityType.ENDER_DRAGON)) return "Ender Dragon";
			else if(type.equals(EntityType.WITHER)) return "Wither";
			else if(type.equals(EntityType.BAT)) return "Bat";
			else if(type.equals(EntityType.WITCH)) return "Witch";
			else if(type.equals(EntityType.ENDERMITE)) return "Endermite";
			else if(type.equals(EntityType.GUARDIAN)) return "Guardian";
			else if(type.equals(EntityType.PIG)) return "Pig";
			else if(type.equals(EntityType.SHEEP)) return "Sheep";
			else if(type.equals(EntityType.COW)) return "Cow";
			else if(type.equals(EntityType.CHICKEN)) return "Chicken";
			else if(type.equals(EntityType.SQUID)) return "Squid";
			else if(type.equals(EntityType.WOLF)) return "Wolf";
			else if(type.equals(EntityType.MUSHROOM_COW)) return "Mushroom Cow";
			else if(type.equals(EntityType.SNOWMAN)) return "Snowman";
			else if(type.equals(EntityType.OCELOT)) return "Ocelot";
			else if(type.equals(EntityType.IRON_GOLEM)) return "Iron Golem";
			else if(type.equals(EntityType.HORSE)) return "Horse";
			else if(type.equals(EntityType.RABBIT)) return "Rabbit";
			else if(type.equals(EntityType.VILLAGER)) return "Villager";
			else return "Unknown";
		} else if(arg0 instanceof ItemStack) {
			ItemStack item = (ItemStack) arg0;
			if(item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				if(meta instanceof BookMeta) {
					return ChatColor.stripColor(((BookMeta) meta).getTitle());
				} else {
					if(meta.hasDisplayName() && meta.getDisplayName() != null && !meta.getDisplayName().isEmpty()) {
						return ChatColor.stripColor(meta.getDisplayName());
					}
				}
			}
			
			return CraftItemStack.asNMSCopy(item).getName();
		} else if(arg0 instanceof Material) {
			Material mat = (Material) arg0;
			return CraftItemStack.asNMSCopy(new ItemStack(mat)).getName();
		}
		return "null";
	}
	
}