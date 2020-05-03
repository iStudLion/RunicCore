package aw.rmjtromp.RunicCore.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;
import aw.rmjtromp.RunicCore.utilities.configs.Config;

public abstract class RunicExtension {
	
	protected static final RunicCore plugin = RunicCore.getInstance();
	protected static final Library library = plugin.getLibrary();
	private boolean enabled = Core.getConfig().getBoolean("extensions."+getName().toLowerCase()+".enabled", true);
	private List<RunicCommand> registeredCommands = new ArrayList<RunicCommand>();

	public final void enable() {
		if(enabled) {
			System.out.print("[RunicCore] Enabling "+getName()+" extension.");
			if(this instanceof Listener) plugin.getServer().getPluginManager().registerEvents((Listener) this, plugin);
			onEnable();
			loadConfigurations();
		}
	}
	
	public final void disable() {
		if(this instanceof Listener) HandlerList.unregisterAll((Listener) this);
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
	
	public final Config getConfig() {
		return Core.getConfig().getSection("extensions."+getName());
	}

	public void onEnable() {}
	public void onDisable() {}
	public void loadConfigurations() {}

	public abstract String getName();
	public abstract String getVersion();
	public List<String> getDependencies() {
		return new ArrayList<String>();
	}
	
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
	public boolean equals(Object object) {
		if(object == null) return false;
		if(object instanceof RunicExtension) {
			if(getName().equals(((RunicExtension) object).getName())) return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "RunicExtension::"+getName();
	}
	
}
