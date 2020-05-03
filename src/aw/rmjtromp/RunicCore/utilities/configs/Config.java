package aw.rmjtromp.RunicCore.utilities.configs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import com.avaje.ebean.validation.NotNull;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.core.other.events.RunicCoreReloadEvent;
import aw.rmjtromp.RunicCore.utilities.Debug.Debuggable;

public class Config implements Listener, Debuggable {

	@Override
	public String getName() {
		return "Config";
	}
	
	private RunicCore plugin = RunicCore.getInstance();
	private static HashMap<File, Config> configs = new HashMap<File, Config>();
	
	private FileConfiguration dataConfig;
	private File file = null;
	
	private String folder = null;
	private String filename = null;
	
	private String section;
	
	protected Config(String filename) {
		this.filename = filename;
		reassignFile();
		configs.put(file, this);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	protected Config(String folder, String filename) {
		this.filename = filename;
		this.folder = folder;
		reassignFile();
		configs.put(file, this);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	// basically clone
	@Deprecated
	private Config(Config config) {
		this.file = config.file;
		this.dataConfig = config.dataConfig;
	}
	
	@NotNull
	public static Config init(String filename) {
		if(configs.containsKey(buildFile(null, filename))) return configs.get(buildFile(null, filename));
		else return new Config(filename);
	}
	
	@NotNull
	public static Config init(String folder, String filename) {
		if(configs.containsKey(buildFile(folder, filename))) return configs.get(buildFile(folder, filename));
		else return new Config(folder, filename);
	}
	
	public Config load() {
		if(!save()) error("There was an error saving file ("+file.getName()+")");
		return this;
	}

	@NotNull
	public Config loadFromResource(String resourceName) {
		if(file == null) reassignFile();
		if(!file.exists()) {
			if(save()) {
				try {
					resourceName = resourceName.startsWith("/") ? resourceName : "/"+resourceName;
					resourceName = resourceName.endsWith(".yml") ? resourceName : resourceName+".yml";
					if(this.getClass().getResource(resourceName) != null) {
						InputStream input = this.getClass().getResourceAsStream(resourceName);
						BufferedReader reader = new BufferedReader(new InputStreamReader(input));
						BufferedWriter writer = new BufferedWriter(new FileWriter(file));
						
						String line = null;
					    while ((line = reader.readLine()) != null) {
					        writer.write(line);
					        writer.newLine();
					    }
					    
					    writer.close();
					    reader.close();
					    r(); // load the new config
					} else error("Resource ("+resourceName+") doesnt exist");
				} catch (IOException e) {
					severe(e.getMessage());
				}
			} else error("There was an error saving file ("+file.getName()+")");
		} else {
			if(!save()) error("There was an error saving file ("+file.getName()+")");
		}
		return this;
	}
	
	@NotNull
	public Config loadFromInputStream(InputStream input) {
		if(file == null) reassignFile();
		if(!file.exists()) {
			if(save()) {
				try {
					if(input != null) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(input));
						BufferedWriter writer = new BufferedWriter(new FileWriter(file));
						
						String line = null;
					    while ((line = reader.readLine()) != null) {
					        writer.write(line);
					        writer.newLine();
					    }
					    
					    writer.close();
					    reader.close();
					    r(); // load the new config
					} else error("Input stream can not be null");
				} catch (IOException e) {
					severe(e.getMessage());
				}
			} else error("There was an error saving file ("+file.getName()+")");
		} else {
			if(!save()) error("There was an error saving file ("+file.getName()+")");
		}
		return this;
	}
	
	public String getConfigName() {
		if(folder != null) {
			return folder+"/"+filename+".yml";
		}
		return filename+"yml";
	}
	
	/**
	 * Returns file config
	 * @return FileConfiguration
	 */
	public FileConfiguration getConfig() {
		return this.dataConfig;
	}
	
	/**
	 * Saves the config file
	 * @return boolean whether the file was successfully saved
	 */
	public boolean save() {
		if(dataConfig != null) {
			if(file != null && file.exists()) {
				// save
				return s();
			} else {
				// re-create the file and save
				createFile();
				return s();
			}
		} else {
			if(file != null && file.exists()) {
				dataConfig = YamlConfiguration.loadConfiguration(file);
				dataConfig.options().header("Copyright \u00A9 "+plugin.getName()+" "+Calendar.getInstance().get(Calendar.YEAR)+" All Rights Reserved");
				dataConfig.options().copyHeader(true);
				return s();
			} else {
				// re-create the file, assign to dataConfig and save
				createFile();
				dataConfig = YamlConfiguration.loadConfiguration(file);
				dataConfig.options().header("Copyright \u00A9 "+plugin.getName()+" "+Calendar.getInstance().get(Calendar.YEAR)+" All Rights Reserved");
				dataConfig.options().copyHeader(true);
				return s();
			}
		}
	}
	
	private boolean s() {
		try {
			dataConfig.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void r() {
		dataConfig = YamlConfiguration.loadConfiguration(file);
	}
	
	public void reload() {
		if(file != null && file.exists()) {
			// reload
			r();
		} else {
			// file doesn't exist, save current dataConfig to file
			save();
		}	
	}
	
	private void reassignFile() {
		String filepath = !filename.startsWith("/") ? filename : filename.substring(1);
		filepath = filepath.endsWith(".yml") ? filepath : filepath+".yml";
		if(this.folder != null && !this.folder.isEmpty()) {
			File folder = new File(plugin.getDataFolder() + File.separator + this.folder);
			if(!folder.exists()) folder.mkdir();
			file = new File(plugin.getDataFolder() + File.separator + this.folder + File.separator + filepath);
		} else file = new File(plugin.getDataFolder() + File.separator + filepath);
	}
	
	private static File buildFile(final String folder, final String file) {
		String filepath = !file.startsWith("/") ? file : file.substring(1);
		filepath = filepath.endsWith(".yml") ? filepath : filepath+".yml";
		if(folder != null && !folder.isEmpty()) {
			File f = new File(RunicCore.getInstance().getDataFolder() + File.separator + folder);
			if(!f.exists()) f.mkdir();
			return new File(RunicCore.getInstance().getDataFolder() + File.separator + f + File.separator + filepath);
		} else return new File(RunicCore.getInstance().getDataFolder() + File.separator + filepath);
	}
	
	private boolean createFile() {
		if(file == null || !file.exists()) reassignFile();
		if(!file.exists()) {
			try {
				file.createNewFile();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else return true;
	}
	
	public boolean exists() {
		if(file == null) reassignFile();
		return file.exists();
	}

	@NotNull
	public String getString(String arg0) {
		if(!dataConfig.contains(getPath(arg0)) || !dataConfig.isString(getPath(arg0))) {
			return "null";
		}
		return dataConfig.getString(getPath(arg0));
	}

	@NotNull
	public boolean getBoolean(String arg0) {
		if(!dataConfig.contains(getPath(arg0)) || !dataConfig.isBoolean(getPath(arg0))) {
			return false;
		}
		return dataConfig.getBoolean(getPath(arg0));
	}

	@NotNull
	public int getInt(String arg0) {
		if(!dataConfig.contains(getPath(arg0)) || !dataConfig.isInt(getPath(arg0))) {
			return -1;
		}
		return dataConfig.getInt(getPath(arg0));
	}

	@NotNull
	public long getFloat(String arg0) {
		if(!dataConfig.contains(getPath(arg0)) || !dataConfig.isLong(getPath(arg0))) {
			return -1L;
		}
		return dataConfig.getLong(getPath(arg0));
	}

	@NotNull
	public double getDouble(String arg0) {
		if(!dataConfig.contains(getPath(arg0)) || !dataConfig.isDouble(getPath(arg0))) {
			return -1;
		}
		return dataConfig.getDouble(getPath(arg0));
	}

	@NotNull
	public OfflinePlayer getOfflinePlayer(String arg0) {
		if(!dataConfig.contains(getPath(arg0)) || !dataConfig.isOfflinePlayer(getPath(arg0))) {
			return null;
		}
		return dataConfig.getOfflinePlayer(getPath(arg0));
	}

	@NotNull
	public org.bukkit.inventory.ItemStack getItemStack(String arg0) {
		if(!dataConfig.contains(getPath(arg0)) || !dataConfig.isItemStack(getPath(arg0))) {
			return null;
		}
		return dataConfig.getItemStack(getPath(arg0));
	}

	@NotNull
	public List<String> getStringList(String arg0) {
		if(!dataConfig.contains(getPath(arg0)) || !dataConfig.isList(getPath(arg0))) {
			return null;
		}
		return dataConfig.getStringList(getPath(arg0));
	}

	@NotNull
	public List<Integer> getIntegerList(String arg0) {
		if(!dataConfig.contains(getPath(arg0)) || !dataConfig.isList(getPath(arg0))) {
			return null;
		}
		return dataConfig.getIntegerList(getPath(arg0));
	}

	@NotNull
	public List<Boolean> getBooleanList(String arg0) {
		if(!dataConfig.contains(getPath(arg0)) || !dataConfig.isList(getPath(arg0))) {
			return null;
		}
		return dataConfig.getBooleanList(getPath(arg0));
	}

	@NotNull
	public String getString(String arg0, String defaultValue) {
		if(!dataConfig.contains(getPath(arg0))) set(getPath(arg0), defaultValue);
		return dataConfig.getString(getPath(arg0), defaultValue);
	}

	@NotNull
	public boolean getBoolean(String arg0, Boolean defaultValue) {
		if(!dataConfig.contains(getPath(arg0))) set(getPath(arg0), defaultValue);
		return dataConfig.getBoolean(getPath(arg0), defaultValue);
	}

	@NotNull
	public int getInt(String arg0, Integer defaultValue) {
		if(!dataConfig.contains(getPath(arg0))) set(getPath(arg0), defaultValue);
		return dataConfig.getInt(getPath(arg0), defaultValue);
	}

	@NotNull
	public long getLong(String arg0, Long defaultValue) {
		if(!dataConfig.contains(getPath(arg0))) set(getPath(arg0), defaultValue);
		return dataConfig.getLong(getPath(arg0), defaultValue);
	}

	@NotNull
	public double getDouble(String arg0, Double defaultValue) {
		if(!dataConfig.contains(getPath(arg0))) set(getPath(arg0), defaultValue);
		return dataConfig.getDouble(getPath(arg0), defaultValue);
	}

	@NotNull
	public org.bukkit.inventory.ItemStack getItemStack(String arg0, org.bukkit.inventory.ItemStack defaultValue) {
		if(!dataConfig.contains(getPath(arg0))) set(getPath(arg0), defaultValue);
		return dataConfig.getItemStack(getPath(arg0), defaultValue);
	}

	@NotNull
	public OfflinePlayer getOfflinePlayer(String arg0, OfflinePlayer defaultValue) {
		if(!dataConfig.contains(getPath(arg0))) set(getPath(arg0), defaultValue);
		return dataConfig.getOfflinePlayer(getPath(arg0), defaultValue);
	}
	
	@SuppressWarnings("unchecked")
	@NotNull
	public List<String> getStringList(String arg0, List<String> defaultValue) {
		if(!dataConfig.contains(getPath(arg0))) set(getPath(arg0), defaultValue);
		return (List<String>) dataConfig.getList(getPath(arg0), defaultValue);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public List<Integer> getIntegerList(String arg0, List<Integer> defaultValue) {
		if(!dataConfig.contains(getPath(arg0))) set(getPath(arg0), defaultValue);
		return (List<Integer>) dataConfig.getList(getPath(arg0), defaultValue);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public List<Boolean> getBooleanList(String arg0, List<Boolean> defaultValue) {
		if(!dataConfig.contains(getPath(arg0))) set(getPath(arg0), defaultValue);
		return (List<Boolean>) dataConfig.getList(getPath(arg0), defaultValue);
	}

	@NotNull
	public void set(String arg0, Object value) {
		dataConfig.set(getPath(arg0), value);
		this.save();
	}

	@NotNull
	public boolean contains(String arg0) {
		return this.dataConfig.contains(getPath(arg0));
	}

	@NotNull
	public Object get(String arg0) {
		if(contains(getPath(arg0))) {
			return this.dataConfig.get(getPath(arg0));
		}
		return null;
	}

	@NotNull
	public Object get(String arg0, Object obj) {
		if(contains(getPath(arg0))) {
			return this.dataConfig.get(getPath(arg0));
		} else {
			this.dataConfig.set(getPath(arg0), obj);
			this.save();
			return this.dataConfig.get(getPath(arg0));
		}
	}

	@NotNull
	public Set<String> getKeys(String arg0) {
		return dataConfig.getConfigurationSection(getPath(arg0)).getKeys(false);
	}
	
	public Set<String> getKeys() {
		return dataConfig.getKeys(false);
	}

	@NotNull
	public boolean isString(String arg0) {
		return dataConfig.isString(getPath(arg0));
	}

	@NotNull
	public boolean isBoolean(String arg0) {
		return dataConfig.isBoolean(getPath(arg0));
	}

	@NotNull
	public boolean isInteger(String arg0) {
		return dataConfig.isInt(getPath(arg0));
	}

	@NotNull
	public boolean isList(String arg0) {
		return dataConfig.isList(getPath(arg0));
	}
	
	@NotNull
	public boolean isDouble(String arg0) {
		return dataConfig.isDouble(arg0);
	}
	
	private Config setSection(String arg0) {
		section = arg0;
		return this;
	}
	
	@NotNull
	public Config getSection(String arg0) {
		if(arg0 != null && !arg0.isEmpty()) {
			return new Config(this).setSection(arg0.endsWith(".") ? arg0 : arg0+".");
		}
		return this;
	}
	
	private String getPath(String arg0) {
		return (section != null && !section.isEmpty()) ? section+arg0 : arg0;
	}

	public boolean hasKeys(String arg0) {
		if(contains(arg0) && getKeys(arg0) != null && getKeys(arg0) instanceof Set && getKeys(arg0).size() > 0) return true;
		return false;
	}

	public boolean hasKeys() {
		if(getKeys() != null && getKeys() instanceof Set && getKeys().size() > 0) return true;
		return false;
	}
	
	public FileConfiguration getFileConfiguration() {
		return dataConfig;
	}
	
	@EventHandler
	public void onRunicCoreReload(RunicCoreReloadEvent e) {
		save();
		reload();
	}
	
	@EventHandler
	public void onPluginDisable(PluginDisableEvent e) {
		if(e.getPlugin().equals(plugin)) {
			save();
			HandlerList.unregisterAll(this);
		}
	}
	
}
