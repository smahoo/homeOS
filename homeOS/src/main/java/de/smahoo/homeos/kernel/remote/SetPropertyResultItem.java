package de.smahoo.homeos.kernel.remote;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.cmd.SetCmdResultItem;

public class SetPropertyResultItem extends SetCmdResultItem{
	private String name;
	private String value;
	private String message;
	private boolean success;
	
	public SetPropertyResultItem(String name, String value, boolean success, String message){
		this.name = name;
		this.value = value;
		this.message = message;
		this.success = success;
	}
	
	public Element generateElement(Document doc){
		Element elem = doc.createElement("property");
		elem.setAttribute("name",name);
		elem.setAttribute("value", value);
		if (success) {elem.setAttribute("success", "true");} else elem.setAttribute("success","false"); 
		if (message != null){
			Element msg = doc.createElement("message");
			msg.setTextContent(message);				
			elem.appendChild(msg);
		}
		return elem;
	}
}