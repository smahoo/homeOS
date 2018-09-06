package de.smahoo.homeos.device;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyEvent;
import de.smahoo.homeos.property.PropertyEventListener;


/**
 * 
 * @author Mathias Runge
 *
 */
public abstract class PhysicalDevice extends DeviceImpl{

	
	String   deviceAddress = null;	
	HashMap<String,PhysicalDeviceFunction> functionHash;
	List<PhysicalDeviceFunction> deviceFunctions;
	HashMap<String,DeviceProperty> properties;
//	List<PhysicalDeviceEventListener> deviceEventListeners;
	Driver driver;
	DeviceManager deviceManager;
	private boolean available = false;
	//List<DeviceRole> roles;
	private List<DeviceProperty> changedProperties;
	
	public PhysicalDevice(final String deviceId){
		super(deviceId);
		//roles = new ArrayList<>();
		deviceFunctions = new ArrayList<PhysicalDeviceFunction>();
		functionHash = new HashMap<String, PhysicalDeviceFunction>();
		properties = new HashMap<String,DeviceProperty>();
		changedProperties = new ArrayList<DeviceProperty>();
		//deviceEventListeners =new ArrayList<>();
		generateDeviceFunctions();
		generateProperties();
	}
		
	//public void addDeviceEventListener(final PhysicalDeviceEventListener listener){
		
		//if (deviceEventListeners.contains(listener)) return;
		//deviceEventListeners.add(listener);
	//}
	
	//public void removeDeviceEventListener(final PhysicalDeviceEventListener listener){
	//	if (deviceEventListeners.contains(listener)){
	//		deviceEventListeners.remove(listener);
	//	}
	//}
	
	//protected void dispatchDeviceEvent(final PhysicalDeviceEvent evnt){		
	//	for (PhysicalDeviceEventListener listener : deviceEventListeners){
	//		listener.onDeviceEvent(evnt);
	//	}
	//}
	
	
	protected void setAvailability(final boolean available){
		if (this.available != available){
			this.available = available;
			if (available){
				dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.DEVICE_AVAILABLE, this));
			} else dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.DEVICE_NOT_AVAILABLE, this));
		}
	}
	
	public boolean isAvailable(){
		return available;
	}
	
	
	
	
	
	
	public String getAddress(){
		return deviceAddress;
	}
	
//	protected void addRole(DeviceRole role){
//		new Exception("Device.addRole : not implemented yet").printStackTrace();
//	}
	
	protected void setAddress(final String address){
		if (address == null) return;
		if (deviceAddress != null){
		   if (address.toLowerCase().equals(deviceAddress.toLowerCase())) return;
		   this.deviceAddress = address;
		   return;
		}
		this.deviceAddress = address;
		dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.ADDRESS_CHANGED, this));
	}
	

	
	protected void addDeviceFunction(final PhysicalDeviceFunction function){
		if (deviceFunctions.contains(function)) return;
		deviceFunctions.add(function);
		functionHash.put(function.getName(), function);
		function.setDevice(this);
	}
	
	public List<PhysicalDeviceFunction> getDeviceFunctions(){
		return this.deviceFunctions;
	}
	
	public final void  executeFunction(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException {
		lastActivity = new Date();
		execute(function,params);
		dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.FUNCTION_EXECUTED, this, function, params));
	}
	
	public PhysicalDeviceFunction getFunction(final String name){
		String n = name;		
		if (n.endsWith(")")){
			n = n.substring(0,n.indexOf('('));
		}
		return functionHash.get(n);
	}
	
	public PhysicalDeviceFunction getFunction(int functionType){
		Set<Entry<String,PhysicalDeviceFunction>> set = functionHash.entrySet();
		for (Entry<String, PhysicalDeviceFunction> entry : set){
			if (entry.getValue().getFunctionType() == functionType){
				return entry.getValue();
			}
		}
		return null;
	}
	
	public final void executeFunction(final String functionName, final List<FunctionParameter> params) throws FunctionExecutionException{
		PhysicalDeviceFunction function = getFunction(functionName);
		if (function == null) return;		
		executeFunction(function,params);
	}
	
	public final void executeFunction(final String functionName)  throws FunctionExecutionException{
		executeFunction(functionName,null);		
	}
	
	
	
	public Set<String> getPropertyNames(){
		return properties.keySet();
	}
	
	public List<DeviceProperty> getPropertyList(){
		List<DeviceProperty> propertyList = new ArrayList<DeviceProperty>();
		
		Set<Entry<String,DeviceProperty>> set = properties.entrySet();
		for (Entry<String,DeviceProperty> entry : set){
			propertyList.add(entry.getValue());
		}
		
		return propertyList;
	}
	
	public boolean hasProperties(){
		return !properties.isEmpty();
	}
	
	public boolean hasProperty(final String propertyName){		
		return properties.containsKey(propertyName);				
	}
	
	public DeviceProperty getProperty(final String name){
		return properties.get(name);
	}
	
	public void init(final List<DeviceProperty> properties){
		if (properties == null) return;		
		for (DeviceProperty p : properties){
			if (this.hasProperty(p.getName())){				
				this.properties.get(p.getName()).setValue(p.getValue());
			}
		}
	}
	
	protected void addProperty(final DeviceProperty property){
		if (property == null) return;
		property.parent = this;
		property.addEventListener(new PropertyEventListener() {
								
			@Override
			public void onPropertyEvent(PropertyEvent evnt) {
				evaluatePropertyEvent(evnt);				
			}
		});
		properties.put(property.getName(), property);
	}
	
	private synchronized void evaluatePropertyEvent(final PropertyEvent event){
		if (event.getEventType() == EventType.PROPERTY_VALUE_CHANGED){
			onPropertyChanged((DeviceProperty)event.getProperty());
			if (!changedProperties.contains(event.getProperty())){
				changedProperties.add((DeviceProperty)event.getProperty());
			}			
		}
		// FIXME
		// prevent multiple Event dispatching for property changes 
		// Problem: SensorClimate has two properties. when both properties changed, the devices dispatches two events
		//          Remote will send two update messages, DB will save to entries, etc
		// Event has to be thrown when all properties have been changed.
		//this.dispatchDeviceEvent(new PhysicalDeviceEvent(event.getEventType(),this,(DeviceProperty)event.getProperty()));	
	}
	
	protected synchronized void dispatchChangeEventsIfNeeded(){
		if (changedProperties.isEmpty()) return;
		this.dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.DEVICE_PROPERTY_CHANGED,this,changedProperties));	
		changedProperties.clear();
	}
	
	/**
	 * Changes the value of property without throwing a PropertyChangeEvent
	 * @param property property that needs to be changed
	 * @param value new value to be set
	 */
	protected void setPropertyValue(final DeviceProperty property, Object value){
		property.setValueWithoutEvent(value);
	}
	
	public Driver getDriver(){
		return driver;
	}
	
	@Override
	public String toString(){
		return this.getName();
	}
	
		
	
	//abstract public List<DeviceType> getDeviceTypes();
	abstract protected void onPropertyChanged(final DeviceProperty property);	
	abstract protected void generateDeviceFunctions();
	abstract protected void generateProperties();	
	abstract protected void execute(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException;
}
