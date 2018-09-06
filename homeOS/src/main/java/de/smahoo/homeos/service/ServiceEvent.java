package de.smahoo.homeos.service;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;

public class ServiceEvent extends Event{

	private ServiceState state;
	private Service service;
	private String description = null;
	
	public ServiceEvent(final ServiceState state, final Service service){
		super(EventType.SERVICE_STATE);
		this.state = state;
		this.service = service;
	}
	
	public ServiceEvent(final ServiceState state, final Service service, final String description){		
		this(state,service);
		this.description = description;
	}
	
	public boolean hasDescription(){
		return (description != null);
	}
	
	public String getDescription(){
		return description;
	}
	
	public String toString(){
		String res = "Service \'"+service.getName()+"\' ";
		
		switch(state){
		case STARTING : res = res + "is starting"; break;
		case STARTED  : res = res + "is started";  break;
		case STOPPING : res = res + "is stopping"; break;
		case STOPPED  : res = res + "is stopped";  break;
		case PAUSING  : res = res + "is pausing";  break;
		case PAUSED   : res = res + "is paused";   break;
		case ERROR    : res = "ERROR "+description; break;
		}
		
		return res;
	}
}
