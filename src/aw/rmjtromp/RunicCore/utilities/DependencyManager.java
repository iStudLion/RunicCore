package aw.rmjtromp.RunicCore.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.other.events.DependencyStateChangeEvent;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.chat.Chat;

public final class DependencyManager implements Listener {
	
	private static final RunicCore plugin = (RunicCore) Bukkit.getPluginManager().getPlugin("RunicCore");
	
	private static Economy Economy = null;
	private static Chat Chat = null;
	private static ProtocolManager ProtocolManager = null;

	private DependencyManager() {
		enable();
	}
	
	public enum Dependency {
		/*VAULT("Vault") {
			@Override
			public void register() {
				super.register();
				Economy = null;
				Chat = null;
				
				if(getInstance() != null && getInstance().isEnabled()) {
					RegisteredServiceProvider<Economy> ersp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
					if (ersp != null) Economy = ersp.getProvider();
					
					RegisteredServiceProvider<Chat> crsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
				    if(crsp != null) Chat = crsp.getProvider();
				}
			}
			
			@Override
			public void unregister() {
				super.unregister();
				Economy = null;
			}
		},*/
		PLACEHOLDERAPI("PlaceholderAPI"),
		CITIZENS("Citizens"),
		VOTIFIER("Votifier"),
		PROTOCOLLIB("ProtocolLib") {
			@Override
			public void register() {
				super.register();
				ProtocolManager = null;
				ProtocolManager = ProtocolLibrary.getProtocolManager();
			}
			
			@Override
			public void unregister() {
				super.unregister();
				ProtocolManager = null;
			}
		},
		WORLDGUARD("WorldGuard"),
		WORLDEDIT("WorldEdit");
		
		private String name="";
		private static List<Dependency> registered = new ArrayList<>();
		
		Dependency(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public Plugin getInstance() {
			return Bukkit.getPluginManager().getPlugin(getName());
		}
		
		public boolean isRegistered() {
			return registered.contains(this);
		}
		
		public void register() {
			if(!isRegistered()) {
				registered.add(this);
				DependencyStateChangeEvent DSCE = new DependencyStateChangeEvent(this);
				Bukkit.getPluginManager().callEvent(DSCE);
			}
		}
		
		public void unregister() {
			if(isRegistered()) {
				registered.remove(this);
				DependencyStateChangeEvent DSCE = new DependencyStateChangeEvent(this);
				Bukkit.getPluginManager().callEvent(DSCE);
			}
		}
		
		public static void reload() {
			List<Dependency> pre = new ArrayList<>();
			pre.addAll(registered);
			
			registered.clear();
			for(Dependency dependency : Dependency.values()) {
				if(Bukkit.getPluginManager().getPlugin(dependency.getName()) != null && Bukkit.getPluginManager().getPlugin(dependency.getName()).isEnabled()) {
					registered.add(dependency);
					if(!pre.contains(dependency)) dependency.register();
				} else {
					if(pre.contains(dependency)) dependency.unregister();
				}
			}
			
			pre.clear();
		}
		
	}
	
	private void enable() {
		onEnable();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	protected void onEnable() {
		Dependency.reload();
	}
	
	private void disable() {
		HandlerList.unregisterAll(this);
	}
	
	public static DependencyManager init() {
		return new DependencyManager();
	}
	
	@EventHandler
	public void onPluginEnable(PluginEnableEvent e) {
		for(Dependency dependency : Dependency.values()) {
			if(e.getPlugin().getName().equalsIgnoreCase(dependency.getName())) {
				if(!dependency.isRegistered()) dependency.register();
				break;
			}
		}
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		for(Dependency dependency : Dependency.values()) {
			if(e.getPlugin().getName().equalsIgnoreCase(dependency.getName())) {
				if(dependency.isRegistered()) dependency.unregister();
				break;
			}
		}
		if(e.getPlugin().equals(plugin)) disable();
	}
	
	@EventHandler
	public void onRunicCoreReload(RunicCoreReloadEvent e) {
		Dependency.reload();
	}
	
	@Deprecated
	public static Economy getEconomy() {
		return Economy;
	}
	
	@Deprecated
	public static Chat getChat() {
		return Chat;
	}
	
	public static ProtocolManager getProtocolManager() {
		return ProtocolManager;
	}
	
}
