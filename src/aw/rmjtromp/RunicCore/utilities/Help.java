package aw.rmjtromp.RunicCore.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;

public class Help {

	public static Help create(int max) {
		return new Help(max);
	}
	
	public static Help create() {
		return new Help(9);
	}
	
	private int max = 9;
	private HashMap<String, String> commands = new HashMap<String, String>();
	
	private Help(int max) {
		this.max = max;
	}
	
	public enum Usage {
		PLAYER_ONLY,
		CONSOLE_ONLY,
		ALL
	}
	
	public void addCommand(String command, String description, Usage usage, String permission) {
		if(!commands.containsKey(command.toLowerCase())) {
			commands.put(command.toLowerCase(), description);
		}
	}
	
	public void removeCommand(String command) {
		if(commands.containsKey(command.toLowerCase())) {
			commands.remove(command.toLowerCase());
		}
	}
	
	public boolean containsCommand(String command) {
		return commands.containsKey(command.toLowerCase());
	}
	
	public List<String> getHelp(String label, CommandSender sender, int page) {
		TreeMap<String, String> sortedHelp = new TreeMap<String, String>();
		sortedHelp.putAll(commands);
		page = page < 1 ? 1 : page > getPageCount() ? getPageCount() : page;
		List<String> help = new ArrayList<String>();

		int offset = page*this.max-this.max;
		int max = page*this.max > sortedHelp.size() ? sortedHelp.size() : page*this.max;
		for(int i = offset; i < max; i++) {
			String command = (String) sortedHelp.keySet().toArray()[i];
			String description = (String) sortedHelp.values().toArray()[i];
			help.add("&e/"+label.toLowerCase()+" "+command+" &6- &7"+description);
		}
		
		help.add("&ehelp &7- (Page &f"+page+"&7/"+getPageCount()+")");
		return help;
	}
	
	public List<String> getHelp(String label, CommandSender sender) {
		return getHelp(label, sender, 1);
	}
	
	public List<String> getHelp(CommandSender sender) {
		return getHelp(sender, 1);
	}
	
	public List<String> getHelp(CommandSender sender, int page) {
		TreeMap<String, String> sortedHelp = new TreeMap<String, String>();
		sortedHelp.putAll(commands);
		page = page < 1 ? 1 : page > getPageCount() ? getPageCount() : page;
		List<String> help = new ArrayList<String>();

		int offset = page*this.max-this.max;
		int max = page*this.max > sortedHelp.size() ? sortedHelp.size() : page*this.max;
		for(int i = offset; i < max; i++) {
			String command = (String) sortedHelp.keySet().toArray()[i];
			String description = (String) sortedHelp.values().toArray()[i];
			help.add("&e/"+command+" &6- &7"+description);
		}
		
		help.add("&eHelp &7- (Page &f"+page+"&7/"+getPageCount()+")");
		return help;
	}
	
	public int getPageCount() {
		return (int) (Math.floor(commands.size()/max)+(commands.size()%max > 0 ? 1 : 0));
	}
	
}
