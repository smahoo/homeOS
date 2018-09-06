package de.smahoo.homeos.remote.connection;

import java.net.Socket;

import de.smahoo.homeos.common.EventType;


public class RemoteUpdateListener  extends Thread{
	
	private int serverPort = 0;
	private String serverIp = null;
	private boolean keepAlive = true;
	private int reconnectInterval = 10000; // 10 sec
	private RemoteConnectionEventListener listener;
	
	
	public RemoteUpdateListener(RemoteConnectionEventListener listener){
		this.listener = listener;
	}
	
	public void setServerIp(String serverIp){
		this.serverIp = serverIp;
	}
	
	public void setServerPort(int serverPort){
		this.serverPort = serverPort;
	}
	
	public void disconnect(){
		this.keepAlive = false;
	}
	
	public void run(){
		while (keepAlive){
			try {			
				Socket socket;				
				socket = new Socket(serverIp,serverPort);
				listener.onRemoteConnectionEvent(new RemoteConnectionEvent(EventType.CONNECTION_ESTABLISHED, ""+serverIp+":"+serverPort));
				int read;
				int lastRead = 0;
				StringBuffer buffer = new StringBuffer();
				while(true){				
					read = socket.getInputStream().read();
					buffer.append((char)read);				
					if ((((char)read)=='%')&&(read == lastRead)){						
						listener.onRemoteConnectionEvent(new RemoteUpdateEvent(buffer.toString()));
						buffer = new StringBuffer();
					}
					lastRead = read;
				}			
			} catch (Exception exc){
				exc.printStackTrace();
				listener.onRemoteConnectionEvent(new RemoteConnectionEvent(EventType.CONNECTION_LOST, exc.getMessage()));
			}
			
			try {
				Thread.sleep(reconnectInterval);
			} catch (Exception exc){
				
			}
			// Trying to Reconnect....			
		}
		listener.onRemoteConnectionEvent(new RemoteConnectionEvent(EventType.CONNECTION_CLOSED,"UpdateListener with connection to "+serverIp+":"+serverPort+" closed."));
	}

}
