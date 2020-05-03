package aw.rmjtromp.RunicCore.core.features.main.moderation;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.PunishmentResult;
import aw.rmjtromp.RunicCore.core.other.PunishmentResult.PunishmentType;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicOfflinePlayer;

public class Punishment extends RunicFeature implements CommandExecutor, TabCompleter {
	
	public Punishment() {
		super(false); // disabled
	}

	@Override
	public String getName() {
		return "Punishment";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}
	
	public static List<PunishmentResult> getPunishments(RunicOfflinePlayer player) {
		return null;
	}
	
	public static PunishmentResult punish(RunicOfflinePlayer player, PunishmentType type, String reason, int expiration) {
		return null;
	}

}
