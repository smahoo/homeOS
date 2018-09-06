package de.smahoo.homeos.driver;


import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.device.DeviceManager;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;
import de.smahoo.homeos.utils.JarResources;

public abstract class Driver {

	DeviceManager deviceManager = null;
	private List<DriverEventListener> eventListeners;	
	protected String filename = null;
	protected JarResources jarResource = null;
	protected DriverMode driverMode = DriverMode.DRIVER_MODE_NOT_INITIALIZED;
	
	public Driver(){
		eventListeners = new ArrayList<DriverEventListener>();
	}
	
	protected DeviceManager getDeviceManager(){
		if (deviceManager == null){
			deviceManager = new DeviceManager();
		}
		return deviceManager;
	}
	
	protected void dispatchDriverEvent(DriverEvent evnt){
		if (eventListeners.isEmpty()) return;
		for (DriverEventListener listener : eventListeners){
			listener.onDriverEvent(evnt);
		}
	}
	
	public void addDriverEventListener(DriverEventListener listener){
		if (eventListeners.contains(listener)) return;
		
		eventListeners.add(listener);
	}
	
	public void removeEventListener(DriverEventListener listener){
		if (eventListeners.contains(listener)){
			eventListeners.remove(listener);
		}
	}
	
	@Override
	public String toString(){
		return getName()+" "+getVersion();
	}
	
	
		
	
	abstract protected void startLearnMode();
	abstract protected void cancelLearnMode();
	abstract protected void startRemoveMode();
	abstract protected void cancelRemoveMode();
	abstract public boolean init(Element elem);
	abstract public String getName();
	abstract public String getVersion();
	abstract public String getCompanyName();
	abstract public Element toXmlElement(Document doc);
	abstract public RemoteResultItem processCmd(Element elem);
	
}
