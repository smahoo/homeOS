package de.smahoo.homeos.kernel;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyEventListener;
import de.smahoo.homeos.property.PropertyType;

public class SystemClock {

	Property date = null;
	Timer dateTimer = null;
	
	public SystemClock(){
		date = new Property(PropertyType.PT_DATE,"");
		Date d = new Date();		
		date.setValue(d);
		dateTimer = new Timer();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);	
		Date start = new Date(d.getTime()+(60-calendar.get(Calendar.SECOND))*1000);		
		dateTimer.schedule(new DateUpdater(), start, 60000);		
	}
	
	public void addDatePropertyEventListener(PropertyEventListener listener){
		date.addEventListener(listener);
	}
	
	public void removeDatePropertyEventListener(PropertyEventListener listener){
		date.removeEventListener(listener);
	}
	

	public Date getDate(){
		return (Date)date.getValue();
	}
	
	public String getFormat(){
		return "yyyy-MM-dd HH:mm:ss";
	}
	
	private class DateUpdater extends TimerTask{
		public void run(){
			date.setValue(new Date());		
		}
	}
	
}
