package de.smahoo.homeos.remote.devices;

import org.w3c.dom.NodeList;

import de.smahoo.homeos.devices.Dimmable;
import de.smahoo.homeos.remote.RemoteDevice;

public class RemoteDimmer extends RemoteDevice implements Dimmable{


	public void turnOn(){
		executeFunction("turnOn");
	}
	public void turnOff(){
		executeFunction("turnOff");
	}	
	
	public void dimmUp(){
		executeFunction("dimmUp");
	}
	
	public void dimmDown(){
		executeFunction("dimmDown");
	}
	
	public void setDimmLevel(int dimmLevel){
		executeFunction("dimm");
	}
	
	@Override
	protected void updateProperties(NodeList properties){
		// no properties defined
	}

	
	
}
