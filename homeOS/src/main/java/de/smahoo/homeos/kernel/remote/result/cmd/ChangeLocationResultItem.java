package de.smahoo.homeos.kernel.remote.result.cmd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.SetPropertyResultItem;
import de.smahoo.homeos.location.Location;

public class ChangeLocationResultItem extends PropertyCmdResultItem{
	
	protected Location location = null;
	
	public ChangeLocationResultItem(Location location){
		this.location = location;
	}
	
	public Element generateElement(Document doc){
		Element elem = doc.createElement("location");
		
		if (isSuccess()){
			elem.setAttribute("success","true");
		} else {
			elem.setAttribute("success", "false");
			elem.appendChild( generateErrorElement(doc));
			return elem;
		}
		
		if (location == null){
			elem.setAttribute("success", "false");		
			return elem;
		}
		
		elem.setAttribute("id",location.getId());
		if (props != null){
			for (SetPropertyResultItem prop : props){
				elem.appendChild(prop.generateElement(doc));
			}
		}
		return elem;
	}
	
	
}
