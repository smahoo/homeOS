package de.smahoo.homeos.devices;


import de.smahoo.homeos.device.Device;

public interface Dimmable extends Switchable{	
	public void setDimmLevel(int dimmLevel);
}
