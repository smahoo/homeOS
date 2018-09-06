package de.smahoo.homeos.kernel.remote.result;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.kernel.remote.result.cmd.DeleteCmdResultItem;

public class DeleteDeviceResultItem extends DeleteCmdResultItem{

	private String deviceId = null;
	
	public DeleteDeviceResultItem(){
		super();	
		
	}
	
	public void setDeviceId(String deviceId){
		this.deviceId = deviceId;
	}
	
	public Element generateElement(Document doc){
		Element elem = doc.createElement("device");
		if (isSuccess()){
			if (deviceId != null){
				elem.setAttribute("id", deviceId);
			}
			elem.setAttribute("success","true");
		} else {
			return generateErrorElement(doc);						
		}
		return elem;
	}
	
}
