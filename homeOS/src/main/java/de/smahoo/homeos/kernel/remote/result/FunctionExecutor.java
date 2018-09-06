package de.smahoo.homeos.kernel.remote.result;

import java.util.List;


import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.devices.Dimmable;
import de.smahoo.homeos.devices.ExtendedTelevision;
import de.smahoo.homeos.devices.HeatingRtc;
import de.smahoo.homeos.devices.Lamp;
import de.smahoo.homeos.devices.Socket;
import de.smahoo.homeos.utils.AttributeValuePair;

public class FunctionExecutor {

	public ExecutionResultItem processExecutionCmd(DeviceType type, Device device, String function, List<AttributeValuePair> avList ) {
		
		// FIX ME - delete ( and ) from function first		
		
		switch(type){		
			case DIMMER: return executeDimmerFunction((Dimmable)device, function, avList);			
			case SOCKET: return executeSocketFunction((Socket)device, function, avList);				
			case LAMP: 	 return executeLampFunction((Lamp)device, function, avList);
			case HEATING_RTC: return executeHeatingFunction((HeatingRtc)device, function, avList);
			case TELEVISION_EXTENDED: return executeTVExtFunction((ExtendedTelevision)device,function,avList);
		}
		return new ExecutionResultItem(device.getDeviceId(), function, false, "unsupported device type "+type.name());
	}
	
	private ExecutionResultItem executeTVExtFunction(ExtendedTelevision tv,String function, List<AttributeValuePair> avList){
		if (function.equalsIgnoreCase("channelDown")){
			tv.channelDown();
			return new ExecutionResultItem(tv.getDeviceId(),function);
		}
		if (function.equalsIgnoreCase("channelUp")){
			tv.channelUp();
			return new ExecutionResultItem(tv.getDeviceId(),function);
		}
		if (function.equalsIgnoreCase("volumeDown")){
			tv.volumeDown();
			return new ExecutionResultItem(tv.getDeviceId(),function);
		}
		if (function.equalsIgnoreCase("volumeUp")){
			tv.volumeUp();
			return new ExecutionResultItem(tv.getDeviceId(),function);
		}
		if (function.equalsIgnoreCase("power")){
			tv.power();
			return new ExecutionResultItem(tv.getDeviceId(),function);
		}
		if (function.equalsIgnoreCase("mute")){
			tv.mute();
			return new ExecutionResultItem(tv.getDeviceId(),function);
		}
		return new ExecutionResultItem(tv.getDeviceId(), function, false, "unsupported function "+function+" for device type Television");
	}
	
	private ExecutionResultItem executeHeatingFunction(HeatingRtc heating,  String function,  List<AttributeValuePair> avList){
		if (function.equalsIgnoreCase("setTemperature")){
			for (AttributeValuePair avp : avList){
				if (avp.getAttribute().equalsIgnoreCase("temperature")){
					heating.setTemperature(Double.parseDouble(avp.getValue()));
					return new ExecutionResultItem(heating.getDeviceId(), function);
				}
			}
			return new ExecutionResultItem(heating.getDeviceId(), function, false, "parameter \"temperature\" is missing");
		}
		return new ExecutionResultItem(heating.getDeviceId(), function, false, "unsupported function "+function+" for device type Heating");
	}
	
	private ExecutionResultItem executeDimmerFunction(Dimmable dimmer, String function,  List<AttributeValuePair> avList){
		
		if (function.equalsIgnoreCase("dimm")){
			for (AttributeValuePair avp : avList){
				if (avp.getAttribute().equalsIgnoreCase("temperature")){
					dimmer.setDimmLevel(Integer.parseInt(avp.getValue()));
					return new ExecutionResultItem(dimmer.getDeviceId(), function);
				}
			}
			return new ExecutionResultItem(dimmer.getDeviceId(), function);
		}
		if (function.equalsIgnoreCase("turnOn")){
			dimmer.turnOn();
			return new ExecutionResultItem(dimmer.getDeviceId(), function);
		}
		if (function.equalsIgnoreCase("turnOff")){
			dimmer.turnOff();
			return new ExecutionResultItem(dimmer.getDeviceId(), function);
		}
		return new ExecutionResultItem(dimmer.getDeviceId(), function, false, "unsupported function "+function+" for device type Dimmer");
		
	}
	
	private ExecutionResultItem executeSocketFunction(Socket socket, String function,  List<AttributeValuePair> avList){
		
		if (function.equalsIgnoreCase("turnOn")){
			socket.turnOn();
			return new ExecutionResultItem(socket.getDeviceId(), function);
		}
		if (function.equalsIgnoreCase("turnOff")){
			socket.turnOff();
			return new ExecutionResultItem(socket.getDeviceId(), function);
		}
		return new ExecutionResultItem(socket.getDeviceId(), function, false, "unsupported function "+function+" for device type Socket");
		
	}
	
	private ExecutionResultItem executeLampFunction(Lamp lamp, String function,  List<AttributeValuePair> avList){
		
		if (function.equalsIgnoreCase("turnOn")){
			lamp.turnOn();
			return new ExecutionResultItem(lamp.getDeviceId(), function);
		}
		if (function.equalsIgnoreCase("turnOff")){
			lamp.turnOff();
			return new ExecutionResultItem(lamp.getDeviceId(), function);
		}
		return new ExecutionResultItem(lamp.getDeviceId(), function, false, "unsupported function "+function+" for device type Lamp");
		
	}
	
	
}
