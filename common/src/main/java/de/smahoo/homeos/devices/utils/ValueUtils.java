package de.smahoo.homeos.utils;

import java.util.Date;

public class ValueUtils {

	
	
	public static int compare(Class<?> c, Object value1, Object value2) throws Exception{
		int res = 0;
		
		if (!c.isInstance(value1)) throw new Exception("value1 ("+value1+") is not instance of class "+c.getCanonicalName());
		if (!c.isInstance(value2)) throw new Exception("value1 ("+value2+") is not instance of class "+c.getCanonicalName());
		
		if (c == Integer.class){
			if ((Integer)value1 > (Integer)value2){
				res = 1;
			} else {
				if ((Integer)value1 < (Integer)value2){
					res = -1;
				}
			}
		}
		
		if (c == Double.class){
			if ((Double)value1 > (Double)value2){
				res = 1;
			} else {
				if ((Double)value1 < (Double)value2){
					res = -1;
				}
			}
		}
		if (c == Date.class){
			//FIXME
		}
		
		
		return res;
	}
	
}
