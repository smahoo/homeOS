package de.smahoo.homeos.device;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.homeos.utils.AttributeValuePair;

public class PropertyHistoryData {

	private Date timestamp;
	private Device device;
	private boolean on;
	private boolean available;
	private List<AttributeValuePair> values;
	
	public PropertyHistoryData(Date timestamp, Device device, boolean on, boolean available){
		this.timestamp = timestamp;		
		this.on = on;
		this.available = available;
		values = new ArrayList<AttributeValuePair>();
	}
	
	public Date getTimeStamp(){
		return timestamp;
	}
	
	
	public Object getValue(Property property){
		if (property == null){
			return null;
		}
		String value = this.getValue(property.getName());
		return PropertyType.getValue(property.getPropertyType(), value);
	}
	
	public boolean isAvailabe(){
		return available;
	}
	
	public boolean isOn(){
		return on;
	}
	
	public void addAttributeValuePair(AttributeValuePair avp){
		values.add(avp);
	}
	
	public String getValue(String propertyName){
		for (AttributeValuePair avp : values){
			if (avp.getAttribute().equalsIgnoreCase(propertyName)){
				return avp.getValue();
			}
		}
		return null;
	}
	
	public List<String> getPropertyNames(){
		List<String> names = new ArrayList<String>();
		if (this.values != null){
			if (!this.values.isEmpty()){
				for (AttributeValuePair avp : values){
					names.add(avp.getAttribute());
				}
			}
		}
		return names;
	}
	
	public List<AttributeValuePair> getValues(){
		return values;
	}
	
	
}
