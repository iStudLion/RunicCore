package aw.rmjtromp.RunicCore.core.other.extensions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockIterator;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.main.moderation.AntiSwear;
import aw.rmjtromp.RunicCore.core.features.main.permissionmanager.PermissionGroup;
import aw.rmjtromp.RunicCore.core.features.main.permissionmanager.PermissionUser;
import aw.rmjtromp.RunicCore.core.other.events.AFKStatusChangeEvent;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;
import aw.rmjtromp.RunicCore.utilities.DependencyManager;
import aw.rmjtromp.RunicCore.utilities.DependencyManager.Dependency;
import aw.rmjtromp.RunicCore.utilities.Levenshtein;
import aw.rmjtromp.RunicCore.utilities.PlayerSelector;
import aw.rmjtromp.RunicCore.utilities.RunicUtils;
import aw.rmjtromp.RunicCore.utilities.configs.Config;
import aw.rmjtromp.RunicCore.utilities.essential.DelayedTeleport;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class RunicPlayer extends BukkitPlayer implements Listener {

	protected static final RunicCore plugin = (RunicCore) Bukkit.getPluginManager().getPlugin("RunicCore");
	private static HashMap<UUID, RunicPlayer> players = new HashMap<>();
	
	private List<PlayerHome> homes = new ArrayList<>();
	private List<UUID> ignoreList = new ArrayList<>();
	
	public RunicPlayer(Player player) {
		super(player);
		players.put(player.getUniqueId(), this);
		loadConfigurations();
		
		Config config = getPlayerConfig();
		config.set("player.name", getName());
		config.set("player.uuid", getUniqueId().toString());
		updateLastActivity();
		loadHomes(); // loads player homes
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public static RunicPlayer getPlayer(String name) {
		List<String> playerNames = new ArrayList<String>();
		// adds all players (depends on vanished ones are included)
		for(Player p : Bukkit.getOnlinePlayers()) playerNames.add(p.getName());
		
		// calculates the distance between selector and player name
		HashMap<String, Double> results = new HashMap<String, Double>();
		if(playerNames.size() > 0) {
			for(String playerName : playerNames) {
				double distance = Levenshtein.similarity(name, playerName)*100;
				if(distance >= 80) results.put(playerName, distance);
			}
		}
		
		if(results.size() > 0) {
			// sorts the results by most similar player name
			PlayerSelector.entriesSortedByValues(results);

			// adds the first result to the selected list
			return RunicPlayer.cast(Bukkit.getPlayerExact((String) results.keySet().toArray()[0]));
		} else {
			// if no match is higher than 80% just use bukkit's default player selector
			RunicPlayer p = RunicPlayer.cast(Bukkit.getPlayer(name));
			if(p != null) return p;
		}
		return null;
	}
	
	public static List<RunicPlayer> getOnlinePlayers() {
		List<RunicPlayer> players = new ArrayList<>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			RunicPlayer rp = cast(player);
			if(rp.isOnline()) players.add(rp);
		}
		return players;
	}
	
	public static List<RunicPlayer> getVanishedPlayers() {
		List<RunicPlayer> players = new ArrayList<>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			RunicPlayer rp = cast(player);
			if(rp.isOnline() && rp.isVanished()) players.add(rp);
		}
		return players;
	}
	
	public static List<RunicPlayer> getVisiblePlayers() {
		List<RunicPlayer> players = new ArrayList<>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			RunicPlayer rp = cast(player);
			if(rp.isOnline() && !rp.isVanished()) players.add(rp);
		}
		return players;
	}
	
	/**
	 * @param * instance of RunicPlayer|BukkitPlayer|Player|OfflinePlayer
	 * @return RunicPlayer|null
	 */
	public static RunicPlayer cast(Object arg0) {
		if(arg0 instanceof RunicPlayer) return (RunicPlayer) arg0;
		else if(arg0 instanceof BukkitPlayer) {
			if(players.containsKey(((BukkitPlayer) arg0).getUniqueId())) return players.get(((BukkitPlayer) arg0).getUniqueId());
			else {
				RunicPlayer player = new RunicPlayer(((BukkitPlayer) arg0).getPlayer());
				players.put(player.getUniqueId(), player);
				return player;
			}
		} else if(arg0 instanceof Player) {
			if(players.containsKey(((Player) arg0).getUniqueId())) return players.get((((Player) arg0).getUniqueId()));
			else {
				RunicPlayer player = new RunicPlayer(((Player) arg0));
				if(player != null) {
					players.put(player.getUniqueId(), player);
					return player;
				}
			}
		} else if(arg0 instanceof OfflinePlayer) {
			if(players.containsKey(((OfflinePlayer) arg0).getUniqueId())) return players.get((((OfflinePlayer) arg0).getUniqueId()));
			else {
				RunicPlayer player = new RunicPlayer(((OfflinePlayer) arg0).getPlayer());
				if(player != null) players.put(player.getUniqueId(), player);
				return player;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private static String afk_enter, afk_leave, afk_message, back_return, burn_extinguished, clearinventory_clear, condense_self;
	private static void loadConfigurations() {
		afk_enter = Core.getMessages().getString("features.afk.enter", "&7You're now AFK!");
		afk_leave = Core.getMessages().getString("features.afk.leave", "&7You're no longer AFK!");
		afk_message = Core.getMessages().getString("features.afk.message", "&e{TARGET} &7is currently AFK and may not respond.");
		
		back_return = Core.getMessages().getString("features.back.return", "&7Returning to previous location.");
		
		condense_self = Core.getMessages().getString("features.condense.condense", "&7Your inventory was condensed.");
		for(RunicPlayer player : players.values()) {
			player.load();
		}
	}
	
	private void load() {
		getMaxHome(true); // re-check for max homes count 
		loadHomes(); // reload Homes
		loadIgnores();
	}
	
	private void updateLastActivity() {
		getPlayerConfig().set("lastActivity", Math.round(System.currentTimeMillis()/1000));
	}
	
	public CraftPlayer getCraftPlayer() {
		return (CraftPlayer) getPlayer();
	}
	
	public int getKills() {
		return getStatistic(Statistic.PLAYER_KILLS);
	}
	
	public int getDeaths() {
		return getStatistic(Statistic.DEATHS);
	}
	
	public void sendTitle(String jsonTitle, String jsonSubtitle, int fadeInDuration, int duration, int fadeOutDuration) {
		IChatBaseComponent chatTitle = ChatSerializer.a(jsonTitle);
		PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
		
		IChatBaseComponent chatSubtitle = ChatSerializer.a(jsonSubtitle);
		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubtitle);
		
		PacketPlayOutTitle length = new PacketPlayOutTitle(fadeInDuration, duration, fadeOutDuration);

		getCraftPlayer().getHandle().playerConnection.sendPacket(subtitle);
		getCraftPlayer().getHandle().playerConnection.sendPacket(title);
		getCraftPlayer().getHandle().playerConnection.sendPacket(length);
	}

	public void sendTitle(String jsonTitle, int fadeInDuration, int duration, int fadeOutDuration) {
		IChatBaseComponent chatTitle = ChatSerializer.a(jsonTitle);
		PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
		
		PacketPlayOutTitle length = new PacketPlayOutTitle(fadeInDuration, duration, fadeOutDuration);

		getCraftPlayer().getHandle().playerConnection.sendPacket(title);
		getCraftPlayer().getHandle().playerConnection.sendPacket(length);
	}
	
	public boolean hasEmptySlot(int amount) {
		int emptySlot = 0;
		for(ItemStack is : getInventory().getContents()) {
			if(is== null) {
				emptySlot++;
			}
		}
		return (emptySlot >= amount) ? true: false;
	}
	
	public void sendJSONMessage(String json) {
		getCraftPlayer().getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(json)));
	}
	
	public void send(String server) {
		if(plugin.getCore().getBungeeCord().hasBungee()) {
			if(server.equalsIgnoreCase(plugin.getCore().getBungeeCord().getServerName())) {
				sendMessage("&7You're already on "+server.toString()+".");
				return;
			}
			
			if(plugin.getCore().getBungeeCord().getServerList().contains(server)) {
				String servername = plugin.getCore().getBungeeCord().getServerList().get(plugin.getCore().getBungeeCord().getServerList().indexOf(server));
				sendMessage("&7Sending you to &e" + servername + "&7...");
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(servername);
				sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
			} else {
				sendMessage("&cServer '"+server+"' could not be found.");
			}
		} else {
			sendMessage("&cServer doesn't support bungee");
		}
	}
	
    public int getExpToLevelUp(){
        if(getLevel() <= 15){
            return 2*getLevel()+7;
        } else if(getLevel() <= 30){
            return 5*getLevel()-38;
        } else {
            return 9*getLevel()-158;
        }
    }
    
    public int getExpAtLevel(){
        if(getLevel() <= 16) return (int) (Math.pow(getLevel(),2) + 6*getLevel());
        else if(getLevel() <= 31) return (int) (2.5*Math.pow(getLevel(),2) - 40.5*getLevel() + 360.0);
        else return (int) (4.5*Math.pow(getLevel(),2) - 162.5*getLevel() + 2220.0);
    }
    
    public int getPlayerExp(){
        int exp = 0;
        exp += getExpAtLevel();
        exp += Math.round(getExpToLevelUp() * getExp());
        return exp;
    }
    
    public void giveExperience(int exp){
    	int currentExp = getPlayerExp();
 	   	player.setExp(0);
 	   	player.setLevel(0);
 	   	int newExp = currentExp + exp;
 	   	player.giveExp(newExp);
    }
    
    public void damage(double arg0, BukkitPlayer arg1) {
    	damage(arg0, arg1.getPlayer());
    }
    
    public boolean teleport(PlayerHome arg0) {
    	if(arg0 == null) return false;
    	return teleport(arg0.getLocation());
    }
    
    public boolean teleport(PlayerHome arg0, TeleportCause arg1) {
    	if(arg0 == null) return false;
    	return teleport(arg0.getLocation(), arg1);
    }
    
//	public boolean teleport(IRunicLocation arg0) {
//		if(arg0 == null) return false;
//		return teleport(arg0.getLocation());
//	}
//    
//	public boolean teleport(IRunicLocation arg0, TeleportCause arg1) {
//		if(arg0 == null || arg1 == null) return false;
//		return teleport(arg0.getLocation(), arg1);
//	}
    
	public boolean teleport(BukkitPlayer arg0) {
		if(arg0 == null) return false;
		return teleport(arg0.getPlayer());
	}
    
	public boolean teleport(BukkitPlayer arg0, TeleportCause arg1) {
		if(arg0 == null || arg1 == null) return false;
		return teleport(arg0.getPlayer(), arg1);
	}
	
	public boolean casSee(BukkitPlayer player) {
		return canSee(getPlayer());
	}
	
	public void hidePlayer(BukkitPlayer player) {
		hidePlayer(getPlayer());
	}
	
	public void showPlayer(BukkitPlayer player) {
		showPlayer(getPlayer());
	}
	
	public boolean isOnline() {
		return Bukkit.getOfflinePlayer(getUniqueId()).isOnline();
	}
	
	public boolean isInsideRegion(String arg0) {
		if(Dependency.WORLDGUARD.isRegistered()) {
	        LocalPlayer localPlayer = ((WorldGuardPlugin) Dependency.WORLDGUARD.getInstance()).wrapPlayer(player);
	        com.sk89q.worldedit.Vector playerVector = localPlayer.getPosition();
	        RegionManager regionManager = ((WorldGuardPlugin) Dependency.WORLDGUARD.getInstance()).getRegionManager(player.getWorld());
	        ApplicableRegionSet applicableRegionSet = regionManager.getApplicableRegions(playerVector);
	        
	        for(ProtectedRegion region : applicableRegionSet) {
	        	if(region.getId().equalsIgnoreCase(arg0)) return true;
	        }
		}
        return false;
	}
	
	public boolean isInsideSafeRegion() {
		if(Dependency.WORLDGUARD.isRegistered()) {
	        LocalPlayer localPlayer = ((WorldGuardPlugin) Dependency.WORLDGUARD.getInstance()).wrapPlayer(player);
	        com.sk89q.worldedit.Vector playerVector = localPlayer.getPosition();
	        RegionManager regionManager = ((WorldGuardPlugin) Dependency.WORLDGUARD.getInstance()).getRegionManager(player.getWorld());
	        ApplicableRegionSet applicableRegionSet = regionManager.getApplicableRegions(playerVector);
	        
	        if(applicableRegionSet.queryState(null, DefaultFlag.PVP) == StateFlag.State.DENY) return true;
		}
		
		// TODO support for ASkyBlock
        return false;
	}
	
	public boolean hasMetadata(String arg0, Plugin arg1) {
		if(hasMetadata(arg0)) {
			MetadataValue metadata = getMetadata(arg0, arg1);
			if(metadata != null) return true;
		}
		return false;
	}
	
	public MetadataValue getMetadata(String arg0, Plugin arg1) {
		if(hasMetadata(arg0)) {
			List<MetadataValue> metadatas = getMetadata(arg0);
			for(MetadataValue metadata : metadatas) {
				if(metadata.getOwningPlugin().equals(arg1)) {
					return metadata;
				}
			}
		}
		return null;
	}
	
	// teleport safety
	private long lastTeleport = System.currentTimeMillis();
	
	public long getLastTelportTime() {
		return lastTeleport;
	}
	
	////////////////////// START AFK //////////////////////
	
	private boolean afk = false;
	private Long lastmovement = System.currentTimeMillis();
	
	public void setAFK(boolean arg0) {
		if(isAFK() != arg0) {
			afk = arg0;
			Bukkit.getPluginManager().callEvent(new AFKStatusChangeEvent(this));
			if(isAFK()) {
				sendMessage(Placeholder.parse(afk_enter, this).getString());
				setSleepingIgnored(true);
				spigot().setCollidesWithEntities(false);
			} else {
				lastmovement = System.currentTimeMillis();
				sendMessage(Placeholder.parse(afk_leave, this).getString());
				setSleepingIgnored(false);
				spigot().setCollidesWithEntities(true);
			}
		}
	}
	
	public boolean isAFK() {
		return afk;
	}
	
	////////////////////// END AFK //////////////////////
	
	////////////////////// START BACK //////////////////////
	
	private Location lastLocation = getLocation();
	
	public void sendBack() {
		teleport(getLastTeleportLocation(), TeleportCause.UNKNOWN);
		sendMessage(Placeholder.parse(back_return, this).getString());
	}
	
	public Location getLastTeleportLocation() {
		return lastLocation;
	}
	
	////////////////////// END BACK //////////////////////	
	
	////////////////////// START BURN //////////////////////
	
	public void burn(int seconds) {
		if(!isDead()) {
			if(seconds > 0) setFireTicks(seconds * 20);
			else extinguish();
		}
	}
	
	public boolean isBurning() {
		if(!isDead() && getFireTicks() > 0) return true;
		return false;
	}
	
	public void extinguish() {
		if(!isDead() && isBurning()) setFireTicks(0);
	}
	
	////////////////////// END BURN //////////////////////
	
	////////////////////// START CLEARINVENTORY //////////////////////
	
	public void clearInventory() {
		getInventory().clear();
		getInventory().setHelmet(null);
		getInventory().setChestplate(null);
		getInventory().setLeggings(null);
		getInventory().setBoots(null);
	}

	public void removeActivePotionEffects() {
		if(!isDead()) {
			if(getActivePotionEffects().size() > 0) {
				for(PotionEffect effect : getActivePotionEffects()) removePotionEffect(effect.getType());
			}
		}
	}
	
	////////////////////// END CLEARINVENTORY //////////////////////
	
	////////////////////// START CONDENSE //////////////////////
	
	public void condenseInventory() {
//		if(getInventory().contains(Material.IRON_INGOT)) {
//			HashMap<Integer, ? extends ItemStack> irons = getInventory().all(Material.IRON_INGOT);
//		}
	}
	
	public boolean isCustomItem(ItemStack i) {
		ItemStack a = new ItemStack(Material.COBBLESTONE);
		System.out.print("default: "+a.hasItemMeta());
//		if(i.hasItemMeta())
		return false;
	}
	
	public void sortInventory() {
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(ItemStack i : getInventory().getContents()) items.add(i);
		
		Collections.sort(items, new Comparator<ItemStack>() {
			
		    @SuppressWarnings("deprecation")
			public int compare(ItemStack i1, ItemStack i2) {
		      if ((i1 == null) && (i2 != null)) return 1;
		      else if ((i1 != null) && (i2 == null)) return -1;
		      else if ((i1 == null) && (i2 == null)) return 0;
		      else if (i1.getType().getId() < i2.getType().getId()) return -1;
		      else if (i1.getType().getId() > i2.getType().getId()) return 1;
		      else if (i1.getDurability() < i2.getDurability()) return -1;
		      else if (i1.getDurability() > i2.getDurability()) return 1;
		      else return 0;
		    }
		    
		});
		getInventory().clear();
		for(ItemStack i : items) {
			if(i != null) getInventory().addItem(i);
		}
	}
	
	////////////////////// END CONDENSE //////////////////////
	
	////////////////////// START HOMES //////////////////////
	
	public enum HOME_RESULTS {
		INVALID_LOCATION,
		INVALID_NAME,
		HOME_ALREADY_EXISTS,
		SUCCESS;
	}
	
	private void loadHomes() {
		if(homes.size() > 0) homes.clear();
		Config config = getPlayerConfig();
		if(config.contains("homes")) {
			Set<String> keys = config.getKeys("homes");
			for(String key : keys) {
				if(key.matches("\\w{3,16}")) {
					if(config.isString("homes."+key)) {
						Location loc = RunicUtils.str2loc(config.getString("homes."+key));
						if(loc != null) {
							homes.add(new PlayerHome(this, loc, key));
						} else {
							System.out.print("[RunicCore] "+getName()+" has an invalid home location, removing home.");
							config.set("homes."+key, null);
						}
					} else {
						System.out.print("[RunicCore] "+getName()+" has an invalid home location, removing home.");
						config.set("homes."+key, null);
					}
				} else {
					System.out.print("[RunicCore] "+getName()+" has an invalid home name, removing home.");
					config.set("homes."+key, null);
				}
			}
		}
	}
	
	public boolean hasHome(String name) {
		for(PlayerHome home : homes) {
			if(home.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public PlayerHome getHome(String name) {
		for(PlayerHome home : homes) {
			if(home.getName().equalsIgnoreCase(name)) {
				return home;
			}
		}
		return null;
	}
	
	public List<PlayerHome> getHomes() {
		return homes;
	}
	
	public boolean isOwnerOf(PlayerHome home) {
		return (home != null && this.equals(home.getOwner()) && homes.contains(home));
	}
	
	public boolean deleteHome(PlayerHome home) throws Exception {
		if(isOwnerOf(home)) {
			home.delete();
			return true;
		}
		return false;
	}
	
	public boolean deletehome(String name) throws Exception {
		if(hasHome(name)) {
			return deleteHome(getHome(name));
		}
		return false;
	}
	
	private int maxHomes = Integer.MIN_VALUE;
	private int getMaxHome(boolean force) {
		if(maxHomes == Integer.MIN_VALUE || force) {
			if(isOp()) return Integer.MAX_VALUE;
			else {
				maxHomes = 0;
				for(PermissionAttachmentInfo perm : getEffectivePermissions()) {
					if(perm.getPermission().startsWith("runic.home.")) {
						String[] chunk = perm.getPermission().split("\\.");
						if(chunk.length == 3) {
							String a = chunk[2];
							if(NumberUtils.isNumber(a)) {
								maxHomes = Integer.parseInt(a);
							}
						}
					}
				}
			}
		}
		return maxHomes;
	}
	
	public int getMaxHome() {
		return getMaxHome(false);
	}
	
	public PlayerHome createHome(String name, Location location) throws Exception {
		return PlayerHome.createHome(this, location, name);
	}
	
	/**
	 * Creates a home at the player's current location
	 * @param name Home name
	 * @return PlayerHome
	 * @throws Exception
	 */
	public PlayerHome createHome(String name) throws Exception {
		return createHome(name, getLocation());
	}

	////////////////////// END HOMES //////////////////////
	
	////////////////////// START IGNORES //////////////////////
	
	private void loadIgnores() {
		if(ignoreList.size() > 0) ignoreList.clear();
		Config config = getPlayerConfig();
		if(config.contains("ignores")) {
			if(config.isList("ignores")) {
				List<String> ignores = new ArrayList<String>();
				ignores = config.getStringList("ignores", ignores);
				ignores.forEach((i) -> {
					UUID uuid = RunicUtils.stringToUUID(i);
					if(uuid != null) {
						ignoreList.add(uuid);
					}
				});
			}
		}
	}
	
	public boolean hasIgnored(BukkitPlayer arg0) {
		if(arg0 == null) return false;
		return ignoreList.contains(arg0.getUniqueId());
	}
	
	public boolean hasIgnored(Player arg0) {
		if(arg0 == null) return false;
		return ignoreList.contains(arg0.getUniqueId());
	}
	
	public boolean hasIgnored(OfflinePlayer arg0) {
		if(arg0 == null) return false;
		return ignoreList.contains(arg0.getUniqueId());
	}
	
	public boolean hasIgnored(UUID arg0) {
		if(arg0 == null) return false;
		return ignoreList.contains(arg0);
	}
	
	////////////////////// END IGNORES //////////////////////
	
	/////////////////// VANISHED ////////////////////////////////////////////////////////////////////////////////////////////
	// TODO
	
	public boolean isVanished() {
		return false;
	}
	
	public boolean isTeleporting() {
		if(hasMetadata("teleporting", plugin)) {
			MetadataValue metadata = getMetadata("teleporting", plugin);
			DelayedTeleport teleport = (DelayedTeleport) metadata.value();
			if(!teleport.hasTeleported() && !teleport.isCancelled()) return true;
		}
		return false;
	}
	
	public void cancelTeleporting() {
		if(hasMetadata("teleporting", plugin)) {
			MetadataValue metadata = getMetadata("teleporting", plugin);
			DelayedTeleport teleport = (DelayedTeleport) metadata.value();
			if(!teleport.hasTeleported() && !teleport.isCancelled()) teleport.cancel(null);
		}
	}
	
	public Location getTeleportDestination() {
		if(hasMetadata("teleporting", plugin)) {
			MetadataValue metadata = getMetadata("teleporting", plugin);
			DelayedTeleport teleport = (DelayedTeleport) metadata.value();
			if(!teleport.hasTeleported() && !teleport.isCancelled()) return teleport.getDestination();
		}
		return null;
	}
	
	public void feed() {
		if(!isDead()) {
			if(getFoodLevel() < 20) setFoodLevel(20);
		}
	}
	
	public void heal() {
		if(!isDead()) {
			if(isBurning()) extinguish();
			removeActivePotionEffects();
			feed();
			if(getHealth() < getMaxHealth()) setHealth(getMaxHealth());
		}
	}
	
	public void toggleFlight() {
		if(getAllowFlight()) {
			setFlying(false);
			setAllowFlight(false);
		} else {
			setAllowFlight(true);
			setFlying(true);
		}
	}
	
	public boolean sendPacket(PacketContainer packet) throws InvocationTargetException {
		ProtocolManager pm = DependencyManager.getProtocolManager();
		if(pm != null) {
			pm.sendServerPacket(getPlayer(), packet);
			return true;
		}
		return false;
	}
	
	public Location lookingAt() {
        BlockIterator iter = new BlockIterator(this.player, 256);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock.getLocation();
	}
	
	////////////////////// START EVENTS ////////////////////
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(player.equals(e.getPlayer())) {
			if(e.getFrom().getBlockX() == e.getTo().getBlockX()
				&& e.getFrom().getBlockY() == e.getTo().getBlockY()
				&& e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;
			lastmovement = System.currentTimeMillis();
			if(isAFK()) setAFK(false);
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if(this.equals(e.getPlayer())) {
			if(e.getFrom().getBlockX() == e.getTo().getBlockX()
					&& e.getFrom().getBlockY() == e.getTo().getBlockY()
					&& e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;
				lastmovement = System.currentTimeMillis();
				
				if(e.getCause().equals(TeleportCause.COMMAND) || e.getCause().equals(TeleportCause.PLUGIN)) {
					lastTeleport = System.currentTimeMillis();
					lastLocation = e.getFrom();
				}
				
				if(isAFK()) setAFK(false);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(this.equals(e.getPlayer())) {
			if(isAFK()) setAFK(false);
			else lastmovement = System.currentTimeMillis();
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(this.equals(e.getPlayer())) {
			if(isAFK()) setAFK(false);
			else lastmovement = System.currentTimeMillis();
		}
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		if(this.equals(e.getPlayer())) {
			if(isAFK()) setAFK(false);
			else lastmovement = System.currentTimeMillis();
		}
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		if(this.equals(e.getPlayer())) {
			if(!e.getMessage().toLowerCase().startsWith("/afk") && isAFK()) setAFK(false);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(this.equals(e.getEntity())) {
			if(isAFK()) setAFK(false);
			else lastmovement = System.currentTimeMillis();
			lastTeleport = System.currentTimeMillis();
			lastLocation = e.getEntity().getLocation();
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if(this.equals(e.getPlayer())) {
			if(isAFK()) setAFK(false);
			else lastmovement = System.currentTimeMillis();
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && this.equals(e.getEntity())) {
//			if(Math.round((System.currentTimeMillis() - lastTeleport)/1000) <= 7) {
//				if(e.getDamager() != null && e.getDamager() instanceof Player && !this.equals(e.getDamager())) {
//					RunicPlayer.cast(e.getDamager())
//					.sendMessage(Placeholder
//							.parse("&7You can not hit &e{TARGET} &7for another &e"+((7 -Math.round((System.currentTimeMillis() - lastTeleport)/1000)) > 1 ? (7-(Math.round((System.currentTimeMillis() - lastTeleport)/1000)))+"&7 seconds" : (7-(Math.round((System.currentTimeMillis() - lastTeleport)/1000))+"&7 second")+"."))
//							.set("{TARGET}", e.getEntity().getName())
//							.getString()
//							);
//				}
//    			e.setCancelled(true);
//    		}
		}
		if(e.getDamager() != null && e.getDamager() instanceof Player && this.equals(e.getDamager()) && isAFK()) setAFK(false);
	}
	
	@EventHandler
	public void onEntityShootBowEvent(EntityShootBowEvent e) {
		if(this.equals(e.getEntity())) {
			if(isAFK()) setAFK(false);
			else lastmovement = System.currentTimeMillis();
		}
	}
	
	@EventHandler
	public void onPlayerFish(PlayerFishEvent e) {
		if(this.equals(e.getPlayer())) {
			if(isAFK()) setAFK(false);
			else lastmovement = System.currentTimeMillis();
		}
	}
	
	@EventHandler
	public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent e) {
		if(this.equals(e.getPlayer())) {
			if(isAFK()) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(this.equals(e.getPlayer())) {
			if(isAFK()) setAFK(false);
			else lastmovement = System.currentTimeMillis();
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(this.equals(e.getPlayer())) {
			if(player.isSleepingIgnored()) player.setSleepingIgnored(false);
			if(!player.spigot().getCollidesWithEntities()) player.spigot().setCollidesWithEntities(true);
			
			Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
        	Team team = scoreboard.getTeam("nt-"+player.getName());
        	if(team != null) team.unregister();
        	
//        	Unregister player after 10 seconds
    		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
  				public void run() {
  					if(players.containsKey(e.getPlayer().getUniqueId())) {
  	  					unregisterPlayer(players.get(e.getPlayer().getUniqueId()));
  	  					homes = null;
  					}
  				}
    		}, 10*20);
		}
	}
	
	public static void unregisterPlayer(RunicPlayer player) {
		if(!player.isOnline()) {
			if(players.containsKey(player.getUniqueId())) {
				try { HandlerList.unregisterAll(players.get(player.getUniqueId())); } catch(Exception no) {}
				if(players.containsKey(player.getUniqueId())) players.remove(player.getUniqueId());
//				if(configs.containsKey(player.getUniqueId())) configs.remove(player.getUniqueId());
				player.updateLastActivity();
			}
		}
	}
	
	@EventHandler
	public static void onRunicCoreReload(RunicCoreReloadEvent e) {
		loadConfigurations();
	}
	
	public PermissionUser getPermissionUser() {
		return PermissionUser.getUser(getUniqueId());
	}
	
	public PermissionGroup getRank() {
		List<PermissionGroup> groups = new ArrayList<>(getPermissionUser().getGroups());
		Collections.sort(groups);
		
		if(groups.size() > 0) return groups.get(0);
		else return null;
	}

	public String getPrefix() {
		PermissionUser user = getPermissionUser();
		if(user.getPrefix() != null && !user.getPrefix().isEmpty()) return ChatColor.translateAlternateColorCodes('&', user.getPrefix());
		else {
			String prefix = "";
			List<PermissionGroup> groups = new ArrayList<>(PermissionGroup.getGroups());
			Collections.sort(groups);
			
			for(PermissionGroup group : groups) {
				if(group.getPrefix() != null && !group.getPrefix().isEmpty()) {
					prefix = group.getPrefix();
					break;
				}
			}
			return ChatColor.translateAlternateColorCodes('&', prefix);
		}
	}
	
	public String getSuffix() {
		PermissionUser user = getPermissionUser();
		if(user.getSuffix() != null) return ChatColor.translateAlternateColorCodes('&', user.getSuffix());
		else {
			String suffix = "";
			List<PermissionGroup> groups = new ArrayList<>(PermissionGroup.getGroups());
			Collections.sort(groups);
			
			for(PermissionGroup group : groups) {
				if(group.getSuffix() != null && !group.getSuffix().isEmpty()) {
					suffix = group.getSuffix();
					break;
				}
			}
			return ChatColor.translateAlternateColorCodes('&', suffix);
		}
	}
	
	public String getTablistPrefix() {
		PermissionUser user = getPermissionUser();
		if(user.getPrefix() != null) return ChatColor.translateAlternateColorCodes('&', user.getTablistPrefix());
		else {
			String prefix = "";
			List<PermissionGroup> groups = new ArrayList<>(PermissionGroup.getGroups());
			Collections.sort(groups);
			
			for(PermissionGroup group : groups) {
				if(group.getTablistPrefix() != null && !group.getTablistPrefix().isEmpty()) {
					prefix = group.getTablistPrefix();
					break;
				}
			}
			return ChatColor.translateAlternateColorCodes('&', prefix);
		}
	}
	
	public String getTablistSuffix() {
		PermissionUser user = getPermissionUser();
		if(user.getSuffix() != null) return ChatColor.translateAlternateColorCodes('&', user.getTablistSuffix());
		else {
			String suffix = "";
			List<PermissionGroup> groups = new ArrayList<>(PermissionGroup.getGroups());
			Collections.sort(groups);
			
			for(PermissionGroup group : groups) {
				if(group.getTablistSuffix() != null && !group.getTablistSuffix().isEmpty()) {
					suffix = group.getTablistSuffix();
					break;
				}
			}
			return ChatColor.translateAlternateColorCodes('&', suffix);
		}
	}
	
	////////////////////// END EVENTS //////////////////////
	
    public Runnable getRepeatingTask() {
        return () -> {
            if(isOnline()) {
            	long currentTime = System.currentTimeMillis();
            	// Check AFK status
            	if(!isAFK()) {
            		if(Math.round((currentTime - lastmovement)/1000) >= 300) {
            			setAFK(true);
            		}
            	}
            	
            	/*
            	// update nametag above head
            	if(!getPrefix().isEmpty() || !getSuffix().isEmpty()) {
                	Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
                	
                	Team team = scoreboard.getTeam("nt-"+player.getName());
                	if(team == null) team = scoreboard.registerNewTeam("nt-"+player.getName());

                	if(!getPrefix().isEmpty()) team.setPrefix(getPrefix());
                	if(!getSuffix().isEmpty()) team.setSuffix(getSuffix());
                	
                	team.addEntry(getName());
            	}*/
            }
        };
    }
    
    @Override
    public boolean equals(Object object) {
    	if(object == null) return false;
    	
    	if(object instanceof BukkitPlayer) {
    		BukkitPlayer a = (BukkitPlayer) object;
    		return getPlayer().equals(a.getPlayer());
    	} else if(object instanceof BukkitOfflinePlayer) {
    		BukkitOfflinePlayer a = (BukkitOfflinePlayer) object;
    		return getPlayer().equals(a.getPlayer());
    	} else {
    		return getPlayer().equals(object);
    	}
    }
	
	@Override
	public String toString() {
		return "RunicPlayer::{\"player\":\""+getName()+"\"}";
	}
	
	public final static class PlayerHome {
		
		private Location location;
		private String name;
		private RunicPlayer owner;
		
		public static PlayerHome createHome(RunicPlayer player, Location location, String name) throws Exception {
			if(player.getHomes().size() + 1 > player.getMaxHome(true)) {
				if(!name.isEmpty() ) {
					if(name.length() >= 3) {
						if(name.length() <= 16) {
							if(name.matches("\\w{3,16}")) {
								if(!AntiSwear.containsSwear(name)) {
									if(location != null) {
										if(!player.hasHome(name)) {
											player.getPlayerConfig().set("homes."+name, RunicUtils.loc2str(location));
											
											// add home to player's current homes list
											player.homes.add(new PlayerHome(player, RunicUtils.roundLocation(location), name));
											
											return new PlayerHome(player, location, name);
										} else throw new Exception("Home with that name already exists");
									} else throw new Exception("Invalid home location");
								} else throw new Exception("Home name can not contain innapropriate words");
							} else throw new Exception("Home name contains invalid characters, only a-z0-9 and _ are allowed");
						} else throw new Exception("Home name is too long");
					} else throw new Exception("Home name is too short");
				} else throw new Exception("Home name can't be empty");
			} else throw new Exception("Can't create more than "+player.getMaxHome()+" home"+(player.getMaxHome() > 1 ? "s" : ""));
		}
		
		private PlayerHome(RunicPlayer p, Location l, String n) {
			location = l; owner = p; name = n;
		}
		
		public String getName() {
			return name;
		}
		
		public Location getLocation() {
			return location;
		}
		
		public RunicPlayer getOwner() {
			return owner;
		}
		
		public boolean delete() throws Exception {
			if(owner.homes.contains(this)) {
				if(owner.getPlayerConfig().contains("homes."+name)) {
					owner.getPlayerConfig().set("homes."+name, null);
				}
				owner.homes.remove(this);
				return true;
			} else throw new Exception("Home with that name doesn't exist");
		}
		
		@Override
		public String toString() {
			return "PlayerHome{\"owner\":\""+owner.getName()+"\",\"location\":\""+RunicUtils.loc2str(getLocation())+"\"}";
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof PlayerHome) {
				PlayerHome home = (PlayerHome) obj;
				return (getName().equals(home.getName()) && getLocation().equals(home.getLocation()) && getOwner().equals(home.getOwner()));
			}
			return false;
		}
		
	}

}
