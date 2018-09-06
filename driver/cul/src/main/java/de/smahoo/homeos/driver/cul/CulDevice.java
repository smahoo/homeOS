package de.smahoo.homeos.driver.cul;


import de.smahoo.homeos.device.PhysicalDevice;

import de.runge.cul.DeviceEvent;

public abstract class CulDevice extends PhysicalDevice{
	
	de.runge.cul.Device culDevice = null;	

	public CulDevice(de.runge.cul.Device device){		
		super(device.getDeviceId());
		
		this.culDevice = device;
		this.setAddress(device.getDeviceCode());		
		culDevice.addDeviceListener(new de.runge.cul.DeviceEventListener(){
			@Override
			public void onDeviceEvent(DeviceEvent evt){
				evaluateCulDeviceEvent(evt);
			}
		});
		setAvailability(false);
	}	
	
	@Override
	protected void setLastActivity(){
		super.setLastActivity();
	}
	
	@Override
	protected void setAvailability(boolean available){
		super.setAvailability(available);
	}
	
	protected void evaluateCulDeviceEvent(de.runge.cul.DeviceEvent evnt){		
		if (evnt.getEventType() == DeviceEvent.PROPERTY_CHANGED){			
			setLastActivity();
			setAvailability(true);
			applyProperties();	
			this.dispatchChangeEventsIfNeeded();
			
			
		//	this.dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.DEVICE_PROPERTY_CHANGED,this));	
		}
		if (evnt.getEventType() == DeviceEvent.CONIFURATION_STARTED){
			//this.setAvailability(false);
		}
		if (evnt.getEventType() == DeviceEvent.CONIFURATION_FINISHED){
		//	this.setAvailability(true);
		}
	}
		
	protected void enable(){	
		this.setAvailability(true);
	}
	
	protected void disable(){
		this.setAvailability(false);
	}
	
	abstract protected void applyProperties();
	
}
