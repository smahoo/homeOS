package de.smahoo.homeos.driver.zwave.fibaro;

import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.Lamp;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSwitchMultiLevel;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.JWaveNodeCommandFactory;
import de.smahoo.jwave.node.JWaveNode;

public class Fibaro_RGBController extends ZWaveGenericSwitchMultiLevel implements Lamp{

	public Fibaro_RGBController(String id, JWaveNode node){
		super(id,node);
	}
		
	@Override
	protected void initDevice(){		
		// nothing to to so far
	}
	
	
	public void setColor(int red, int green, int blue){
		
		JWaveNodeCommand nodeCmd = cmdFactory.generateCmd_ColorControl_State_Set();
		try {
			nodeCmd.setParamValue(0, 3);
			byte[] bytes = new byte[6];
			bytes[0] = 2; // capability id red
			bytes[1] = (byte)(255 * red/100);
			bytes[2] = 3; // capability id green
			bytes[3] = (byte)(255 * green/100);
			bytes[4] = 4; // capability id blue
			bytes[5] = (byte)(255 * blue/100);
			nodeCmd.setParamValue(1, bytes);
			getNode().sendData(nodeCmd);
		} catch (Exception exc){
			// FIXME
			exc.printStackTrace();
		}		
	}
	
	@Override
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd){
		super.evaluateReceivedNodeCmd(cmd);
	}
	
	@Override
	protected void onPropertyChanged(final DeviceProperty property){
		//
	}
	
	@Override
	protected void generateDeviceFunctions(){
		super.generateDeviceFunctions();
		
    	List<FunctionParameter> l = new ArrayList<FunctionParameter>();
    	FunctionParameter fm = new FunctionParameter(PropertyType.PT_INTEGER,"red");    	
    	l.add(fm);
    	fm = new FunctionParameter(PropertyType.PT_INTEGER,"green");
    	l.add(fm);
    	fm = new FunctionParameter(PropertyType.PT_INTEGER,"blue");
    	l.add(fm);
    	PhysicalDeviceFunction function;
    	function = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SET_COLOR,l);
    	addDeviceFunction(function);
	}
	
	@Override
	protected void generateProperties(){
		super.generateProperties();
	}
	
	@Override
	protected void executeDeviceFunction(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		super.executeDeviceFunction(function, params);
		
		switch (function.getFunctionType()){		 
		  case PhysicalDeviceFunction.SET_COLOR : 
			  if (params != null){
			      if (params.size() == 3){
				      FunctionParameter fm = params.get(0);
				      Integer red = (Integer)fm.getValue();
				      fm = params.get(1);
				      Integer green = (Integer)fm.getValue();
				      fm = params.get(2);
				      Integer blue = (Integer)fm.getValue();
				      setColor(red, green, blue);
			      }
			  }
		}
	}
}
