package de.smahoo.homeos.utils;

public class AttributeValuePair {
	String value;
	String attribute;
	
	public AttributeValuePair(String attribute){
		this.attribute = attribute;
		this.value = null;
	}
	
	public AttributeValuePair(String attribute, String value){
		this.attribute= attribute;
		this.value = value;
	}
	
	
	public void setAttribute(String attribute){
		this.attribute = attribute;
	}
	
	public String getAttribute(){
		return attribute;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	@Override
	public String toString(){
		return attribute+"=\""+value+"\"";
	}
}
