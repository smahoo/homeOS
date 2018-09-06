package de.smahoo.homeos.kernel.remote.result.request;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;
import de.smahoo.homeos.location.Location;

public class LocationRequestResultItem extends RemoteResultItem{
	
	private Location location = null;
	
	public LocationRequestResultItem(Location location){
		this.location = location;
	}
	
	public LocationRequestResultItem(String message){
		this.location = null;
		this.setMessage(message);
		this.setSuccess(false);
	}
	
	public Element generateElement(Document doc, boolean recursive){
		Element result = generateElement(doc);
		if (!recursive) return result;
		List<Location> locList = location.getChildLocations();		
		LocationRequestResultItem itemTmp;
		for (Location loc : locList){
			itemTmp = new LocationRequestResultItem(loc);
			result.appendChild(itemTmp.generateElement(doc, recursive));
		}
		return result;
	}
	
	public Element generateElement(Document doc){
		Element result = doc.createElement("location");
		if (location != null){
			result.setAttribute("id",location.getId());
			result.setAttribute("name",location.getName());
			result.setAttribute("type", location.getLocationType().name());
			result.setAttribute("success", "true");
		} else {
			result.setAttribute("success", "false");
			Element msg = doc.createElement("message");
			msg.setTextContent(this.getMessage());
			result.appendChild(msg);
		}
		return result;
	}
}
