package de.smahoo.homeos.simulation.devices;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.MeterElectricity;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;



public class SimMeterElectricity extends SimDevice implements MeterElectricity{
	
	double totalConsumption;
	double currentConsumption;
	Timer totalTimer = null;
	Date lastTotalCalcTimeStamp = null;
	
	public SimMeterElectricity(String deviceId){
		super(deviceId);
	}
	
	public double getTotalConsumption(){
		return totalConsumption;
	}
	
	public double getCurrentConsumption(){
		return currentConsumption;
	}
	
	protected void onPropertyChanged(final DeviceProperty property){
		//
	}
	
	protected void generateDeviceFunctions(){
		//
	}
	
	public void setCurrentConsumption(double current){
		if (currentConsumption > 0){
			// adding total Consumption
			Date tmp = new Date();
			long seconds = (tmp.getTime() - lastTotalCalcTimeStamp.getTime())/1000;
			setTotalConsumption(getTotalConsumption()+currentConsumption*seconds/3600);
			lastTotalCalcTimeStamp = tmp;
		}
		this.currentConsumption = current;
		Property prop = this.getProperty("current");
		prop.setValue(current);
		if (current > 0){
			
			if (totalTimer == null){
				lastTotalCalcTimeStamp = new Date();
				totalTimer = new Timer();
				totalTimer.schedule(new TotalConsumptionCalculator(), 60*1000, 60*1000);
			}
		}
		this.dispatchChangeEventsIfNeeded();
	}
	
	public void setTotalConsumption(double total){
		
		this.totalConsumption = (Math.round(total*1000))/1000.0;
		Property prop = this.getProperty("total");
		prop.setValue(totalConsumption);
		this.dispatchChangeEventsIfNeeded();
	}
	
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE,"current"," kW"));
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE,"total"," kWh"));
	}
	
	protected void execute(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		//
	}
	
	private class TotalConsumptionCalculator extends TimerTask{
		public TotalConsumptionCalculator(){
			
		}
		
		public void run(){
			Date tmp = new Date();
			long seconds = (tmp.getTime() - lastTotalCalcTimeStamp.getTime())/1000;
			setTotalConsumption(getTotalConsumption()+currentConsumption*seconds/3600);
			lastTotalCalcTimeStamp = tmp;			
		}
	}
		
	
}
