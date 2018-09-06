package de.smahoo.homeos.driver.cul;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.MeterElectricity;
import de.smahoo.homeos.property.PropertyType;

import de.runge.cul.Device;
import de.runge.cul.Em1000;

public class CulEm1000 extends CulDevice implements MeterElectricity{
	

	private Date lastIncomingData = null;
	private Timer timer = null;
	private final static int SIX_MINUTES = 1000*60*6;
	private final static int FIVE_MINUTES = 1000*60*5;
	
	public CulEm1000(Device device){
		super(device);
		super.setAvailability(false);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Date ts = getTimeStamp();
				if (ts != null){
					Date tmp = new Date();
					setAvailability((tmp.getTime() - ts.getTime())>FIVE_MINUTES);				
				} else {
					setAvailability(false);
				}
				
			}
		}, SIX_MINUTES, SIX_MINUTES);
	}
	
	private synchronized void setTimeStamp(){
		lastIncomingData = new Date();
		
	}
	
	private synchronized Date getTimeStamp(){
		return lastIncomingData;
	}
	
	protected void applyProperties(){
		setTimeStamp();
		if (culDevice == null) return;
		Em1000 em =  (Em1000)culDevice;
		DeviceProperty p = getProperty("peak");		
		if (p.isValueSet()){
			double tmp = (Double)p.getValue();
			if (tmp != em.getPeakConsumption()){
				p.setValue(em.getPeakConsumption()*1000);
			}
		} else p.setValue(em.getPeakConsumption()*1000);
		p = getProperty("current");		
		if (p.isValueSet()){
			double tmp = (Double)p.getValue();
			if (tmp != em.getCurrentConsumption()){
				p.setValue(em.getCurrentConsumption()*1000);
			}
		} else p.setValue(em.getCurrentConsumption()*1000);
		p = getProperty("total");		
		if (p.isValueSet()){
			double tmp = (Double)p.getValue();
			if (tmp != em.getTotalConsumption()){
				p.setValue(em.getTotalConsumption());
			}
		} else p.setValue(em.getTotalConsumption());
	}
	
	protected void onPropertyChanged(final DeviceProperty property){
		// do nothing
	}
	
	protected void generateDeviceFunctions(){
		// device has no functions
	}
	

	protected void execute(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		// device has no functions
	}
	
	
	protected void generateProperties(){
		DeviceProperty p = new DeviceProperty(PropertyType.PT_DOUBLE,"peak","W");
		addProperty(p);
		p = new DeviceProperty(PropertyType.PT_DOUBLE,"current","W");
		addProperty(p);
		p = new DeviceProperty(PropertyType.PT_DOUBLE,"total","kWh");
		addProperty(p);
	}
	
	
	public boolean isOn(){
		return isAvailable();
	}
	
	@Override
	protected void setAvailability(boolean available){
		if (available == isAvailable()) return;
		
		super.setAvailability(available);		
		if (available){
			this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_ON, this));
		} else {
			this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_OFF, this));
		}
	}
	
	public double getCurrentConsumption(){		
		DeviceProperty p = getProperty("current");		
		if (p.isValueSet()){
			return (Double)p.getValue();
		}
		return 0.0;
	}
	
	public double getTotalConsumption(){
		DeviceProperty p = getProperty("total");		
		if (p.isValueSet()){
			return (Double)p.getValue();
		}
		return 0.0;
	}
	
	
	
}
