package aw.rmjtromp.RunicCore.utilities.essential;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.RunicExtension;

public final class Cooldown implements Listener {
	
	private static final RunicCore plugin = (RunicCore) Bukkit.getPluginManager().getPlugin("RunicCore");

	private String identifier = UUID.randomUUID().toString();
	
	private HashMap<RunicPlayer, Long> cooldown = new HashMap<RunicPlayer, Long>();
	private int sec = 3;
	
	public Cooldown(RunicExtension extension, int cooldown) {
		this.identifier = extension.getName();
		this.sec = cooldown;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public Cooldown(RunicFeature feature, int cooldown) {
		this.identifier = feature.getName();
		this.sec = cooldown;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public Cooldown(String name, int cooldown) {
		this.identifier = name+"{"+UUID.randomUUID().toString()+"}";
		this.sec = cooldown;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public boolean containsPlayer(RunicPlayer player) {
		if(cooldown.containsKey(player)) {
			long lastTime = cooldown.get(player);
			if(System.currentTimeMillis() - lastTime <= sec*1000) {
				return true;
			} else removePlayer(player);
		}
		return false;
	}
	
	public void addPlayer(RunicPlayer player) {
		if(cooldown.containsKey(player)) {
			cooldown.replace(player, System.currentTimeMillis());
		} else {
			cooldown.put(player, System.currentTimeMillis());
		}
	}
	
	public void removePlayer(RunicPlayer player) {
		if(cooldown.containsKey(player)) {
			cooldown.remove(player);
		}
	}
	
	public int getTimeLeft(RunicPlayer player) {
		if(containsPlayer(player)) {
			long lastTime = cooldown.get(player);
			int timeLeft = (int) (sec - (System.currentTimeMillis() - lastTime)/1000);
			return timeLeft > 0 ? timeLeft : 0;
		}
		return 0;
	}
	
	public long getTimeLeft(RunicPlayer player, boolean inMillies) {
		if(containsPlayer(player)) {
			long lastTime = cooldown.get(player);
			long timeLeft = 0;
			if(inMillies) {
				timeLeft = System.currentTimeMillis() - lastTime;
			} else timeLeft = (sec - (System.currentTimeMillis() - lastTime)/1000);
			return timeLeft > 0 ? timeLeft : 0;
		}
		return 0;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		RunicPlayer player = RunicPlayer.cast(e.getPlayer());
		if(containsPlayer(player)) removePlayer(player);
	}
	
	@EventHandler
	public void onRunicCoreReload(RunicCoreReloadEvent e) {
		for(RunicPlayer player : cooldown.keySet()) {
			if(!player.isOnline()) removePlayer(player);
		}
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		cooldown.clear();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Cooldown) {
			if(identifier.equals(((Cooldown) obj).identifier)) return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Cooldown::"+identifier;
	}
	
}
