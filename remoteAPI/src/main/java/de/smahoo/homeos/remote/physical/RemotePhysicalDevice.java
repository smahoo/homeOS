package de.smahoo.homeos.remote.physical;

import java.util.List;


import de.smahoo.homeos.common.Function;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.remote.RemoteDevice;

public class RemotePhysicalDevice {

	protected List<Function> functions;
	protected List<Property> properties;
	protected RemoteDevice device;
	protected String driverName = null;
	protected String driverCompany = null;
	protected String driverVersion = null;
	
	public List<Function> getFunctions(){
		return functions;
	}
	
	
	public List<Property> getProperties(){
		return properties;
	}
	
	public RemoteDevice getRemoteDevice(){
		return device;
	}
	
	public String getDriverName(){
		return driverName;
	}
	
	public String getDriverVersion(){
		return driverVersion;
	}
	
	public String getDriverCompany(){
		return driverCompany;
	}
	
}
