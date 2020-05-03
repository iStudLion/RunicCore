package aw.rmjtromp.RunicCore.core.other.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import aw.rmjtromp.RunicCore.core.features.main.moderation.Flagger.FlagReason;
import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;

public final class PlayerFlagEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	
	private boolean cancelled = false;
	private RunicPlayer player = null;
	private FlagReason reason;
	private String message;
	
	public PlayerFlagEvent(RunicPlayer player, FlagReason reason, String message) {
		this.player = player; this.message = message; this.reason = reason;
	}
	
	public RunicPlayer getPlayer() {
		return this.player;
	}
	
	public FlagReason getReason() {
		return reason;
	}
	
	public void setReason(FlagReason reason) {
		this.reason = reason;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
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
