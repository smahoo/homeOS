package de.smahoo.homeos.devices;

import java.net.URL;


public interface IpCamera extends Camera{
	
	public boolean isPasswordProtected();
	public URL getUrl();
	
}
