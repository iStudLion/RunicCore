package aw.rmjtromp.RunicCore.utilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import aw.rmjtromp.RunicCore.RunicCore;

public final class RunicCommandManager {

	private static final RunicCore plugin = (RunicCore) Bukkit.getPluginManager().getPlugin("RunicCore");
	
	public static boolean registerCommand(RunicCommand cmd) {
		if(cmd == null) {
			System.out.println("RunicCommand command can not be null.");
			return false;
		}
        
		if(plugin.getCommand(cmd.getName()) == null) {
			PluginCommand command = getCommand(cmd.getName(), plugin);
			if(cmd.getCommandExecutor() != null) {
//				command.setName(cmd.getName());
				if(cmd.getPermission() != null) command.setPermission(cmd.getPermission());
				if(cmd.getPermissionMessage() != null) command.setPermissionMessage(cmd.getPermissionMessage());
				if(cmd.getAliases() != null) command.setAliases(cmd.getAliases());
				if(cmd.getDescription() != null) command.setDescription(cmd.getDescription());
				if(cmd.getUsage() != null) command.setUsage(cmd.getUsage());
				
			    try {
			    	getCommandMap().register(plugin.getName(), command);

				    plugin.getCommand(command.getName()).setExecutor(cmd.getCommandExecutor());
				    if(cmd.getTabCompleter() != null) {
				    	plugin.getCommand(command.getName()).setTabCompleter(cmd.getTabCompleter());
				    }
				    return true;
				} catch (Exception e) {
					return false;
				}
			}
		}
		return false;
	}
	
	public static boolean unregisterCommand(RunicCommand cmd) {
		if(plugin.getCommand(cmd.getName()) != null) {
			PluginCommand command = getCommand(cmd.getName(), plugin);
		    try {
		        Object result = getPrivateField(plugin.getServer().getPluginManager(), "commandMap");
		        SimpleCommandMap commandMap = (SimpleCommandMap) result;
		        Object map = getPrivateField(commandMap, "knownCommands");
		        @SuppressWarnings("unchecked")
		        HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
		        knownCommands.remove(command.getName());
		        for (String alias : command.getAliases()){
		           if(knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(command.getName())){
		                knownCommands.remove(alias);
		            }
		        }
		        return true;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		return false;
	}
	
	public static boolean isCommandRegistered(RunicCommand cmd) {
		if(plugin.getCommand(cmd.getName()) != null) {
			PluginCommand command = getCommand(cmd.getName(), plugin);
		    try {
		        Object result = getPrivateField(plugin.getServer().getPluginManager(), "commandMap");
		        SimpleCommandMap commandMap = (SimpleCommandMap) result;
		        Object map = getPrivateField(commandMap, "knownCommands");
		        @SuppressWarnings("unchecked")
		        HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
		        
		        // checks if command exist, and checks if it has same description and aliases
		        if(knownCommands.containsKey(command.getName())) {
		        	if(knownCommands.get(command.getName()).getDescription().equals(command.getDescription())) {
		        		if(knownCommands.get(command.getName()).getAliases().containsAll(command.getAliases())) {
		        			return true;
		        		}
		        	}
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		return false;
	}
	
	private static Object getPrivateField(Object object, String field)throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
	    Class<?> clazz = object.getClass();
	    Field objectField = clazz.getDeclaredField(field);
	    objectField.setAccessible(true);
	    Object result = objectField.get(object);
	    objectField.setAccessible(false);
	    return result;
	}
	
	  private static PluginCommand getCommand(String name, Plugin plugin) {
	    PluginCommand command = null;
	    try {
	      Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(new Class[] { String.class, Plugin.class });
	      c.setAccessible(true);
	      
	      command = (PluginCommand)c.newInstance(new Object[] { name, plugin });
	    } catch (SecurityException e) {
	      e.printStackTrace();
	    } catch (IllegalArgumentException e) {
	      e.printStackTrace();
	    } catch (IllegalAccessException e) {
	      e.printStackTrace();
	    } catch (InstantiationException e) {
	      e.printStackTrace();
	    } catch (InvocationTargetException e) {
	      e.printStackTrace();
	    } catch (NoSuchMethodException e) {
	      e.printStackTrace();
	    }
	    return command;
	  }
	  
	  private static CommandMap getCommandMap() {
	    CommandMap commandMap = null;
	    try {
	      if ((Bukkit.getPluginManager() instanceof SimplePluginManager)) {
	        Field f = SimplePluginManager.class.getDeclaredField("commandMap");
	        f.setAccessible(true);
	        
	        commandMap = (CommandMap)f.get(Bukkit.getPluginManager());
	      }
	    } catch (NoSuchFieldException e) {
	      e.printStackTrace();
	    } catch (SecurityException e) {
	      e.printStackTrace();
	    } catch (IllegalArgumentException e) {
	      e.printStackTrace();
	    } catch (IllegalAccessException e) {
	      e.printStackTrace();
	    }
	    return commandMap;
	  }
	
}
