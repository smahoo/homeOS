package de.smahoo.homeos.remote;

import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.utils.AttributeValuePair;

public class RemoteFunctionExecutionEvent extends DeviceEvent{
	private String functionName = null;
	private  List<AttributeValuePair> parameter = null;
	
	public RemoteFunctionExecutionEvent(EventType eventType, Device device, String functionName,  List<AttributeValuePair> parameter){
		super(eventType,device);
		this.functionName = functionName;
		this.parameter = parameter;
	}

	public boolean hasParameter(){
		if (parameter != null){
			return !parameter.isEmpty();
		} else {
			return false;
		}
	}
	
	public  List<AttributeValuePair> getParameter(){
		return parameter;
	}
	
	public String getFunctionName(){
		return functionName;
	}
	
}
