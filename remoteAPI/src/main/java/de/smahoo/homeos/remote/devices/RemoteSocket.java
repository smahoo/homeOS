package de.smahoo.homeos.remote.devices;

import org.w3c.dom.NodeList;

import de.smahoo.homeos.devices.Socket;
import de.smahoo.homeos.remote.RemoteDevice;

public class RemoteSocket extends RemoteDevice implements Socket{

	public void turnOn(){
		executeFunction("turnOn");
	}
	public void turnOff(){
		executeFunction("turnOff");
	}
	
	@Override
	protected void updateProperties(NodeList properties){
		
	}
	
}
