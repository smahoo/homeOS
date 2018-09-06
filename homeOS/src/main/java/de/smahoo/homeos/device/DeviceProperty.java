package de.smahoo.homeos.device;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyEvent;
import de.smahoo.homeos.property.PropertyType;


public class DeviceProperty extends Property{

	
	
	protected Device parent 	= null;


	public DeviceProperty(PropertyType propertyType, String name, String unit){
		super(propertyType,name);		
		this.unit = unit;
	}
	
	
	protected void setValueWithoutEvent(Object value){
		this.value = value;
	}
	
	
	
	@Override
	public synchronized void setValue(Object value){
		if (value.equals(getValue())) return;
		super.setValue(value);
		dispatchEvent(new PropertyEvent(EventType.PROPERTY_VALUE_CHANGED, this));
	}
	
	public synchronized boolean isValueSet(){
		return (super.getValue() != null);
	}
	
	
	
		
	@Override
	public String toString(){
		if (super.getValue() != null){
		  return getName()+" = "+ getValueClass().cast(getValue()).toString()+unit;
		} else return getName()+" not set";
	}
	
	
	public Device getDevice(){
		return parent;
	}
	
}
