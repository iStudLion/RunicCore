package aw.rmjtromp.RunicCore.core.other.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import aw.rmjtromp.RunicCore.core.other.extensions.RunicPlayer;

public class CombatTagEvent extends Event implements Cancellable {
	
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	
	private boolean cancelled = false;
	private RunicPlayer player, offender = null;
	
	public CombatTagEvent(RunicPlayer player) {
		this.player = player;
	}
	
	public CombatTagEvent(RunicPlayer player, RunicPlayer offender) {
		this.player = player;
		this.offender = offender;
	}
	
	public RunicPlayer getPlayer() {
		return this.player;
	}
	
	public boolean hasOffender() {
		return offender != null;
	}
	
	public RunicPlayer getOffender() {
		return offender;
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
