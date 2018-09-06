package de.smahoo.homeos.device.role;

import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyEvent;
import de.smahoo.homeos.property.PropertyEventListener;
import de.smahoo.homeos.property.PropertyType;

public class RoleProperty extends Property implements PropertyEventListener{

	protected DeviceRole deviceRole = null;
	protected Property bindedProperty = null;
	
	
	public RoleProperty(PropertyType valueType, String name){
		super(valueType,name);
	}
	
	protected void bindProperty(Property property){
		property.addEventListener(this);
		bindedProperty = property;
	}
	
	protected void unbindProperty(){
		if (bindedProperty == null) return;
		bindedProperty.removeEventListener(this);
		bindedProperty = null;
	}
	
	public void onPropertyEvent(PropertyEvent evnt){
		this.dispatchEvent(evnt);
	}
	
	public Property getBindedProperty(){
		return bindedProperty;
	}
	
	public Object getValue(){
		return bindedProperty.getValue();
	}
	
	
}
