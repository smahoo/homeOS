package de.smahoo.homeos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventListener;
import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.device.PropertyHistoryData;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.kernel.EventBus;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.homeos.utils.AttributeValuePair;


public class DataBaseManager{

	private String serverIp = null;
	private String username = null;
	private String password = null;
	private String dbname 	= null;
	private List<DataBaseManagerEventListener> eventListeners;
	private List<Device> registeredDevices;
	protected BlockingQueue<Event> eventQueue;
	private DBEventHandler eventHandler;
	
	private DataBaseMode currentDbMode = DataBaseMode.DBMODE_OFF;
	Connection dbConnection = null;
		
	public DataBaseManager(){
		eventListeners = new ArrayList<DataBaseManagerEventListener>();
		registeredDevices = new ArrayList<Device>();
		eventQueue = new ArrayBlockingQueue<Event>(1024);
	}	
	
	public void addEventListener(DataBaseManagerEventListener listener){
		if (listener == null) return;
		if (eventListeners.contains(listener)) return;
		eventListeners.add(listener);
	}
	
	public void removeEventListener(DataBaseManagerEventListener listener){
		if (listener == null) return;
		if (eventListeners.isEmpty()) return;
		if (eventListeners.contains(listener)){
			eventListeners.remove(listener);
		}
	}
	
	protected void dispatchDataBaseManagerEvent(DataBaseManagerEvent event){
		if (event == null) return;
		if (eventListeners == null) return;
		for (DataBaseManagerEventListener listener : eventListeners){
			listener.onDataBaseManagerEvent(event);
		}
	}
	
	protected void init(){
		
		try {
			System.gc();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	
		dbConnection = getConnection();
		try {
			System.gc();
		} catch (Exception exc){
			exc.printStackTrace();
		}
		createDefaultTables();
		
		
		eventHandler = new DBEventHandler();
		eventHandler.start();
		
		EventBus bus = HomeOs.getInstance().getEventBus();
		bus.addListener(new EventListener() {			
			@Override
			public void onEvent(Event event) {
				try {
					eventQueue.put(event);
				} catch (Exception exc){
					exc.printStackTrace();
				}			
			}
		});	
		
	}
	
	
	protected boolean isLogDevice(Device device){
		switch (currentDbMode){
			case DBMODE_LOG_ALL_DEVICES: return true;
			case DBMODE_OFF : return false;
			case DBMODE_LOG_REGISTRATED_DEVICES:	return (this.registeredDevices.contains(device));			
		}		
		return false;
	}
	
	protected synchronized void evaluateHomeOsEvent(Event event){
		if (event instanceof DeviceEvent){
			DeviceEvent de = (DeviceEvent)event;
			switch (de.getEventType()) {
			case DEVICE_RENAMED :
				changeDeviceName(de.getDevice());
				break;
			case DEVICE_AVAILABLE:
			case DEVICE_NOT_AVAILABLE:				
			case DEVICE_PROPERTY_CHANGED:
			case DEVICE_OFF:
			case DEVICE_ON:
			case PROPERTY_VALUE_CHANGED:
				if (isLogDevice(de.getDevice())){
					insertValues(de.getDevice());
				}
				break;
			case DEVICE_ADDED:
				if (isLogDevice(de.getDevice())){				
					createTable(de.getDevice());
					addDeviceDetails(de.getDevice());
				}				
				break;
			case DEVICE_REMOVED:
				
				break;
			default:
				// do nothing
				break;
			}
		}
		logEvent(event);
	}
	
	
	protected synchronized void addLocationDetails(Location location){
		Connection conn = getConnection();
		if (conn == null){
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, "Unable to add location details"));
			return;
		}
		try {		
			String query = "INSERT INTO locations(locationId,locationName, locationType,parentLocation) ";
			
			if (location.getParentLocation()!= null){
				query = query + "VALUES ('"+location.getId()+"','"+location.getName()+"','"+location.getLocationType().name()+"','"+location.getParentLocation().getId()+"')"; 
			} else {
				query = query + "VALUES ('"+location.getId()+"','"+location.getName()+"','"+location.getLocationType().name()+"',NULL)";
			}
			query = query + " ON DUPLICATE KEY UPDATE locationId=locationId;"; 
			Statement statement = conn.createStatement();			
			boolean result = statement.execute(query);
		//	System.out.println("result = "+result);
		} catch (Exception exc){
			exc.printStackTrace();
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, exc.getMessage()));
		}
	}
	
	
	
	protected String getCreateTablePropertyStrings(Device device){
		String res = "";
		if (device instanceof PhysicalDevice){
			PhysicalDevice pd = (PhysicalDevice)device;
			List<DeviceProperty> properties = pd.getPropertyList();
			for (DeviceProperty prop : properties){
				switch (prop.getPropertyType()){
				case PT_BOOLEAN :	res = res + prop.getName().replace(" ","_")+ " tinyint(1) NULL,"; break;
				case PT_DOUBLE : 	res = res + prop.getName().replace(" ","_")+ " double NULL,"; 	  break;
				case PT_INTEGER : 	res = res + prop.getName().replace(" ","_")+ " int NULL,";        break;
				case PT_LONG : 		res = res + prop.getName().replace(" ","_")+ " bigint NULL,";    break;
				case PT_STRING:	 	res = res + prop.getName().replace(" ","_")+ " text NULL,";       break;
				}
			}
		}
		return res;
	}
	
	
	protected synchronized void createLocationDetailsTable(){
		Connection conn = getConnection();
		if (conn == null){
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, "Unable to create table locations"));
			return;
		}
		try {
			
			String query = "CREATE TABLE IF NOT EXISTS locations "+
					"( id int NOT NULL AUTO_INCREMENT,"+		
					"locationId varchar(25) NOT NULL,"+
					"locationName varchar(255) NOT NULL,"+
					"locationType varchar(25) NOT NULL,"+
					"parentLocation int NULL,"+			
					"UNIQUE (locationId),"+
				    "PRIMARY KEY (id)"+					
					");";
			
	
			Statement statement = conn.createStatement();
			//System.out.println(query);
			
			statement.execute(query);

		} catch (Exception exc){
			exc.printStackTrace();
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, exc.getMessage()));
		}
	}
	
	protected synchronized void createDeviceDetailsTable(){
		Connection conn = getConnection();
		if (conn == null){
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, "Unable to create table devices"));
			return;
		}
		try {
			
			String query = "CREATE TABLE IF NOT EXISTS devices "+
					"( id int NOT NULL AUTO_INCREMENT,"+
					"deviceId varchar(25) NOT NULL,"+
					"deviceName varchar(255) NOT NULL,"+					
					"locationId varchar(25) NULL,"+
					"isHidden tinyint(1) NOT NULL default 0,"+
					"isRole tinyint(1) NOT NULL default 0,"+
					"UNIQUE (deviceId),"+
				    "PRIMARY KEY (id)"+
				
					");";
			
	
			Statement statement = conn.createStatement();
			System.out.println(query);
			
			statement.execute(query);

		} catch (Exception exc){
			exc.printStackTrace();
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, exc.getMessage()));
		}
	}
	
	
	protected synchronized void addDeviceDetails(Device device){
		Connection conn = getConnection();
		if (conn == null){
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, "Unable to add device details"));
			return;
		}
		try {		
			String query = "INSERT INTO devices( deviceId, deviceName, locationId, isHidden, isRole) ";
			if (device.getLocation() != null){
				query = query + "VALUES ('"+device.getDeviceId()+"','"+device.getName()+"','"+device.getLocation().getId()+"',"+bool2Int(device.isHidden())+","+bool2Int(device instanceof DeviceRole)+")"; 
			} else {
				query = query + "VALUES ('"+device.getDeviceId()+"','"+device.getName()+"',NULL,"+bool2Int(device.isHidden())+","+bool2Int(device instanceof DeviceRole)+")";
			}
			query = query + " ON DUPLICATE KEY UPDATE deviceId=deviceId;"; 
			Statement statement = conn.createStatement();		
			boolean result = statement.execute(query);
		//	System.out.println("result = "+result);
		} catch (Exception exc){
			exc.printStackTrace();
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, exc.getMessage()));
		}
	}
	
	protected synchronized void createDefaultTables(){
		System.out.println("   Creating default tables");
		List<Device> devList = HomeOs.getInstance().getDeviceManager().getDevices();
		
		createDeviceDetailsTable();
		createLocationDetailsTable();
		createLogTable();
		
		if (!devList.isEmpty()){
			switch (currentDbMode){
			case DBMODE_LOG_ALL_DEVICES: 
				for (Device dev : devList){
					createTable(dev);
					addDeviceDetails(dev);
				}
				break;
			
			case DBMODE_LOG_REGISTRATED_DEVICES:
				for (Device dev : registeredDevices){
					createTable(dev);
					addDeviceDetails(dev);
				}
				break;
			}
		}
		
		List<Location> locList = HomeOs.getInstance().getLocationManager().getAllLocations();
		if (!locList.isEmpty()){
			for (Location loc: locList){
				addLocationDetails(loc);
			}
		}
	}
	
	protected synchronized void createLogTable(){
		Connection conn = getConnection();
		if (conn == null){
			return;
		}
		try {
			
			String query = "CREATE TABLE IF NOT EXISTS logs "+
					"( id int NOT NULL AUTO_INCREMENT,"+			
					"timestamp datetime NOT NULL,"+
					"eventtype varchar(100) NOT NULL,"+
					"description text NOT NULL,"+					
				    "PRIMARY KEY (id)"+					
					");";
			
	
			Statement statement = conn.createStatement();
			//System.out.println(query);
			
			statement.execute(query);

		} catch (Exception exc){
			exc.printStackTrace();
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, exc.getMessage()));
		}
	}
	
	protected synchronized void createTable(Device device){
		Connection conn = getConnection();
		if (conn == null){
			return;
		}
		try {
			
			String query = "CREATE TABLE IF NOT EXISTS "+device.getDeviceId()+" "+
					"( id int NOT NULL AUTO_INCREMENT,"+			
					"timestamp datetime NOT NULL,"+
					"isOn tinyint(1) NOT NULL default 0,"+
					"isAvailable tinyint(1) NOT NULL default 0,"+
					getCreateTablePropertyStrings(device)+
				    "PRIMARY KEY (id)"+					
					");";
			
	
			Statement statement = conn.createStatement();
			//System.out.println(query);
			
			statement.execute(query);

		} catch (Exception exc){
			exc.printStackTrace();
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, exc.getMessage()));
		}
	}
	
	protected String getPropertyColNames(Device device){		
		String res = "";
		if (device instanceof PhysicalDevice){
			PhysicalDevice pd = (PhysicalDevice)device;
			List<DeviceProperty> properties = pd.getPropertyList();
			for (DeviceProperty prop : properties){
				res = res +","+prop.getName().replace(" ","_");
			}
		}
		return res;
	}
	
	protected String getPropertyColValues(Device device){
		String res = "";
		if (device instanceof PhysicalDevice){
			PhysicalDevice pd = (PhysicalDevice)device;
			List<DeviceProperty> properties = pd.getPropertyList();
			for (DeviceProperty prop : properties){
				if (prop.getPropertyType() == PropertyType.PT_STRING){
					res = res + ",'"+prop.getValue()+"'";
				} else {
					res = res + ","+prop.getValue();
				}
			}
		}
		return res;
	}
	
	protected String getTimeStampString(){		
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( new Date() );
		
	}
	
	protected int bool2Int(boolean bool){
		if (bool){
			return 1;
		}
		return 0;
	}
	
	
	protected void changeDeviceName(Device device){
		Connection conn = getConnection();
		if (conn == null){
			return;
		}
		try {		
			String deviceId = device.getDeviceId();
			if (deviceId.contains("@")){
				deviceId = deviceId.substring(deviceId.indexOf('@'), deviceId.length());
			}
			String query = "UPDATE devices SET deviceName = '"+device.getName()+"' WHERE deviceId='"+deviceId+"'";
			

			Statement statement = dbConnection.createStatement();
	
			
			boolean result = statement.execute(query);
		//	System.out.println("result = "+result);
		} catch (Exception exc){
			exc.printStackTrace();
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, exc.getMessage()));
		}
	}
	
	protected void insertValues(Device device){
		Connection conn = getConnection();
		if (conn == null){
			return;
		}
		try {
			
			int isOn = bool2Int(device.isOn());
			int isAvailable = 0;
			if (device instanceof PhysicalDevice){
				isAvailable = bool2Int(((PhysicalDevice)device).isAvailable());
			}
			
			String query = "INSERT INTO "+device.getDeviceId()+" "+
					"( timestamp, isOn, isAvailable"+getPropertyColNames(device)+") "+
					"VALUES ('"+getTimeStampString()+"',"+isOn+","+isAvailable+getPropertyColValues(device)+");";
			

			Statement statement = dbConnection.createStatement();
			System.out.println(query);
			
			boolean result = statement.execute(query);
		//	System.out.println("result = "+result);
		} catch (Exception exc){
			exc.printStackTrace();
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, exc.getMessage()));
		}
	}
	
	public synchronized void setDbMode(DataBaseMode mode){
		this.currentDbMode = mode;
	}
	
	public void init(String server, int port,String dbname, String username, String password, DataBaseMode dbmode){
		dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.CONFIGURATION_STARTED, "DataBaseManager will be initialized"));
		this.serverIp = server;
		this.password = password;
		this.username = username;
		this.dbname = dbname;
		this.currentDbMode = dbmode;
		init();
		dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.CONFIGURATION_FINISHED, "DataBaseManager initialized"));
	}
	
	public void registerDevice(Device device){
		if (device != null){
			if (registeredDevices.contains(device)) return;
			registeredDevices.add(device);
			//createTable(device);
		}
	}
	
	protected void registerDevices(NodeList nodelist){
		Element tmp = null;
		Device tmpDev;
		for (int i = 0; i< nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				tmp = (Element)nodelist.item(i);
				if (tmp.getTagName().equalsIgnoreCase("device")){
					tmpDev = HomeOs.getInstance().getDeviceManager().getDevice(tmp.getAttribute("deviceId"));
					if (tmpDev != null){
						registerDevice(tmpDev);
					}
				}
			}
		}
	}
	
	
	public Element getConfigElement(Document doc){
		Element elem = doc.createElement("database");
		
		elem.setAttribute("serverip", serverIp);
		elem.setAttribute("username",username);
		elem.setAttribute("password", password);
		elem.setAttribute("dbname",dbname);
		elem.setAttribute("mode",currentDbMode.name());
		
		if (registeredDevices != null){
			if (!registeredDevices.isEmpty()){
				// FIXME: add registered Devices
			}
		}
		
		return elem;
	}
	
	public void init(Element element){		
		dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.CONFIGURATION_STARTED, "DataBaseManager will be initialized"));
		if (element == null){
			currentDbMode = DataBaseMode.DBMODE_OFF;
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.CONFIGURATION_FINISHED, "DataBaseManager has been initialized"));
			return;
		}
		if (!element.getTagName().equalsIgnoreCase("database") ){
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR_CONFIGURATION,"No configuration found for database"));
			return;
		}
		try {			
			currentDbMode = DataBaseMode.valueOf(element.getAttribute("mode"));
			serverIp = element.getAttribute("serverip");			
			username = element.getAttribute("username");
			password = element.getAttribute("password");
			dbname 	 = element.getAttribute("dbname");
			if (currentDbMode == DataBaseMode.DBMODE_OFF){
				dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.CONFIGURATION_FINISHED, "DataBaseManager has been initialized"));
				return;
			}
			
			if (element.hasChildNodes()){
				registerDevices(element.getChildNodes());
			}
		
		} catch (Exception exc){
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR_CONFIGURATION, exc.getMessage()));
			return;
		}		
		init();
		dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.CONFIGURATION_FINISHED, "DataBaseManager has been initialized"));
	}
	
	protected Connection getConnection(){
		if (dbConnection != null){
			try {
				if (dbConnection.isClosed()){
					dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.CONNECTION_LOST, "Lost connection do mysql server @"+serverIp+"!"));
					dbConnection = null;
					return getConnection();
				} else {
					return dbConnection;
				}
			} catch (Exception exc){
				exc.printStackTrace();
				dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.CONNECTION_LOST, exc.getMessage()));
				return null;
			}		
		}				
		Connection conn = null;
		dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.CONNECTING,"Connecting to mysql '"+serverIp+"'."));
		try {
			String connString = "jdbc:mysql://"+serverIp+":3306/"+dbname+"?user="+username+"&password="+password+"&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
			System.out.println("   Connecting to mysql server");
			conn = DriverManager.getConnection(connString);
			System.out.println("   Connected");
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.CONNECTION_ESTABLISHED,"Connected to mysql '"+serverIp+"'."));
		} catch (Exception exc){
			exc.printStackTrace();
		
			conn = null;
			dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, exc.getMessage()));
		}		
		return conn;
	}	
	
	public List<PropertyHistoryData> getHistoryData(Set<String> propertyNames, Device device, Date start, Date end){
		if (!(device instanceof PhysicalDevice)){
			
		}	
		Connection conn = getConnection();
		if (conn == null){
			return null;
		}
		List<PropertyHistoryData> result = new ArrayList<PropertyHistoryData>();
		String propertyStr = "timestamp, isOn, isAvailable";
		for (String pname : propertyNames){			
			propertyStr = propertyStr+","+pname;
		}
		
		try {
			Statement statement = conn.createStatement();			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String query="SELECT "+propertyStr+" FROM "+device.getDeviceId()+" WHERE (timestamp > '"+formatter.format(start)+"' AND timestamp < '"+formatter.format(end)+"') ORDER BY timestamp";
			ResultSet resultSet = statement.executeQuery(query);
			PropertyHistoryData tmp;	
			while (resultSet.next()){				
				tmp = new PropertyHistoryData(resultSet.getTimestamp("timestamp"), device, (resultSet.getInt("isOn")>0),(resultSet.getInt("isAvailable")>0));
				for (String pname : propertyNames){			
					tmp.addAttributeValuePair(new AttributeValuePair(pname,resultSet.getString(pname)));
				}
				result.add(tmp);
			}
		} catch (Exception exc){
			exc.printStackTrace();
			return null;
		}
		return result;
	}
	
	public List<PropertyHistoryData> getHistoryData(Device device, Date start, Date end){
		if (!(device instanceof PhysicalDevice)){
			return null;
		}	
		PhysicalDevice dev = (PhysicalDevice)device;
		return getHistoryData(dev.getPropertyNames(), device, start, end);
		
	}

	protected void logEvent(Event event){
		if (currentDbMode == DataBaseMode.DBMODE_OFF) return;
		Connection conn = getConnection();
		if (conn == null){
			return;
		}
		String query = "not set";
		
		try {
			
		query = "INSERT INTO logs ( timestamp, eventtype, description) "+
					"VALUES ('"+getTimeStampString()+"','"+event.getEventType().name()+"','"+event.getDescription()+"');";
			

			Statement statement = dbConnection.createStatement();
	
			
			boolean result = statement.execute(query);
		//	System.out.println("result = "+result);
		} catch (Exception exc){
			exc.printStackTrace();
			System.out.println(query);
			//dispatchDataBaseManagerEvent(new DataBaseManagerEvent(EventType.ERROR, exc.getMessage()));
		}
	}

	
	protected class DBEventHandler extends Thread{
		
		boolean keepAlive = true;
		
		public synchronized void turnOff(){
			keepAlive = false;
		}
		
		protected synchronized boolean isKeepAlive(){
			return keepAlive;
		}
		
		public void run(){
			while (isKeepAlive()){
				while(!eventQueue.isEmpty()){
					evaluateHomeOsEvent(eventQueue.poll());
				}
				try {
					Thread.sleep(100);
				} catch (Exception exc){
					//
				}
			}
		}
	}
	
}
