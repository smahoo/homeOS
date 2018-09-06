package de.smahoo.homeos.common;

import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;


public class FunctionParameter extends Property{
	
	public FunctionParameter(PropertyType propertyType, String name){
		super(propertyType,name);
	}
	
	public FunctionParameter(PropertyType propertyType, Object value, String name){
		super(propertyType, value,name);
	}
	
}
