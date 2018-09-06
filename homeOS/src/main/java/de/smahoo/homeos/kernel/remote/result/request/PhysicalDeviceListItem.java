package de.smahoo.homeos.kernel.remote.result.request;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

public class PhysicalDeviceListItem extends RemoteResultItem{

	private List<PhysicalDeviceItem> items;
	
	public PhysicalDeviceListItem(){
		items = new ArrayList<PhysicalDeviceItem>();
	}
	
	public void add(PhysicalDeviceItem item){
		items.add(item);
	}
	
	public Element generateElement(Document doc){
		Element elem = doc.createElement("physical");
		for (PhysicalDeviceItem i :items){			
			elem.appendChild(i.generateElement(doc));
		}
		return elem;
	}
}
