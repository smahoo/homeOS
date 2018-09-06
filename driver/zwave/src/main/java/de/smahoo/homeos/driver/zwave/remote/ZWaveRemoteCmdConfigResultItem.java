package de.smahoo.homeos.driver.zwave.remote;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

public class ZWaveRemoteCmdConfigResultItem extends RemoteResultItem{
	
	
	
	
	public Element generateElement(Document doc){
		Element element = doc.createElement("config");
		
		return element;
	}
	
}