package de.smahoo.homeos.device;

import de.smahoo.homeos.common.Function;
import de.smahoo.homeos.common.FunctionExecutionException;



public abstract class PhysicalDeviceFunction implements Function {

	public static final int ERROR_NOT_SET				= -1;
	public static final int ERROR_UNSUPPORTED			= -2;
	public static final int ERROR_MISC					= -3;
	
	public static final int MISC					= 999999999; 
	
	public static final int SEND					= 100000000;
	
	public static final int TURN_ON					= 100000010;
	public static final int SET_TURN_ON_DIMMTIME	= 100000110;
	
	public static final int TURN_OFF				= 100000011;
	public static final int SET_TURN_OFF_DIMMTIME	= 100000111;
	
	public static final int SWITCH					= 100000012;
	public static final int DIMM					= 100000013;
	public static final int DIMM_UP					= 100000014;
	public static final int DIMM_DOWN				= 100000015;
	public static final int SET_BRIGHTNES			= 100000016;
	
	public static final int SET_WAKEUP_INTERVAL		= 100000100;
	
	public static final int SET_TEMPERATURE			= 100010001;
	public static final int SET_TEMPERATURE_DAY 	= 100010002;
	public static final int SET_TEMPERATURE_NIGHT 	= 100010003;
	public static final int SET_TEMPERATURE_WINDOW  = 100010004;
	
	public static final int SET_ALARM				= 200000000;
	
	public static final int SET_COLOR				= 300000001;

	
	public static final int REQUEST_POWER			= 400000001;
	
	
	String functionFlag = "UNKNOWN";
	int functionType = ERROR_NOT_SET;	
	String name;
	PhysicalDevice assignedDevice = null; 
	
	
	public PhysicalDeviceFunction(String name){
		this(PhysicalDeviceFunction.MISC, name);
	}
	
	public PhysicalDeviceFunction(int functionType, String name){
		this(functionType);
		this.name = name;
	}
	
	
	
	public PhysicalDeviceFunction(int functionType){
		this.functionType = functionType;
		name = getDefaultName(functionType);		
	}
	
	public String getName(){
		return name;
	}
	
	public PhysicalDevice getDevice(){
		return this.assignedDevice;
	}
	
	@Override
	public String toString(){
		return getName();
	}
		
	
	protected void setDevice(PhysicalDevice device){
		this.assignedDevice = device;
	}
	
	public int getFunctionType(){
		return this.functionType;
	}
	
	
	
	public final static int[] getDefaultTypes(){
		int[] types = new int[19];
		
		types[0]   = ERROR_NOT_SET;
		types[1]   = ERROR_UNSUPPORTED;
		types[2]   = ERROR_MISC;		
		types[3]   = MISC	;		
		types[4]   = TURN_ON;
		types[5]   = SET_TURN_ON_DIMMTIME;		
		types[6]   = TURN_OFF;
		types[7]   = SET_TURN_OFF_DIMMTIME;		
		types[8]   = SWITCH;
		types[9]   = DIMM;
		types[10]  = DIMM_UP;
		types[11]  = DIMM_DOWN;		
		types[12]  = SET_TEMPERATURE;
		types[13]  = SET_TEMPERATURE_DAY;
		types[14]  = SET_TEMPERATURE_NIGHT;
		types[15]  = SET_TEMPERATURE_WINDOW;
		types[16]  = SEND;
		types[17]  = SET_BRIGHTNES;
		types[18]  = SET_COLOR;
		
		return types;
	}
	
	public final static int getDefaultType(String functionName){
		if (functionName == null) return ERROR_NOT_SET;
		if (functionName.toLowerCase().equals("turnon")) return TURN_ON;
		if (functionName.toLowerCase().equals("turnoff")) return TURN_OFF;
		if (functionName.toLowerCase().equals("switch")) return SWITCH;
		if (functionName.toLowerCase().equals("dimm")) return DIMM;
		if (functionName.toLowerCase().equals("dimmup")) return DIMM_UP;
		if (functionName.toLowerCase().equals("dimmdown")) return DIMM_DOWN;
		if (functionName.toLowerCase().equals("settemperature")) return SET_TEMPERATURE;
		if (functionName.toLowerCase().equals("settemperatureday")) return SET_TEMPERATURE_DAY;
		if (functionName.toLowerCase().equals("settemperaturenight")) return SET_TEMPERATURE_NIGHT;
		if (functionName.toLowerCase().equals("settemperaturewindow")) return SET_TEMPERATURE_WINDOW;
		if (functionName.toLowerCase().equals("setturnoffdimmtime")) return SET_TURN_OFF_DIMMTIME;
		if (functionName.toLowerCase().equals("setturnondimmtime")) return SET_TURN_ON_DIMMTIME;
		if (functionName.toLowerCase().equals("setcolor")) return SET_COLOR;
		if (functionName.toLowerCase().equals("send")) return SEND;
		if (functionName.toLowerCase().equals("setbrightnes")) return SET_BRIGHTNES;
		return MISC;
	}
	
	public final static String getDefaultName(int functionType){		
		
		switch (functionType) {
			case ERROR_NOT_SET : return "ERROR_NOT_SET";
			case ERROR_UNSUPPORTED : return "ERROR_UNSUPPORTED";
			case ERROR_MISC : return "ERROR_MISC";		
			case MISC		: return "MISC";	
			case TURN_ON 	: return "turnOn";
			case TURN_OFF 	: return "turnOff";
			case SWITCH		: return "switch";
			case DIMM		: return "dimm";
			case DIMM_UP	: return "dimmUp";
			case DIMM_DOWN  : return "dimmDown";
			case SET_BRIGHTNES : return "setBrightnes";
			case SET_COLOR  : return "setColor";
			case SET_TEMPERATURE		: return "setTemperature";
			case SET_TEMPERATURE_DAY 	: return "setTemperatureDay";
			case SET_TEMPERATURE_NIGHT 	: return "setTemperatureNight";
			case SET_TEMPERATURE_WINDOW : return "setTemperatureWindow";
			case SET_TURN_OFF_DIMMTIME	: return "setTurnOffDimmTime";
			case SET_TURN_ON_DIMMTIME   : return "setTurnOnDimmTime";
			case SEND					: return "send";
		}
		
		
		return "unknownFunction";
	}
	
	
    public final void execute() throws FunctionExecutionException{
	   assignedDevice.executeFunction(this,null);
    }
    
    
	
	
}
