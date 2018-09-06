package de.smahoo.homeos.kernel.remote.result.request;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

public class LocationListRequestResultItem extends RemoteResultItem{
	
	private List<LocationRequestResultItem> items;
	
	public LocationListRequestResultItem(){
		items = new ArrayList<LocationRequestResultItem>();
	}
	
	public void addLocationRequestResultItem(LocationRequestResultItem item){
		items.add(item);
	}
	
	public Element generateElement(Document doc){
		Element result = doc.createElement("locationlist");
		for (LocationRequestResultItem item : items){
			result.appendChild(item.generateElement(doc, true));	
		}
		
		return result;
	}
}
