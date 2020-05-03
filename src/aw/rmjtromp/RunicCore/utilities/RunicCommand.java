package aw.rmjtromp.RunicCore.utilities;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.utilities.configs.MessageConfig.MESSAGE;

public final class RunicCommand {
	
	protected final UUID uuid = UUID.randomUUID();
	
	private CommandExecutor executor = null;
	private TabCompleter tabcompleter = null;
	
	private String name = null;
	private String description = null;
	private List<String> aliases = null;
	private String usage = "/<command>";
	private String permission = null;
	private String permission_message = Core.getMessages().getMessage(MESSAGE.NO_PERMISSION);

	public RunicCommand(String name) {
		this.setName(name);
	}
	
	public RunicCommand(String name, String description, List<String> aliases, String usage) {
		this.setName(name); this.setDescription(description); this.setAliases(aliases); this.setUsage(usage);
	}
	
	public RunicCommand(String name, String description, List<String> aliases) {
		this.setName(name); this.setDescription(description); this.setAliases(aliases);
	}
	
	public RunicCommand(String name, String description, String usage) {
		this.setName(name); this.setDescription(description); this.setUsage(usage);
	}
	
	public RunicCommand(String name, String description) {
		this.setName(name); this.setDescription(description);
	}

    public RunicCommand setExecutor(CommandExecutor executor){
        this.executor = executor;
        return this;
    }
    
    public CommandExecutor getCommandExecutor() {
    	return executor;
    }
    
    public RunicCommand setTabCompleter(TabCompleter tabcompleter) {
    	this.tabcompleter = tabcompleter;
        return this;
    }
    
    public TabCompleter getTabCompleter() {
    	return tabcompleter;
    }

	public String getName() {
		return name;
	}

	public RunicCommand setName(String name) {
		this.name = name;
        return this;
	}

	public String getDescription() {
		return description;
	}

	public RunicCommand setDescription(String description) {
		this.description = description;
        return this;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public RunicCommand setAliases(List<String> aliases) {
		this.aliases = aliases;
        return this;
	}

	public String getUsage() {
		return usage;
	}

	public RunicCommand setUsage(String usage) {
		this.usage = usage;
        return this;
	}

	public String getPermission() {
		return permission;
	}

	public RunicCommand setPermission(String permission) {
		this.permission = permission;
        return this;
	}

	public String getPermissionMessage() {
		return ChatColor.translateAlternateColorCodes('&', permission_message);
	}

	public RunicCommand setPermissionMessage(String message) {
		this.permission_message = message;
        return this;
	}
	
	public boolean isRegistered() {
		return RunicCommandManager.isCommandRegistered(this);
	}
	
	public boolean register() {
		return RunicCommandManager.registerCommand(this);
	}
	
	public boolean unregister() {
		return RunicCommandManager.unregisterCommand(this);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == null) return false;
		if(object instanceof RunicCommand) {
			if(uuid.equals(((RunicCommand) object).uuid)) return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "RunicCommand::"+name.toLowerCase();
	}
    
}
