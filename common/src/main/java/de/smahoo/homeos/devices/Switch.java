package de.smahoo.homeos.devices;

import de.smahoo.homeos.device.Device;

public interface Switch extends Device{

	public int getButtonCount();
	public boolean isButtonPressed(int button);
	
}
