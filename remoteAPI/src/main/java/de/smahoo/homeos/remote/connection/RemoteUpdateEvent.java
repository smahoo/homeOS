package de.smahoo.homeos.remote.connection;

import de.smahoo.homeos.common.EventType;

public class RemoteUpdateEvent extends RemoteConnectionEvent{
		
	public RemoteUpdateEvent(String message){
		super(EventType.UPDATE,message);		
	}
		
}
