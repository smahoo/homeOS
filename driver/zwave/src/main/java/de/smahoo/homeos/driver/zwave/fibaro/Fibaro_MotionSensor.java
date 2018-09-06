package de.smahoo.homeos.driver.zwave.fibaro;

import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.devices.SensorMotion;
import de.smahoo.homeos.devices.SensorTemperature;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSensorBinary;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveCommandParameterType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.report.JWaveReportConfiguration;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportSensorAlarm;
import de.smahoo.jwave.cmd.report.JWaveReportSensorMultilevel;
import de.smahoo.jwave.node.JWaveNode;

public class Fibaro_MotionSensor extends ZWaveGenericSensorBinary implements SensorMotion, SensorTemperature{

	
	public Fibaro_MotionSensor(String id, JWaveNode node) {
		super(id, node);		
	}
	
	public void initDevice(){		
		associateNode(1, this.getPrimaryControllerId());	// motions reports
		associateNode(2, this.getPrimaryControllerId());	// tamper reports
		associateNode(3, this.getPrimaryControllerId());	// light and luminance changes
		requestValues();	// requesting current values
		setTamperOperatingMode(1); 	// activate tamper cancellation by device
	}
	
	
	@Override
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd){
		super.evaluateReceivedNodeCmd(cmd);
		try {
			switch (cmd.getCommandClassKey()){
				case 0x9C:  // COMMAND_CLASS_SENSOR_ALARM
					if (cmd.getCommandKey() == 0x02) { // REPORT					
						evalAlarmReport(JWaveReportFactory.generateSensorAlarmReport(cmd));
					}				
					break;
				case 0x31: // COMMAND_CLASS_SENSOR_MULTILEVEL
					setSensorValue(JWaveReportFactory.generateSensorMultilevelReport(cmd));				
					break;
				case 0x70:
					if (cmd.getCommandKey() == 0x06){
						evaluateConfigurationReport(JWaveReportFactory.generateConfigurationReport(cmd));
					}
					break;
				default:
					break;
			}
		} catch (Exception exc) {
			exc.printStackTrace();	
		}
	}	
	
	protected void requestValues(){
		requestTemperature();	
		requestLuminance();	
		requestSensorBinary();
	}
	
	
	protected void requestLuminance(){		
		getNode().sendData(cmdFactory.generateCmd_SensorMultilevel_Get_V5(3));
	}
	
	protected void requestTemperature(){		
		getNode().sendData(cmdFactory.generateCmd_SensorMultilevel_Get_V5(1));		
	}
	
	
	protected void evalAlarmReport(JWaveReportSensorAlarm report){
		DeviceProperty property = this.getProperty("tamper");
		if (property.isValueSet()){
			Boolean pVal = (Boolean)property.getValue();
			if (pVal != report.isAlarm()){
				property.setValue(report.isAlarm());				
			}
		} else {
			property.setValue(report.isAlarm());
		}
	}
	
	protected void setSensorValue(JWaveReportSensorMultilevel report){
		switch (report.getSensorType()){
		case 1:
			if (report.getPrecission() > 0){
				setTemperature((double)report.getValue()/(double)report.getPrecission());
			} else {
				setTemperature(report.getValue());
			}
			break;
		case 3:
			if (report.getPrecission() > 0){
				setLuminance((double)report.getValue()/(double)report.getPrecission());
			} else {
				setLuminance(report.getValue());
			}
			break;
		}
	}
	
		protected void setLuminance(double luminance){
			DeviceProperty property = this.getProperty("luminance");
			if (property.isValueSet()){
				Double pVal = (Double)property.getValue();
				if (pVal != luminance){
					property.setValue(luminance);				
				}
			} else {
				property.setValue(luminance);
			}
		}
		
		protected void setTemperature(double temperature){			
			DeviceProperty property = this.getProperty("temperature");
			if (property.isValueSet()){
				Double pVal = (Double)property.getValue();
				if (pVal != temperature){
					property.setValue(temperature);				
				}
			} else {
				property.setValue(temperature);
			}
			
		}
		
	protected void evaluateConfigurationReport(JWaveReportConfiguration confRep){
		
	}
	

	
	
	
	@Override
	protected void updateValue(boolean value){
		DeviceProperty property = this.getProperty("motion");
		if (property.isValueSet()){
			Boolean pVal = (Boolean)property.getValue();
			if (pVal != value){
				property.setValue(value);				
			}
		} else {
			property.setValue(value);
		}
	}	
	
	public boolean isMotion(){
		DeviceProperty property = this.getProperty("motion");
		if (property.isValueSet()){
			return (Boolean)property.getValue();
			
		}
		return false;
	}
	
	
	
	@Override
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_BOOLEAN, "motion", ""));		
		addProperty(new DeviceProperty(PropertyType.PT_BOOLEAN, "tamper", ""));		
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "temperature", "°C"));
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "luminance", "lx"));		
	}

	
	
	@Override
	public double getTemperature() {
		DeviceProperty property = this.getProperty("temperature");
		if (property.isValueSet()){
			Double pVal = (Double)property.getValue();
			return pVal;
		} else {
			return 0.0;
		}
	}
	
	
	/**
	 * Sets the motion sensitivity
	 * 
	 * @param sensitivity
	 * 		available settings	: 8-255
	 * 		default settings	: 10
	 */
	protected void setMotionsSensitivity(int sensitivity){
		if (sensitivity < 8) return;
		if (sensitivity > 255) return;
		
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(1),JWaveCommandParameterType.BYTE,sensitivity));
	}
	
	/**
	 * Sets the motion sensor blind time. 
	 * Period of time through which the PIR sensor is "blind" (insensitive) to motion. After this time period the PIR sensor will be again
	 * able to detect motion. The longer the insensitivity period, the longer the battery life. If the sensor is required to detect motion 
	 * quickly, the time period may be shortened. the time of insensitivity should be shorter than the time period set in parameter 6.
	 * 
	 * 		 
	 * @param value
	 * 
	 * 		Formula to calculate the time : time [s] = 0.5 * (value + 1)
	 * 		available settings	: 0 - 15
	 * 		default settings	: 8 (15s)
	 *	
	 */
	protected void setMotionSensorBlindTime(int value){
		if (value < 0) return;
		if (value > 15) return;
		
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(2),JWaveCommandParameterType.BYTE,value));
	}
	
	
	/**
	 * Sets the number of moves required for the PIR sensor to report motions, The lower the value, the less sensitive the PIR sensor.
	 * Its not recommended to modify this parameter setting.
	 * 
	 * @param value
	 * 
	 * 		Formula to calculate the number of pulses:  pulses = value +1
	 * 		available settings	: 0 - 3
	 * 		default settings	: 1 (2 pulses)
	 * 		
	 */
	protected void setMotionsSensorPulseCounter(int value){
		if (value < 0) return;
		if (value > 3) return;
		
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(3),JWaveCommandParameterType.BYTE,value));
	}
	
	
	
	/**
	 * Period of time during which the number of moves set with setMotionsSensorPulseCounter() must be detected in order for the PIR
	 * sensor to report motion. The higher the value, the more sensitive the PIR sensor. It's not recommended to modify this parameter setting.
	 * 
	 * @param value
	 * 
	 * 		Formula to calculate the time: time [s] = 4x(value + 1)
	 * 		available settings	: 0-3
	 * 		default settings	: 2 (12s)
	 */
	protected void setMotionsSensorWindowTime(int value){
		if (value < 0) return;
		if (value > 3) return;
		
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(4),JWaveCommandParameterType.BYTE,value));
	}
	
	
	/**
	 * Motion alarm will be cancelled in the main controller and the associated devices after the period of time set in
	 * this parameter. Any motion detected during the cancellation delay time countdown will result in the countdown being restarted.
	 * In case of small values, below 10s, the value motion blind time must be modified.
	 * 
	 * @param value
	 * 
	 * 		available settings	: 1 - 65535
	 * 		default settings	: 30
	 */
	protected void setMotionsAlarmCancelationDelay(int value){
		if (value < 0) return;
		if (value > 65535) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(6),JWaveCommandParameterType.WORD,value));		
	}
	
	
	
	/**
	 * the parameter determines the part of day in which the PIR sensor will be active. This parameter influences
	 * only the motions reports and associations. Tamper, light intensity and temperature measurements will be still active, 
	 * regardless of this parameter settings.
	 * 
	 * @param value
	 * 
	 * 		0 - PIR sensor always active
	 * 		1 - PIR sensor active during the day only
	 * 		2 - PiR sensor active durign the night only
	 */
	protected void setMotionsSensorOperatingMode(int value){
		if (value < 0) return;
		if (value > 2) return;
		
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(8),JWaveCommandParameterType.BYTE,value));
	}
	
	
	
	/**
	 * The parmeter defines the difference between night and day in terms of light intensity used for operating mode 1 or 2.
	 * 
	 * @param value
	 * 
	 * 		available settings: 1 - 65535 (copied from manual -> doubtful sensor range of 1 to 65535 lx)
	 * 		default   settings:	200 (200lx)		
	 *  
	 */
	protected void setNightDayThreshold(int value){
		if (value < 0) return;
		if (value > 65535) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(9),JWaveCommandParameterType.WORD,value));		
	}
	
	
	/**
	 * The parameter determines the command rames sent in 1st association group, assigned to PIR sensor.
	 * 
	 * @param value
	 * 
	 * 		0 - BASIC ON and BASIC off command frames sent in Basic Command Class
	 * 		1 - only the BASIC ON command will be sent
	 * 		2 - only the BASIC OFF command will be sent
	 * 
	 * 
	 * 		default settings	: 0
	 * 		HINT: Values of BASIC ON and BASIC OFF command frames may be modified by other methodes ()
	 */
	protected void setBasicCommandConfiguration(int value){
		if (value < 0) return;
		if (value > 3) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(12),JWaveCommandParameterType.BYTE,value));		
	}
	
	
	/**
	 * BASIC ON can be used for dimming commands (COMMAND_CLASS_SWITCHMULTILEVEL)
	 * @param value
	 * 
	 * 		available values: 0-255 (0 off, 255 on last memorized dimming value)
	 * 		default settings: 255
	 */
	protected void setBasicOnCommandFrameValues(int value){
		if (value < 0) return;
		if (value > 255) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(14),JWaveCommandParameterType.BYTE,value));			
	}
	
	/**
	 * BASIC OFF can be used for dimming commands (COMMAND_CLASS_SWITCHMULTILEVEL)
	 * @param value
	 * 
	 * 		available values: 0-255 (0 off, 255 on last memorized dimming value)
	 * 		default setting	:	0
	 */
	protected void setBasicOffCommandFrameValues(int value){
		if (value < 0) return;
		if (value > 255) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(16),JWaveCommandParameterType.BYTE,value));			
	}
	
	
	
	/**
	 * Sets the sensititivy of the tamper sensor
	 * 
	 * @param sensitivity
	 * 		available settings 	: 0 - 122 (0.08g - 2g; multiply by 0.016g; 0 = tamper off)
	 * 		default setting		: 15 (0.224g)
	 */
	protected void setTamperSensitivity(int sensitivity){
		if (sensitivity < 0) return;
		if (sensitivity > 122) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(20),JWaveCommandParameterType.BYTE,sensitivity));
		
	}
	
	
	/**
	 * Time period after which a tamper alarm will be cancelled.
	 * Another tampering detected during the countdown to cancellation will not extend the delay.
	 * 
	 * @param value
	 * 	
	 * 		available settings	: 1 - 65535
	 * 		default value		: 30
	 * 
	 */
	protected void setTamperCancellationDelayTime(int value){
		if (value < 0) return;
		if (value > 65535) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(22),JWaveCommandParameterType.WORD,value));	
	}
	
	
	/**
	 * Sets the tamper operating mode.
	 * 
	 * @param value
	 * 
	 * 		0	Tamper alarm is reported in Sensor Alarm command class. Cancellation is not reported
	 * 
	 * 		1	Tamper alarm is reported in Sensor Alarm Command Class. Cancellation will be reported after cancellation 
	 * 			delay (set with setTamperCancellationDelay())
	 * 
	 * 		2	Tamper alarm is reported in Sensor Alarm command class. Cancellation is not reported.
	 * 			Sensors orientation is reported in Fibar Command Class after cancellation delay
	 * 
	 * 		3	Tamper alarm is reported in sensor alarm command class. Cancellation is reported after cancellation delay. 
	 * 			Sensors orientation is reported in Fibar Command Class after cancellation delay
	 * 			
	 * 		4	The maximum level of vibrations recorded in the cancellation delay time is reported. Reports stop being sent
	 * 			when the vibrations cease. The reports are sent in Sensor Alarm command class.
	 * 										
	 */
	protected void setTamperOperatingMode(int value){
		if (value < 0) return;
		if (value > 4) return;
		
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(24),JWaveCommandParameterType.BYTE,value));
	}
	
	/**
	 * Set whether the tamper alarm frame will or will not be sent in broadcast mode. Alarm frames sent in broadcast mode may
	 * be received by all of the devices within communication range (if they accept such frames)
	 * 
	 * @param value
	 * 
	 * 		0	Tamper alarm is not sent in broadcast mode
	 * 		1 	Tamper alarm is sent in broadcast mode
	 */
	protected void setTamperAlarmBroadcastMode(int value){
		if (value < 0) return;
		if (value > 1) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(26),JWaveCommandParameterType.BYTE,value));
	}
	
	
	/**
	 * Set the change in light intensity level resulting in illumination report being sent to the main controller
	 * 
	 * @param value
	 * 
	 * 		available settings	:	0 - 65535 ( 1 - 65535 lx; 0 means reports will not be sent)
	 * 		default settings	:	200 (200lx)
	 */
	protected void setIlluminationReportThreshold(int value){
		if (value < 0) return;
		if (value > 65535) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(40),JWaveCommandParameterType.WORD,value));
	}
	
	
	/**
	 * Sets the time interval between consecutive illumination reports. The reports are sent even if there are no changes in the light intensity.	 * 
	 * 
	 * @param value
	 * 
	 * 		available settings	: 0 - 65535 (seconds; 0 means report are not sent)
	 * 		default settings	: 0
	 */
	public void setLuminanceReportInterval(int value){		
		if (value < 0) return;
		if (value > 65535) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(42),JWaveCommandParameterType.WORD,value));		
	}
	
	
	/**
	 * sets the change in level of temperature resulting in temperature report being sent to main controller.
	 * 
	 * @param value
	 * 
	 * 		available settings	: 0-255 (0.1 - 25.5 °C; 0 = no reports will be sent)
	 * 		default settings	: 10 (1.0°C)
	 * 
	 */
	protected void setTemperatureReportThreshold(int value){
		if (value < 0) return;
		if (value > 255) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(60),JWaveCommandParameterType.BYTE,value));
	}
	
	
	/**
	 * Sets how often the temperature will be measured. The shorter the time, the more frequently the temperature will be measured, 
	 * but the battery life will shorten. 
	 * 
	 * @param value
	 * 
	 * 		available settings	:	0 - 65535 (1- 65535 seconds; 0 = no temperature measurement)
	 * 		default settings	:	900 (900 seconds)
	 */
	protected void setTemperatureMeasurementInterval(int value){
		if (value < 0) return;
		if (value > 65535) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(62),JWaveCommandParameterType.WORD,value));
	}
	
	
	/**
	 * Sets the time interval between consecutive temperature reports. The reports are sent even if there are no changes in the temperature.
	 * 
	 * @param value
	 * 
	 * 		available settings	: 0 - 65535 (seconds; 0 means report are not sent)
	 * 		default settings	: 0
	 * 
	 */
	protected void setTemperatureReportInterval(int value){		
		if (value < 0) return;
		if (value > 65536) return;
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(64),JWaveCommandParameterType.WORD,value));
		
	}
	
	/**
	 * The value to be added to the current temperature, measured by the sensor.
	 * 
	 * @param value
	 * 
	 * 		available settings	: 	0 - 100 (0 - 10.0°C)
	 * 								64535 - 65535 ( -10.0 - 0.10 °C)
	 * 		default settings	: 	0
	 */
	protected void setTemperatureOffset(int value){
		if (value < 0) return;
		if (value > 65535) return;
		
		if ((value > 100) && (value < 64535)){
			return;
		}
		
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(66),JWaveCommandParameterType.WORD,value));
		
	}
	

}
