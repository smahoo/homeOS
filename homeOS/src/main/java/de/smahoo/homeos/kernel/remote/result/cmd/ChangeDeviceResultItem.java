package de.smahoo.homeos.kernel.remote.result.cmd;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.kernel.remote.SetPropertyResultItem;

public class ChangeDeviceResultItem extends PropertyCmdResultItem{
	
	private Device device;
	
	
	public ChangeDeviceResultItem(Device device){
		this.device = device;
	}

	
	
	public Element generateElement(Document doc){
		Element elem = doc.createElement("device");
		elem.setAttribute("id",device.getDeviceId());
		if (props != null){
			for (SetPropertyResultItem prop : props){
				elem.appendChild(prop.generateElement(doc));
			}
		}
		return elem;
	}
	
	
	
}
