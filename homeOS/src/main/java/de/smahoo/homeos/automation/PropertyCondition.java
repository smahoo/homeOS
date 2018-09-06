package de.smahoo.homeos.automation;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyEvent;
import de.smahoo.homeos.property.PropertyEventListener;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.homeos.utils.ValueUtils;

public class PropertyCondition extends Condition implements PropertyEventListener{

	public static final int OT_EQUAL 		= 0;
	public static final int OT_LOWER 		= 1;
	public static final int OT_LOWER_EQUAL 	= 2;
	public static final int OT_HIGHER 		= 3;
	public static final int OT_HIGHER_EQUAL = 4;
	public static final int OT_NOT 			= 5;
	
	private Property 	property 	= null;
	private Object 		value 		= null;
	private int 		operatorType;
	private boolean complied = false;
	
	public PropertyCondition(Property property, Object value, int operatorType){
		this.property = property;
		property.addEventListener(this);
		this.value = value;
		this.operatorType = operatorType;
	}
	
	protected void prepareDeletion(){
		if (property == null){
			return;
		}
		property.removeEventListener(this);
	}
	
	public PropertyCondition(Property property, String strValue, int operatorType){
		this.property = property;
		property.addEventListener(this);		
		this.operatorType = operatorType;		
		value = PropertyType.getValue(property.getPropertyType(), strValue);
		
	}
	
	public PropertyCondition(Property property, String strValue, String operatorType){
		this(property,strValue,PropertyCondition.getOperatorType(operatorType));
	}
	
	private void checkCompliance(){		
		switch(operatorType){
		case OT_EQUAL: complied = property.equals(value); break;
		case OT_HIGHER:  complied = isHigher(property.getValue()); break;
		case OT_HIGHER_EQUAL : complied = (property.equals(value))||(isHigher(property.getValue())); break;
		case OT_LOWER: complied = !isHigher(property.getValue());
		case OT_LOWER_EQUAL: complied = !isHigher(property.getValue()) || property.equals(value); break;
		case OT_NOT: complied = !property.equals(value); break;
		}				
	}
	
	private boolean isHigher(Object value){
		int compare = 0;
		try {
			compare = ValueUtils.compare(property.getValueClass(),this.value, value);
			return compare == -1;
		} catch (Exception exc){
			exc.printStackTrace();
		}
		return false;
	}
	
	
	public boolean isComplied(){
		if (isActive()){
			return complied;
		} else return true;  // need to return true. compliance will not be checked when inactive -> inactive condition should not prevent
							 // overall compliance of all active conditions  
	}
	
	public void onPropertyEvent(PropertyEvent evnt){
		if (!isActive()) return;
		if (evnt.getEventType() != EventType.PROPERTY_VALUE_CHANGED) return;
		boolean tmp = complied;
		checkCompliance();		
		// only fire when compliance state changed to true. 
		if ((isComplied()) && (!tmp)){
			this.dispatchConditionCompliedEvent(new ConditionCompliedEvent(this));
		}
	}
	
	public static int getOperatorType(String strType){
		if (strType.equalsIgnoreCase("OT_EQUAL")) 		return OT_EQUAL;
		if (strType.equalsIgnoreCase("OT_LOWER")) 		return OT_LOWER;
		if (strType.equalsIgnoreCase("OT_LOWER_EQUAL")) return OT_LOWER_EQUAL;
		if (strType.equalsIgnoreCase("OT_HIGHER")) 		return OT_HIGHER;
		if (strType.equalsIgnoreCase("OT_HIGHER_EQUAL"))return OT_HIGHER_EQUAL;
		if (strType.equalsIgnoreCase("OT_NOT"))			return OT_NOT;
		return OT_EQUAL;
	}
	
	public static String getOperatorSymbol(int operatorType){
		switch(operatorType){
		case OT_EQUAL: return "=";
		case OT_HIGHER:  return ">";
		case OT_HIGHER_EQUAL : return ">=";
		case OT_LOWER: return "<";
		case OT_LOWER_EQUAL: return "<=";
		case OT_NOT: return "!=";
		}	
		return "";
	}
	
	public String toString(){
		String strProperty = property.getName();
		if (property instanceof DeviceProperty){
			strProperty = ((DeviceProperty)property).getDevice().getDeviceId()+"."+strProperty;
		}	
		return "Property: "+strProperty+" "+getOperatorSymbol(this.operatorType)+" "+value;
	}
}
