package de.smahoo.homeos.remote;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.remote.connection.RemoteConnection;
import de.smahoo.homeos.utils.AttributeValuePair;
import de.smahoo.homeos.utils.xml.XmlUtils;

public class RemoteCommunicationProcessor {

	private RemoteLocationManager locManager = null;
	private RemoteDeviceManager	  devManager = null;
	private RemoteConnection connection;
	
	public RemoteCommunicationProcessor(RemoteConnection connection, RemoteDeviceManager devManager, RemoteLocationManager locManager){
		this.locManager = locManager;		
		this.devManager = devManager;
		this.connection = connection;
	}
	
	
	public void evaluateUpdate(Document doc){	
		if (doc == null) return;
		Element root = doc.getDocumentElement();
		if (root.getTagName().equalsIgnoreCase("update")){
			NodeList nodelist = root.getChildNodes();
			Element tmp;
			for (int i = 0; i<nodelist.getLength(); i++){
				if (nodelist.item(i) instanceof Element){
					tmp = (Element)nodelist.item(i);				
					if (tmp.getTagName().equalsIgnoreCase("device")){
						updateDevices(tmp);
					}
					if (tmp.getTagName().equalsIgnoreCase("devicegroup")){
						//
					}
					if (tmp.getTagName().equalsIgnoreCase("location")){
						evaluateLocationUpdate(tmp);
					}
					if (tmp.getTagName().equalsIgnoreCase("locationlist")){
						evaluateLocationListUpdate(tmp);
					}
				}
			}
		}	
		if (root.getTagName().equalsIgnoreCase("new")){
			NodeList nodelist = root.getChildNodes();
			Element tmp;
			for (int i = 0; i<nodelist.getLength(); i++){
				if (nodelist.item(i) instanceof Element){
					tmp = (Element)nodelist.item(i);
					if (tmp.getTagName().equalsIgnoreCase("device")){
						addDevices(tmp);
					}					
				}
			}
		}
		if (root.getTagName().equalsIgnoreCase("delete")){
			
				evaluateDeletion(root);		
		}
	}

	
	private void addDevices(Element elem){
		if (!elem.getTagName().equalsIgnoreCase("device")) return;		
		NodeList nodelist = elem.getChildNodes();
		for (int i = 0; i< nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				addDevice((Element)nodelist.item(i));
			}
		}	
	}
		
	private void evaluateDeletion(Element elem){
		NodeList nodelist = elem.getChildNodes();
		Element tmp;
		for (int i = 0; i<nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				tmp = (Element)nodelist.item(i);
				if (tmp.getTagName().equalsIgnoreCase("device")){
					devManager.removeDevice(tmp.getAttribute("id"));
				}					
			}
		}
	}
	
		
	private void updateDevices(Element elem){
		if (!elem.getTagName().equalsIgnoreCase("device")) return;		
		NodeList nodelist = elem.getChildNodes();
		for (int i = 0; i< nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				updateDevice((Element)nodelist.item(i));
			}
		}		
	}
	
	private void addDevice(Element elem){
		RemoteDeviceFactory rdf = new RemoteDeviceFactory(connection, devManager, locManager);
		RemoteDevice device = rdf.generateDevice(elem);
		devManager.addDevice(device);
		devManager.dispatchEvent(new RemoteDeviceEvent(EventType.DEVICE_ADDED, device, false));
	}
	
	private void updateDevice(Element elem){		
		String deviceId = elem.getAttribute("id");
		RemoteDevice device = (RemoteDevice)devManager.getDevice(deviceId);
		
		if (device == null){
			RemoteDeviceFactory df = new RemoteDeviceFactory(connection, devManager, locManager);
			device = df.generateDevice(elem);			
			return;
		}
		
		if (elem.hasAttribute("ison")){
			device.updateIsOnState(Boolean.parseBoolean(elem.getAttribute("ison")));
		}
		
		if (elem.hasAttribute("isAvailable")){
			device.updateIsAvailableState(Boolean.parseBoolean(elem.getAttribute("isAvailable")));
		}
		
		if (elem.hasAttribute("location")){
			Location loc = locManager.getLocation(elem.getAttribute("location"));
			device.updateLocation(loc);
		} else {
			device.updateLocation(null);
		}		
		if (elem.hasAttribute("name")){
			if (!device.getName().equalsIgnoreCase(elem.getAttribute("name"))){
				device.updateName(elem.getAttribute("name"));
			}
		}		
		if (elem.hasAttribute("lastActivity")){
			SimpleDateFormat formatter = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				device.lastActivity = formatter.parse(elem.getAttribute("lastActivity"));
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}
		if (elem.hasChildNodes()){
			device.update(elem.getChildNodes());
		}
		device.lastUpdate = new Date();
	}
	
	private void evaluateLocationListUpdate(Element elem){
		
	}
	
	private void evaluateLocationUpdate(Element elem){
		if (elem == null) return;
		if (!elem.getTagName().equalsIgnoreCase("location")) return;
		if (elem.hasAttribute("id")){
			Location location = locManager.getLocation(elem.getAttribute("id"));
			if (location != null){
				
			} else {
				
			}
		}
	}
	
	
/*+++++++++++++++   EVENT TRANSFORMATION  ++++++++++++++++++++++++++*/
	public Document transformEvent(Event event){
		Document doc = null;
		if (event instanceof DeviceEvent){
			doc = transformDeviceEvent((DeviceEvent)event);
		}				
		return doc;
	}
	
	private Element transformFunctionExecutionEvent(Document doc, RemoteFunctionExecutionEvent event){
		return transformFunctionExecutionEvent(doc,event.getDevice(), event.getFunctionName(), event.getParameter());
	}
	
	private Element transformFunctionExecutionEvent(Document doc, Device device, String functionName,List<AttributeValuePair> parameter){
		Element exeElem = doc.createElement("execute");
		exeElem.setAttribute("device", device.getDeviceId());
		exeElem.setAttribute("function",functionName);
		if (parameter != null){
			Element param;
			for (AttributeValuePair avp : parameter){
				param = doc.createElement("parameter");
				param.setAttribute("name",avp.getAttribute());
				param.setAttribute("value",avp.getValue());
				exeElem.appendChild(param);
			}
		}
		return exeElem;
	}
	
	private Document transformDeviceEvent(DeviceEvent event){
		Document doc = XmlUtils.createDocument();
		Element root = doc.createElement("cmd");
		Element elem = null;
		switch (event.getEventType()){	
			case DEVICE_RENAMED:
			case LOCATION_ASSIGNED: elem = transformDevicePropertyChangeEvent(doc, event.getDevice()); break;
			case FUNCTION_EXECUTED: elem = transformFunctionExecutionEvent(doc,(RemoteFunctionExecutionEvent)event); break;
		}
		
		if (elem != null){
			root.appendChild(elem);
		} else return null;
		doc.appendChild(root);
		return doc;
	}
	
	private Element transformDevicePropertyChangeEvent(Document doc, Device device){
		Element elem = doc.createElement("change");
		Element devElem = doc.createElement("device");
		devElem.setAttribute("id",device.getDeviceId());
			Element devProp = doc.createElement("property");
			devProp.setAttribute("name","name");
			devProp.setAttribute("value",device.getName());
			devElem.appendChild(devProp);
			devProp = doc.createElement("property");
			devProp.setAttribute("name","location");
			if (device.getLocation() != null){
				devProp.setAttribute("value",device.getLocation().getId());
			} else {
				devProp.setAttribute("value","NONE");
			}
			devElem.appendChild(devProp);
		elem.appendChild(devElem);
		return elem;
	}
	
	
	static public String generatePhysicalRequest(String deviceId){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><request requestType=\"PHYSICAL\"><device deviceId=\""+deviceId+"\"/></request>";
	}
	
	static public String generateRequestComand(){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><request requestType=\"COMPLETE\"/>";
	}
	
	static public String generateHistoryRequestCommand(Device device, Date start, Date end){
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");		
		String cmd ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><request requestType=\"HISTORY\"";
		cmd = cmd + " start=\""+formatter.format(start)+"\" end=\""+formatter.format(end)+"\">";
		cmd = cmd + "<device deviceId=\""+device.getDeviceId()+"\"/> </request>";
		return cmd;
	}
	
	static public String generateDeleteDeviceCmd(Device device){
		String cmd ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><cmd><delete>";
		cmd = cmd + " <device id=\""+device.getDeviceId()+"\"/>";
		cmd = cmd + "</delete></cmd>";
		return cmd;
	}
}
