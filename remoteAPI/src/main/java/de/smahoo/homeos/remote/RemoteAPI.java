package de.smahoo.homeos.remote;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventListener;
import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.PropertyHistoryData;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.remote.connection.RemoteConnection;
import de.smahoo.homeos.remote.connection.RemoteConnectionEvent;
import de.smahoo.homeos.remote.connection.RemoteConnectionEventListener;
import de.smahoo.homeos.remote.connection.RemoteUpdateListener;
import de.smahoo.homeos.remote.physical.RemotePhysicalDevice;
import de.smahoo.homeos.remote.physical.RemotePhysicalFactory;
import de.smahoo.homeos.utils.xml.XmlUtils;

public class RemoteAPI {
	
	
	protected final static String VERSION = "0.2";

	protected RemoteDeviceManager 	devManager = null;
	protected RemoteLocationManager locManager = null;
	protected RemoteConnection 		remoteConnection = null;
	protected RemoteUpdateListener	updateListener = null;
	protected RemoteHistoryProcessor historyProcessor = null;
	protected RemoteCommunicationProcessor commProcessor = null;
	protected List<EventListener> 	eventListener;
	private EventListener 			deviceListener;
	private RemoteDeviceFactory rdf = null;
	
	
	
	public RemoteAPI(EventListener listener){
		devManager = new RemoteDeviceManager();
		deviceListener = new EventListener() {
			
			@Override
			public void onEvent(Event event) {
				evaluateDeviceEvent(event);
				
			}
		};
		devManager.addEventListener(deviceListener);
		locManager = new RemoteLocationManager();
		remoteConnection = new RemoteConnection();
		updateListener = new RemoteUpdateListener(new RemoteConnectionEventListener() {
			
			@Override
			public void onRemoteConnectionEvent(RemoteConnectionEvent event) {
				evaluateConnectionEvent(event);
				
			}
		});
		commProcessor = new RemoteCommunicationProcessor(remoteConnection, devManager, locManager);
		eventListener = new ArrayList<EventListener>();
		eventListener.add(listener);
		historyProcessor = new RemoteHistoryProcessor(remoteConnection);
		rdf = new RemoteDeviceFactory(remoteConnection, devManager, locManager);
	}
	
	public void saveHistoryCache(File file){
		Document doc = XmlUtils.createDocument();
		Element root = doc.createElement("persistence");
		List<Device> devList = devManager.getDeviceList();
		RemoteDevice rdev;
		Element devElem;
		Element cacheElem;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		for (Device dev : devList){
			devElem = doc.createElement("device");
			devElem.setAttribute("deviceId",dev.getDeviceId());
			rdev = (RemoteDevice)dev;
			if (!rdev.historyProcessor.getCache().isEmpty()){
				cacheElem = getCacheElement(doc,rdev.historyProcessor.getCache().getData());
			
				cacheElem.setAttribute("start", formatter.format(rdev.historyProcessor.getCache().getStart()));
				cacheElem.setAttribute("end", formatter.format(rdev.historyProcessor.getCache().getEnd()));
				devElem.appendChild(cacheElem);
			}
			root.appendChild(devElem);
		}
		
		doc.appendChild(root);
		
		String xmlStr = XmlUtils.xml2String(doc);
		
		
    	try {
    		FileWriter writer = new FileWriter(file);
    		writer.write(xmlStr);
    		writer.flush();
    		writer.close();
    	} catch (Exception exc){
    		exc.printStackTrace();
    	}
	}
	
	protected Element getCacheElement(Document doc, List<PropertyHistoryData> cache){
		Element elem = doc.createElement("historydata");
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		
		Element tmp = null;
		for (PropertyHistoryData phd : cache){
			tmp = doc.createElement("entry");
			tmp.setAttribute("isOn",String.valueOf(phd.isOn()));
			tmp.setAttribute("isAvailable",String.valueOf(phd.isAvailabe()));			
			tmp.setAttribute("timestamp",formatter.format(phd.getTimeStamp()));
			
			List<String> attributes = phd.getPropertyNames();
			for (String name : attributes){
				tmp.setAttribute(name,phd.getValue(name));
			}
			elem.appendChild(tmp);
		}
		
		return elem;
	}
	
	public void loadHistoryCache(File file) throws IOException{
		RemoteHistroyCacheLoader loader = new RemoteHistroyCacheLoader(devManager);		
		loader.loadHistoryCache(file);
	}
	
	public void setRemoteConnection(RemoteConnection connection){
		this.remoteConnection = connection;
		historyProcessor.setConnection(connection);
	}
	
	private void evaluateConnectionEvent(RemoteConnectionEvent event){
		//System.out.println(event.toString());
		switch (event.getEventType()){
		case CONNECTION_LOST: 
			devManager.setConnectionDate(null);			
			dispatchEvent(event);			
			break;
		case CONNECTION_ESTABLISHED: 
			devManager.setConnectionDate(new Date());
			dispatchEvent(event); break;
		case UPDATE:
			evaluateUpdateMessage(event.getMessage());
			break;
		}
	}
	
	private void evaluateUpdateMessage(String message){
		String xmlMsg = message.replace("##","");
		xmlMsg = xmlMsg.replace("%%","");		
		Document doc = XmlUtils.parseDoc(xmlMsg);
		if (doc != null){
			try {
				commProcessor.evaluateUpdate(doc);
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}
	}
	
	private void evaluateDeviceEvent(Event event){
		dispatchEvent(event);
		if (event instanceof RemoteDeviceEvent){
			if (((RemoteDeviceEvent)event).isUpdating()) return;
		}
		if (event.getEventType() == EventType.DEVICE_REMOVED){
			return;
		}
		Document doc = commProcessor.transformEvent(event);
		if (doc!= null){
			try {
				String cmd = XmlUtils.xml2String(doc);
				//System.out.println("cmd = "+cmd);
				String response = remoteConnection.sendCommand(cmd);
				//System.out.println(response);
			} catch (Exception exc){
				exc.printStackTrace();
			}
			//Document responseDoc = XmlUtils.parseDoc(response);
			// check Response....		
		}
	}
	
	private void dispatchEvent(Event event){
		for (EventListener listener : eventListener){
			listener.onEvent(event);
		}
	}
	
	public RemotePhysicalDevice getPhysicalDeviceDetails(RemoteDevice device){
		RemotePhysicalFactory rpf = new RemotePhysicalFactory();
		String cmd = RemoteCommunicationProcessor.generatePhysicalRequest(device.getDeviceId());
		
		String response = null;
		try {
			response = remoteConnection.sendCommand(cmd);
		} catch (Exception exc){
			exc.printStackTrace();
			// FIXME throw Exception !!!
			return null;
		}
	//	System.out.println(response);
		Document doc = XmlUtils.parseDoc(response);
		if (doc != null) {		
			Element tmp = doc.getDocumentElement();
			NodeList tmpList = tmp.getElementsByTagName("physical");
			if (tmpList.getLength()== 1){
				tmp = (Element)tmpList.item(0);
				NodeList listdevices = tmp.getElementsByTagName("device");
				if (listdevices.getLength() == 1){
					return rpf.generatePhysicalDevice(device, (Element)listdevices.item(0));
				}
				
			}
			
			
		} else {
			// FIX ME System.out.println("Doc is null!");
		}
		
		
		return null;
	}
	
	public List<Location> getLocationTree(){
		return locManager.getLocations();
	}
	
	public List<Location> getLocationList(){
		return locManager.getAllLocations();
	}
	
	public List<Device> getDevices(){
		return devManager.getDeviceList();
	}
		
	public void deleteDevice(Device device){
		String cmd = RemoteCommunicationProcessor.generateDeleteDeviceCmd(device);
		try {
			String response = remoteConnection.sendCommand(cmd);			
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	public void connect(String serverAddress, int port) throws IOException{
		
			remoteConnection.setURL(new URL("http://"+serverAddress+":"+port+"/homeos/remote"));
			updateListener.setServerIp(serverAddress);
			updateListener.setServerPort(port+1);
			synchronizeWithServer();
			updateListener.start();			
		
		
	}

	public boolean disconnect(){
		if (updateListener == null) return true;
		updateListener.disconnect();
		return true;
	}
	
	protected void init(Document doc){
		if (doc == null) return;		
		Element root = doc.getDocumentElement();
		NodeList nodelist = root.getElementsByTagName("locationlist");
		for (int i = 0; i<nodelist.getLength(); i++){
			//if (nodelist.item(i) instanceof Element){
				init((Element)nodelist.item(i));
			//}
		}
		nodelist = root.getElementsByTagName("devicelist");
		for (int i = 0; i<nodelist.getLength(); i++){
			//if (nodelist.item(i) instanceof Element){
				init((Element)nodelist.item(i));
			//}
		}
	}
	
	protected void init(Element element){
		
		if (element.getTagName().equalsIgnoreCase("devicelist")){
			devManager.init(element,rdf);
		}
		if (element.getTagName().equalsIgnoreCase("locationlist")){
			locManager.init(element);
		}
	}
	
	
	public void synchronizeWithServer() throws IOException{
		String cmd = RemoteCommunicationProcessor.generateRequestComand();
		String response = remoteConnection.sendCommand(cmd);		
		Document doc = XmlUtils.parseDoc(response);
		if (doc != null) {			
			init(doc);
		} else {
			// FIX ME System.out.println("Doc is null!");
		}
	}
	
	public static String getVersion(){
		return VERSION;
	}
	
	
}
