package de.smahoo.homeos.remote.connection;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;

public class RemoteConnectionEvent extends Event{
	
	
	private String message;
	
	public RemoteConnectionEvent(EventType type, String message){
		super(type, "RemoteConnectionEvent ("+message+")");
		this.message = message;		
	}
	
	public String getMessage(){
		return message;
	}
	
}
