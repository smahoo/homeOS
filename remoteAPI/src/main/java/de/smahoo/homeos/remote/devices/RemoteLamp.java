package de.smahoo.homeos.remote.devices;

import org.w3c.dom.NodeList;

import de.smahoo.homeos.devices.Lamp;
import de.smahoo.homeos.remote.RemoteDevice;

public class RemoteLamp extends RemoteDevice implements Lamp{
	
	
	
	public void turnOn(){
		executeFunction("turnOn");
	}
	public void turnOff(){
		executeFunction("turnOff");
	}
	
	@Override
	protected void updateProperties(NodeList properties){
		// no properties supported
	}
	
}
