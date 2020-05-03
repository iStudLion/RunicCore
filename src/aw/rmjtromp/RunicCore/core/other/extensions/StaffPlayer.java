package aw.rmjtromp.RunicCore.core.other.extensions;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class StaffPlayer extends RunicPlayer implements Listener {

	private static HashMap<UUID, StaffPlayer> players = new HashMap<>();
	
	public enum ACTION {
		BLOCK_BREAK(STATE.DENY),
		BLOCK_PLACE(STATE.DENY);
		
		private STATE state;
		ACTION(STATE state) {
			this.state = state;
		}
		
		public STATE getDefault() {
			return state;
		}
	}
	
	public enum STATE {
		ALLOW,
		DENY;
	}
	
	private HashMap<ACTION, STATE> permissions = new HashMap<>();
	
	public static StaffPlayer cast(Object arg0) {
		if(arg0 instanceof StaffPlayer) return (StaffPlayer) arg0;
		else {
			RunicPlayer player = RunicPlayer.cast(arg0);
			if(player != null) {
				if(players.containsKey(player.getUniqueId())) return players.get(player.getUniqueId());
				else {
					players.put(player.getUniqueId(), new StaffPlayer(player));
					return players.get(player.getUniqueId());
				}
			}
		}
		return null;
	}
	
	private StaffPlayer(RunicPlayer player) {
		super(player.getPlayer());
		
		// load values from config
		
		
		// loads default values
		for(ACTION action : ACTION.values()) {
			if(!permissions.containsKey(action)) permissions.put(action, action.getDefault());
		}
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public RunicPlayer getRunicPlayer() {
		return new RunicPlayer(this);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(this.equals(e.getPlayer())) {
			
//        	Unregister player after 10 seconds
    		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
  				public void run() {
  					if(players.containsKey(e.getPlayer().getUniqueId())) {
  						if(!e.getPlayer().isOnline()) {
  	  						try { HandlerList.unregisterAll(players.get(e.getPlayer().getUniqueId())); } catch(Exception no) {}
  	  						players.remove(e.getPlayer().getUniqueId());
  						}
  					}
  				}
    		}, 10*20);
		}
	}

}
