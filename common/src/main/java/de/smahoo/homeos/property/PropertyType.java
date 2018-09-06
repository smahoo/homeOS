package de.smahoo.homeos.property;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum PropertyType {
	PT_BOOLEAN,
	PT_INTEGER,
	PT_STRING,
	PT_DOUBLE,
	PT_LONG,
	PT_DATE,	
	PT_UNKNOWN;
		
	
	public static Object getValue(PropertyType type, String value){
		SimpleDateFormat formatter;
		switch(type){
			case PT_INTEGER  : return Integer.parseInt(value);
			case PT_LONG	 : return Long.parseLong(value);
			case PT_DOUBLE   : return Double.parseDouble(value);
			case PT_STRING   : return value;	
			case PT_BOOLEAN  : return Boolean.parseBoolean(value);
			case PT_DATE	 : 
				// FIXME format datetime depending on value
				formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return formatter.format(value);				
		}
		return value;
	}
	
	public static PropertyType getType(Object value){
		if (value instanceof String) 	return PT_STRING;
		if (value instanceof Boolean) 	return PT_BOOLEAN;
		if (value instanceof Double) 	return PT_DOUBLE;
		if (value instanceof Integer) 	return PT_INTEGER;
		if (value instanceof Long) 		return PT_LONG;
		if (value instanceof Date)		return PT_DATE;
		
		
		return PT_UNKNOWN;
	}
		
	public static Class<?> getValueClass(PropertyType type){
		switch(type){
		case PT_INTEGER  : return Integer.class;
		case PT_LONG	 : return Long.class;
		case PT_DOUBLE   : return Double.class;
		case PT_STRING   : return String.class; 	
		case PT_BOOLEAN  : return Boolean.class;
		case PT_DATE	 : return Date.class;
		case PT_UNKNOWN:
			break;
		default:
			break;
		}
		
		return null;
	}
	
}
