package de.smahoo.homeos.driver.zwave;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveCommandClassSpecification;
import de.smahoo.jwave.cmd.JWaveCommandParameterType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.JWaveNodeCommandFactory;
import de.smahoo.jwave.cmd.report.JWaveReportBattery;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportWakeUpInterval;
import de.smahoo.jwave.event.JWaveNodeDataEvent;
import de.smahoo.jwave.event.JWaveNodeEvent;
import de.smahoo.jwave.event.JWaveNodeEventListener;
import de.smahoo.jwave.node.JWaveNode;


public abstract class ZWaveDevice extends PhysicalDevice{

	private JWaveNode node = null;
	protected int primaryControllerId = 1;
	protected JWaveNodeCommandFactory cmdFactory = null;
	private List<ZWaveSleepListener> sleepListener = null;
	
	protected static final int DEFAULT_WAKEUP_INTERVAL = 1800;

	private Date lastBatteryRequest = null;
	private boolean wakeupIntervalSet = false;


	public ZWaveDevice(String id, JWaveNode node){		
		super(id);
		this.setAddress(""+node.getNodeId());
		this.node = node;
		node.addEventListener(new JWaveNodeEventListener() {
			
			@Override
			public void onNodeEvent(JWaveNodeEvent event) {
				evaluateNodeEvent(event);				
			}
		});				
	}
	
	
	protected void setAvailability(boolean available){
		super.setAvailability(available);
	}
	
	protected void setBattery(int battery){			
			DeviceProperty property = this.getProperty("battery");
			if (property.isValueSet()){				
				Integer pVal = (Integer)property.getValue();
				if (pVal != battery){
					property.setValue(battery);				
				}
			} else {
				property.setValue(battery);
			}
		
	}
	
	public int getBattery(){
		if (!getNode().supportsClassBattery()){
			return -1;
		}
		DeviceProperty property = this.getProperty("battery");
		if (property.isValueSet()){
			return (Integer)property.getValue();
		} else {
			return 0;
		}
		
	}
	
	
	public long getWakeUpInterval(){
		if (!getNode().supportsClassWakeUp()){
			return 0;
		}
		DeviceProperty property = this.getProperty("wakeup_interval");
		if (property == null){
			return 0;
		}
		if (property.isValueSet()){
			return (Integer)property.getValue();
		}
		return 0;
	}
	
	
	protected int getPrimaryControllerId(){
		return this.primaryControllerId;
	}
	
	protected JWaveCommandClassSpecification getZWaveDefinitions(){
		JWaveCommandClassSpecification defs = null;
		ZWaveDriver zd = (ZWaveDriver)this.getDriver();
		defs = zd.getDefinitions();
		return defs;
	}
	

				
	protected void updateWakeUpInterval(int seconds){		
		DeviceProperty property = this.getProperty("wakeup_interval");
		
		if (property.isValueSet()){
			Integer pVal = (Integer)property.getValue();
			if (pVal != seconds){				
				property.setValue(seconds);
				
			}
		} else {
			property.setValue(seconds);
		}
	}
	
	protected void setWakeUpInterval(int seconds){	
		getNode().sendData(cmdFactory.generateCmd_WakeUpInterval_Set(seconds, this.getPrimaryControllerId()));
		this.wakeupIntervalSet = true;
		//updateWakeUpInterval(seconds);
	}
	
	protected void setDefaultWakeupInterval(){
		setWakeUpInterval(DEFAULT_WAKEUP_INTERVAL);
	}
	
	protected void requestBatteryState(){
		lastBatteryRequest = new Date();
		getNode().sendData(cmdFactory.generateCmd_Battery_Get());
	}
	
	protected void requestWakeUpInterval(){
	
		getNode().sendData(cmdFactory.generateCmd_WakUpInterval_Get());		
	}
	
	protected void associateNode(int group, int nodeId){
		
		getNode().sendData(cmdFactory.generateCmd_Association_Set(group, nodeId));
	}
	
	protected synchronized void evaluateNodeEvent(JWaveNodeEvent event){
		// NODE_EVENT_DATA_RECEIVED
		switch (event.getEventType()){	
			case NODE_EVENT_STATUS_CHANGED:
				// onStatusChanged();
			case NODE_EVENT_WAKEUP:
				requestBatteryStateIfNeeded();
				dispatchWakeUpEvent();
				getNode().setNodeToSleep();
				break;
			case NODE_EVENT_SLEEP:
				dispatchSleepEvent();
				break;
			case NODE_EVENT_DATA_RECEIVED:
					if (event instanceof JWaveNodeDataEvent){
						JWaveNodeCommand cmd = ((JWaveNodeDataEvent)event).getNodeCmd();
						checkInternalReceivedNodeCmd(cmd);
						evaluateReceivedNodeCmd(cmd);
					}
				break;
			default:
				break;
		}
		
		dispatchChangeEventsIfNeeded();
		setLastActivity();
		setAvailability(true);		
	}	
	
	protected void requestBatteryStateIfNeeded(){
		if (node.supportsClassBattery()){	
			if (lastBatteryRequest == null){
				this.requestBatteryState();
				return;
			}
			Date now = new Date();
			// if last request was 24h and more ago;
			if (now.getTime() - lastBatteryRequest.getTime() > 1000 * 60 * 60 * 24){
				this.requestBatteryState();
			}
		}
	}
	
	
	protected void checkInternalReceivedNodeCmd(JWaveNodeCommand cmd){
		try {
			switch (cmd.getCommandClassKey()){
			case 0x80:	// COMMAND_CLASS_BATTERY
				if (cmd.getCommandKey() == 0x03){
					evaluateBatteryReport(cmd);	
				}				
				break;
			case 0x84:	// COMMAND_CLASS_WAKEUP
				if (cmd.getCommandKey() == 0x06){
					evaluateWakeUpIntervalReport(cmd);
				}
				default:
					break;
			}
		} catch (Exception exc){
			exc.printStackTrace();
			// FIXME
		}
	}
	
	protected void evaluateWakeUpIntervalReport(JWaveNodeCommand nodeCmd){
		try {
			JWaveReportWakeUpInterval intRep = JWaveReportFactory.generateWakeUpIntervalReport(nodeCmd);
			this.updateWakeUpInterval(intRep.getInterval());
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	protected void evaluateBatteryReport(JWaveNodeCommand nodeCmd){
		try {
			JWaveReportBattery batRep = JWaveReportFactory.generateBatteryReport(nodeCmd);
			this.setBattery(batRep.getBattery());
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	protected void addSleepListener(ZWaveSleepListener listener){
		if (sleepListener == null){
			sleepListener = new ArrayList<ZWaveSleepListener>();
		}
		if (sleepListener.contains(listener)){
			return;
		}
		sleepListener.add(listener);
	}
	
	protected void dispatchSleepEvent(){
		if (sleepListener == null) return;
		for (ZWaveSleepListener l : sleepListener){
			l.onSleep();
		}
	}
	
	protected void dispatchWakeUpEvent(){
		if (sleepListener == null) return;
		for (ZWaveSleepListener l : sleepListener){
			l.onWakeUp();
		}
	}
	
	protected void dispatchWakeUpIntervalSetEvent(long seconds){
		if (sleepListener == null) return;
		for (ZWaveSleepListener l : sleepListener){
			l.onWakeUpIntervalSet(seconds);
		}
	}
	
	public Date getLastActivity(){
		return lastActivity;
	}
	
	@Override
	public boolean  isOn(){
		return true;
	}
	
	
	@Override
	public boolean  isAvailable(){
		return true;
	}
	
	protected JWaveNode getNode(){
		return node;
	}
	
	@Override
	protected void execute(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		
		if("sendAssociation".equalsIgnoreCase(function.getName())){
			if (params != null){
				if (params.size() == 2){
					FunctionParameter fm = params.get(0);
					
					int groupId = (Integer)fm.getValue();
					int nodeId = (Integer)params.get(1).getValue();
					
					associateNode(groupId, nodeId);
				}
			}
		}
		
		if ("sendConfiguration".equalsIgnoreCase(function.getName())){
			if (params != null){
				if (params.size() == 3){
					FunctionParameter fm = params.get(0);
					
					int paramId = (Integer)fm.getValue();
					JWaveCommandParameterType type = JWaveCommandParameterType.valueOf((String)params.get(1).getValue());
					int value = (Integer)params.get(2).getValue();
					
					sendConfiguration(paramId, type, value);
					
				}
			}			
		}
		switch (function.getFunctionType()){		  	
		  case PhysicalDeviceFunction.SET_WAKEUP_INTERVAL : 
			  if (params != null){
			      if (params.size() == 1){
				      FunctionParameter fm = params.get(0);
				      Integer value = (Integer)fm.getValue();
				      setWakeUpInterval(value);				      
			      }
			  }
			  return;
			  default:
				  executeDeviceFunction(function, params);
				  break;
		}
		
		
	}
	
	protected void init() {
		addProperties();
		addFunctions();
		if (node.supportsClassBattery()){		
			requestBatteryState();
		}				
		initDevice();
		if (node.supportsClassWakeUp()){
			if (!wakeupIntervalSet){				
				setDefaultWakeupInterval();
				requestWakeUpInterval();
			}
		}	
	}
	
	protected void addProperties(){
		if (node.supportsClassBattery()){
			addProperty(new DeviceProperty(PropertyType.PT_INTEGER, "battery", "%"));		
		}
		if (node.supportsClassWakeUp()){
			addProperty(new DeviceProperty(PropertyType.PT_INTEGER, "wakeup_interval", "sec"));	
		}
	}
	
	protected void addFunctions(){
		
		if (node.supportsClassWakeUp()){
			PhysicalDeviceFunction function;
			
			FunctionParameter fm = new FunctionParameter(PropertyType.PT_INTEGER,"seconds");
			List<FunctionParameter> l = new ArrayList<FunctionParameter>();
			l.add(fm);
			function = new ParameterizedDeviceFunction("setWakeUpInterval",l);
    	
			this.addDeviceFunction(function);	
    	
		}
		if (node.supportsClassConfiguration()){
			PhysicalDeviceFunction function;
			
			FunctionParameter fm = new FunctionParameter(PropertyType.PT_INTEGER,"paramId");
			List<FunctionParameter> l = new ArrayList<FunctionParameter>();
			l.add(fm);
			
			fm = new FunctionParameter(PropertyType.PT_STRING,"valueType");			
			l.add(fm);
			
			
			fm = new FunctionParameter(PropertyType.PT_INTEGER,"value");			
			l.add(fm);
			
			function = new ParameterizedDeviceFunction("sendConfiguration",l);
    	
			this.addDeviceFunction(function);	
		}
		if (node.supportsClassAssociation()){
			PhysicalDeviceFunction function;
			
			FunctionParameter fm = new FunctionParameter(PropertyType.PT_INTEGER,"groupId");
			List<FunctionParameter> l = new ArrayList<FunctionParameter>();
			l.add(fm);						
			
			 fm = new FunctionParameter(PropertyType.PT_INTEGER,"nodeId");			
			 l.add(fm);						
				
			
			function = new ParameterizedDeviceFunction("sendAssociation",l);
    	
			this.addDeviceFunction(function);	
		}
	}
	

	protected void sendConfiguration(int paramId, JWaveCommandParameterType type, int value){
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set(paramId, type, value));
	}
	
	abstract protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd);
	abstract protected void initDevice();
	abstract protected void executeDeviceFunction(PhysicalDeviceFunction function,List<FunctionParameter> params) throws FunctionExecutionException;
	//abstract protected void executeFunction(PhysicalDeviceFunction function,List<FunctionParameter> params);
	
}
