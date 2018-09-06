package de.smahoo.homeos.testing.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;


import de.smahoo.homeos.automation.RuleEvent;
import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventListener;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.PhysicalDeviceEvent;
import de.smahoo.homeos.driver.DriverEvent;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.location.LocationEvent;

import java.awt.Font;




public class PanelLogger extends JPanel {
	
	private List<Event> eventList;
	private int bufferSize = 16000;
	private static final int EVENT_TYPE_LENGTH = 20;
	private static final int DETAILS_LENGTH = 60;
	
	public PanelLogger() {
		setName("PanelLogger");
		eventList = new ArrayList<Event>();
		
		HomeOs.getInstance().getEventBus().addListener(new EventListener() {
			
			@Override
			public void onEvent(Event event) {
				evaluateEvent(event);
				
			}
		});
		initGui();
	}
	
	private void evaluateEvent(Event event){		
		eventList.add(event);		
		addLine(event);
		if (eventList.size() >= bufferSize){
			eventList.remove(0);
		}
	}
	
	private String getDetails(Event event){	
		String details = null;
		if (event instanceof PhysicalDeviceEvent){
			PhysicalDeviceEvent phEvent = (PhysicalDeviceEvent)event;
			switch (event.getEventType()){
				case FUNCTION_EXECUTED : if (phEvent.hasFunction()) return phEvent.getDevice().getDeviceId()+"."+phEvent.getDeviceFunction().getName()+"()"; break;
				case PROPERTY_VALUE_CHANGED: if (phEvent.hasProperty()) return phEvent.getProperty().getName()+"="+phEvent.getProperty().getValue(); break;
				case DEVICE_PROPERTY_CHANGED: break;
			}
			if (details == null){
				return "\""+phEvent.getDevice().getName() +"\" ("+ phEvent.getDevice().getDeviceId()+")";
				
			}
		}		
		if (event instanceof DeviceEvent){
			return ((DeviceEvent)event).getDevice().getName()+" ("+ ((DeviceEvent)event).getDevice().getDeviceId()+")";
		}
		
		if (event instanceof LocationEvent){
			LocationEvent locEvent = (LocationEvent)event;
			return "\""+locEvent.getLocation().getName()+"\" ("+locEvent.getLocation().getId();
		}
		if (event instanceof DriverEvent){
			DriverEvent drvEvent = (DriverEvent)event;
			return drvEvent.getDriver().getName()+" "+drvEvent.getDriver().getVersion(); 
		}
		if (event instanceof RuleEvent){
			RuleEvent ruleEvent = (RuleEvent)event;
			return ruleEvent.getRule().getName();
		}
		
		return null;
	}
	
	private void addLine(Event event){
		String strEventType = event.getEventType().name();
		String strDetails = getDetails(event);
		String strDescription = null;
		if (event.hasDescription()){
			strDescription = event.getDescription();
		}
		
		while (strEventType.length() <  EVENT_TYPE_LENGTH) strEventType = " "+strEventType;
		String newLine = "["+ (new SimpleDateFormat("HH:mm:ss")).format(event.getTimeStamp())+"] "+strEventType+" ";
		
		
		
		if (strDetails != null){
			while (strDetails.length() < DETAILS_LENGTH) strDetails = strDetails+" ";
			newLine = newLine +strDetails;
		}
		if (strDescription != null){
			newLine = newLine +" "+strDescription;
		}
		newLine = newLine+"\r\n";
		addLine(newLine);
	}
	
	private void addLine(String line){
		textArea.append(line);
		scrollPane.getVerticalScrollBar().setValue(textArea.getHeight());
	}	
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);		
	}
	
	private JScrollPane scrollPane;
	private JTextArea textArea;
}

