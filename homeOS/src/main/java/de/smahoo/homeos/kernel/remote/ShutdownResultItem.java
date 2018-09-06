package de.smahoo.homeos.kernel.remote;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

public class ShutdownResultItem extends RemoteResultItem{
	
	
	
	
	public Element generateElement(Document doc){
		Element result = doc.createElement("shutdown");
		if (isSuccess()){
			result.setAttribute("success","true");
		} else {
			result.setAttribute("success", "false");
			result.appendChild( generateErrorElement(doc));
		}
		return result;
	}
}
