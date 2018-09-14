package de.smahoo.homeos.kernel;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.smahoo.homeos.service.*;
import de.smahoo.homeos.utils.Logger;
import de.smahoo.homeos.utils.xml.XmlUtils;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.automation.RuleEngine;
import de.smahoo.homeos.automation.RuleEvent;
import de.smahoo.homeos.automation.RuleEventListener;
import de.smahoo.homeos.automation.RuleFactory;
import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventListener;
import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.db.DataBaseManager;
import de.smahoo.homeos.db.DataBaseManagerEvent;
import de.smahoo.homeos.db.DataBaseManagerEventListener;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceEventListener;
import de.smahoo.homeos.device.DeviceManager;
import de.smahoo.homeos.device.role.DeviceRoleEvent;
import de.smahoo.homeos.device.role.DeviceRoleEventListener;
import de.smahoo.homeos.device.role.DeviceRoleFactory;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.driver.DriverEvent;
import de.smahoo.homeos.driver.DriverEventListener;
import de.smahoo.homeos.driver.DriverLoader;
import de.smahoo.homeos.driver.DriverManager;
import de.smahoo.homeos.driver.DriverMode;
import de.smahoo.homeos.io.IOManager;
import de.smahoo.homeos.kernel.remote.RemoteProcessor;
import de.smahoo.homeos.kernel.remote.RemoteServer;
import de.smahoo.homeos.location.LocationEvent;
import de.smahoo.homeos.location.LocationEventListener;
import de.smahoo.homeos.location.LocationManager;
import de.smahoo.homeos.utils.JarResources;


/*
 * ================================================
 * 				CHANGE HISTORY
 * ================================================
 *
 * 0.2.30   - Services could be initialized with XML-config
 * 			- new Device Co2-Sensor
 *			- refactored project structure
 *
 * 0.2.29	- Rule
 * 
 * 0.2.28	- synchronized saveConfiguration in ConfigurationManager
 * 			  (could be conflicted otherwise due to numerous events in short time that impact the save procedure)
 * 
 * 0.2.27	- added sending possibilities for rules (email)
 * 
 * 0.2.26	- reconfiguration for AeoTec Multisensor after init failure
 * 			- fixed problems during boot up: some devices sending data during controller initialization which might result in problems
 * 
 * 0.2.25   - new Remote Commands (save config / driver specific)
 * 
 * 0.2.24   - fixed UTF-8 support for configuration load/save
 * 
 * 0.2.23	- Generating XML Configuration
 * 			- extended config xml profile: added attribute autoSaveConfig for home tag 
 * 
 * 0.2.22	- Locations and Devices can be added and changed via RemoteAPI
 * 
 * 0.2.21	- added IOManager
 * 				- provides CommPorts
 * 				- Driver should not be able to open a CommPort by themselfs! The have to ask the HomeOS to open it for them.
 * 				- Z-Wave Driver will be the first driver using the IOManager to open the CommPort (not implemented yet - 2013-12-2013). 
 * 
 * 0.2.20	-added DRIVER_RUNTIME_ERROR to EventType
 * 				COC Extension for Raspberry might freeze after a while. When CulController doesn't receive any message for more than 1 hour,
 * 				the driver stops and sends an DRIVER_RUNTIME_ERROR event. The HomeOs Kernel will react 
 * 
 * 0.2.19   - fixed bug in http server -> fix separator for filepath
 * 
 * 0.2.16   - added last activity timestamp
 * 
 * 0.2.15	- BUGFIX history mixed 24h and 12h time format
 * 
 * 0.2.14	- RemoteAPI handles property value caching for history data, including saving and loading
 * 			- Fixed Bug in DataBaseManager. DBManager created wrong column types for value type PT_LONG
 * 
 * 0.2.13   - Devices can be deleted now
 * 				- will not removed from DB yet
 * 				- remoteAPI needs to be tested
 * 					- remoteAPI presents subdevices of a multi-device as single devices. Deleting sensor temperature of RHC (eg. FHT 80B) results in deleting
 * 					  RHC in Backend but only subdevice in remoteAPI.    
 * 				- is not working for DeviceRoles
 *  
 * 0.2.12   - new Event: DEVICE_PROPERTY_CHANGED containing all properties that have been changed. 
 * 						 System is now able to wait with reaction until all properties has been changed
 * 			- Rule Engine supports action execution with action parameters
 * 			- HueDriver integrated (Gateway)
 *  
 * 0.2.11	- all request results contains attribute systemId
 * 			- SystemId must be set in config file (will be given by cloud service in future)
 * 			- added DeviceTypes MeterEnergy and MeterElectritiy
 * 				- implemented CUL-Device EM1000 (mobile meter for electricity) * 						 
 *  
 * 0.2.10   - HistoryData is available in RemoteAPI
 * 			- PropertyType implemented
 * 
 * 0.2.9	- DatabaseManager handles Events with BlockingQueue
 * 			- Getter and Setter of DeviceProperty synchronized to prevent wrong entries in database
 *  
 * 0.2.8	- MySQL Database integrated
 *  
 * 0.2.7	- LocationTypes implemented
 *  
 * 0.2.6	- CallbackServer is using Thread for updating all clients
 *  
 * 0.2.5	- PhysicalDetails can be communicated due to HomeOs Remote API
 * 			--> is not working for device roles yet
 *  
 * =================================================
 * 					To Do's
 * =================================================
 * 
 * 		- (done) ASAP HomeOs must be stoppable due to console command 
 * 		- HistoryRequest is not working for DeviceRole
 * 		- HistoryRequest should support data selection by device type (e.g. only Actuator for Heating@...)
 * 		- insert Exceptions (numerous function returning null instead of throwing exceptions) 		
 * 		- RemoteAPI communication has no handshake to communicate versions information
 * 		- homeos-update mechanism to transmit updates remotely
 * 		- loading new config file
 * 			- sending new config file via remoteAPI
 * 			- receiving current configuration via config file with remoteAPI
 * 		- Communication to system periphery should be done by homeOs (e.g. CulDriver and COM)
 * 		- System resources should be transmitted via RemoteAPI to Clients
 * 		- Function and Property abstraction via remoteAPI (ex. support device unspecific functionality - e.g. setColor for LAMP).
 * 		- (done with 0.2.13) Device Deletion
 * 			 -> also via remoteAPI
 * 		- availability State is not propagated to RemoteDevice in RemoteAPI
 * 		- Urgent! Reset functionality
 * 
 */


public class HomeOs extends Thread{

	private static final int DEFAULT_REMOTE_PORT = 2020;
	
	private static HomeOs instance = null;
	private static String strCompany = "smahoo";
	private static String strName 	 = "Home Operating System";
	private static String strVersion = "0.2.30";	// VERSION.SUBVERSION.BUILDNUMBER
	
	
	protected RemoteServer				cmdServer		= null;
	protected DataBaseManager			dataBaseManager = null;
	protected RemoteProcessor 			cmdProcessor 	= null;
	protected DeviceManager 			deviceManager 	= null;
	protected DriverManager 			driverManager 	= null;
	protected LocationManager 			locationManager = null;
	protected ServiceManager  			serviceManager 	= null;
	protected String 					configFileName 	= null;
	protected ConfigurationManager 		cfgManager 		= null;
	protected EventBus					eventBus 		= null;
	protected RuleEngine				ruleEngine 		= null;
	protected SystemClock				clock			= null;
	protected IOManager					ioManager		= null;
	
	private boolean booting = true;
	private boolean stop = false;
	private String systemId;
	protected List<Service> services;
		
	/**
	 * HomeOs is working as a singleton, always use getInstance() instead of calling the constructor!
	 * @return return the single instance of HomeOS
	 */
	public static HomeOs getInstance(){
		if (instance == null){
			instance = new HomeOs();			
		}
		return instance;
	}
	
	/**
	 * Do not use this constructor, use HomeOs.getInstance() instead
	 */
	protected HomeOs(){		
		if (instance != null) return;		
		instance = this;
		clock = new SystemClock();
		eventBus = new EventBus();
		ioManager = new IOManager();	
		cmdProcessor = new RemoteProcessor();
		cmdServer = new RemoteServer(cmdProcessor);
		
		cmdServer.addEventListener(new EventListener() {
			
			@Override
			public void onEvent(Event event) {
				processRemoteEvent(event);
				
			}
		});
		
		deviceManager = new DeviceManager();		
		deviceManager.addDeviceEventListener(new DeviceEventListener() {
			

			@Override
			public void onDeviceEvent(DeviceEvent evt) {
				evaluateDeviceEvent(evt);
				
			}
		});
		deviceManager.addDeviceRoleEventListener(new DeviceRoleEventListener() {
			
			@Override
			public void onDeviceRoleEvent(DeviceRoleEvent evnt) {
				evaluateDeviceRoleEvent(evnt);
				
			}
		});
		driverManager = new DriverManager();
		driverManager.addDriverEventListener(new DriverEventListener() {

			public void onDriverEvent(DriverEvent evnt) {
				evaluateDriverEvent(evnt);
				
			}
		});
		
		locationManager = new LocationManager();
		locationManager.addEventListener(new LocationEventListener() {
			
			@Override
			public void onLocationEvent(LocationEvent evnt) {
				evaluateLocationEvent(evnt);
				
			}
		});
		serviceManager = new ServiceManager();
		serviceManager.addServiceEventListener(new ServiceListener() {
			
			@Override
			public void onServiceEvent(ServiceEvent event) {
				evaluateServiceEvent(event);
				
			}
		});
		cfgManager = new ConfigurationManager();
		ruleEngine = new RuleEngine();
		ruleEngine.addEventListener(new RuleEventListener() {
			
			@Override
			public void onRuleEvent(RuleEvent event) {
				evaluateRuleEvent(event);				
			}
		});
		dataBaseManager = new DataBaseManager();
		dataBaseManager.addEventListener(new DataBaseManagerEventListener() {			
			@Override
			public void onDataBaseManagerEvent(DataBaseManagerEvent event) {
				evaluateDataBaseManagerEvent(event);
				
			}
		});
		
		services = new ArrayList<Service>();
	}
	
		
	public String getSystemid(){
		return this.systemId;
	}
	
	/**
	 * Starts the HomeOs with a given config xml file. 
	 * @param configFileName Path of the config xml file.
	 */
	public void start(final String configFileName){
		this.configFileName = configFileName;		
		start();
	}
	
	public String getConfigFileName(){
		return configFileName;
	}
	
	public void saveConfiguration() throws IOException{
		cfgManager.writeConfigFile(configFileName);
	}
	
	public void saveConfiguration(String filename) throws IOException{
		cfgManager.writeConfigFile(filename);
	}
	
	public String getConfiguration(){
		return XmlUtils.xml2String(cfgManager.generateConfigXml());
	}
	
	public Element getConfiguration(Document doc){
		return cfgManager.getConfigElement(doc);
		
	}
	
	public static void printDetails(){
		System.out.println();
		System.out.println(strName+" "+strVersion);
		System.out.println(strCompany);
		System.out.println();
	}
		
	public synchronized void shutdown(){
		stop = true;
	}
	
	protected void init(){		
				
	}
	
	public RuleEngine getRuleEngine(){
		return ruleEngine;
	}
	
	public EventBus getEventBus(){
		return eventBus;
	}
	
	public void init(final String configFileName){
		init();		
	}
	
	public RemoteProcessor getCommandProcessor(){
		return this.cmdProcessor;
	}
	
	public DeviceManager getDeviceManager(){
		return deviceManager;
	}
		
	public DataBaseManager getDataBaseManager(){
		return this.dataBaseManager;
	}
	
	public DriverManager getDriverManager(){
		return driverManager;
	}
	
	public LocationManager getLocationManager(){
		return locationManager;
	}

	public ServiceManager getServiceManager() {
		return serviceManager;
	}
	
	public IOManager getIoManager(){
		return ioManager;
	}
	
	public static String getVersion(){
		return strVersion;
	}
	
	
	public static String getCompanyName(){
		return strCompany;
	}
	
	private synchronized boolean doStop(){
		return stop;
	}
	
	private void evaluateDeviceRoleEvent(final DeviceRoleEvent evnt){
		eventBus.dispatch(evnt);
	}
	
	private void evaluateDeviceEvent(final DeviceEvent evnt){
		if (evnt.getEventType()==EventType.DEVICE_ADDED){
			this.ruleEngine.checkRuleApplicability();
			
			this.driverManager.setMode(DriverMode.DRIVER_MODE_NORMAL);
			
			if (!booting){
				try {
					autoSaveConfig();
				} catch (Exception exc){
					exc.printStackTrace();
				}
			}
		}
		eventBus.dispatch(evnt);
	}
	
	private void evaluateDriverEvent(final DriverEvent evnt){		
		eventBus.dispatch(evnt);
		if (evnt.getEventType() == EventType.DRIVER_RUNTIME_ERROR){
			shutdown();
			restart();
		}
	}
	
	private void processRemoteEvent(Event event){
		eventBus.dispatch(event);
	}
	
	private void evaluateLocationEvent(final LocationEvent evnt){
		eventBus.dispatch(evnt);
		if (!booting) {
			try {
				autoSaveConfig();
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}
	}
	
	private void evaluateRuleEvent(final RuleEvent event){
		eventBus.dispatch(event);
	}
	
	private void evaluateServiceEvent(final ServiceEvent event){
		eventBus.dispatch(event);
	}
	
	private void evaluateDataBaseManagerEvent(final DataBaseManagerEvent event){
		eventBus.dispatch(event);
	}
	
	private void restart(){
		System.out.println("");
		System.out.println("=========================================");
		System.out.println("     SYSTEM IS GOING TO RESTART NOW!");
		System.out.println("=========================================");
		System.out.println("");		
		try  {
		   Runtime rt = Runtime.getRuntime();
		   rt.exec("sudo reboot");
		} catch(Exception e) {
		   System.out.println("Exception");
		}
		
	}
	
	@Override
	public void run(){
		printDetails();
		System.out.println("Kernel is starting...");
		System.out.println();		
		try {
			//System.out.println("############## Loading Configfile....");
			loadConfigFile();
		
			//System.out.println("############## Loading Location List....");
			loadLocationManager();
		
			//System.out.println("############## Loading Drivers....");
			loadDriver();
		
			//System.out.println("############## Loading Init Drivers....");
			initDrivers();
		
			//System.out.println("############## Loading Roles....");
			loadRoles();	
		
			//System.out.println("############## starting Rule Engine....");
			startRuleEngine();	
		
			//System.out.println("############## Loading Services");
		    loadServices();

			initServices();

		    //System.out.println("############## Starting Services ....");
			startServices();
		
			//System.out.println("############## Init DB manager....");
			initDataBaseManager();
		
			//System.out.println("############## Starting Command Server ....");
			startCommandServer();
		} catch (Exception exc){
			exc.printStackTrace();
		}
			
		System.out.println();	
		System.out.println("Kernel ready");		
		booting = false;
		printTotalMemory();
		while (!doStop()){
			try {
				sleep(500);
				
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}
		try {
			sleep(2000);
			
		} catch (Exception exc){
			exc.printStackTrace();
		}
		stopCommandServer();
		stopServices();
		System.out.println();
		System.out.println(".....Kernel stopped");
		System.exit(0);
	}
	
	protected void loadDriver(){
		System.out.println("Loading Driver");		
		File f = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"driver");
		if (!f.exists()) return;


		File[] files = f.listFiles();
		DriverLoader drl = new DriverLoader();
		String className = null;		
		for (File file : files){
			if (file.getPath().endsWith(".jar")) {
				className = getDriverClassName(file.getAbsolutePath());
				if (className != null) {
					try {
						System.out.print("Loading " + className + " ...");
						Driver driver = drl.loadDriver(file.getAbsolutePath(), className);
						System.out.println("OK");
						addDriver(driver);

					} catch (Exception exc) {
						System.out.println("ERROR");
						System.out.println(exc.getMessage());
						exc.printStackTrace();

					}
				}
			}
		}		
	}

	private String getDriverClassName(final String filename){
		return getClassName(filename,"DRIVER.INF");
	}

	private String getServiceClassName(final String filename){
		return getClassName(filename,"SERVICE.INF");
	}

	private String getClassName(final String filename, String metaFileName){
		JarResources res = new JarResources(filename);				
		Properties props = new Properties();

		ByteArrayInputStream stream = new ByteArrayInputStream(res.getResource(metaFileName));
		try {
			props.load(stream);
			
			if (props.containsKey("class")){
				return (String)props.get("class");
			}			
		} catch (Exception exc){
			exc.printStackTrace();
		}
		return null;
	}
	
	
	
	
	protected void startRuleEngine(){
		System.out.println("Loading Rules");	
		if (cfgManager.getRulesConfiguration() != null){
			RuleFactory.getInstance().initRuleEngine(cfgManager.getRulesConfiguration());
		}
	}
	
	protected void loadConfigFile(){		
		if (this.configFileName == null) return;
		System.out.println("Loading "+configFileName);
		cfgManager.loadConfigXml(configFileName);
		this.systemId = cfgManager.getSystemId();
		System.out.println("SystemId = "+this.systemId);
	}
	
	protected synchronized void autoSaveConfig() throws IOException{
		if (cfgManager.isAutoSaveConfig()){
			saveConfigFile();
		}
	}
	
	protected synchronized void saveConfigFile() throws IOException{
		System.out.println("Saving "+configFileName+".tmp");
		cfgManager.writeConfigFile(configFileName+".tmp");
		File newFile = new File(configFileName+".tmp");
		File oldFile = new File(configFileName);
				
		oldFile.delete();
		newFile.renameTo(oldFile);
	}
	
	
	
	protected void initDataBaseManager(){
		System.out.println("Initializing DatabaseManager");
		dataBaseManager.init(cfgManager.getDataBaseConfiguration());		
	}
	
	protected void loadRoles(){
		System.out.println("Loading Device Roles");
		DeviceRoleFactory drf = DeviceRoleFactory.getInstance();
		drf.generateRolesFromXml(cfgManager.getRolesConfiguration());		
	}
	
	protected void loadLocationManager(){
		System.out.println("Initialising Locations");
		locationManager.init(cfgManager.getLocationConfigurations());
	}
	
	protected void loadServices(){

		System.out.println("Loading Services");
		File f = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"services");
		if (!f.exists()) return;
		File[] files = f.listFiles();
		ServiceLoader sl = new ServiceLoader();
		String className = null;
		for (File file : files){
			if (file.getPath().endsWith(".jar")) {
				className = getServiceClassName(file.getAbsolutePath());
				if (className != null) {
					try {
						System.out.print("Loading " + className + " ...");
						Service service = sl.loadService(file.getAbsolutePath(), className);
						System.out.println("OK");
						addService(service);

					} catch (Exception exc) {
						System.out.println("ERROR");
						System.out.println(exc.getMessage());
						exc.printStackTrace();

					}
				}
			}
		}


	}

	protected void initServices(){
		for (Service service : serviceManager.getServices()){
			System.out.println("try to get configuration for class "+service.getClass().getCanonicalName());
			service.init(cfgManager.getServiceConfiguration(service.getClass().getCanonicalName()));
		}
	}

	protected void addService(Service service){
		serviceManager.addService(service);
	}
	
	protected void startServices(){
		serviceManager.startAllServices();
	}
	
	protected void stopServices(){
		serviceManager.stopAllServices();
	}
	
	protected void stopCommandServer(){
		cmdServer.stop();
	}
	
	protected void startCommandServer(){
		try {	
		   cmdServer.start(DEFAULT_REMOTE_PORT);
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	public boolean loadDriver(final String filename,final String classname){		
		DriverLoader dl = new DriverLoader();
		try {
			Driver driver = dl.loadDriver(filename, classname);
			if (driver == null) return false;		
			addDriver(driver);
			return true;
		} catch (Exception exc){
			exc.printStackTrace();
		}
		return false;
	}
	
	public void addDriver(final Driver driver){		
		
		driverManager.addDriver(driver);	
	}
	
	public void initDrivers(){
		for (Driver driver: driverManager.getLoadedDriver()){
			initDriver(driver);
		}
	}
	
	public void initDriver(Driver driver){
		System.out.print("initializing Driver "+driver.getName()+" "+driver.getVersion()+".......");
		if (driver.init(cfgManager.getDriverConfiguration(driver.getClass().getCanonicalName()))){
			System.out.println("OK");
		} else {
			System.out.println("ERROR");
		}
	}
	
	private static void sendStopCommand(){	
		String cmd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cmd><shutdown password=\"sh1td2wn!\"/></cmd>";
	
		try {			
			URL url = new URL("http://localhost:2020/homeos/remote");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();			
			connection.setRequestMethod( "POST" );			
			connection.setDoInput( true );
			connection.setDoOutput( true );
		//	connection.setUseCaches( false );			
			connection.setRequestProperty( "Content-Type","text/xml" );
			connection.setRequestProperty( "Content-Length", String.valueOf(cmd.length()) );
						
			OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() );
			writer.write(cmd);
			writer.flush();
			//BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			
			InputStreamReader in = new InputStreamReader(connection.getInputStream());
			StringBuffer buffer = new StringBuffer();
			int read = 0;
			while ((read = in.read()) !=-1 ){
				buffer.append((char)read);
			}
		
		
			writer.close();
			in.close();			
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename= null;  
		HomeOs os;
		// check for arguments with static processing first
		if (args.length == 1){
			if ((args[0].equals("--version"))||(args[0].equals("-v"))){
				printVersion();
				return;
			}			
			if ((args[0].equals("--help"))||(args[0].equals("-h"))){
				printHelp();
				return;
			}
			if ((args[0].equals("--stop"))||(args[0].equals("-h"))){
				sendStopCommand();
				return;
			}			
			
		}
		// no static processing arguments found
		if (args.length > 0){
			// last argument should always be a config file name without "-" at the beginning
			filename = args[args.length-1];			
			if (filename.startsWith("-") || (filename.startsWith("--"))){
				System.out.println("invalid config filename ("+filename+")");
			}


			// filename set -> evaluate rest of the arguments 
			// no other arguments supported yet
		}
		
		os = new HomeOs();
		if (filename != null){
			String absoluteFilePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"config"+System.getProperty("file.separator")+filename;
			//String absoluteFilePath = filename;
			os.start(absoluteFilePath);
		} else os.start();
	}
	
	public static void printVersion(){
		System.out.println();
		System.out.println(strName +" "+strVersion);
		System.out.println(strCompany);
	}
	
	private static void printTotalMemory(){
		NumberFormat format = NumberFormat.getInstance();
		try {		
			System.out.println("   total Memory : "+format.format(Runtime.getRuntime().totalMemory()/1024)+" kB");
			System.out.println("    free memory : "+format.format(Runtime.getRuntime().freeMemory()/1024)+" kB");
			System.out.println("     max memory : "+format.format(Runtime.getRuntime().maxMemory()/1024)+" kB");
			System.out.println("   total - free : "+format.format((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024/1014)+" MB");
			System.out.println("free disk space : "+format.format(getFreeDiskSpace()/1024/1024)+" MB");
		} catch (Exception exc){
			//
		}
	}
	
	public static long getFreeDiskSpace(){
		File f = new File("/");
		return f.getFreeSpace();
	}
	
	public static void printHelp(){
		//
	}

	public Logger getLogger(String name){
		return new LoggerWrapper(name);
	}

	public class LoggerWrapper implements Logger{
		org.slf4j.Logger logger;

		public LoggerWrapper(String name){
			logger = LoggerFactory.getLogger(name);
		}

		public void info(String message){
			logger.info(message);
		}

		public void error(String message){
			logger.error(message);
		}

		public void error(String message, Throwable throwable){
			logger.error(message,throwable);
		}

		public void warn(String message){
			logger.error(message);
		}

		public void debug(String message){
			logger.debug(message);
		}
	}

}
