package aw.rmjtromp.RunicCore.core.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;
import aw.rmjtromp.RunicCore.utilities.Debug.Debuggable;
import aw.rmjtromp.RunicCore.utilities.Library;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.RunicCommandManager;

public abstract class RunicFeature implements Listener, Debuggable, Comparable<RunicFeature> {

	protected static final RunicCore plugin = RunicCore.getInstance();
	protected static final Library library = plugin.getLibrary();
	private static HashMap<String, RunicFeature> features = new HashMap<>();
	protected boolean enabled = true;
	private List<RunicCommand> registeredCommands = new ArrayList<RunicCommand>();
	private UUID uuid = UUID.randomUUID();
	
	public RunicFeature() {
		enable();
	}
	
	public RunicFeature(boolean enabled) {
		this.enabled = enabled;
		enable();
	}

	public final void enable() {
		if(enabled) {
			if(getName() != null && !getName().equalsIgnoreCase("null")) {
				features.put(getName(), this);
				plugin.getServer().getPluginManager().registerEvents(this, plugin);
				onEnable();
				loadConfigurations();
				debug("enabled");
			} else error("Runic feature name can not be 'null' (at "+getClass().getCanonicalName()+"), not enabling extension.");
		}
	}
	
	public static final Collection<RunicFeature> getFeatures() {
		return features.values();
	}
	
	public final void disable() {
		HandlerList.unregisterAll(this);
		List<RunicCommand> clone = new ArrayList<RunicCommand>();
		clone.addAll(registeredCommands);
		for(RunicCommand command : clone) unregisterCommand(command);
		clone.clear();
		enabled = false;
		onDisable();
	}
	
	protected final boolean registerCommand(RunicCommand command) {
		boolean registered = RunicCommandManager.registerCommand(command);
		if(registered) registeredCommands.add(command);
		return registered;
	}
	
	protected final boolean unregisterCommand(RunicCommand command) {
		if(registeredCommands.contains(command)) {
			boolean unregistered = RunicCommandManager.unregisterCommand(command);
			if(unregistered) registeredCommands.remove(command);
			return unregistered;
		}
		return false;
	}
	
	public final boolean isEnabled() {
		return enabled;
	}

	public void onEnable() {}
	public void onDisable() {}
	public void loadConfigurations() {}
	
	public abstract String getName();
	
	@EventHandler
	public void onRunicCoreReload(RunicCoreReloadEvent e) {
		if(!e.isCancelled()) {
			loadConfigurations();
		}
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		if(e.getPlugin() instanceof RunicCore) {
			disable();
		}
	}
	
	@Override
	public final boolean equals(Object obj) {
		if(obj instanceof RunicFeature) return uuid.equals(((RunicFeature) obj).uuid);
		return false;
	}
	
	@Override
	public final int compareTo(RunicFeature o) {
		return getName().compareToIgnoreCase(o.getName());
	}
	
}
