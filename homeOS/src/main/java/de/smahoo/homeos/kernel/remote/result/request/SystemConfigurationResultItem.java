package de.smahoo.homeos.kernel.remote.result.request;



import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

public class SystemConfigurationResultItem extends RemoteResultItem {
	
	public SystemConfigurationResultItem(){
		this.setSuccess(true);
	}
	
	
	 public Element generateElement(Document doc){
		return HomeOs.getInstance().getConfiguration(doc);
	 }
}