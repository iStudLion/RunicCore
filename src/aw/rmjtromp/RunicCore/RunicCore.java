package aw.rmjtromp.RunicCore;

import org.bukkit.plugin.java.JavaPlugin;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.utilities.Debug;
import aw.rmjtromp.RunicCore.utilities.Library;

public final class RunicCore extends JavaPlugin {
	
	private static RunicCore plugin;
	
	private Library library;
	
	private Core core;

	@Override
	public void onEnable() {
		if(isEnabled()) {
			plugin = this;
			Debug.init();
			library = new Library();
			core = Core.init();
		}
	}
	
	/*
	 * Getters
	 */
	
	public static RunicCore getInstance() {
		return plugin;
	}
	
	public Core getCore() {
		return core;
	}

	public Library getLibrary() {
		return library;
	}
	
}
