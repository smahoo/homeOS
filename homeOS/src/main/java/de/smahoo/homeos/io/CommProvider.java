package de.smahoo.homeos.io;

import java.io.IOException;

import gnu.io.*;

public class CommProvider {
	
	public IOStreams openComm(String portName, int baudrate) throws IOException{

		CommPort commPort;		
		CommPortIdentifier portIdentifier;

		try {
	        portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
	    } catch (NoSuchPortException exc){
	        	exc.printStackTrace();
	        	throw new IOException("CommPort with the name '"+portName+"' is not available! ",exc);
	    }
	    
		if ( portIdentifier.isCurrentlyOwned() ) {	            
	            throw new IOException("CommPort '"+portName+"' is currently in use! ");
	    } else   {
	    	try {
	          commPort = portIdentifier.open(this.getClass().getName(),2000);
	            
	            if ( commPort instanceof SerialPort )  {
	            	
	                SerialPort serialPort = (SerialPort) commPort;
	                serialPort.setSerialPortParams(baudrate,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);                    
	                serialPort.enableReceiveTimeout(500000);   
	                
	                
	                return new SerialIO(serialPort, portName, serialPort.getInputStream(), serialPort.getOutputStream());
	              
	               
	            }  else  {
	            	throw new IOException("CommPort '"+portName+"' is not a SerialPort!");	         
	            }
	    	} catch (Exception exc){
	    		exc.printStackTrace();
	    		throw new IOException("Unable to provide Comm '"+portName+"'! ",exc);
	    	}
	   }
	}

	
	
	
}
