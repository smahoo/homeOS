package de.smahoo.homeos.home.driver.hue;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;
import de.smahoo.homeos.devices.Lamp;
import de.smahoo.homeos.property.PropertyType;

public class HueBulb extends PhysicalDevice implements Lamp {

	protected String bulbType = null;
	protected String bulbModelId = null;
	protected String bulbSwversion = null;
	protected String bulbHueId = null;
	protected boolean on = false;
	protected HueComm comm;
	
	public HueBulb(final String deviceId){
		super(deviceId);
	}
	
	public String getHueId(){
		return bulbHueId;
	}
	
	public void turnOn(){
		comm.turnOn(this);
		if (!on){
			on = true;
			dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_ON,this));
		}
	}
	
	
	public void setBrightness(double brightness){
		long val = Math.round(255 * brightness);
		comm.setBrightnes(this,val);
		this.getProperty("bri").setValue(val);
  		this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_PROPERTY_CHANGED, this));
	}
	
	public void setColor(long color){
		comm.setColor(this,color);
		   this.getProperty("hue").setValue(color);
	  		this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_PROPERTY_CHANGED, this));	
	}
	
	public void turnOff(){
		comm.turnOff(this);
		if (on){
			on = false;
			dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_OFF,this));
		}
	}
	
	public boolean isOn(){
		return on;
	}
	
	protected boolean updateProperty(String propertyName, Object value){
		if (value == null) return false;
		boolean result = false;
		DeviceProperty property = this.getProperty(propertyName);
		if (property.isValueSet()){
			if (!property.equals(value)){
				property.setValue(value);
				result = true;
			}
		} else {
			result = true;
			property.setValue(value);
		}
		
		
		return result;
	}
	
	protected void update(JSONObject data){
		if (data == null) return;
		if (data.isEmpty()) return;
		JSONObject state = (JSONObject)data.get("state");
	
		if (state != null){
			
			updateProperty("bri",state.get("bri"));			
			updateProperty("hue",state.get("hue"));			
			updateProperty("sat",state.get("sat"));			
			updateProperty("ct",state.get("ct"));
			updateProperty("alert",state.get("alert"));
			updateProperty("effect",state.get("effect"));
			updateProperty("colormode",state.get("colormode"));
			
			boolean on = (Boolean)state.get("on");
			if (on != isOn()){
				this.on = on;
				if (on){
					dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_ON, this));
				} else {
					dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_OFF, this));
				}
			}	
			
		}
		this.bulbType = (String)data.get("type");
		this.setName((String)data.get("name"));
		this.bulbModelId = (String)data.get("modelid");
		this.bulbSwversion = (String)data.get("swversion");
		this.setAvailability(true);	// bug in hue-api -> devices are always available, even they are off
		this.dispatchChangeEventsIfNeeded();
		
		
	}
	
	protected void onPropertyChanged(final DeviceProperty property){
		//
	}
	
	protected void generateDeviceFunctions(){
		SimpleDeviceFunction function;
		function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_ON);
    	this.addDeviceFunction(function);
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_OFF);
    	this.addDeviceFunction(function);
    	
    	FunctionParameter fm = new FunctionParameter(PropertyType.PT_LONG,"color");
    	List<FunctionParameter> l = new ArrayList<FunctionParameter>();
    	l.add(fm);
    	ParameterizedDeviceFunction func = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SET_COLOR,l);
    	this.addDeviceFunction(func);
    	
    	fm = new FunctionParameter(PropertyType.PT_DOUBLE,"brightness");
    	l = new ArrayList<FunctionParameter>();
    	l.add(fm);
    	func = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SET_BRIGHTNES,l);
    	this.addDeviceFunction(func);
    	
	}
		
	protected void generateProperties(){		
		DeviceProperty p = new DeviceProperty(PropertyType.PT_LONG,"bri","");		
		addProperty(p);
		p = new DeviceProperty(PropertyType.PT_LONG,"hue","");		
		addProperty(p);
		p = new DeviceProperty(PropertyType.PT_STRING,"alert","");		
		addProperty(p);
		p = new DeviceProperty(PropertyType.PT_STRING,"effect","");		
		addProperty(p);		
		p = new DeviceProperty(PropertyType.PT_STRING,"colormode","");		
		addProperty(p);
		p = new DeviceProperty(PropertyType.PT_LONG,"ct","");		
		addProperty(p);
		p = new DeviceProperty(PropertyType.PT_LONG,"sat","");		
		addProperty(p);
		p = new DeviceProperty(PropertyType.PT_LONG,"ct","");		
		addProperty(p);		
	}
	
	protected void execute(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		switch (function.getFunctionType()){
		  case PhysicalDeviceFunction.TURN_ON  : turnOn(); break;
		  case PhysicalDeviceFunction.TURN_OFF : turnOff(); break;
		  case PhysicalDeviceFunction.SET_COLOR :
			  if (params != null){
			      if (params.size() == 1){
				      FunctionParameter fm = params.get(0);
				      if (fm.isValueSet()){
				    	  long value = (Long)fm.getValue();
				    	  setColor(value);
				      }
			      }
			  }
			  break;
		  case ParameterizedDeviceFunction.SET_BRIGHTNES :	
			  if (params.size() == 1){
			      FunctionParameter fm = params.get(0);
			      if (fm.isValueSet()){
			    	  double value = (Double)fm.getValue();
			    	  setBrightness(value);
			      }
		      }
			  break;
		}
	}
	
	protected void enable(){
		this.setAvailability(true);
	}
	
	protected void disable(){
		this.setAvailability(false);
	}
	
}
