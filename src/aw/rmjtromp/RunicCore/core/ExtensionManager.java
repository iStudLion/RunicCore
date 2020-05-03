package aw.rmjtromp.RunicCore.core;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import aw.rmjtromp.RunicCore.RunicCore;
import aw.rmjtromp.RunicCore.utilities.Debug.Debuggable;
import aw.rmjtromp.RunicCore.utilities.RunicExtension;

public final class ExtensionManager implements Debuggable {

	private static final RunicCore plugin = RunicCore.getInstance();
	
	private static HashMap<String, RunicExtension> extensions = new HashMap<String, RunicExtension>();
	private static HashMap<RunicExtension, List<String>> extWithMissingDepsList = new HashMap<>();
	
	
	@Override
	public String getName() {
		return "ExtensionManager";
	}
	
	private ExtensionManager() {
		File folder = new File(plugin.getDataFolder(), "extensions");
	    if (!folder.exists()) folder.mkdirs();
		registerAllExtension();
	}
	
	public static ExtensionManager init() {
		return new ExtensionManager();
	}
	
	public boolean hasExtension(String name) {
		if(extensions.containsKey(name.toLowerCase())) {
			RunicExtension ext = extensions.get(name.toLowerCase());
			if(ext != null) return ext.isEnabled();
		}
		return false;
	}
	
	public RunicExtension getExtension(String name) {
		if(hasExtension(name)) return extensions.get(name.toLowerCase());
		return null;
	}
	
	public static void unregisterAllExtensions() {
		for(RunicExtension extension : extensions.values()) {
			extension.disable();
		}
		extensions.clear();
		extWithMissingDepsList.clear();
	}
	
	public void reloadExtensions() {
		unregisterAllExtensions();
		registerAllExtension();
	}
	
	public boolean registerExtension(RunicExtension extension) {
		if(extension != null && extension.getName() != null && !extension.getName().isEmpty()) {
			extensions.put(extension.getName().toLowerCase(), extension);
			
			List<String> missingDependencies = new ArrayList<String>();
			if(extension.getDependencies().size() > 0) {
				for(String dependency : extension.getDependencies()) {
					if(!hasExtension(dependency)) missingDependencies.add(dependency.toLowerCase());
				}
			}
			
			if(missingDependencies.size() < 1) {
				try {
					extension.enable();
					List<RunicExtension> extensionsToRemove = new ArrayList<>();
					for(RunicExtension ext : extWithMissingDepsList.keySet()) {
						List<String> missdeps = extWithMissingDepsList.get(ext);
						for(String dependency : extWithMissingDepsList.get(ext)) {
							if(dependency.equalsIgnoreCase(extension.getName())) {
								missdeps.remove(dependency);
							}
						}
						extWithMissingDepsList.replace(ext, missdeps);
						
						if(missdeps.size() < 1) {
							try {
								ext.enable();
								extensionsToRemove.add(ext);
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
					for(RunicExtension ext : extensionsToRemove) {
						extWithMissingDepsList.remove(ext);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else {
				extWithMissingDepsList.put(extension, missingDependencies);
			}
			return true;
		} else  return false;
	}
	
	private void registerAllExtension() {
		debug("[ExtensionManager] Checking for extensions...");
	    if (plugin == null) {
	    	return;
	    }

	    List<Class<?>> subs = getClasses("extensions", RunicExtension.class);
	    if (subs == null || subs.isEmpty()) return;

		debug("[ExtensionManager] "+subs.size()+" "+(subs.size()>1?"extensions":"extension")+" found.");
	    for (Class<?> klass : subs) {
	    	RunicExtension ex = createInstance(klass);
	    	if (ex != null) {
	    		registerExtension(ex);
	    		debug("[ExtensionManager] Registering "+ex.getName()+"...");
	    	}
	    }
	}
	
	public RunicExtension registerExtension(String fileName) {
		List<Class<?>> subs = getClasses("extensions", fileName, RunicExtension.class);
		if (subs == null || subs.isEmpty()) return null;

		// only register the first instance found as an extension jar should only have 1 class
		// extending RunicExtension
		RunicExtension ex = createInstance(subs.get(0));
		if (registerExtension(ex)) return ex;
		return null;
	}
	
	private RunicExtension createInstance(Class<?> klass) {
		if (klass == null) {
			return null;
		}

		RunicExtension ex = null;
		if (!RunicExtension.class.isAssignableFrom(klass)) return null;

		try {
			Constructor<?>[] c = klass.getConstructors();
			if (c.length == 0) ex = (RunicExtension) klass.newInstance();
			else {
				for (Constructor<?> con : c) {
		        	if (con.getParameterTypes().length == 0) {
		        		ex = (RunicExtension) klass.newInstance();
		        		break;
		        	}
				}
			}
		} catch (Throwable t) {
			plugin.getLogger().severe("Failed to initialize RunicExtension from class: " + klass.getName());
			plugin.getLogger().severe(t.getMessage());
		}
		return ex;
	  }
	
	private List<Class<?>> getClasses(String folder, Class<?> type) {
		return getClasses(folder, null, type);
	}

	private List<Class<?>> getClasses(String folder, String fileName, Class<?> type) {
		List<Class<?>> list = new ArrayList<>();

		try {
			File f = new File(plugin.getDataFolder(), folder);
			if (!f.exists()) return list;

			FilenameFilter fileNameFilter = (dir, name) -> {
				if (fileName != null) {
					return name.endsWith(".jar") && name.replace(".jar", "").equalsIgnoreCase(fileName.replace(".jar", ""));
				}

				return name.endsWith(".jar");
			};

			File[] jars = f.listFiles(fileNameFilter);
			if (jars == null) return list;

			for (File file : jars) {
				list = gather(file.toURI().toURL(), list, type);
			}

			return list;
		} catch (Throwable t) {}
		return null;
	}
	
	  private static List<Class<?>> gather(URL jar, List<Class<?>> list, Class<?> clazz) {
		  if(list == null) list = new ArrayList<>();

		  try (URLClassLoader cl = new URLClassLoader(new URL[]{jar}, clazz.getClassLoader());
		  JarInputStream jis = new JarInputStream(jar.openStream())) {

			  while (true) {
				  JarEntry j = jis.getNextJarEntry();
				  if (j == null) break;

				  String name = j.getName();
				  if (name == null || name.isEmpty()) continue;

				  if (name.endsWith(".class")) {
					  name = name.replace("/", ".");
					  String cname = name.substring(0, name.lastIndexOf(".class"));

					  Class<?> c = cl.loadClass(cname);
					  if (clazz.isAssignableFrom(c)) {
						  list.add(c);
					  }
				  }
			  }
		  } catch (Throwable t) {}
		  return list;
	}
	  
}
