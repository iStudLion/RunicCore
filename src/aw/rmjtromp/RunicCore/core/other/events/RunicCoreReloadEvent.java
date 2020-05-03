package aw.rmjtromp.RunicCore.core.other.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class RunicCoreReloadEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	
	private boolean cancelled = false;
	private CommandSender executor;
	private String reason;
	
	public RunicCoreReloadEvent(CommandSender executor) {
		this.executor = executor;
	}
	
	public CommandSender getExecutor() {
		return executor;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		// removes color codes
		this.reason = reason.replaceAll("\\&[0-9a-frluko]", "");
	}
	
	public boolean hasReason() {
		return !reason.isEmpty() && reason != null;
	}

}
