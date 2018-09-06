package de.smahoo.homeos.io;

import java.io.IOException;

public class IOManager {
	
	protected CommProvider commProvider = null;
	
	
	public IOManager(){
		init();
	}
	
	protected void init(){
		commProvider = new CommProvider();
	}
	
	
	public IOStreams openComPort(String portName, int baudrate) throws IOException{
		if (commProvider == null){
			// FIXME: IOManager was not initialized
			return null;
		}
		
		return commProvider.openComm(portName, baudrate);		
		
	}
	
	
}
