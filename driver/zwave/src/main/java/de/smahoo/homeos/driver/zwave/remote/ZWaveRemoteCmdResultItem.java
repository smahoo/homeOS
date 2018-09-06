package de.smahoo.homeos.driver.zwave.remote;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;
import de.smahoo.homeos.kernel.remote.result.cmd.DriverCommandResultItem;

public class ZWaveRemoteCmdResultItem extends DriverCommandResultItem{

	
	
	
	public ZWaveRemoteCmdResultItem(Driver driver){
		super();
		setDriver(driver);
	}
	
	
	@Override
	public Element generateElement(Document doc) {
		Element elem = doc.createElement("driver");
		if (!isSuccess()){
			elem.appendChild(generateErrorElement(doc));
			return elem;
		}
		
		for (RemoteResultItem item : getChildItems()){
			elem.appendChild(item.generateElement(doc));
		}
		
		return elem;
	}
	
}
