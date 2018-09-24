package de.smahoo.homeos.driver.zwave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

import de.smahoo.jwave.specification.JWaveSpecification;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.driver.DriverEvent;
import de.smahoo.homeos.driver.DriverMode;
import de.smahoo.homeos.driver.zwave.remote.ZWaveRemoteCmdConfigResultItem;
import de.smahoo.homeos.driver.zwave.remote.ZWaveRemoteCmdResultItem;
import de.smahoo.homeos.driver.zwave.remote.ZWaveRemoteCmdSetResultItem;
import de.smahoo.homeos.io.IOStreams;
import de.smahoo.homeos.io.SerialIO;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.kernel.remote.SetPropertyResultItem;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;
import de.smahoo.homeos.location.Location;
import de.smahoo.jwave.JWaveController;
import de.smahoo.jwave.cmd.JWaveCommandClassSpecification;
import de.smahoo.jwave.cmd.JWaveNodeCommandFactory;
import de.smahoo.jwave.event.JWaveEvent;
import de.smahoo.jwave.event.JWaveEventListener;
import de.smahoo.jwave.event.JWaveNodeEvent;
import de.smahoo.jwave.node.JWaveNode;
import de.smahoo.jwave.utils.xml.XmlConvertionException;
import de.smahoo.jwave.utils.xml.XmlUtils;

 
/*
 * ======================================================
 *		Change History 
 * ======================================================
 * 
 *  0.2.21 - fixed Generic Switch Binary, property on is not valid for current MySQL version
 * 
 *  0.1.20	- added Generic Meter
 *  
 *  0.1.19	- added Fibaro Motions Sensor
 *  
 *  0.1.18  - added availability check
 * 
 *  0.1.17	- fixed bug of AL_DSB05_ZWEU (MultiSensor) when sending a temperature of 6k°C
 *  		- added generic abstracted functions for association and configuration
 *  
 *  0.1.16	- added periodical battery request (24h)
 * 
 *  0.1.15  - increased timeout -> important for aeonlabs 
 * 
 *  0.1.14  - added remote adding device with params
 * 
 *  0.1.12  - fixed bug: manufactureId, etc... not set after config load.
 *  		- config generation also generates property-elements
 * 
 *  0.1.11  - fixed bug: loading devices from config missed to assign location
 * 
 *  0.1.10	- initialization via XML
 *  			- loading Devices
 *  			- setting up zwave lib with configuration data
 *  			
 * 
 *	0.1.9   - added Configuration Xml generation 
 *
 *	0.1.8	- fighting with the stupid everspring sensor
 *
 *	0.1.7
 *
 *	0.1.6	- added Fibaro Smoke Detector *
 *			- added Fibaro WallPlug *
 *			- added Fibaro Window Contact *
 *			- added Fibaro RGB Controller *
 *			- added Danfoss Living Connect version 2 *
 *			- added Vision Security Siren ZM1602 (not working cause of sucking device) *
 *
 *	0.1.5	- added Eversrping ST814 *
 *			- added AeonTec Multisensor *
 *			- added Reports and Report Factory *			
 *			- added ZWaveGenericSensorMultilevel *
 *
 *	0.1.4	- added Danfoss LivingConnect *
 *
 *	0.1.2	- added ZWaveGenericSwitchBinary *
 *			- added ZWaveGenericSwitchMultilevel *
 *
 *		*) not finished
 *
 */



public class ZWaveDriver extends Driver{

	private final static String VERSION 	     = "0.1.20";
	private final static String COMPANY_NAME     = "smahoo Solutions GmbH & Co. KG";
	private final static String NAME 		     = "Z-Wave Driver";
	private final static String FILE_CMD_CLASSES = "cmd_classes.xml";
	private IOStreams ioStreams = null;
	
	private JWaveController cntrl = null;
	private ZWaveDeviceFactory devFactory = null;
	private JWaveCommandClassSpecification defs = null;
	private JWaveNodeCommandFactory nodeCmdFactory = null;
	private java.util.List<ZWaveDevice> deviceList = null;
	private Element initElement = null;
	private Date driverInitTimestamp = null;
	private Timer availabilityCheckTimer = null;

	
	public boolean init(Element elem){
		driverInitTimestamp = new Date();
		initElement = elem;
		JWaveController.doLogging(true);
		deviceList = new ArrayList<ZWaveDevice>();
		
		if (elem == null){
			this.dispatchDriverEvent(new DriverEvent(EventType.DRIVER_PROBLEM,this,"no valid configuration"));
			return false;
		}
		try {
			defs = loadCommandClasses();
			if (defs == null){
				this.dispatchDriverEvent(new DriverEvent(EventType.DRIVER_PROBLEM,this,"Unable to load command classes due to driver initialization."));
				return false;
			}
			nodeCmdFactory = new JWaveNodeCommandFactory(defs);
			cntrl = new JWaveController(defs);
			cntrl.addCntrlListener(new JWaveEventListener() {
				

				public void onJWaveEvent(JWaveEvent event) {
					evaluateEvent(event);
					
				}
			});
			
			setConnection(elem.getElementsByTagName("connection"));
			devFactory = new ZWaveDeviceFactory(nodeCmdFactory);
			cntrl.init(ioStreams.getInputStream(),ioStreams.getOutputStream());
			cntrl.setSecurityEnabled(false);
			availabilityCheckTimer = new Timer();
			availabilityCheckTimer.schedule(new CheckAvailabilityTask(), 300000, 300000);
			
		} catch (Exception exc){
			exc.printStackTrace();
			this.dispatchDriverEvent(new DriverEvent(EventType.DRIVER_PROBLEM,this,"Unable to initialise driver for z-wave technology. "+exc.getMessage()));
			return false;
		}
		
		return true;
		
	}
	
	public JWaveController getJWaveController(){
		return cntrl;
	}
	
	protected void initZWaveLib(NodeList nodeList){
		
		Element tmp;
		for (int i = 0; i< nodeList.getLength(); i++){
			if( nodeList.item(i) instanceof Element){
				tmp = (Element)nodeList.item(i);
				if ("zwave-configuration".equalsIgnoreCase(tmp.getTagName())){
					try {
						
						cntrl.setConfiguration(tmp);	
					} catch (Exception exc){
						//FIXME: 
						exc.printStackTrace();
					}
				}
			}
			
		}		
		
	}
	
	protected void initDevices(NodeList nodeList){
		Element tmp;
		for (int i = 0; i< nodeList.getLength(); i++){
			if( nodeList.item(i) instanceof Element){
				tmp = (Element)nodeList.item(i);
				if ("device".equalsIgnoreCase(tmp.getTagName())){
					initDevice(tmp);
				}
			}
		}
	}
	
	protected void initDevice(Element elem){
		int nodeId = Integer.parseInt(elem.getAttribute("nodeId"));
		JWaveNode node = cntrl.getNode(nodeId);
		String className = elem.getAttribute("class");
		String id = elem.getAttribute("deviceId");
		ZWaveDevice dev = devFactory.generateZWaveDevice(id,node, className);
		dev.setName(elem.getAttribute("name"));
		if (elem.hasAttribute("location")){
			Location loc = HomeOs.getInstance().getLocationManager().getLocation(elem.getAttribute("location"));
			loc.assignDevice(dev);
		}
		dev.cmdFactory = this.nodeCmdFactory;
		deviceList.add(dev);
		dev.addProperties();
		dev.addFunctions();
	
		this.getDeviceManager().addDevice(dev,this);	
		if (elem.hasChildNodes()){
			setDefaultProperties(dev, elem.getChildNodes());
		}		
	}
	
	protected void setDefaultProperties(ZWaveDevice dev, NodeList nodelist){
		Element tmp;
		for (int i = 0; i < nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				tmp = (Element)nodelist.item(i);
				if ("property".equalsIgnoreCase(tmp.getTagName())){
					DeviceProperty prop = dev.getProperty(tmp.getAttribute("name"));
					prop.setValue(tmp.getAttribute("value"));
				}
			}
		}
	}
	
	protected JWaveCommandClassSpecification getDefinitions(){
		return defs;
	}
	
	protected JWaveNodeCommandFactory getNodeCmdFactory(){
		return nodeCmdFactory;
	}
	
	private void setConnection(NodeList nodeList){
		Element tmp;
		for (int i = 0; i<nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				tmp = (Element)nodeList.item(i);
				try {					
					ioStreams = connect(tmp);
				} catch (Exception exc){
					exc.printStackTrace();
				}
			}
		}
	}
	
	private Element getConnectionElement(Document doc){
		Element elem = doc.createElement("connection");
		if (ioStreams instanceof SerialIO){			
			SerialIO sIO = (SerialIO)ioStreams;
			elem.setAttribute("type",sIO.getType().name());			
			elem.setAttribute("portname",sIO.getPort());
			elem.setAttribute("baudrate",""+sIO.getBaudRate());
		}
		return elem;
	}

	
	/**
	 * 
	 */
	@Override
	public Element toXmlElement(final Document doc){
		// FIXME driver element should be generated in abstract driver class. each driver only generates the things
		// within driver element
		Element elem = doc.createElement("driver");
		
		elem.setAttribute("class",this.getClass().getName());
		elem.setAttribute("name", getName());
		elem.setAttribute("version",getVersion());
		
		elem.appendChild(getConnectionElement(doc));
		
		
		elem.appendChild(cntrl.getConfiguration(doc));
		
		Element device;
		
		
		
		for (ZWaveDevice dev : deviceList){
			try {
				// sometimes config generation failes. we assume it has to do with invalid config file at startup
				// for further details see BUG AIR-189		
				device = doc.createElement("device");
				if (dev.getNode() != null){
					device.setAttribute("nodeId",""+dev.getNode().getNodeId());
				}
				device.setAttribute("class", dev.getClass().getName());
				device.setAttribute("name",dev.getName());
				if (dev.getLocation() != null){
					device.setAttribute("location", dev.getLocation().getId());
				}
				device.setAttribute("deviceId", dev.getDeviceId());		
				addProperties(device,dev,doc);
				elem.appendChild(device);
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}
	
		
				
		return elem;
	}
	
	
	protected void addProperties(Element elem, final ZWaveDevice device, final Document doc){
		// FIXME store only properties that are marked as persistent. It makes no sense to save all values (like temperature, etc)
		Element tmp;
		for (DeviceProperty prop : device.getPropertyList()){
			if (prop.isValueSet()){
				tmp = doc.createElement("property");
				tmp.setAttribute("name",prop.getName());
				tmp.setAttribute("value", ""+prop.getValue());
				elem.appendChild(tmp);
			}
		}
	}
	
	private IOStreams connect(Element elem) throws IOException{
		IOStreams res = null;
		
		if (elem.hasAttribute("type")){
			if (elem.getAttribute("type").equalsIgnoreCase("IO_TYPE_SERIAL")){
				if (!elem.hasAttribute("portname")) {
					throw new IOException("driver configuration does not contain attribute portname for connection type IO_TYPE_SERIAL");
				}
				if (!elem.hasAttribute("baudrate")){
					throw new IOException("driver configuration does not contain attribute baudrate for connection type IO_TYPE_SERIAL");					
				}
				String portName = elem.getAttribute("portname");
				int baud;
				try {
					baud = Integer.parseInt(elem.getAttribute("baudrate"));
				} catch (Exception exc){
					throw new IOException("'"+elem.getAttribute("baudrate")+"' is not a valid baudrate!");
				}
				
				res = HomeOs.getInstance().getIoManager().openComPort(portName, baud);
				
			}
		} else {
			throw new IOException("driver configuration does not contain connection type. Unable to initialize the connection.");
		}
		
		
		return res;
	}
	
	protected JWaveCommandClassSpecification loadCommandClasses() throws IOException,XmlConvertionException, SAXException, ParserConfigurationException{


		return JWaveSpecification.loadDefaultSpecification();
/*		Document doc = null;

		String str = null;


		try {
			str = new String(jarResource.getResource(FILE_CMD_CLASSES));
		} catch (Exception exc){
			throw new IOException("Unable to load file '"+FILE_CMD_CLASSES+"' from '"+this.filename+"'.",exc);
		}		
		
		doc = XmlUtils.parseDoc(str);
		
		JWaveCommandClassSpecification defs = new JWaveCommandClassSpecification(doc);
		return defs;
		*/
	}
	
	public String getName(){
		return NAME;
	}
	
	
	public String getVersion(){
		return VERSION;
	}
	
	
	public String getCompanyName(){
		return COMPANY_NAME;
	}
	
	
	
	
	@Override
	public void startLearnMode(){
		if (cntrl == null){
			// FIXME: handle that
			return;
		}
		
		cntrl.setInclusionMode(true,false);
	}
	
	@Override
	public void cancelLearnMode(){
		if (cntrl == null){
			// FIXME: handle that
			return;
		}
		
		cntrl.setNormalMode();
	}
	
	@Override
	public void startRemoveMode(){
		if (cntrl == null){
			// FIXME: handle that
			return;
		}
		
		cntrl.setExlusionMode();
	}
	
	@Override
	public void cancelRemoveMode(){
		if (cntrl == null){
			// FIXME: handle that
			return;
		}
		
		cntrl.setNormalMode();
	}
	
	protected synchronized void evaluateEvent(JWaveEvent event){
		switch(event.getEventType()){
			case CNTRL_EVENT_INIT_COMPLETED:
				if (initElement != null){
					initZWaveLib(initElement.getElementsByTagName("zwave-configuration"));
					initDevices(initElement.getElementsByTagName("device"));
				}
				dispatchDriverEvent(new DriverEvent(EventType.CONFIGURATION_FINISHED, this));
				break;
			case NODE_EVENT_NODE_ADDED:
				System.out.println("Node added");
				break;
						
			
			case NODE_EVENT_INTERVIEW_STARTED:
				System.out.println("Interview started.");
				break;
			case NODE_EVENT_INTERVIEW_ERROR:
				System.out.println("Interview Error");			
				break;
			case NODE_EVENT_INTERVIEW_FINISHED:
					JWaveNode node = null;
					if (event instanceof JWaveNodeEvent){
						JWaveNodeEvent nev = (JWaveNodeEvent)event;
						node = nev.getNode();
					
						if (node != null){
							try {
								ZWaveDevice device = devFactory.generateZWaveDevice(node);	
								if (device != null){
									deviceList.add(device);
									
									device.init();
									if(node.supportsClassWakeUp()){										
										node.setNodeToSleep();
									}
									driverMode = DriverMode.DRIVER_MODE_NORMAL;
									this.getDeviceManager().addDevice(device,this);	
								}
							} catch (Exception exc){
								exc.printStackTrace();
							}
						}
					}
				
					
				break;
			case NODE_EVENT_NODE_REMOVED:
				
				break;
			default:		
					break;
		}
	}
	
	@Override
	public RemoteResultItem processCmd(Element elem){
		
		 ZWaveRemoteCmdResultItem item = new ZWaveRemoteCmdResultItem(this);
		item.setDriver(this);
		
		if (elem.hasChildNodes()){
			NodeList nodeList = elem.getChildNodes();
			Element tmp = null;
			for (int i = 0; i< nodeList.getLength(); i++){
				if (nodeList.item(i) instanceof Element){
					tmp = (Element)nodeList.item(i);
					if ("set".equalsIgnoreCase(tmp.getTagName())){
						item.addResultItem(processSetCmd(tmp));
					}
					if ("config".equalsIgnoreCase(tmp.getTagName())){
						item.addResultItem(processConfigCmd(tmp));
					}
					if ("reinit".equalsIgnoreCase(tmp.getTagName())){
						// FIXME: add call for init device procedure
					}
				}
			}
		} else {
			item.setSuccess(false);
			item.setMessage("Nothing to do! ");
			return item;
		}
		
		return item;
	}
	
	protected ZWaveRemoteCmdConfigResultItem processConfigCmd(Element elem){
		ZWaveRemoteCmdConfigResultItem rItem = new ZWaveRemoteCmdConfigResultItem();
		
		
		
		
		return rItem;
	}
	
	protected ZWaveRemoteCmdSetResultItem processSetCmd(Element elem){
		ZWaveRemoteCmdSetResultItem item = new ZWaveRemoteCmdSetResultItem();
		
		NodeList nodeList = elem.getChildNodes();
		Element tmp;
		for (int i = 0; i<nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				tmp = (Element)nodeList.item(i);
				if (tmp.getTagName().equalsIgnoreCase("property")){
					
					item.addChangeCmdResultItem(setProperty(tmp));
				}
			}
		}
		
		return item;
	}
	
	protected SetPropertyResultItem setProperty(Element elem){
		String name = elem.getAttribute("name");
		String value = elem.getAttribute("value");
		
		if ("CONTROLLER_MODE".equalsIgnoreCase(name)){
			if ("CONTROLLER_MODE_RESET".equalsIgnoreCase(value)){
				cntrl.resetController();
				return new SetPropertyResultItem(name, value, true, "Z-Wave Controller will reset.");
			}
			if ("CONTROLLER_MODE_ADD_DEVICE".equalsIgnoreCase(value)){
				
				boolean reqManufacturer = true;
				boolean reqVersions 	= true;				
				Element tmp;
				NodeList nodeList = elem.getChildNodes();
				if (elem.hasChildNodes()){
					for (int i=0; i<nodeList.getLength(); i++){
						if (nodeList.item(i) instanceof Element){
							tmp = (Element)nodeList.item(i);
							if ("param".equalsIgnoreCase(tmp.getTagName())){
								if (!tmp.hasAttribute("name") || !tmp.hasAttribute("value")){
									return new SetPropertyResultItem(name, value, false,"param has no attributes 'name' & 'value'!");
								}								
								if ("requestManufactureDetails".equalsIgnoreCase(tmp.getAttribute("name"))){
									reqManufacturer = Boolean.parseBoolean(tmp.getAttribute("value"));
								}
								if ("requestVersionDetails".equalsIgnoreCase(tmp.getAttribute("name"))){
									reqVersions = Boolean.parseBoolean(tmp.getAttribute("value"));
								}
							}
						}
						
					}
				}
				cntrl.setInclusionMode(reqManufacturer, reqVersions);				
				return new SetPropertyResultItem(name, value, true, "Z-Wave Controller was set to add mode ("+reqManufacturer+","+reqVersions+")");
			}
			if ("CONTROLLER_MODE_REMOVE_DEVICE".equalsIgnoreCase(value)){
				cntrl.setExlusionMode();
				return new SetPropertyResultItem(name, value, true, "Z-Wave Controller was set to remove mode.");
			}
			if ("CONTROLLER_MODE_NORMAL".equalsIgnoreCase(value)){
				cntrl.setNormalMode();
				return new SetPropertyResultItem(name, value, true, "Z-Wave Controller was set to normal mode.");
			}
			return new SetPropertyResultItem(name, value, false,"Unvalid value ("+value+") for property "+name+"!");
		}
		
		return new SetPropertyResultItem(name, value, false,"Unkown property "+name+" to set!");
	}
	
	protected void checkAvailability(){
		long wakeupInterval;
		Date now = new Date();
		for (ZWaveDevice dev : deviceList){
			wakeupInterval = dev.getWakeUpInterval();
			checkAvailabilityForWakeupDevices(dev,2*wakeupInterval, now.getTime());
		}
	}
	
	
	 protected void checkAvailabilityForWakeupDevices(ZWaveDevice device, long maxMillis, long currentMillis){
	    	if (!device.getNode().supportsClassWakeUp()){
	    		return;
	    	}
	    	if (device.getLastActivityTimeStamp() != null){
	    		if (currentMillis - device.getLastActivityTimeStamp().getTime() > maxMillis){
	    			device.setAvailability(false);
	    		}
	    	} else {
	    		if (currentMillis - driverInitTimestamp.getTime() > maxMillis){
	    			device.setAvailability(false);
	    		}
	    	}
	    }
	
	// --------------------------------------  Sub Classes -------------------------------------------------------------------------
	
    class CheckAvailabilityTask extends TimerTask {
        public void run() {
          checkAvailability();            
        }
    }

}
