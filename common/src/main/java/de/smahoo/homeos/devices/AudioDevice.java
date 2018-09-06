package de.smahoo.homeos.devices;

import de.smahoo.homeos.device.Device;

public interface AudioDevice extends Device{

	public void volumeUp();
	public void volumeDown();
	public void mute();
	
}
