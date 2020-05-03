package aw.rmjtromp.RunicCore.core.other.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;

public class PlayerReportEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	
	private boolean cancelled = false;
	private RunicPlayer player, target = null;
	private String reason;
	
	public PlayerReportEvent(RunicPlayer player, RunicPlayer target, String reason) {
		this.player = player; this.target = target; this.reason = reason;
	}
	
	public RunicPlayer getPlayer() {
		return this.player;
	}
	
	public RunicPlayer getTarget() {
		return target;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
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
