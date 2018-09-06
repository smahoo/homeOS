package de.smahoo.homeos.remote.devices;

import org.w3c.dom.NodeList;

import de.smahoo.homeos.devices.Television;
import de.smahoo.homeos.remote.RemoteDevice;

public class RemoteTV extends RemoteDevice implements Television {
	public void mute(){
		executeFunction("mute");
	}
	public void power(){
		executeFunction("power");
	}
	
	public void channelUp(){
		executeFunction("channelUp");
	}
	
	public void channelDown(){
		executeFunction("channelDown");
	}
	
	public void volumeUp(){
		executeFunction("volumeUp");
	}
	
	public void volumeDown(){
		executeFunction("volumeDown");
		
	}
	
	protected void updateProperties(NodeList properties){
		
	}
}
