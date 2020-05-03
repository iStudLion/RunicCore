package aw.rmjtromp.RunicCore.core.features.main.links;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.events.DependencyStateChangeEvent;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.Debug.Debuggable;
import aw.rmjtromp.RunicCore.utilities.DependencyManager.Dependency;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Vote extends RunicFeature implements CommandExecutor, Listener, Debuggable {
	
	@Override
	public String getName() {
		return "Vote";
	}
	
	private RunicCommand cmd;
	private Listener listener;
	
	@Override
	public void onEnable() {
		cmd = new RunicCommand("vote")
			.setDescription("Shows list of voting urls")
			.setAliases(Arrays.asList("vote"))
			.setUsage("/vote")
			.setExecutor(this);
		
		
		listener = new Listener() {
			
			@EventHandler
			public void onPlayerVote(VotifierEvent e) {
				RunicPlayer voter = RunicPlayer.getPlayer(e.getVote().getUsername());
				plugin.getServer().getConsoleSender().sendMessage(Placeholder.parse(vote_voted).set("{PLAYER}", voter.getName()).getString());
				for(RunicPlayer player : RunicPlayer.getOnlinePlayers()) { // broadcast json message to everyone
					player.sendJSONMessage(Placeholder.parse(vote_voted_json, player).set("{PLAYER}", voter.getName()).getString());
				}
			}
			
		};
		
		
		if(Dependency.VOTIFIER.isRegistered()) {
			registerVoting();
		}
	}
	
	private List<String> votingLinks = new ArrayList<String>();
	private String vote_not_setup, vote_voted, vote_voted_json, vote_header;
	@Override
	public void loadConfigurations() {
		vote_not_setup = Core.getMessages().getString("features.link.vote.not-setup", "&cVoting has not yet been setup by administrators.");
		vote_header = Core.getMessages().getString("features.link.vote.header", "&eClick one of the links below to vote for the server:");
		vote_voted = Core.getMessages().getString("features.link.vote.voted", "&8&l(&6!&8&l) &6{PLAYER} &evoted for the server and earned rewards.");
		vote_voted_json = Core.getMessages().getString("features.link.vote.voted-json", "[\"\",{\"text\":\"(\",\"bold\":true,\"color\":\"dark_gray\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vote\"}},{\"text\":\"!\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vote\"}},{\"text\":\")\",\"bold\":true,\"color\":\"dark_gray\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vote\"}},{\"text\":\" \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vote\"}},{\"text\":\"{PLAYER}\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vote\"}},{\"text\":\" voted for the server and earned rewards.\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/vote\"}}]");
		
		List<String> defaultVote = new ArrayList<String>();
		defaultVote.add("https://example.com/vote");
		
		votingLinks = Core.getConfig().getStringList("features.link.voting-links", defaultVote);
		if(votingLinks.contains("https://example.com/vote")) votingLinks.remove("https://example.com/vote");
		for(String link : votingLinks) {
			try {
				new URL(link).toURI();
			} catch (MalformedURLException | URISyntaxException e) {
				error("Invalid voting URL provided (skipping) : "+e.getMessage());
				votingLinks.remove(link);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(votingLinks.size() > 0) {
			sender.sendMessage(Placeholder.parse(vote_header+"\n&7"+Strings.join(votingLinks, "\n&7"), sender).getString());
		} else sender.sendMessage(Placeholder.parse(vote_not_setup, sender).getString());
		return true;
	}
	
	@EventHandler
	public void onDependencyStateChange(DependencyStateChangeEvent e) {
		if(e.getDependency().equals(Dependency.VOTIFIER)) {
			if(e.getDependency().isRegistered()) {
				// was now enabled
				registerVoting();
			} else {
				// was now disabled
				unregisterVoting();
			}
		}
	}
	
	private void registerVoting() {
		try {
			if(!cmd.isRegistered()) cmd.register();
			Bukkit.getPluginManager().registerEvents(listener, plugin);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void unregisterVoting() {
		try {
			if(cmd.isRegistered()) cmd.unregister();
			VotifierEvent.getHandlerList().unregister(listener);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
