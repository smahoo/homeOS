package de.smahoo.homeos.kernel.remote;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;

public class RemoteEvent extends Event {

	public RemoteEvent(EventType type, String message){
		super(type,message);
	}
	
}
