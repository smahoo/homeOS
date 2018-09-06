package de.smahoo.homeos.driver.zwave.generic;

import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorAlarm;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveCommandParameterType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportSensorAlarm;
import de.smahoo.jwave.node.JWaveNode;

public class ZWaveGenericSensorAlarm extends ZWaveDevice implements SensorAlarm{
	

		public ZWaveGenericSensorAlarm(String id, JWaveNode node) {
			super(id, node);		
		}

		@Override
		public boolean isAlarm() {
			DeviceProperty p = this.getProperty("alarm");
			if (p.isValueSet()){
				return (Boolean)p.getValue();				
			}
			return false;
		}

		@Override
		protected void evaluateReceivedNodeCmd(JWaveNodeCommand nodeCmd) {
			try {
				switch (nodeCmd.getCommandClassKey()){
				case 0x9C:  // COMMAND_CLASS_SENSOR_ALARM
					if (nodeCmd.getCommandKey() == 0x02) { // REPORT
						JWaveReportSensorAlarm report = JWaveReportFactory.generateSensorAlarmReport(nodeCmd);
						
						int alarm = 0; // no alarm
						
						if (report.isAlarm()){
							alarm = 1;
						}
						
						updateAlarm(alarm);
						
					}
					break;
				case 0x20: // COMMAND_CLASS_BASIC
					if (nodeCmd.getCommandKey() == 0x01){ //BASIC_SET
						if (0 == JWaveCommandParameterType.toInteger(nodeCmd.getParamValue(0))){					
							updateAlarm(0);	
						} else {
							updateAlarm(1);
						}
						
					}
					break;
				default :
					
					
				
				}
			}catch (Exception exc){
				
				//throw new JWaveException("Unable to interprete incoming JWaveNodeCommand",exc);
			}
			
			
		}

		protected void updateAlarm(int alarm){
			boolean boolAlarm = (alarm == 1); 
		
			DeviceProperty p = this.getProperty("alarm");
			if (p.isValueSet()){
				boolean val = (Boolean)p.getValue();
				if (val != boolAlarm){
					p.setValue(boolAlarm);
				}
			} else {
				p.setValue(boolAlarm);
			}
			
		}
		
		@Override
		protected void initDevice() {		
			associateNode(1, 1);
		}

		@Override
		protected void executeDeviceFunction(PhysicalDeviceFunction function, List<FunctionParameter> params)  throws FunctionExecutionException {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void onPropertyChanged(DeviceProperty property) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void generateDeviceFunctions() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void generateProperties() {
			DeviceProperty p = new DeviceProperty(PropertyType.PT_BOOLEAN, "alarm", "");
			p.setValue(false);
			addProperty(p);
			
		}


}
