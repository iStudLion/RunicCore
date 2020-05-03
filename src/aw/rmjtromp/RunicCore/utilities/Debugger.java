package aw.rmjtromp.RunicCore.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;

@Deprecated
public class Debugger {

	private static final RunicCore plugin = RunicCore.getInstance();
	private static final File file = new File(plugin.getDataFolder() + File.separator + "debug.log");
	private static boolean enabled = Core.getConfig() != null ? Core.getConfig().getBoolean("debug", false) : plugin.getConfig() != null ? plugin.getConfig().getBoolean("debug", false) : false;
	private String prefix = "";
	
	public Debugger(String prefix) {
		if(enabled) {
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected Debugger(RunicFeature feature) {
		prefix = feature.getName();
		if(enabled) {
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected Debugger(RunicExtension extension) {
		prefix = extension.getName();
		if(enabled) {
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public final void log(String... args) {
		for(String arg : args) {
			write("[INFO] "+(prefix != null && !prefix.isEmpty() ? prefix+" " : "")+arg);
		}
	}
	
	public final void error(String... args) {
		for(String arg : args) {
			write("[ERROR] "+(prefix != null && !prefix.isEmpty() ? prefix+" " : "")+arg);
		}
	}
	
	public final void warn(String... args) {
		for(String arg : args) {
			write("[WARNING] "+(prefix != null && !prefix.isEmpty() ? prefix+" " : "")+arg);
		}
	}
	
	public final void severe(String... args) {
		for(String arg : args) {
			write("[SEVERE] "+(prefix != null && !prefix.isEmpty() ? prefix+" " : "")+arg);
		}
	}
	
//	public void log(String... args) {
//		for(String arg : args) {
//			write("[INFO] "+arg);
//		}
//	}
//	
//	public static void error(String... args) {
//		for(String arg : args) {
//			write("[ERROR] "+arg);
//		}
//	}
//	
//	public static void warn(String... args) {
//		for(String arg : args) {
//			write("[WARNING] "+arg);
//		}
//	}
//	
//	public static void severe(String... args) {
//		for(String arg : args) {
//			write("[SEVERE] "+arg);
//		}
//	}
	
	private final static void write(String string) {
		if(enabled) {
			try {
				if(!file.exists()) file.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
				writer.append(string);
				writer.append("\n");
				
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
