package aw.rmjtromp.RunicCore.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import aw.rmjtromp.RunicCore.RunicCore;

public final class BungeeCord implements PluginMessageListener {
	
	private static final RunicCore plugin = RunicCore.getInstance();
	private List<String> servers = new ArrayList<String>();
	private String server;
	
	private boolean hasBungee = false;
	
	private BungeeCord() {
		enable();
	}
	
	public static BungeeCord init() {
		return new BungeeCord();
	}
	
	private void enable() {
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
		getServers();
		getServer();
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		hasBungee = true;
        if(channel.equals("BungeeCord")) {
            try {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
                String subchannel = in.readUTF();
             
    	        if(subchannel.equals("GetServers")) {
    	                 String[] serverList = in.readUTF().split(", ");
    	                 for(String server : serverList) {
    	                	 if(!servers.contains(server)) servers.add(server);
    	                 }
    	        } else if(subchannel.equals("GetServer")) {
                    server = in.readUTF();
    	        }
    	    } catch (Exception e) {
                 e.printStackTrace();
            }
        }
        System.out.print(channel);
	}
	
	private void getServers() {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream  out = new DataOutputStream(b);
		    try {
		         out.writeUTF("GetServers");
		     } catch(Exception e) {
		         e.printStackTrace();
		     }
		Bukkit.getServer().sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}
	
	private void getServer() {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream  out = new DataOutputStream(b);
		    try {
		         out.writeUTF("GetServer");
		     } catch(Exception e) {
		         e.printStackTrace();
		     }
		Bukkit.getServer().sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}
	
	public List<String> getServerList() {
		return servers;
	}
	
	public String getServerName() {
		if(server == null || server.isEmpty()) getServer();
		return server;
	}
	
	public boolean hasBungee() {
		return hasBungee;
	}
	
}
