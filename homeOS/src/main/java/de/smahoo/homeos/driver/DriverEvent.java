package de.smahoo.homeos.driver;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;

public class DriverEvent extends Event {

	public static final int READY 			= 1;	// everything works fine
	public static final int INITIALIZING 	= 2;	// driver is initializing
	public static final int PROBLEM 		= 3;	// driver is still running but problems occured
	public static final int ADDED			= 4;    // driver was added/loaded
	
	// ERRORS
	public static final int ERROR			 			= -1;
	public static final int ERROR_CONFIGURATION			= -2;
	
	protected Driver driver;
	
	public DriverEvent(EventType eventType, Driver driver){
		super(eventType);
		this.driver = driver;		
	}
	
	public DriverEvent(EventType eventType, Driver driver, String description){
		this(eventType,driver);
		this.description  = description;
	}
			
	@Override
	public String toString(){
		String res = null;
		
		switch (eventType){
			case DRIVER_INITIALIZING: res = eventType.name()+" "+driver.getName()+" "+driver.getVersion(); break;		
			case ERROR: res = "ERROR "+driver.getName()+" "+driver.getVersion(); break;
			case ERROR_CONFIGURATION: res = "CONFIGURATION_ERROR "+driver.getName()+" "+driver.getVersion(); break;
			case READY: res = "READY "+ driver.getName()+" "+driver.getVersion()+" READY "; break;
		}		
		if (res == null){
			res = super.toString()+" "+driver.getName()+" "+driver.getVersion(); 
		} else {
			if (hasDescription()){
				res = res + " "+getDescription();
			}
		}
		return res;
	}
	
	public Driver getDriver(){
		return driver;
	}
}
