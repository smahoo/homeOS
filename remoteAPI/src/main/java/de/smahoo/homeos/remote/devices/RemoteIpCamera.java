package de.smahoo.homeos.remote.devices;

import java.net.URL;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.devices.IpCamera;
import de.smahoo.homeos.remote.RemoteDevice;

public class RemoteIpCamera extends RemoteDevice implements IpCamera{

	private boolean isProtected = false;
	protected URL url;
	
	@Override
	public boolean isPasswordProtected(){
		return isProtected;
	}
	
	@Override
	public URL getUrl(){
		return url;
	}
	
	@Override
	protected void updateProperties(NodeList properties){
		
		if (properties.getLength() > 0){
			Element tmp;
			for (int i= 0; i< properties.getLength(); i++){
			
				if (properties.item(i) instanceof Element){
					tmp = (Element)properties.item(i);
					if (tmp.getAttribute("name").equalsIgnoreCase("isPasswordProtected")){
						isProtected = Boolean.parseBoolean(tmp.getAttribute("value"));
					}
					if (tmp.getAttribute("name").equalsIgnoreCase("url")){
						try {
							url = new URL(tmp.getAttribute("value"));
						} catch (Exception exc){
							exc.printStackTrace();
						}
					}
				
				}
			}		
		}
	}
	
}
