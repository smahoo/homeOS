package de.smahoo.homeos.io;

import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;

public class SerialIO implements IOStreams{
	
	SerialPort serialPort = null;
	InputStream in = null;
	OutputStream out = null;
	String portname = null;
	
	public SerialIO(SerialPort serialPort, String portname, InputStream in, OutputStream out){
		this.serialPort = serialPort;
		this.in = in;
		this.out = out;
		this.portname = portname;
	}
	
	public int getBaudRate(){
		return serialPort.getBaudRate();
	}
	
	public String getPort(){		
		return portname;
	}
	
	public InputStream getInputStream(){
		return in;
	}
	
	public OutputStream getOutputStream(){
		return out;
	}		
	
	public IOTypes getType(){
		return IOTypes.IO_TYPE_SERIAL;
	}
	
}
