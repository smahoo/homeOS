package de.smahoo.homeos.driver.zwave.aeonlabs;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorClimate;
import de.smahoo.homeos.devices.SensorMotion;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.driver.zwave.ZWaveSleepListener;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveCommandParameterType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.report.JWaveReportConfiguration;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportSensorMultilevel;
import de.smahoo.jwave.node.JWaveNode;

import java.util.Date;
import java.util.List;

/**
 * Created by Matze on 13.11.16.
 */
public class AL_Multisensor_6 extends ZWaveDevice implements SensorClimate, SensorMotion {

    protected Date lastValueUpdate = null;
    protected Date lastReInit = null;
    protected Date lastValuesRequest = null;

    protected boolean luminanceEnabled = false;
    protected boolean humidityEnabled = false;
    protected boolean temperatureEnabled = false;
    protected boolean motionEnabled = false;
    protected int periodicUpdatesInterval = -1; // not set


    public AL_Multisensor_6(String id, JWaveNode node){
        super(id,node);
        super.addSleepListener(new ZWaveSleepListener() {

            @Override
            public void onWakeUpIntervalSet(long seconds) {
                //
            }

            @Override
            public void onWakeUp() {
                checkDeviceFunctionality();
            }

            @Override
            public void onSleep() {
                //
            }
        });
    }

    protected void initDevice(){

        associateNode(1,this.getPrimaryControllerId());
        // FIXME: request associations
        // FIXME: request association capabilities
        requestValues();
        setMotionSensorEnabled(true);
        getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(101), JWaveCommandParameterType.BIT_32,224)); // report humidity, temperature and luminance
        getNode().sendData(cmdFactory.generateCmd_Configuration_Get((byte)101));
        //getNode().sendData(cmdFactory.generateCmd_Configuration_Get((byte)111));

    }

    protected void setMotionSensorEnabled(boolean enabled){
        if (enabled){
            getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(4),JWaveCommandParameterType.BYTE,1)); // enable Motion Sensor
            getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(5),JWaveCommandParameterType.BYTE,2)); // use SENSOR_BINARY_REPORT instead of BASIC_SET

        } else {
            getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(4),JWaveCommandParameterType.BYTE,0)); // disable Motion Sensor
        }

        getNode().sendData(cmdFactory.generateCmd_Configuration_Get((byte)4));
        getNode().sendData(cmdFactory.generateCmd_Configuration_Get((byte)5));
    }

    protected void requestValues(){
        requestTemperature();
        requestHumidity();
        requestLuminance();
        lastValuesRequest = new Date();
    }

    protected void reInitDevice(){
        associateNode(1,this.getPrimaryControllerId());
        setMotionSensorEnabled(true);
        getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(101),JWaveCommandParameterType.BIT_32,224)); // report humidity, temperature and luminance
        getNode().sendData(cmdFactory.generateCmd_Configuration_Get((byte)101));
        lastReInit = new Date();
        // FIXME: dispatch DeviceEvent - DEVICE_REINITIALIZATION
    }

    protected void requestLuminance(){
        getNode().sendData(cmdFactory.generateCmd_SensorMultilevel_Get_V5(3));
    }

    protected void requestTemperature(){
        getNode().sendData(cmdFactory.generateCmd_SensorMultilevel_Get_V5(1));
    }

    protected void requestHumidity(){
        getNode().sendData(cmdFactory.generateCmd_SensorMultilevel_Get_V5(5));
    }

    public double getTemperature(){
        DeviceProperty property = this.getProperty("temperature");
        if (property.isValueSet()){
            Double pVal = (Double)property.getValue();
            return pVal;
        } else {
            return 0.0;
        }
    }

    public double getHumidity(){
        DeviceProperty property = this.getProperty("humidity");
        if (property.isValueSet()){
            Double pVal = (Double)property.getValue();
            return pVal;
        } else {
            return 0.0;
        }
    }

    public boolean isMotion(){
        DeviceProperty property = this.getProperty("motion");
        if (property.isValueSet()){
            boolean pVal = (Boolean)property.getValue();
            return pVal;
        } else {
            return false;
        }
    }

    protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd){
        try {
            switch (cmd.getCommandClassKey()){
                case 0x31: // COMMAND_CLASS_SENSOR_MULTILEVEL
                    setValue(JWaveReportFactory.generateSensorMultilevelReport(cmd));
                    break;
                case 0x30: // COMMAND_CLASS_SENSOR_BINARY
                    evaluateSensorBinary(cmd);
                    break;
                case 0x70:
                    if (cmd.getCommandKey() == 0x06){
                        evaluateConfigurationReport(JWaveReportFactory.generateConfigurationReport(cmd));
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception exc){
            exc.printStackTrace();
            // FIXME
        }
    }

    protected void evaluateConfigurationReport(JWaveReportConfiguration confReport){
        switch(confReport.getParamId()){
            case 4: // enable / disable motion sensor
                break;
            case 5: // what to send if motion is detected Basic-Set or Report
                break;
            case 101: // what to report
                break;
            case 111:  // periodic report interval
                break;
        }
    }

    protected void evaluateSensorBinary(JWaveNodeCommand cmd){
        byte[] value = cmd.getParamValue(0);

        if (value.length == 1){
            setMotion(value[0]==(byte)0xFF);
        }
    }

    protected void setMotion(boolean motion){
        DeviceProperty property = this.getProperty("motion");
        if (property.isValueSet()){
            Boolean pVal = (Boolean)property.getValue();
            if (pVal != motion){
                property.setValue(motion);
                dispatchMotionEvent(motion);
            }
        } else {
            property.setValue(motion);
            dispatchMotionEvent(motion);
        }
    }

    protected void dispatchMotionEvent(boolean motion){
        if (motion){
            dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_ON, this));
        } else {
            dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_OFF, this));
        }
    }

    protected void setValue(JWaveReportSensorMultilevel report){

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
            case 5:
                if (report.getPrecission() > 0){
                    setHumidity((double)report.getValue()/(double)report.getPrecission());
                } else {
                    setHumidity(report.getValue());
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
        // sensor has bug. sometimes it sends a value of more than 6000 °C
        // ignore the temperature whenever such a high temperature will be received
        if (temperature > 1000){
            System.out.println("BUG | Multisensor "+getDeviceId()+" ignoring received temperature of "+temperature+"°C");
            return;
        }
        DeviceProperty property = this.getProperty("temperature");
        if (property.isValueSet()){
            Double pVal = (Double)property.getValue();
            if (pVal != temperature){
                property.setValue(temperature);
            }
        } else {
            property.setValue(temperature);
        }
        this.lastValueUpdate = new Date();
    }

    protected void setHumidity(double humidity){

        DeviceProperty property = this.getProperty("humidity");
        if (property.isValueSet()){
            Double pVal = (Double)property.getValue();
            if (pVal != humidity){
                property.setValue(humidity);
            }
        } else {
            property.setValue(humidity);
        }
        this.lastValueUpdate = new Date();
    }

    /**
     * We realized that the aeonlabs multisensor forgets its configuration in the field. Thus,
     * a functionality check is needed to validate whether the sensor works correctly or not
     */
    protected void checkDeviceFunctionality(){
        Date now = new Date();
        if (lastValueUpdate != null){
            // if no value was sent since more than one our
            if ((now.getTime() - lastValueUpdate.getTime())>1000*60*5){
                if (lastReInit != null){
                    // if it was reinitialized up to 10 minutes before, do nothing, else request the values
                    if ((now.getTime() - lastReInit.getTime())>1000*60*20){
                        // nothing happens since more then 10 minutes after reInitialization, so request the values manually
                        // this will be done at each wakeup from now on.
                        requestValues();
                        // try to reInit device after 2h
                        if ((now.getTime() - lastReInit.getTime())>1000*60*60*2){
                            reInitDevice();
                        }
                    }
                } else {
                    reInitDevice();
                }
            }
        } else {
            // maybe system was restarted, to get the current values, request them
            requestValues();
        }
    }

    protected void onPropertyChanged(final DeviceProperty property){
        // FIXME: not needed, properties can't be changed from outside
    }

    protected void generateDeviceFunctions(){
        // FIXME
    }

    protected void generateProperties(){
        addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "temperature", "°C"));
        addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "humidity", "%"));
        addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "luminance", "lx"));
        addProperty(new DeviceProperty(PropertyType.PT_BOOLEAN, "motion", ""));
    }

    protected void executeDeviceFunction(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException {
        //
    }
}
