package aw.rmjtromp.RunicCore.core.features.essentials;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;

public class Condense extends RunicFeature implements CommandExecutor, TabCompleter {

	@Override
	public String getName() {
		return "Condense";
	}
	
	private enum PERMISSION {
		CONDENSE_SELF("runic.condense"),
		CONDENSE_OTHERS("runic.condense.others");
		
		private String permission;
		PERMISSION(String permission) {
			this.permission = permission;
		}
		
		@Override
		public String toString() {
			return permission;
		}
	}
	
	@Override
	public void onEnable() {
		registerCommand(new RunicCommand("condense")
				.setDescription("Marks you as away-from-keyboard")
				.setAliases(Arrays.asList("compact", "blocks", "toblock", "toblocks"))
				.setPermission(PERMISSION.CONDENSE_SELF.toString())
				.setUsage("/condense [player]")
				.setExecutor(this)
				.setTabCompleter(this));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
