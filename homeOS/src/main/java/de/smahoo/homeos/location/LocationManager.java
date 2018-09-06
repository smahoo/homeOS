package de.smahoo.homeos.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.location.LocationEvent;
import de.smahoo.homeos.location.LocationEventListener;
import de.smahoo.homeos.location.LocationType;

public class LocationManager implements LocationEventListener{

	private static int idCounter = 0;
	
	private HashMap<String,Location> allLocations;	
	private List<LocationImpl> locationList;
	private List<LocationEventListener> eventListeners;
	
	public LocationManager(){
		allLocations = new HashMap<String,Location>();
		locationList = new ArrayList<LocationImpl>();
		eventListeners = new ArrayList<LocationEventListener>();		
	}
	
	public void init(Element elem){
		if (elem == null) return;	
		if (!elem.hasChildNodes()) return;
		Node tmp;
		Location tmpLoc;
		Element e;
		for (int i = 0; i<elem.getChildNodes().getLength(); i++){
			tmp = elem.getChildNodes().item(i);
			if (tmp instanceof Element){
				e = (Element)tmp;
				tmpLoc = generateLocation(e.getAttribute("id"), e.getAttribute("name"), e.getAttribute("type"));
				generateChildLocations((LocationImpl)tmpLoc,e);
				this.addLocation((LocationImpl)tmpLoc);
			}
		}
	}
	
	protected void generateChildLocations(LocationImpl loc, Element elem){
		if (elem == null) return;
		if (!elem.hasChildNodes()) return;
		Node tmp;
		Location tmpLoc;
		Element e;
		for (int i = 0; i<elem.getChildNodes().getLength(); i++){
			tmp = elem.getChildNodes().item(i);
			if (tmp instanceof Element){
				e = (Element)tmp;
				tmpLoc = generateLocation(e.getAttribute("id"), e.getAttribute("name"), e.getAttribute("type"));
				generateChildLocations((LocationImpl)tmpLoc,e);
				loc.childLocations.add(tmpLoc);
			}
		}
	}
	
	public Location generateLocation(String name, String type){
		String id = generateLocationId();
		
		return generateLocation(id, name, type);
	}
	
	protected boolean hasLocation(String id){
		return (this.allLocations.get(id) != null);
		
	}
	
	protected String generateLocationId(){
		
		
		int id = 1;
		String strId = "000"+id;
		String tmp = "loc"+strId;
		
		while (hasLocation(tmp)){
			id++;
			strId = ""+id;
			while (strId.length() < 4){
				strId = "0"+strId;
			}	
			tmp = "loc"+strId;
		}		
		return tmp;  
	}
	
	protected Location generateLocation(String id, String name, String type){
		LocationImpl loc = new Room();
		loc.id = id;
		loc.name = name;		
		try {
			loc.locationType = LocationType.valueOf(type);
		} catch (Exception exc){
			loc.locationType = LocationType.LT_NOT_GIVEN;
		}		
		return loc;
	}
	
	public List<Location> getAllLocations(){
		List<Location> list = new ArrayList<Location>();
		Set<Entry<String,Location>> set = allLocations.entrySet();
		for (Entry<String,Location> entry : set){
			list.add(entry.getValue());
		}
		return list;
	}
	
	public List<Location> getLocations(){
		List<Location> locList = new ArrayList<Location>();
		for (Location location : locationList){
			locList.add(location);
		}
		return locList;
	}
	
	public Location getLocation(String locationId){
		return allLocations.get(locationId);
	}
	
	public void addEventListener(LocationEventListener listener){
		if (listener == null) return;
		if (eventListeners.contains(listener)) return;
		eventListeners.add(listener);
	}
	
	public void removeEventListener(LocationEventListener listener){
		if (listener == null) return;
		if (!eventListeners.contains(listener)) return;
		eventListeners.remove(listener);
	}
	
	protected void dispatchLocationEvent(LocationEvent evnt){
		if (evnt == null) return;
		if (eventListeners.isEmpty()) return;
		for (LocationEventListener lel : eventListeners){
			lel.onLocationEvent(evnt);
		}
	}
	
	public Room createRoom(){
		Room room = new Room();
		room.id = this.generateId();
		room.addEventListener(this);
		addLocation(room);
		return room;
	}
	
	public Room createRoom(String name){
		Room room = createRoom();
		room.setName(name);
		return room;
	}
	
	public void addLocation(Location location){
		if (location instanceof LocationImpl){
			addLocation((LocationImpl)location);
		}
	}
	
	private void addLocation(LocationImpl location){
		if (location == null) return;
		location.locManager = this;
		location.addEventListener(this);
		if (allLocations.get(location.getId())== null){
			allLocations.put(location.getId(),location);
		}
		if (!locationList.contains(location)){
			locationList.add(location);
		}
		
		if (location.hasChildLocations()){
			for (Location loc:location.getChildLocations()){
				addChildLocation((LocationImpl)loc);
			}
		}
		this.dispatchLocationEvent(new LocationEvent(EventType.LOCATION_ADDED, location));
	}
	
	private void addChildLocation(LocationImpl location){
		if (location == null) return;
		location.locManager = this;
		location.addEventListener(this);
		if (allLocations.get(location.getId())== null){
			allLocations.put(location.getId(),location);
		}
		if (location.hasChildLocations()){
			for (Location loc:location.getChildLocations()){
				addChildLocation((LocationImpl)loc);
			}
		}
	}
	
	private String generateId(){
		String res = null;
		idCounter++;
		res = ""+idCounter;
		while (res.length()<4){
			res = "0"+res;
		}
		res = "loc"+res;
		return res;
	}
	
	public void onLocationEvent(LocationEvent evnt){
		dispatchLocationEvent(evnt);
	}
}
