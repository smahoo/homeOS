package de.smahoo.homeos.home.driver.hue;

/*
 * ================================================
 * 				CHANGE HISTORY
 * ================================================
 * 
 *  0.2
 * 
 * 
 * =================================================
 * 					To Do's
 * =================================================
 * 
 * 	- Registration for new user (see http://developers.meethue.com/gettingstarted.html)
 * 
 */



import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.driver.Driver;
import de.smahoo.driver.DriverEvent;
import de.smahoo.kernel.remote.result.RemoteResultItem;

public class HueDriver extends Driver{

	protected final static int DEFAULT_UPDATE_INTERVAL = 20000;
	
	protected final static String DRIVER_COMPANY = "smahoo Solutions GmbH & Co. KG";
	protected final static String DRIVER_NAME = "Hue Driver";
	protected final static String DRIVER_VERSION = "0.2";
	
	protected HueComm comm = null;
	protected List<HueBulb> bulbs;
	protected UpdaterThread updater;
	
	
	
	public HueDriver(){		
		bulbs = new ArrayList<HueBulb>();		
	}
	
	@Override
	public boolean init(Element elem){
		if (elem == null){
			return false;
		}
		dispatchDriverEvent(new DriverEvent(EventType.DRIVER_INITIALIZING, this));
		if ((elem.hasAttribute("hueIp"))&&(elem.hasAttribute("hueUser"))){
			comm = new HueComm(elem.getAttribute("hueIp"),elem.getAttribute("hueUser"));
		} else {
			disableAllDevices();
			dispatchDriverEvent(new DriverEvent (EventType.DRIVER_PROBLEM,this, "Unable to initialize "+DRIVER_NAME+"! Missing parameters \"hueIp\" and \"hueUser\" in configuration data."));			
			return false;
		}		
		JSONObject json;
		updater = new UpdaterThread();
		updater.start();
		try {
			if (elem.hasChildNodes()){
				configDevices(elem.getChildNodes());
			}		
			json = comm.getAll();
			if (json != null){			
				update(json);				
				return true;
			}
		} catch (Exception exc){
			dispatchDriverEvent(new DriverEvent(EventType.DRIVER_PROBLEM, this,exc.getMessage()));
			disableAllDevices();
			
		}		
		
		return false;
	}
	
	protected void disableAllDevices(){
		if (bulbs.isEmpty()) return;
		for (HueBulb bulb : bulbs){
			bulb.disable();
		}
	}
	
	protected void enableAllDevices(){
		if (bulbs.isEmpty()) return;
		for (HueBulb bulb : bulbs){
			
			bulb.enable();
		}
	}
	
	protected void configDevices(NodeList nodeList){
		Element tmp;
		String deviceId;
		for (int i = 0; i< nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				try {
					tmp = (Element)nodeList.item(i);
					deviceId = tmp.getAttribute("deviceId");
					HueBulb bulb = generateDevice(deviceId);
					bulbs.add(bulb);
					if (tmp.hasAttribute("name")){
						bulb.setName(tmp.getAttribute("name"));
					} 
					if (tmp.hasAttribute("location")){
						bulb.assignLocation(tmp.getAttribute("location"));
					}
					getDeviceManager().addDevice(bulb, this);
				} catch (Exception exc){
					dispatchDriverEvent(new DriverEvent(EventType.DRIVER_PROBLEM, this,exc.getMessage()));
				}				
			}
		}
	}
	
	protected HueBulb getBulb(String bulbId){
		for (HueBulb bulb : this.bulbs){
			if (bulb.bulbHueId.equals(bulbId)){
				return bulb;
			}
		}
		return null;
	}
	
	@Override
	public String getName(){
		return DRIVER_NAME;
	}
	
	@Override
	public String getVersion(){
		return DRIVER_VERSION;
	}
	
	@Override
	public String getCompanyName(){
		return DRIVER_COMPANY;
	}
	
	@Override
	public Element toXmlElement(Document doc){
		return null;
	}
	
	
	protected void update(JSONObject data){
		if (data == null){
			return;
		}
		JSONObject lights = (JSONObject)data.get("lights");
		updateDevices(lights);
		
	}
	
	protected void updateDevices(JSONObject data){
		
		if (data == null){
			return;
		}
		
		if (data.isEmpty()){
			return;
		}
		
		// FIXME: not sure if light-ids will be incremented like expected (step of 1)
		
		int idCnt = 1;
		String id = ""+idCnt;
		JSONObject tmp;
		String deviceId;
		while(data.containsKey(id)){
			tmp = (JSONObject)data.get(id);
			if (id.length()<10){
				id = "0"+id;
			}
			deviceId = "HUE_BULB_"+id;
			
			Device device = getDeviceManager().getDevice(deviceId);
			if (device == null){
				device = generateDevice(deviceId,tmp);
				getDeviceManager().addDevice((PhysicalDevice)device, this);
			} else {
				if (device instanceof HueBulb){
					((HueBulb)device).update(tmp);
				}
			}
			if (device != null){
				((HueBulb)device).bulbHueId = ""+idCnt;
			}
			
			
			idCnt++;
			id = ""+idCnt;			
		}
	}
	
	protected HueBulb generateDevice(String id){
		return generateDevice(id,null);
	}
	
	protected HueBulb generateDevice(String id, JSONObject data){
		HueBulb bulb = new HueBulb(id);
		if (data != null) {
			bulb.update(data);
		}
		bulb.comm = this.comm;
		return bulb;
	}
		
	
	@Override
	public void startLearnMode(){
		
	}
	
	@Override
	public void cancelLearnMode(){
		
	}
	
	@Override
	public void startRemoveMode(){
		
	}
	
	@Override
	public void cancelRemoveMode(){
		
	}
	
	protected void addDomoUser(){
		// Aint that easy, User needs to press round button on gateway!
		// thus, functionality is not implemented yet
		
		//	http://<bridge ip address>/api
		//	{"devicetype":"test user","username":"domouser"}
		// POST
		
		// expected response: 
		// [ { "success": {"username" : "domouser"}} ]
	}
	
	
	public static void main(String[] args){
		//HueDriver driver = new HueDriver();
		HueComm comm = new HueComm("192.168.178.80","newdeveloper");	
		try {
			JSONObject json = comm.getAll();
			System.out.println(json.toJSONString());
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	
	private class UpdaterThread extends Thread{
		
		private boolean terminate = false;
		
		protected synchronized boolean keepAlive(){
			return !terminate;
		}
		
		public void run(){
			JSONObject json;
			while (keepAlive()){			
				try {
					Thread.sleep(DEFAULT_UPDATE_INTERVAL);
				} catch (Exception exc){
					exc.printStackTrace();
				}
				try {					
					json = comm.getAll();
					if (json != null){			
						update(json);					
					}
					enableAllDevices();
				} catch (Exception exc){				
					disableAllDevices();				
				}	
				
			}
		}
		
	}
	
	
	@Override
	public RemoteResultItem processCmd(Element elem){
		return null;
	}
}
