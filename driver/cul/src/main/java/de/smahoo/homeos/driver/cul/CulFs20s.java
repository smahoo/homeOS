package de.smahoo.homeos.driver.cul;

import java.util.List;


import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.Switch;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

import de.runge.cul.Device;
import de.runge.cul.FS20S;


public class CulFs20s extends CulDevice implements Switch{

	public CulFs20s(Device device){
		super(device);		
	}
	
    protected void generateDeviceFunctions(){
	
    	// none
	}

	
	protected void execute(PhysicalDeviceFunction function, List<FunctionParameter> params){
		// none
	}
	
	protected FS20S getFs20sDevice(){
		return (FS20S)culDevice;
	}
	
	protected void generateProperties(){
	//	addProperty(new DeviceProperty(Integer.class,"channels",""));
		addProperty(new DeviceProperty(PropertyType.PT_INTEGER,"channel_00",""));
		addProperty(new DeviceProperty(PropertyType.PT_INTEGER,"channel_01",""));
		addProperty(new DeviceProperty(PropertyType.PT_INTEGER,"channel_02",""));
		addProperty(new DeviceProperty(PropertyType.PT_INTEGER,"channel_03",""));
	//	addProperty(new DeviceProperty(Integer.class,"channel_02",""));
	//	addProperty(new DeviceProperty(Integer.class,"channel_03",""));		
	}
	
	@Override
	protected void onPropertyChanged(DeviceProperty property){
						
	}
	
	protected void setCulProperty(DeviceProperty property){		
		
	}
	
	@Override
	protected void applyProperties(){
		if (culDevice == null) return;
		//this.getPropertyList().size()
		DeviceProperty dp = null;
		String tmpStr;
		for (int i = 0; i< getFs20sDevice().getChannels(); i++){
			tmpStr = ""+i;
			if (tmpStr.length()<2) tmpStr = "0"+tmpStr;
			dp = this.getProperty("channel_"+tmpStr);
			if (dp == null){
				dp = new DeviceProperty(PropertyType.PT_INTEGER,"channel_"+tmpStr,"");		
				this.addProperty(dp);
			}
			if (dp.isValueSet()){
				int tmp = (Integer)dp.getValue();
				if (tmp != getFs20sDevice().getState(i)){
					dp.setValue(getFs20sDevice().getState(i));
				}
			} else dp.setValue(getFs20sDevice().getState(i));
			
					
		}
	}
	
	public int getButtonCount(){
		FS20S s = getFs20sDevice();
		return s.getChannels()*2;
	}
	
	public boolean isButtonPressed(int button){
		return false; // not able to serve that kind of information
	}
		
	@Override
	public boolean isOn(){
		return isAvailable();
	}
}
