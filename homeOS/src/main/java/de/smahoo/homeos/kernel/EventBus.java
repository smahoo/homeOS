package de.smahoo.homeos.kernel;

import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventListener;

public class EventBus {

	private List<EventListener> listeners = null;
	
	protected EventBus(){
		listeners = new ArrayList<EventListener>();
	}
	
	public void addListener(EventListener listener){
		synchronized (listeners) {
			if (listeners.contains(listener)) {
				return;
			}
			listeners.add(listener);
		}
	}
	
	public void removeListener(EventListener listener){
		synchronized (listeners) {
			if (listeners.isEmpty()) return;
			if (listeners.contains(listener)) {
				listeners.remove(listener);
			}
		}
	}
	
	protected synchronized void dispatch(final Event event){
		// FIXME: Dispatching has to be done in separate Thread
		for (EventListener listener : listeners){
			listener.onEvent(event);
		}
	}

}
