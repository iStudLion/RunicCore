package aw.rmjtromp.RunicCore.core.features.main.links;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.Timer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import aw.rmjtromp.RunicCore.core.Core;
import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import aw.rmjtromp.RunicCore.utilities.RunicCommand;
import aw.rmjtromp.RunicCore.utilities.placeholders.Placeholder;

public final class Discord extends RunicFeature implements CommandExecutor {

	private static String url = "";
	private static long last_check = 0;
	
	@Override
	public String getName() {
		return "Discord";
	}
	
	@Override
	public void onEnable() {
		updateInviteUrl();
		ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
            	updateInviteUrl();
            }
        };

		Timer timer = new Timer(1000*30*60,al); // Timer(TimeInMilliSeconds, ActionListener) 1000ms = 1s
		timer.setRepeats(true); // repeat timer to update URL every 30 minutes
		timer.start();

		registerCommand(new RunicCommand("discord")
				.setDescription("Shows the Discord invite URL")
				.setAliases(Arrays.asList("disc"))
				.setUsage("/discord")
				.setExecutor(this));
	}
	
	private String discord_join;
	@Override
	public void loadConfigurations() {
		// if \\u25ba doesnt work, use ►
		discord_join = Core.getMessages().getString("feature.link.discord", "[\"\",{\"text\":\"\\u25ba\",\"color\":\"dark_gray\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"{LINK}\"}},{\"text\":\" \",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"{LINK}\"}},{\"text\":\"Click \",\"color\":\"gray\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"{LINK}\"}},{\"text\":\"here\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"{LINK}\"}},{\"text\":\" to join our Discord server.\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"{LINK}\"}}]");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			RunicPlayer player = RunicPlayer.cast(sender);
			player.sendJSONMessage(Placeholder.parse(discord_join, player).set("{LINK}", getInviteUrl()).getString());
		} else {
			sender.sendMessage(url);
		}
		return true;
	}
	
	private void updateInviteUrl() {
		if(url == null || url.isEmpty() || System.currentTimeMillis() - last_check > 29*1000*60) {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
		        @Override
		        public void run() {
					try {				
						URL urlObj = new URL("https://runicsky.com/proxy.php");
						HttpURLConnection httpCon = (HttpURLConnection) urlObj.openConnection();
					
						if(httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
							BufferedReader in = new BufferedReader(
							new InputStreamReader(httpCon.getInputStream()));
							String line, response = "";
							while ((line = in.readLine()) != null) {
								if (response.length() == 0) response = line;
								else response += "\n" + line;
							}
							in.close();
							
							JSONObject json = (JSONObject) new JSONParser().parse(response);
							
							if(json.containsKey("instant_invite")) {
								String inviteURL = json.get("instant_invite").toString();
								new URL(inviteURL).toURI(); // if url is invalid, it will throw exception and not continue
								
								url = inviteURL;
								last_check = System.currentTimeMillis();
							}
						}
					} catch (IOException | ParseException | URISyntaxException e) {
						e.printStackTrace();
					}
		        }
		    });
		}
	}
	
	public static String getInviteUrl() {
		return url.isEmpty() ? "https://runicsky.com/#discord" : url;
	}

}
