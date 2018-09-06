package de.smahoo.homeos.location;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.common.Function;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.location.Location;

public class LocationFunction implements Function{

	List<PhysicalDeviceFunction> deviceFunctions;
	Location location = null;
	private String name;
	
	
	public LocationFunction(String name){
		deviceFunctions = new ArrayList<PhysicalDeviceFunction>();
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		if (name == null) return;
		if (this.name != null){
			if (this.name.equals(name)){
				return;
			}
		}
		this.name = name;		
	}
	
	public List<PhysicalDeviceFunction> getDeviceFunctions(){
		return deviceFunctions;
	}
	
	public void removeDeviceFunction(PhysicalDeviceFunction function){
		if (deviceFunctions.isEmpty()) return;
		if (deviceFunctions.contains(function)) {
			deviceFunctions.remove(function);
		}
	}
	
	public void addDeviceFunction(PhysicalDeviceFunction function){
		if (deviceFunctions.contains(function)) return;
		deviceFunctions.add(function);
	}
	
	@Override
	public void execute(){
		if (deviceFunctions.isEmpty()) return;
		
		for (PhysicalDeviceFunction function : deviceFunctions){
			try {
				function.execute();
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}		
	}
	
	
	
}
