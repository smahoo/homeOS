package de.smahoo.homeos.driver;

import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.common.EventType;

public class DriverManager implements DriverEventListener{

	private static DriverManager instance;
	
	protected List<Driver> driverList;
	protected List<DriverEventListener> eventListeners;
	protected DriverMode driverMode = DriverMode.DRIVER_MODE_NORMAL;
	
	public DriverManager(){
		init();
	}
	
	public static DriverManager getInstance(){
		if (instance == null){
			instance = new DriverManager();
		}
		return instance;		
	}
	
	protected void init(){		
		driverList = new ArrayList<Driver>();
		eventListeners = new ArrayList<DriverEventListener>();
	}
	
	private void setDriverMode(DriverMode mode){
		if (driverMode == mode) return;
		
		this.driverMode = mode;	
	}
	
	public void setMode(DriverMode mode){
		switch (mode){
		case DRIVER_MODE_ADD_DEVICE:
			if (driverMode == DriverMode.DRIVER_MODE_NORMAL){
				setAddDeviceMode();
			}			
			break;
		case DRIVER_MODE_REMOVE_DEVICE:
			if (driverMode == DriverMode.DRIVER_MODE_NORMAL){
				setRemoveDeviceMode();
			}
			break;
		case DRIVER_MODE_NORMAL:			
			for (Driver driver : this.driverList){
				if (driver.driverMode == DriverMode.DRIVER_MODE_ADD_DEVICE){
					driver.cancelLearnMode();
					driver.driverMode = DriverMode.DRIVER_MODE_NORMAL;
					dispatchDriverEvent(new DriverEvent(EventType.DRIVER_MODE_CHANGED,driver));
				}
				if (driver.driverMode == DriverMode.DRIVER_MODE_REMOVE_DEVICE){
					driver.cancelRemoveMode();
					driver.driverMode = DriverMode.DRIVER_MODE_NORMAL;
					dispatchDriverEvent(new DriverEvent(EventType.DRIVER_MODE_CHANGED,driver));
				}
				setDriverMode(DriverMode.DRIVER_MODE_NORMAL);		
			}
			break;
			default:
				break;
		}
			
	
	}
	
	protected void setAddDeviceMode(){
		setDriverMode(DriverMode.DRIVER_MODE_ADD_DEVICE);
		for (Driver driver : this.driverList){
			dispatchDriverEvent(new DriverEvent(EventType.DRIVER_MODE_CHANGED,driver));
			driver.driverMode = DriverMode.DRIVER_MODE_ADD_DEVICE;
			driver.startLearnMode();
		}
	}
	
	protected void cancelAddDeviceMode(){
		
		for (Driver driver : this.driverList){
			driver.cancelLearnMode();
			driver.driverMode = DriverMode.DRIVER_MODE_NORMAL;
		}
		setDriverMode(DriverMode.DRIVER_MODE_NORMAL);
	}
	
	protected void setRemoveDeviceMode(){
		setDriverMode(DriverMode.DRIVER_MODE_REMOVE_DEVICE);
		for (Driver driver : this.driverList){
			driver.startRemoveMode();
		}
	}
	
	protected void cancelRemoveDeviceMode(){		
		for (Driver driver : this.driverList){
			driver.cancelRemoveMode();
		}
		setDriverMode(DriverMode.DRIVER_MODE_NORMAL);
	}
	
	
	
	public void addDriverEventListener(DriverEventListener listener){
		if (eventListeners.contains(listener)) return;
		eventListeners.add(listener);
	}
	
	protected synchronized void dispatchDriverEvent(DriverEvent evnt){
		if (eventListeners.isEmpty()) return;
		for (DriverEventListener listener : eventListeners){
			listener.onDriverEvent(evnt);
		}
	}
	
	public void addDriver(Driver driver){
		if (driverList == null) 
			driverList = new ArrayList<Driver>();
		
		if (driverList.contains(driver)) return;
		driverList.add(driver);
		driver.addDriverEventListener(this);
		dispatchDriverEvent(new DriverEvent(EventType.DRIVER_LOADED, driver, "Driver "+driver.getName()+" "+driver.getVersion()+" added"));
	}
	
	public Driver loadDriver(String filename, String classname){
		
		
		
		System.out.println("!!! DriverManager.loadDriver(String className) not implemented yet");
		return null;
	}
	
	public List<Driver> getLoadedDriver(){
		return driverList;
	}
	
	@Override
	public void onDriverEvent(DriverEvent evnt){		
		System.out.println(evnt.getEventType().name());
		if (evnt.getEventType() == EventType.DEVICE_ADDED){			
			System.out.println("DriverManager bekommt mit, dass Geraet hinzugefuegt wurde *freu*");
			//FIXME: when in Learning mode then cancel learning mode of all other drivers  
		}
		dispatchDriverEvent(evnt);
	}
	
	public Driver getDriverByClassName(String className){
		Driver driver = null;
		
		for (Driver d : driverList){
			if (d.getClass().getCanonicalName().equalsIgnoreCase(className)){
				return d;
			}
		}
		
		return driver;
	}
}
