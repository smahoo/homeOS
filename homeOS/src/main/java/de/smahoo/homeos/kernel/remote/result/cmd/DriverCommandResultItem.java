package de.smahoo.homeos.kernel.remote.result.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

public class DriverCommandResultItem extends RemoteResultItem{

	private List<RemoteResultItem> items = null;
	
	
	protected Driver driver = null;
	
	public DriverCommandResultItem(){
		items = new ArrayList<RemoteResultItem>();
	}
	
	public void addResultItem(RemoteResultItem item){
		items.add(item);
	}
	
	protected Collection<RemoteResultItem> getChildItems(){
		return items;
	}
	
	public void setDriver(Driver driver){
		this.driver = driver;
	}
	
	public Driver getDriver(){
		return driver;
	}
	
	@Override
	public Element generateElement(Document doc) {
		Element elem = doc.createElement("driver");
		
		return elem;
	}

}
