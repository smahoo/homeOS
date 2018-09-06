package de.smahoo.homeos.device;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.smahoo.homeos.location.Location;


public interface Device {
	public String 	getName();
	public String 	getDeviceId();
	public void 	setName(String name);
	public Location getLocation();
	public void 	assignLocation(Location location);	
	public boolean 	isHidden();	
	public boolean  isOn();	
	public boolean  isAvailable();
	public List<PropertyHistoryData> getHistoryData(Date start, Date end);
	public Date getLastActivityTimeStamp();
	

}
