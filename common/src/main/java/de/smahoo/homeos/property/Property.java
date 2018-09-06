package de.smahoo.homeos.property;

import java.util.ArrayList;
import java.util.List;

public class Property {

	Class<?> valueClass = null;
	PropertyType valueType;
	protected Object value = null;
	String name = null;
	protected List<PropertyEventListener> eventListeners = null;	
	protected String unit		= null;
	
	
	public void addEventListener(PropertyEventListener listener){
		if (eventListeners.contains(listener)) return;
		eventListeners.add(listener);
	}
	
	
	public void setUnit(String unit){
		this.unit = unit;
	}
	
	public boolean hasUnit(){
		return unit != null;
	}
	
	public String getUnit(){
		return unit;
	}
	
	public void removeEventListener(PropertyEventListener listener){
		if (eventListeners.contains(listener)){
			eventListeners.remove(listener);
		}
	}
	
	protected void dispatchEvent(PropertyEvent evnt){
		if (eventListeners.isEmpty()) return;
		for (PropertyEventListener listener : eventListeners){
			listener.onPropertyEvent(evnt);
		}
	}
	
	public Property(PropertyType valueType, String name){
		this.valueType = valueType;
		eventListeners = new ArrayList<PropertyEventListener>();
		this.name = name;
		this.valueClass = PropertyType.getValueClass(valueType);
	}
	
	public Property(PropertyType valueType, Object value, String name){
		this(valueType,name);		
		this.value = value;
	}
	
	public synchronized void setValue(String value) throws NumberFormatException{
		this.value = PropertyType.getValue(this.valueType, value);
	}
	
	public Class<?> getValueClass(){
		return valueClass;
	}
	
	public void setValue(Object value){
		
		this.value = value;		
	}

	public PropertyType getPropertyType(){
		return valueType;
	}
	
	public synchronized Object getValue(){
		return value;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean isValueSet(){
		return (this.value != null);
	}
	
	
	
	@Override
	public boolean equals(Object value){
		if (!isValueSet()) return false;
		if (!this.value.getClass().isInstance(value)) return false;		
		return this.value.equals(value);
	}
}
