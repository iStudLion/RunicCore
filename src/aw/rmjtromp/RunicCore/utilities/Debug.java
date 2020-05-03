package aw.rmjtromp.RunicCore.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;

public class Debug implements Listener {
	
	private static final RunicCore plugin = RunicCore.getInstance();
	private static final File file = new File(plugin.getDataFolder() + File.separator + "debug.log");
	private static boolean enabled = Core.getConfig() != null ? Core.getConfig().getBoolean("debug", false) : plugin.getConfig() != null ? plugin.getConfig().getBoolean("debug", false) : false;
	private static BufferedWriter writer = null;
	
	private static Debug debug = null;
	
	private Debug() {
		debug = this;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		if(enabled) {
			try {
				if(file.exists()) file.delete();
				file.createNewFile();
				writer = new BufferedWriter(new FileWriter(file, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Debug init() {
		return debug != null ? debug : new Debug();
	}
	
	private static void write(String string) {
		if(enabled) {
			try {
				if(!file.exists()) file.createNewFile();
				if(writer == null) {
					writer = new BufferedWriter(new FileWriter(file, true));
				}

				writer.append(string);
				writer.append("\n");
				
				writer.close();
				writer = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		if(e.getPlugin().equals(plugin)) {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	@EventHandler
	public void onRunicCoreReload(RunicCoreReloadEvent e) {
		boolean before = enabled;
		enabled = Core.getConfig() != null ? Core.getConfig().getBoolean("debug", false) : plugin.getConfig() != null ? plugin.getConfig().getBoolean("debug", false) : false;
		
		// if it was just enabled
		if(before == false && enabled == true) {
			try {
				if(writer == null) {
					if(file.exists()) file.delete();
					file.createNewFile();
					
					writer = new BufferedWriter(new FileWriter(file, true));
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public interface Debuggable {
		
		String getName();
		
		default void debug(String... args) {
			for(String arg : args) {
				write("[INFO] "+arg);
			}
		}
		
		default void error(String... args) {
			for(String arg : args) {
				plugin.getLogger().log(Level.WARNING, arg);
				write("[ERROR] "+arg);
			}
		}
		
		default void warn(String... args) {
			for(String arg : args) {
				plugin.getLogger().log(Level.WARNING, arg);
				write("[WARNING] "+arg);
			}
		}
		
		default void severe(String... args) {
			for(String arg : args) {
				plugin.getLogger().log(Level.SEVERE, arg);
				write("[SEVERE] "+arg);
			}
		}
		
	}
	
}
