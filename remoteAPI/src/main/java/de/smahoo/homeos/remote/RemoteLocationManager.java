package de.smahoo.homeos.remote;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.location.LocationType;

public class RemoteLocationManager {
	
	private List<RemoteLocation> locationList;

	
	public RemoteLocationManager(){
		locationList = new ArrayList<RemoteLocation>();
	}
	
	
	public List<Location> getLocations(){
		List<Location> list = new ArrayList<Location>();
		for (Location location : locationList){
			list.add(location);
		}
		return list;
	}
	
	
	public List<Location> getAllLocations(){
		List<Location> list = new ArrayList<Location>();
		for (Location location : locationList){
			addLocation(list,location);
		}
		return list;
	}
	
	private void addLocation(List<Location> list, Location location){
		list.add(location);
		if (location.hasChildLocations()){
			for (Location loc : location.getChildLocations()){
				addLocation(list,loc);
			}
		}
	}

	public RemoteLocation getLocation(String id){
		for(Location location : getAllLocations()){
			if (location.getId().equals(id)) return (RemoteLocation)location;
		}
		return null;
	}

	public void init(Element elem){
		if (!elem.getTagName().equalsIgnoreCase("locationlist")) return;
		NodeList nodelist = elem.getChildNodes();
		for (int i = 0; i<nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				locationList.add(generateLocation((Element)nodelist.item(i)));
			}
		}
	}
	
	protected RemoteLocation generateLocation(Element elem){
		String id = elem.getAttribute("id");
		String name = elem.getAttribute("name");
		LocationType type = LocationType.valueOf(elem.getAttribute("type"));
		RemoteLocation loc = new RemoteLocation();
		loc.locationType = type;
		loc.id = id;
		loc.name = name;
		
		if (elem.hasChildNodes()){
			NodeList nodelist = elem.getChildNodes();
			for (int i = 0; i< nodelist.getLength(); i++){
				if (nodelist.item(i) instanceof Element){
					RemoteLocation tmp = generateLocation((Element)nodelist.item(i));
					tmp.parent = loc;
					loc.childLocations.add(tmp);
				}
			}
		}
		return loc;
	}
}
