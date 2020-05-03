package aw.rmjtromp.RunicCore.utilities.essential;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.other.events.CombatTagEvent;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;

public class DelayedTeleport implements Listener {

	private static final RunicCore plugin = RunicCore.getInstance();
	
	private RunicPlayer player;
	private Location destination;
	private int delay, timeLeft, teleportProtection = 0;
	
	private boolean cancelled = false;
	private TeleportCancelCause cancelReason = TeleportCancelCause.UNDEFINED;
	
	private boolean teleported = false;
	private long teleportTime = System.currentTimeMillis();
	
	private UUID uuid = UUID.randomUUID();
	
	protected enum TeleportCancelCause {
		PLAYER_MOVE,
		COMBAT_TAGGED,
		PLAYER_QUIT,
		UNDEFINED;
	}
	
	public DelayedTeleport(RunicPlayer player, Location destination, final int d, int teleportProtection) {
		this.player = player; this.destination = destination; this.delay = d + 1; this.teleportProtection = teleportProtection > 0 ? teleportProtection : 0;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		player.setMetadata("teleporting", new FixedMetadataValue(plugin, this));
		timeLeft = delay;
		if(d > 0) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if(isCancelled()) {
						cancel();
						return;
					}
					timeLeft--;
					if(timeLeft <= 0) {
						teleport();
						cancel();
					} else {
						onInterval();
					}
				}
			}.runTaskTimer(plugin, 0, 20);
		} else teleport();
	}
	
	public DelayedTeleport(RunicPlayer player, Location destination, int d) {
		this(player, destination, d, 0);
	}
	
//	public DelayedTeleport(RunicPlayer player, IRunicLocation destination, int d) {
//		this(player, destination.getLocation(), d, 0);
//	}
//	
//	public DelayedTeleport(RunicPlayer player, IRunicLocation destination, int d, int tpsafety) {
//		this(player, destination.getLocation(), d, tpsafety);
//	}
	
	public final void cancel(TeleportCancelCause reason) {
		if(cancelled || teleported) return;
		cancelled = true;
		cancelReason = (reason == null) ? TeleportCancelCause.UNDEFINED : reason;

		// remove teleporting metadata from player
		if(player.hasMetadata("teleporting", plugin)) {
			DelayedTeleport teleport = (DelayedTeleport) player.getMetadata("teleporting", plugin).value();
			if(this.equals(teleport)) {
				player.removeMetadata("teleporting", plugin);
			}
		}
		
		onCancelled();
		unRegisterEvents();
	}
	
	public final void teleport() {
		if((delay > 0 && cancelled) || teleported) return;
		teleported = true;
		teleportTime = System.currentTimeMillis();
		player.teleport(destination);
		
		// remove teleporting metadata from player
		if(player.hasMetadata("teleporting", plugin)) {
			DelayedTeleport teleport = (DelayedTeleport) player.getMetadata("teleporting", plugin).value();
			if(this.equals(teleport)) {
				player.removeMetadata("teleporting", plugin);
			}
		}
		
		onTeleport();
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			  public void run() {
				  unRegisterEvents();
			  }
		}, 10*20);
	}
	
	private final void unRegisterEvents() {
		  HandlerList.unregisterAll(this);
	}
	
	public void onCancelled() {
		//
	}
	
	public void onTeleport() {
		//
	}
	
	public void onInterval() {
		//
	}
	
	public final boolean isCancelled() {
		return cancelled;
	}
	
	public final boolean hasTeleported() {
		return teleported;
	}
	
	public final RunicPlayer getPlayer() {
		return player;
	}
	
	public final Location getDestination() {
		return destination;
	}
	
	public final int getTimeLeft() {
		return timeLeft;
	}
	
	public final TeleportCancelCause getCancelCause() {
		return cancelReason;
	}
	
	@EventHandler
	public final void onPlayerMove(PlayerMoveEvent e) {
		if(player.equals(e.getPlayer())) {
			if(e.getFrom().getBlockX() == e.getTo().getBlockX()
					&& e.getFrom().getBlockY() == e.getTo().getBlockY()
					&& e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;
			if(!teleported && !cancelled) cancel(TeleportCancelCause.PLAYER_MOVE);
		}
	}
	
	@EventHandler
	public final void onPlayerTeleport(PlayerTeleportEvent e) {
		if(player.equals(e.getPlayer())) {
			if(!teleported && !cancelled) cancel(TeleportCancelCause.PLAYER_MOVE);
		}
	}
	
	@EventHandler
	public final void onCombatTag(CombatTagEvent e) {
		if(player.equals(e.getPlayer().getPlayer())) {
			if(!teleported && !cancelled) cancel(TeleportCancelCause.COMBAT_TAGGED);
		}
	}
	
	@EventHandler
	public final void onPlayerQuit(PlayerQuitEvent e) {
		if(player.equals(e.getPlayer()) && !teleported && !cancelled) cancel(TeleportCancelCause.PLAYER_QUIT);
	}
	
	@EventHandler
	public void onChuckLoad(ChunkLoadEvent e) {
		for(Entity entity : e.getChunk().getEntities()) {
			if(entity instanceof Player) {
				RunicPlayer player = RunicPlayer.cast(entity);
				Location top = player.getLocation();
				top.setY(player.getWorld().getHighestBlockYAt(player.getLocation()));
				player.teleport(top);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player && player.equals(e.getEntity()) && teleported && (System.currentTimeMillis() - teleportTime < teleportProtection*1000 || e.getCause().equals(DamageCause.SUFFOCATION))) {
			e.setCancelled(true);
			
			// teleport player to top if they're suffocating
			if(e.getCause().equals(DamageCause.SUFFOCATION) && System.currentTimeMillis() - teleportTime < 10*1000) {
				int top = e.getEntity().getLocation().getWorld().getHighestBlockYAt(e.getEntity().getLocation()) + 1;
				Location highest_block = e.getEntity().getLocation().clone();
				highest_block.setY(top);
				e.getEntity().teleport(highest_block);
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DelayedTeleport) {
			return uuid.equals(((DelayedTeleport) obj).uuid);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "DelayedTeleport::"+player.getName();
	}
	
}
