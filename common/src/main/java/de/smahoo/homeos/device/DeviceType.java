package de.smahoo.homeos.device;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.devices.*;


public enum DeviceType {
	DIMMER,
	LAMP,
	TELEVISION,
	TELEVISION_EXTENDED,
	VENTILATION,
	HEATING_RTC,		// Room Temperature Control
	HEATING_RADIATOR,
	SENSOR_BINARY,
	SENSOR_HUMIDITY,
	SENSOR_TEMPERATURE,
	SENSOR_CLIMATE,
	SENSOR_WINDOW,
	SENSOR_MOTION,
	SENSOR_CO2,
	SOCKET,	
	SWITCH,
	SWITCHABLE,
	MISC,
	IP_CAMERA,
	METER_ENERGY,
	METER_ELECTRICITY,
	RGB_CONTROLLER
	;
		
	static public List<DeviceType> getDeviceTypes(Device device){
		List<DeviceType> list = new ArrayList<DeviceType>();
		if (device instanceof Lamp) list.add(LAMP);
		if ((device instanceof Television)&&(!(device instanceof ExtendedTelevision))) list.add(TELEVISION);
		if (device instanceof ExtendedTelevision) list.add(TELEVISION_EXTENDED);
		if (device instanceof SensorCO2) list.add(DeviceType.SENSOR_CO2);
		if ((device instanceof Switchable)&&(!(device instanceof Socket))) list.add(SWITCHABLE);
		if ((device instanceof Socket)) list.add(SOCKET);
		if (device instanceof SensorClimate) list.add(SENSOR_CLIMATE);
		if ((device instanceof SensorHumidity)&&(!(device instanceof SensorClimate))) list.add(SENSOR_HUMIDITY);
		if ((device instanceof SensorTemperature)&&(!(device instanceof SensorClimate))) list.add(SENSOR_TEMPERATURE);
		if (device instanceof Ventilator) list.add(VENTILATION);
		if (device instanceof HeatingRtc) list.add(HEATING_RTC);
		if ((device instanceof HeatingRadiator)&&(!(device instanceof HeatingRtc))) list.add(HEATING_RADIATOR);	
		if (device instanceof Dimmable) list.add(DIMMER);
		if (device instanceof SensorWindow) list.add(SENSOR_WINDOW);
		if (device instanceof Switch) list.add(SWITCH);
		if (device instanceof SensorBinary) list.add(DeviceType.SENSOR_BINARY);
		if (device instanceof SensorMotion) list.add(SENSOR_MOTION);
		if (device instanceof IpCamera) list.add(IP_CAMERA);
		if ((device instanceof MeterEnergy)&&(!(device instanceof MeterElectricity))) list.add(METER_ENERGY);
		if (device instanceof MeterElectricity) list.add(METER_ELECTRICITY);
		if (device instanceof RGBController) list.add(RGB_CONTROLLER);
		
		return list;
	}
	
	static public Class<?> getDeviceInterface(DeviceType deviceType){
		switch (deviceType){
		case LAMP: return Lamp.class;
		case TELEVISION_EXTENDED: return ExtendedTelevision.class;
		case TELEVISION: return Television.class;
		case VENTILATION : return Ventilator.class;
		case HEATING_RTC: return HeatingRtc.class;
		case HEATING_RADIATOR: return HeatingRadiator.class;
		case SENSOR_BINARY: return SensorBinary.class;
		case SENSOR_CO2: return SensorCO2.class;
		case SENSOR_HUMIDITY: return SensorHumidity.class; 
		case SENSOR_TEMPERATURE: return SensorTemperature.class;
		case SENSOR_CLIMATE: return SensorClimate.class;
		case SENSOR_WINDOW: return SensorWindow.class;
		case SOCKET:return Socket.class;		
		case SWITCH : return Switch.class;
		case SWITCHABLE : return Switchable.class;
		case DIMMER : return Dimmable.class;
		case SENSOR_MOTION: return SensorMotion.class;
		case IP_CAMERA: return IpCamera.class;
		case METER_ENERGY: return MeterEnergy.class;
		case METER_ELECTRICITY: return MeterElectricity.class;
		case RGB_CONTROLLER: return RGBController.class;
		default:
			break;
		
		}
		return null;
	}
	
}
