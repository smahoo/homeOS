package de.smahoo.homeos.common;

import java.util.Date;

public abstract class Event {
		
	protected String description = null;
	protected EventType eventType = null;
	protected Date timestamp = null;
	
	public Event(EventType eventType){
		this.timestamp = new Date();
		this.eventType = eventType;
	}
	
	public Event(EventType eventType, String description){
		this(eventType);
		this.description = description;
	}
	
	public boolean hasDescription(){
		return description != null;
	}
	
	public boolean isError(){
		return eventType == EventType.ERROR;
	}
	
	public EventType getEventType(){
		return eventType;
	}
	
	public Date getTimeStamp(){
		return timestamp;
	}
	
	public String getDescription(){
		return description;
	}
	
	@Override
	public String toString(){
		if (hasDescription()){
			return eventType.name()+" "+getDescription();
		} else return eventType.name();
	}
}
