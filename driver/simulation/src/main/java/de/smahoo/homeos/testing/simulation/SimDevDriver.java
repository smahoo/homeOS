package de.smahoo.homeos.testing.simulation;

import java.util.*;

import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.driver.DriverMode;
import de.smahoo.homeos.simulation.devices.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.driver.DriverEvent;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

/*
 * CHANGE HISTORY
 * 
 * 0.2.4	Problems with Driver_Mode_Switching, not already solved yet
 * 
 * 0.2.3	Added Learnmode to add new simulated device after 15sec. This delay gives other drivers the possibility to 
 * 			add devices first. After a driver already added a device, all other drivers will stop the learn mode, even this one. 
 * 
 * 0.2		Supporting initial values from config-file for SensorClimate and MeterElectricity
 * 
 * 
 * 
 * 
 */


public class SimDevDriver extends Driver{
	
	private static final String VERSION = "0.2.4";
	private Timer addDeviceTimer = null;
	private TimerTask taskAddDevice = null;


	public boolean init(){
		return init(null);
	}
	
	public boolean init(Element elem){
		
		addDeviceTimer = new Timer();

		
		

		dispatchDriverEvent(new DriverEvent(EventType.DRIVER_INITIALIZING,this));
		boolean result = true;

		if (elem.hasChildNodes()){
			result = initDevices(elem.getChildNodes());
		}
		driverMode = DriverMode.DRIVER_MODE_NORMAL;
		return result;
	}
	
	private boolean initDevices(NodeList list){
		boolean res = true;
		for (int i = 0; i<list.getLength(); i++){		
			Node n = list.item(i);
			if (n instanceof Element){
				res = res && initDevice((Element)n);
			}
		}	
		return res;
	}
	
	private void setValues(SimMeterElectricity meter, NodeList nodeList){
		Element elem;		
		for (int i = 0; i< nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				elem = (Element)nodeList.item(i);
				if (elem.getNodeName().equalsIgnoreCase("property")){
					if (elem.getAttribute("name").equalsIgnoreCase("current")){
						meter.setCurrentConsumption(Double.parseDouble(elem.getAttribute("value")));
					}
					if (elem.getAttribute("name").equalsIgnoreCase("total")){
						meter.setTotalConsumption(Double.parseDouble(elem.getAttribute("value")));
					}
				}
			}
		}
	}
	
	private void setValues(SimSensorClimate sensor, NodeList nodeList){
		Element elem;		
		for (int i = 0; i< nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				elem = (Element)nodeList.item(i);
				if (elem.getNodeName().equalsIgnoreCase("property")){
					if (elem.getAttribute("name").equalsIgnoreCase("temperature")){
						sensor.setTemperature(Double.parseDouble(elem.getAttribute("value")));
					}
					if (elem.getAttribute("name").equalsIgnoreCase("humidity")){
						sensor.setHumidity(Double.parseDouble(elem.getAttribute("value")));
					}
				}
			}
		}
	}
	
	private boolean initDevice(Element elem){
		
		if (!elem.hasAttribute("class")){
			this.dispatchDriverEvent(new DriverEvent(EventType.ERROR_CONFIGURATION, this, "Missing attribute \'class\'. Unable to initialize device."));
			return false;
		}
		try {
			
			if (elem.getAttribute("class").equals(SimMeterElectricity.class.getCanonicalName())){
				SimMeterElectricity meter = createMeterElectricity(elem.getAttribute("deviceId"),elem.getAttribute("name"),elem.getAttribute("location"));
				if (elem.hasChildNodes()){
					setValues(meter,elem.getChildNodes());
				}
				getDeviceManager().addDevice(meter,this);
			}
			if (elem.getAttribute("class").equals(SimLamp.class.getCanonicalName())){
				SimLamp lamp = createSimLamp(elem.getAttribute("deviceId"),elem.getAttribute("name"),elem.getAttribute("location"));
				
				getDeviceManager().addDevice(lamp,this);
			}
			if (elem.getAttribute("class").equals(SimSensorClimate.class.getCanonicalName())){
				SimSensorClimate sensor = createSimSensorClimate(elem.getAttribute("deviceId"),elem.getAttribute("name"),elem.getAttribute("location"));
				if (elem.hasChildNodes()){
					setValues(sensor,elem.getChildNodes());
				}
				getDeviceManager().addDevice(sensor,this);
			}
			if (elem.getAttribute("class").equals(SimHeating.class.getCanonicalName())){
				
				SimHeating heating = createSimHeating(elem.getAttribute("deviceId"),elem.getAttribute("name"),elem.getAttribute("location"));
				getDeviceManager().addDevice(heating,this);
			}
		} catch (Exception exc){
			this.dispatchDriverEvent(new DriverEvent(EventType.ERROR_CONFIGURATION, this, "Unable to initialize device. ("+exc.getMessage()+")"));
			exc.printStackTrace();
		}		
		return true;
	}


	
	private SimHeating createSimHeating(String id, String name, String location){
		SimHeating heating = new SimHeating(id);
		setNameAndLocation(heating,name,location);
		return heating;
	}
	
	private SimSensorClimate createSimSensorClimate(String id, String name, String location){
		SimSensorClimate sensor = new SimSensorClimate(id);
		setNameAndLocation(sensor,name,location);
		return sensor;
	}
	
	private SimMeterElectricity createMeterElectricity(String id, String name, String location){
		SimMeterElectricity meter = new SimMeterElectricity(id);
		setNameAndLocation(meter, name, location);
		return meter;
	}
	
	private SimLamp createSimLamp(String id,String name,  String location){
		SimLamp lamp = new SimLamp(id);		
		setNameAndLocation(lamp,name,location);
		return lamp;
	}
	
	private void setNameAndLocation(PhysicalDevice device, String name, String location){
		if (name != null){
			device.setName(name);
		}
		if (location != null){
			device.assignLocation(location);
		}
	}
		
	public String getName(){
		return "Simulation Driver";
	}
	
	public String getVersion(){
		return VERSION;
	}
	
	public String getCompanyName(){
		return "Smart Home Technologies";
	}
	
	public Element toXmlElement(Document doc){

		Element elem = doc.createElement("driver");
		elem.setAttribute("class",this.getClass().getName());
		elem.setAttribute("name", getName());
		elem.setAttribute("version",getVersion());


		for (Device dev : getDeviceManager().getDevices(this)){
			if (dev instanceof SimDevice) {
				elem.appendChild(((SimDevice)dev).toXml(doc));
			}
		}

		return elem;

	}

	protected Element getDeviceElement(Device dev, Document doc){
		Element elem = doc.createElement("device");

		return elem;
	}
	
	protected void addDefaultDevice(){
		String deviceId = "SimDevice-" + UUID.randomUUID().toString();
		String deviceName = "new Device";
		SimSensorClimate sensor = createSimSensorClimate(deviceId,deviceName,null);		
		getDeviceManager().addDevice(sensor,this);
		
	}
	
	@Override
	public void startLearnMode(){
		if (taskAddDevice == null){
			taskAddDevice = new AddDeviceTask();
		}
		
		addDeviceTimer.schedule(taskAddDevice, 5000);
	}
	
	@Override
	public void cancelLearnMode(){
		if (taskAddDevice != null){
			taskAddDevice.cancel();
			taskAddDevice = null;
		}
		//addDeviceTimer.cancel();
	}
	
	@Override
	public void startRemoveMode(){
		
	}
	
	@Override
	public void cancelRemoveMode(){
		
	}
	
	private class AddDeviceTask extends TimerTask{
		public void run(){
			addDefaultDevice();
		}
	}
	
	@Override
	public RemoteResultItem processCmd(Element elem){
		return null;
	}
	
}
