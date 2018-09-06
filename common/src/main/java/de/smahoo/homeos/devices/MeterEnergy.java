package de.smahoo.homeos.devices;

import de.smahoo.homeos.device.Device;

public interface MeterEnergy extends Device{
	public double getTotalConsumption();
	public double getCurrentConsumption();
}
