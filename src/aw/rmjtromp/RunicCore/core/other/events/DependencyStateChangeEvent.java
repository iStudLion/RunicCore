package aw.rmjtromp.RunicCore.core.other.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import aw.rmjtromp.RunicCore.utilities.DependencyManager.Dependency;

public class DependencyStateChangeEvent extends Event {
	
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private Dependency dependency;
	
	public DependencyStateChangeEvent(Dependency dependency) {
		this.dependency = dependency;
	}
	
	public Dependency getDependency() {
		return dependency;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}
	
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
	
}
