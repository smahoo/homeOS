package de.smahoo.homeos.driver.zwave.generic;

import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.MeterEnergy;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.JWaveController;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportMeter;
import de.smahoo.jwave.node.JWaveNode;
import de.smahoo.jwave.utils.logger.LogTag;

public class ZWaveGenericMeter extends ZWaveDevice implements MeterEnergy{

	double value;
	
	public ZWaveGenericMeter(String id, JWaveNode node) {
		super(id, node);		
	}

	@Override
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd) {
		try {
			switch (cmd.getCommandClassKey()){
				case 0x32 : // COMMAND_CLASS_METER_REPORT
					evaluateReport(JWaveReportFactory.generateMeterReport(cmd));
					break;	
				default:
					break;
			}
		} catch (Exception exc){
			exc.printStackTrace();
			// FIXME
		}
		
	} 
	
	protected void evaluateReport(JWaveReportMeter report){
		
		if (report.getPrecission() > 0){
			value = (double)report.getValue()/(double)report.getPrecission();

		} else {
			value = (double)report.getValue();
		}
		
		switch (report.getScale()){
			case JWaveReportMeter.SCALE_KWH:
			case JWaveReportMeter.SCALE_KVAH:
				updateUsageValue(value*1000);				
				break;
			case JWaveReportMeter.SCALE_W:
				updateConsumptionValue(value);
				break;
			default:
				JWaveController.log(LogTag.DEBUG,"new meter report received but not processed: value = "+value+", scale = "+report.getScale());
				break;
		}
		
	}
	
	protected void updateConsumptionValue(double value){
		// although the specification says, the value has scale type W, it's kW
		
		DeviceProperty prop = getProperty("power");
		if (prop == null) {
			JWaveController.log(LogTag.WARN,""+this.getClass().getName()+ "unable to find property power");
			return;
		}
		prop.setValue(value);
	}
	
	protected void updateUsageValue(double value){
		DeviceProperty prop = getProperty("consumption");
		if (prop == null){
			JWaveController.log(LogTag.WARN,""+this.getClass().getName()+ "unable to find property consumption");
			return;
		}
		prop.setValue(value);
	}

	@Override
	protected void initDevice() {
		// TODO Auto-generated method stub
		associateNode(01,01);
		associateNode(02,01);
	}

	@Override
	protected void executeDeviceFunction(PhysicalDeviceFunction function,
			List<FunctionParameter> params) throws FunctionExecutionException {
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
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "consumption", "Wh"));
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "power", "W"));					
	}

	@Override
	public double getTotalConsumption() {
		DeviceProperty p = this.getProperty("consumption");
		if (p.isValueSet()){
			return (Double)p.getValue();
		}
		return 0;
	}

	@Override
	public double getCurrentConsumption() {
		DeviceProperty p = this.getProperty("power");
		if (p == null){
			
			return 0;
		}
		if (p.isValueSet()){
			return (Double)p.getValue();
		}
		return 0;
	}

}
