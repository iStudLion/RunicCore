package aw.rmjtromp.RunicCore.core.other.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;

public final class PlayerSpawnEvent extends Event {

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private RunicPlayer player;
	
	public PlayerSpawnEvent(RunicPlayer player) {
		this.player = player;
	}
	
	public RunicPlayer getPlayer() {
		return player;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}
	
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
