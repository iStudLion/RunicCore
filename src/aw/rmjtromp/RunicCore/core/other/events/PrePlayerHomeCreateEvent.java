package aw.rmjtromp.RunicCore.core.other.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;

public final class PrePlayerHomeCreateEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	
	private boolean cancelled = false;
	private RunicPlayer player;
	private String reason;
	
	public PrePlayerHomeCreateEvent(RunicPlayer player) {
		this.player = player;
	}
	
	public RunicPlayer getPlayer() {
		return player;
	}
	
	public void setReason(String r) {
		this.reason = r;
	}
	
	public String getReason() {
		return reason;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}
	
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
