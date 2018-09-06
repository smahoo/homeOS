package de.smahoo.homeos.kernel.remote;

import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventListener;
import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.kernel.remote.result.DeleteDeviceResultItem;
import de.smahoo.homeos.kernel.remote.result.DeviceRequestResultItem;
import de.smahoo.homeos.kernel.remote.result.cmd.DeleteCmdResultItem;
import de.smahoo.homeos.kernel.remote.result.request.LocationRequestResultItem;
import de.smahoo.homeos.location.LocationEvent;
import de.smahoo.homeos.utils.xml.XmlUtils;

public class CallBackServer extends Thread{

	
	private static final int BUFFERSIZE_CALLBACK_COMMANDS = 9600;
	private boolean keepAlive = true;
	private static EventListener eventListener = null;
	private int port = 2021;
	ServerSocket ssocket = null;
	List<Socket> clientList;
	BlockingQueue<String> callbackList;
	private Sender sender;
	
	
	public CallBackServer(){
		clientList = new ArrayList<Socket>();
		if (eventListener == null){
			eventListener = new EventListener() {			
				@Override
				public void onEvent(Event event) {				
					evaluateEvent(event);
				}
			};
		}
		HomeOs.getInstance().getEventBus().addListener(eventListener);
		callbackList = new ArrayBlockingQueue<String>(BUFFERSIZE_CALLBACK_COMMANDS);
		sender = new Sender();
		sender.start();
	}
	
	
	public void setPort(int port){
		this.port = port;
	}
	
	
	private void send(String text){
		try {
			callbackList.put(text);
		} catch (Exception exc){
			exc.printStackTrace();
		}		
	}
	
	
	private void sendDeviceRemovedMsg(DeviceEvent event){
		Document doc = XmlUtils.createDocument();
	//	Element elem = doc.createElement("delete");
		DeleteCmdResultItem item = new DeleteCmdResultItem();		
		DeleteDeviceResultItem devItem = new DeleteDeviceResultItem();
		devItem.setDeviceId(event.getDevice().getDeviceId());
		devItem.setSuccess(true);
		item.addResultItem(devItem);
		//elem.appendChild(item.generateElement(doc));
		doc.appendChild(item.generateElement(doc));
		String cmd = XmlUtils.xml2String(doc);
		send(cmd);
	}
	
	private void evaluateEvent(Event event){
		if (event instanceof DeviceEvent){
			Document doc = XmlUtils.createDocument();
			switch (event.getEventType()){					
			case DEVICE_ADDED:
				//sendDeviceAddedMsg(device)				
				break;
			case DEVICE_REMOVED:
				 sendDeviceRemovedMsg((DeviceEvent)event);
				 return;				
			}
			if (event.getEventType() == EventType.DEVICE_REMOVED){				
				//DeleteCmdResultItem item = new DeleteCmdResultItem();
				//DeleteDeviceResultItem devItem = new DeleteDeviceResultItem();
				//devItem.setDeviceId(((DeviceEvent)event).getDevice().getDeviceId());
				//item.addResultItem(devItem);
				return;
			}
			if (((DeviceEvent)event).getDevice().isHidden()){
				return;
			}
						
			Element elem;
			if ((event.getEventType() == EventType.DEVICE_ADDED)){
				elem = doc.createElement("new");
			} else {
				elem = doc.createElement("update");
			}
			DeviceRequestResultItem item = new DeviceRequestResultItem(((DeviceEvent)event).getDevice());
			elem.appendChild(item.generateElement(doc));
			doc.appendChild(elem);
			String response = XmlUtils.xml2String(doc);
			send(response);
		}	
		if (event instanceof LocationEvent){
			Document doc = XmlUtils.createDocument();
			Element elem = doc.createElement("update");
			LocationRequestResultItem item = new LocationRequestResultItem(((LocationEvent)event).getLocation());
			elem.appendChild(item.generateElement(doc));
			doc.appendChild(elem);
			String response = XmlUtils.xml2String(doc);
			send(response);
		}
	}
	
	private void addClient(Socket socket){
		if (socket == null) return;
		clientList.add(socket);
	}
	
	public void run(){		
			try {
				ssocket = new ServerSocket(port);			
			} catch (Exception exc){
				
			}
		while (keepAlive){			
			Socket client = null;			
			try {
				client = ssocket.accept();
				addClient(client);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}
	
	private class Sender extends Thread{
		public void run(){
			while(keepAlive){
				try {
					if (callbackList.isEmpty()){
						try {
							Thread.sleep(100);
						} catch (Exception exc){
						
						}
					} else {
						sendCommand(callbackList.poll());
					}
				} catch (Exception exc){
					exc.printStackTrace();
				}
			}
		}
		
		private void sendCommand(String text){
			if (clientList.isEmpty()) return;
			List<Socket> removeList = null;
			for (Socket socket : clientList){
				try {				
					OutputStreamWriter outw = new OutputStreamWriter(socket.getOutputStream());
					outw.write("##"+text+"%%");
					outw.flush();				
				} catch (Exception exc){				
					if (removeList == null){
						removeList = new ArrayList<Socket>();
					};
					removeList.add(socket);
				}
			}
			
			if (removeList != null){
				for (Socket socket : removeList){
					clientList.remove(socket);
				}
			}
		}
		
		
		
	}
}
