package de.smahoo.homeos.driver.cul;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.smahoo.homeos.io.IOStreams;
import de.smahoo.homeos.kernel.HomeOs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.devices.Sensor;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.driver.DriverEvent;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;
import de.smahoo.homeos.property.Property;

import de.smahoo.cul.Controller;
import de.smahoo.cul.ControllerEvent;
import de.smahoo.cul.ControllerEventListener;
import de.smahoo.cul.Device;
import de.smahoo.cul.DeviceEvent;
import de.smahoo.cul.DeviceEventListener;
import de.smahoo.cul.Em1000;
import de.smahoo.cul.FS20S;
import de.smahoo.cul.Fht80;
import de.smahoo.cul.Fht80b;
import de.smahoo.cul.Fhttk;
import de.smahoo.cul.Fs20s_md;
import de.smahoo.cul.Hms100tf;
import de.smahoo.cul.S300th;

/*
 * ===================================================================
 * 							CHANGE HISTORY
 * ===================================================================
 * 
 * 0.2.11   fixed bug: when writing config, location name instead of location id was saved
 * 			fixed bug: when writing config, no class attribute was set
 * 
 * 0.2.10	fixed bug, when there was an exception in SerialReader, the complete ReaderThread stopped. Will now continue after failure.
 * 
 * 0.2.8	added DRIVER_RUNTIME_ERROR to EventType
 * 				COC Extension for Raspberry might freeze after a while. When CulController doesn't receive any message for more than 1 hour,
 * 				the driver sends an DRIVER_RUNTIME_ERROR event. The HomeOs Kernel will react 
 * 
 * 0.2.6    activity timestamp will be set with new received message
 *          message cache is disabled to prevent memory leak
 *
 * 0.2.5    fixed bug with HMS100TF update
 * 
 * 0.2.4	auto add of new devices can be set due to attribute in config element -> autoAddNewDevice
 * 			fixed bug for hms 100 tf sensors -> no updates
 * 
 * 0.2.3	added Device CulFs20sMd	(Motion Detector)
 * 			added Device CulEm1000	(Meter Electricity)	
 * 
 */


public final class CulDriver extends Driver{

	
	private static final String DRIVERNAME  = "CUL";
	private static final String VERSION     = "0.2.11";
	private static final String COMPANYNAME = "smahoo";
	
	private HashMap<String, CulDevice> deviceList;
	
	protected Controller culController = null;
	private boolean autoAddNewDevice = false;
	
	private Timer availableTimer;
	
	private static final long MAX_SENSOR_OFFLINE_MILLIS = 60*60*1000; // 1h
	private Date startTime = null;

	private IOStreams ioStreams = null;
	
	public CulDriver(){
		super();		
		startTime = new Date();
		deviceList = new HashMap<String, CulDevice>();
		culController = new Controller();
		culController.addControllerEventListener(new ControllerEventListener() {
			
			
			public void onControllerEvent(ControllerEvent evnt) {
				evaluateControllerEvent(evnt);
				
			}
		});
		culController.addDeviceEventListener(new DeviceEventListener() {			
			
			public void onDeviceEvent(DeviceEvent evt) {
				evaluateDeviceEvent(evt);				
			}
		});		
		availableTimer = new Timer();
		availableTimer.schedule(new CheckAvailabilityTask(), 900000, 900000);
	}	
	
	@Override
	public String getVersion(){
		return VERSION;
	}	
	
	@Override
	public String getName(){
		return DRIVERNAME;
	}

	@Override
	public String getCompanyName(){
		return COMPANYNAME;
	}
	
	@Override
	public String toString(){
		return DRIVERNAME+" "+VERSION;
	}
	
	@Override
	public boolean init(Element elem){
		
		dispatchDriverEvent(new DriverEvent(EventType.DRIVER_INITIALIZING,this));
		reset();
		if (elem == null){
			return init();			
		}
		System.out.println("Creating Connection");
		setConnection(elem.getElementsByTagName("connection"));
		System.out.println("OK - Connection created");
		if (ioStreams != null) {
			try {
				System.out.println("initializing CUL Controller)");
				culController.init(ioStreams.getInputStream(), ioStreams.getOutputStream());

			} catch (Exception exc){
				exc.printStackTrace();
				dispatchDriverEvent(new DriverEvent(EventType.ERROR,this,"unable to initialize controller -> "+exc.getMessage()));
			}
		} else {

			dispatchDriverEvent(new DriverEvent(EventType.ERROR,this,"unable to initialize controller -> Connection could not be established"));
			return false;
		}
		System.out.println("init Devices)");
		if (elem.hasChildNodes()){
			initDevices(elem.getElementsByTagName("device"));
		}

		if (elem.hasAttribute("autoAddNewDevice")){
			try {
				autoAddNewDevice = Boolean.parseBoolean(elem.getAttribute("autoAddNewDevice"));
			} catch (Exception exc){
				exc.printStackTrace();
				dispatchDriverEvent(new DriverEvent(EventType.ERROR_CONFIGURATION, this,"\""+elem.getAttribute("autoAddNewDevice")+"\" as value for autoAddNewDevice is not valid!"));
				return false;
			}
		}
				
		try {

		} catch (Exception exc){
			dispatchDriverEvent(new DriverEvent(EventType.ERROR,this,"unable to initialize controller -> "+exc.getMessage()));
			exc.printStackTrace();
			return false;
		}
		return true;
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

	private IOStreams connect(Element elem) throws IOException {
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


	@Override
	public void startLearnMode(){
		
	}
	
	@Override
	public void cancelLearnMode(){
		
	}
	
	@Override
	public void startRemoveMode(){
		
	}
	
	@Override
	public void cancelRemoveMode(){
		
	}
	
	protected void enableDevices(){
		for (CulDevice device : this.deviceList.values()){
			device.enable();
		}
	}
	
	protected void disableDevices(){
		for (CulDevice device : this.deviceList.values()){
			device.disable();
		}
	}
	
	protected void reset(){
		if (deviceList.isEmpty()) return;
		for (CulDevice device : deviceList.values()){
			device.removeLocation();
			getDeviceManager().removeDevice(device);
		}
		
	}
	
	protected void initDevices(NodeList list){
		Element elem;
		for (int i = 0; i<list.getLength(); i++){		
			Node n = list.item(i);
			if (n instanceof Element){
				elem = (Element)n;
				if (elem.getTagName().equals("device")) {
					initDevice((Element) n);
				}
			}
		}
	}
	
	protected synchronized void initDevice(Element elem){
		CulDevice device = null;
		int deviceType;
		String address = elem.getAttribute("address");
		String id = elem.getAttribute("deviceid");
		String location = elem.getAttribute("location");
		String name = elem.getAttribute("name");
		boolean hidden = false;
		if (elem.hasAttribute("hidden")){
			try {
				hidden = Boolean.parseBoolean(elem.getAttribute("hidden"));
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}
		
		try {
			 deviceType = Integer.parseInt(elem.getAttribute("devicetype"));
		} catch (Exception exc){
			exc.printStackTrace();
			return;
		}
		
		switch (deviceType){
		case Controller.DEVICETYPE_FS20_DI :
		case Controller.DEVICETYPE_FS20_ST :
				String channelStr = getPropertyValue(elem,"channel");
				int channel = 0;
				if (channelStr != null){
					channel = Integer.parseInt(channelStr);
				}
				if (deviceType == Controller.DEVICETYPE_FS20_ST){
					device = generateFs20st(deviceType, id, address, channel);
				}
				if (deviceType == Controller.DEVICETYPE_FS20_DI){
					device = generateFs20di(deviceType, id, address, channel);
				}
				break;
										
		case Controller.DEVICETYPE_WS300_S300TH 	: 
			device = generateS300th(deviceType,id,address);
			break;
		case Controller.DEVICETYPE_FS20_S	:
			device = generateFs20s(deviceType,id, address);
			break;
		case Controller.DEVICETYPE_HMS_100TF		: 
			device = generateHms100tf(deviceType,id,address); 
			break;
		case Controller.DEVICETYPE_FHT_TK			: 
			device = generateFhttk(deviceType,id,address);
			break;	
		case Controller.DEVICETYPE_FHT_80:
			device = generateFht80(deviceType,id,address);
			break;
		case Controller.DEVICETYPE_FHT_80B			:
			device = generateFht80b(deviceType,id,address);
			break;
		
		case Controller.DEVICETYPE_FS20_IRF :
			device = generateFs20irf(deviceType,id,address);
			break;
		case Controller.DEVICETYPE_FS20_S_MD:
			device = generateFs20s_md(deviceType, id, address);
			break;
		case Controller.DEVICETYPE_EM_1000:
			device = generateEm1000(deviceType, id, address);
			break;
		}
		
		if (device == null) return;
		if (getDeviceManager().getPhysicalDevice(device.getDeviceId())!=null){
			
			return;
		}
		device.setName(name);
		if (location != null){
			device.assignLocation(location);
		}
		device.setHidden(hidden);
		getDeviceManager().addDevice(device,this);
		deviceList.put(device.getDeviceId(),device);
		
	}
	
	private String getPropertyValue(Element deviceElem, String property){
		Element tmp;
		if (deviceElem.hasChildNodes()){
			for (int i = 0; i<deviceElem.getChildNodes().getLength(); i++){
				Node n = deviceElem.getChildNodes().item(i);
				if (n instanceof Element){
					tmp =(Element)n; 
					if (tmp.hasAttribute("name")){
						if (tmp.getAttribute("name").equals(property)){
							return ((Element)n).getAttribute("value");
						}
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public Element toXmlElement(Document doc){				
		if (doc == null) return null;
		Element elem = null;
		elem = doc.createElement("driver");
		elem.setAttribute("name",getName());
		elem.setAttribute("version",getVersion());	
		elem.setAttribute("class", this.getClass().getName());
		elem.setAttribute("autoAddNewDevice",""+this.autoAddNewDevice);
		

		for (CulDevice device : this.deviceList.values()){
			elem.appendChild(generateXmlElement(doc,device));
		}
		
		return elem;
	}
	

	protected boolean init(){
		return false;
	}

	private Element generateXmlElement(Document doc, CulDevice device){
		Element elem = null;
		elem = doc.createElement("device");
		
		elem.setAttribute("deviceid",device.getDeviceId());
		elem.setAttribute("name", device.getName());
		elem.setAttribute("address",device.getAddress());
		//elem.setAttribute("devicefamily",""+device.culDevice getDeviceFamily());
		//elem.setAttribute("devicetype", ""+device.culDevice.getDeviceType());
		if (device.getLocation() != null){
			//elem.setAttribute("location",device.getLocation().getName());
			elem.setAttribute("location",device.getLocation().getId());
		}
		
	
		Element tmp = null;
		if (device.hasProperties()){
			for (DeviceProperty prop : device.getPropertyList()){
				tmp = generateXmlElement(doc,prop);
				if (tmp != null){
					elem.appendChild(tmp);
				}
			}
		}
		
		return elem;
	}
	
	
	private Element generateXmlElement(Document doc, DeviceProperty property){
		Element elem = null;
		elem = doc.createElement("property");
		elem.setAttribute("name",property.getName());
		if (property.isValueSet()){			
			elem.setAttribute("value",property.getValue().toString());
		}
		return elem;
	}
	

	protected CulDevice generateFs20s_md(Fs20s_md sensor){
		CulDevice culDevice = null;
		
		
		culDevice = new CulFs20sMd(sensor);
		culDevice.applyProperties();
		
		return culDevice;
	}
	
	
	protected CulDevice generateFs20s_md(int deviceType, String deviceId, String address){
		CulDevice culDevice = null;		
		Device tmpDev = culController.getDeviceFactory().generateDevice(deviceType,deviceId,address);
		culDevice = new CulFs20sMd(tmpDev);
		culDevice.applyProperties();
		return culDevice;
	}
	
	protected CulDevice generateFs20st(){				
		int i = 1;
		String tmp = "01";
		while (getDeviceManager().getDevice("FS20_ST_"+tmp)!= null){
			i++;
			tmp = ""+i;
			if (tmp.length() < 2){
				tmp = "0"+tmp;
			}
			
		}		
		return generateFs20st(Controller.DEVICETYPE_FS20_ST,"FS20_ST_"+tmp,"6ACA",0);
	}
	
	protected CulDevice generateFs20irf(int deviceType, String deviceId, String address){
		CulDevice culDevice = null;
	
		Device tmpDev = culController.getDeviceFactory().generateDevice(deviceType, deviceId, address);
		
		culDevice = new CulFs20irf(tmpDev);
		return culDevice;
		
	}
	
	protected CulDevice generateFs20st(int deviceType, String deviceId, String address, int channel){
		CulDevice culDevice = null;		
		Device tmpDev = culController.getDeviceFactory().generateDevice(deviceType,deviceId,address);
	
		culDevice = new CulFs20st(tmpDev);		
		
		if (culDevice.hasProperty("channel")){
		  culDevice.getProperty("channel").setValue(channel);
		}
		
		return culDevice;
	}
	
	protected CulDevice generateFs20di(){
				
		int i = 1;
		String tmp = "01";
		while (getDeviceManager().getDevice("FS20_DI_"+tmp)!= null){
			i++;
			tmp = ""+i;
			if (tmp.length() < 2){
				tmp = "0"+tmp;
			}			
		}
		return generateFs20di(Controller.DEVICETYPE_FS20_DI,"FS20_DI_"+tmp,"6ACA",2);
	}
	
	protected CulDevice generateFs20di(int deviceType, String deviceId, String address, int channel){
		CulDevice culDevice;
		Device tmpDev = culController.getDeviceFactory().generateDevice(deviceType,deviceId,address);
	
		culDevice = new CulFs20di(tmpDev);		
		
		if (culDevice.hasProperty("channel")){
		  culDevice.getProperty("channel").setValue(channel);
		}
		
		return culDevice;
	}
	
	protected void evaluateControllerEvent(ControllerEvent evnt){
		
		switch (evnt.getEventType()){
		case ControllerEvent.ERROR :
			this.disableDevices();
			System.out.println("Got error from controller "+ evnt.getMessage());
			dispatchDriverEvent(new DriverEvent(EventType.ERROR,this, evnt.getMessage())); 
			break;
		case ControllerEvent.ERROR_CONNECTION_LOST :			
			this.disableDevices();
			this.dispatchDriverEvent(new DriverEvent(EventType.DRIVER_PROBLEM,this, evnt.getMessage()));
			break;
		case ControllerEvent.CONNECTED :
		//	if (!problemsDuringInit){
				enableDevices();
				dispatchDriverEvent(new DriverEvent(EventType.READY,this));
		//	} else {
			//	dispatchDriverEvent(new DriverEvent(EventType.DRIVER_PROBLEM,this));
		//	}
			break;
		
		}
	}
	
	protected void evaluateDeviceEvent(DeviceEvent evt){		
		if (evt.getEventType() == DeviceEvent.NEW_DEVICE){
			if (!autoAddNewDevice){
				return;
			}
			CulDevice device = null;
			switch (evt.getDevice().getDeviceType()) {
			  case Controller.DEVICETYPE_WS300_S300TH 	: device = generateS300th((S300th)evt.getDevice());	break;
			  case Controller.DEVICETYPE_FHT_80			: device = generateFht80((Fht80)evt.getDevice()); break;
			  case Controller.DEVICETYPE_FHT_80B			: device = generateFht80b((Fht80b)evt.getDevice()); 		break;
			  case Controller.DEVICETYPE_HMS_100TF		: device = generateHms100tf((Hms100tf)evt.getDevice()); break;
			  case Controller.DEVICETYPE_FHT_TK			: device = generateFhttk((Fhttk)evt.getDevice()); 		break;
			  case Controller.DEVICETYPE_FS20_S			: device = generateFs20s((FS20S)evt.getDevice()); 		break;
			  case Controller.DEVICETYPE_FS20_S_MD		: device = generateFs20s_md((Fs20s_md)evt.getDevice()); break;
			  case Controller.DEVICETYPE_EM_1000			: device = generateEm1000((Em1000)evt.getDevice()); break;
			}
			
			if (device != null){
				getDeviceManager().addDevice(device,this);
				deviceList.put(device.getDeviceId(),device);
			}
		}	
		//if (evt.getEventType() == DeviceEvent.NEW_DEVICEMESSAGE){
		//	setActivity(evt.getDevice());
		//}
	}
	

	protected void setActivity(Device device){
		Collection<CulDevice> col = deviceList.values();
		for (CulDevice culd : col){
			if (culd.culDevice == device){
				culd.setLastActivity();				
			}
		}
	}
	
	private void evaluateFs20sEvent(de.smahoo.homeos.device.DeviceEvent event){
		if (event.getEventType() == EventType.DEVICE_PROPERTY_CHANGED){
			if (event.getDevice() instanceof CulFs20s){
				FS20S fs20s = (FS20S)(((CulDevice)event.getDevice()).culDevice);
				updateFS20Devices(fs20s);
			}
		}		
	}
	
	private void updateFS20Devices(FS20S fs20s){
		if (fs20s == null) return;
		String devCode = fs20s.getDeviceCode();
		if (devCode == null) return;
		if (getDeviceManager() == null) return;
		List<PhysicalDevice> devList = getDeviceManager().getPhysicalDevices();
		if (devList == null) return;
		for (PhysicalDevice dev : devList){
			if (dev != null){
				if (dev.getAddress() != null){
					if (dev.getAddress().equalsIgnoreCase(devCode)){
						int channel;
						int state;
						if ((dev instanceof CulFs20st)){
							Property prop = dev.getProperty("channel");
							if (prop != null){					
								channel = (Integer)prop.getValue();
								state = fs20s.getState(channel);
								if (dev instanceof CulFs20st){
									((CulFs20st)dev).setTurnOnState(state == 11);
								}					
							}
						}				
				
					}
				}
			}
		}
	}
	
	protected CulDevice generateFs20s(int deviceType,String deviceId,String address){		
		return generateFs20s((FS20S)culController.getDeviceFactory().generateDevice(deviceType, deviceId, address));		
	}
	
	
	protected CulDevice generateEm1000(int deviceType, String deviceId, String address){
		return generateEm1000((Em1000)culController.getDeviceFactory().generateDevice(deviceType, deviceId, address));
	}
	
	protected CulDevice generateEm1000(Em1000 em1000){
		CulDevice device = new CulEm1000(em1000);
		device.applyProperties();		
		return device;
	}
	
	protected CulDevice generateFs20s(FS20S fs20s){
		CulDevice device = null;		
		device = new CulFs20s(fs20s);
		device.applyProperties();		
		device.addDeviceEventListener(new de.smahoo.homeos.device.DeviceEventListener() {
			public void onDeviceEvent(de.smahoo.homeos.device.DeviceEvent event) {
				evaluateFs20sEvent(event);
				
			}
		});
		return device;
	}
	
	protected CulDevice generateFhttk(int deviceType, String deviceId, String address){
		return generateFhttk((Fhttk)culController.getDeviceFactory().generateDevice(deviceType, deviceId, address));
	}
	
	protected CulDevice generateFhttk(Fhttk fhttk){
		CulDevice dev = null;
		dev = new CulFhttk(fhttk);
		dev.applyProperties();
		return dev;
	}
	
	protected CulDevice generateHms100tf(int deviceType,String deviceId,String address){
		return generateHms100tf((Hms100tf)culController.getDeviceFactory().generateDevice(deviceType, deviceId,address));
	}
	
	protected CulDevice generateHms100tf(Hms100tf hms){
		CulDevice dev = null;		
		dev = new CulHms100tf(hms);
		dev.applyProperties();		
		return dev;
	}
	
	protected CulDevice generateFht80(int deviceType, String deviceId, String address){		
		return generateFht80((Fht80)culController.getDeviceFactory().generateDevice(deviceType, deviceId, address));
	}
	
    protected CulDevice generateFht80(Fht80 fht){
    	CulDevice dev = null;    	
    	dev = new CulFht80(fht);
    	dev.applyProperties();    	
    	return dev;
    }
	
	protected CulDevice generateFht80b(int deviceType, String deviceId, String address){		
		return generateFht80b((Fht80b)culController.getDeviceFactory().generateDevice(deviceType, deviceId, address));
	}
	
    protected CulDevice generateFht80b(Fht80b fht){
    	CulDevice dev = null;    	
    	dev = new CulFht80b(fht);
    	dev.applyProperties();    	
    	return dev;
    }
    
    protected CulDevice generateS300th(int deviceType, String deviceId, String address){    	
    	return generateS300th((S300th)culController.getDeviceFactory().generateDevice(deviceType, deviceId, address));
    }
    
    protected CulDevice generateS300th(S300th s){
    	CulDevice dev = null;    	
    	dev = new CulS300th(s);    	
    	dev.applyProperties();    	
    	return dev;
    }
	
    protected synchronized void checkAvailability(){    	
    	Date date = new Date();
    	Collection<CulDevice> devList = deviceList.values();
    	
    	boolean allDevicesOffline = true;
    	
    	for (CulDevice dev : devList){
    		checkAvailability(dev,MAX_SENSOR_OFFLINE_MILLIS,date.getTime());
    		allDevicesOffline = allDevicesOffline && !dev.isAvailable();
    	}
    	
    	if (allDevicesOffline){
    		System.out.println("All Sensors are offline -> dispatch DRIVER_RUNTIME_ERROR");
    		dispatchDriverEvent(new DriverEvent(EventType.DRIVER_RUNTIME_ERROR,this, "COC - communication interruption."));
    	}
    }
    
    protected void checkAvailability(CulDevice device, long maxMillis, long currentMillis){
    	if (!(device instanceof Sensor)){
    		return;
    	}
    	if (device.getLastActivityTimeStamp() != null){
    		if (currentMillis - device.getLastActivityTimeStamp().getTime() > maxMillis){
    			device.setAvailability(false);
    		}
    	} else {
    		if (currentMillis - startTime.getTime() > maxMillis){
    			device.setAvailability(false);
    		}
    	}
    }
    
    class CheckAvailabilityTask extends TimerTask {
        public void run() {
          checkAvailability();            
        }
    }
    
    @Override
	public RemoteResultItem processCmd(Element elem){
		return null;
	}
	
}
