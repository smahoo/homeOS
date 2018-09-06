package de.smahoo.homeos.kernel.remote.result.request;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.kernel.remote.result.DeviceRequestResultItem;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;
import de.smahoo.homeos.location.Location;

public class DeviceListRequestResultItem extends RemoteResultItem{
	
	List<DeviceRequestResultItem> deviceItems;
	Location location = null;
	DeviceType deviceType = null;
	
	public DeviceListRequestResultItem(){
		deviceItems = new ArrayList<DeviceRequestResultItem>();
	}
	
	public void setLocation(Location location){
		this.location = location;
	}
	
	public void setDeviceType(DeviceType deviceType){
		this.deviceType = deviceType;
	}
	
	public void addDeviceRequestResultItem(DeviceRequestResultItem item){
		deviceItems.add(item);
	}
	
	public Element generateElement(Document doc){
		Element elem = doc.createElement("devicelist");
		if (location != null){
			elem.setAttribute("location",location.getId());			
		}
		if (deviceType != null){
			elem.setAttribute("type",deviceType.name().toLowerCase());
		}
		for (DeviceRequestResultItem item : deviceItems){
			try {
				elem.appendChild(item.generateElement(doc));
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}	
		
		return elem;
	}
}
