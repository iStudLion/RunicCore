package aw.rmjtromp.RunicCore.core.features.main.general;

import java.lang.reflect.Field;

import org.bukkit.scheduler.BukkitRunnable;

import aw.rmjtromp.RunicCore.core.features.RunicFeature;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

public final class Tablist extends RunicFeature {

	@Override
	public String getName() {
		return "Tablist";
	}
	
	@Override
	public void onEnable() {
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        new BukkitRunnable() {

            @Override
            public void run() {
            	if(RunicPlayer.getOnlinePlayers().size() > 0) {
                    try {
                        Field a = packet.getClass().getDeclaredField("a");
                        a.setAccessible(true);
                        Field b = packet.getClass().getDeclaredField("b");
                        b.setAccessible(true);

                        Object header = new ChatComponentText("§&\n§6§lRunic§e§lSky\n");
                        Object footer = new ChatComponentText("\n§eplay.runicsky.com\n§7");
                        
                        a.set(packet, header);
                        b.set(packet, footer);

                        for (RunicPlayer player : RunicPlayer.getOnlinePlayers()) {
                            player.getCraftPlayer().getHandle().playerConnection.sendPacket(packet);
                        }

                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        error("There was an error sending tablist packet: "+e.getMessage());
                    }
            	}
            }
        }.runTaskTimer(plugin, 0, 20);
	}

}
