package de.smahoo.homeos.devices;


import de.smahoo.homeos.device.Device;

public interface HeatingRtc extends HeatingRadiator{

	public void setTemperature(double temperature);
	public double getDesiredTemperature();
}
