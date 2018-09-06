package de.smahoo.homeos.property;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;


public class PropertyEvent extends Event{

	Property property = null;
	
	public PropertyEvent(EventType eventType,Property property){
		super(eventType);
		this.property = property;
		
	}
	
	public Property getProperty(){
		return property;
	}
	
	@Override
	public String toString(){
		return eventType.name() +" "+ property.getName()+ " ("+property.getValue()+")";
	}
	
}