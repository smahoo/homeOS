package de.smahoo.homeos.kernel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import de.smahoo.homeos.service.Service;
import de.smahoo.homeos.service.ServiceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.db.DataBaseManager;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.driver.DriverManager;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.location.LocationManager;
import de.smahoo.homeos.utils.xml.XmlUtils;

public class ConfigurationManager {

	protected static final String XMLVERSION = "1.0";
	
	protected Document currConfigXml = null;
	
	protected HashMap<String, Element> driverConfigs;
	protected HashMap<String, Element> serviceConfigs;
	protected Element rules = null;
	protected Element roles = null;
	protected Element locationConfigs = null;
	protected Element databaseConfig = null;
	
	private boolean autoSaveConfig = false;
	private String systemId = null;
	
	public ConfigurationManager(){
		driverConfigs = new HashMap<String, Element>();
		serviceConfigs = new HashMap<String, Element>();
	}
	
	public boolean isAutoSaveConfig(){
		return autoSaveConfig;
	}
	
	public String getSystemId(){
		return systemId;
	}
	
	public void loadConfigXml(String filename){
		 DocumentBuilderFactory docBuilderFactory;
         DocumentBuilder docBuilder;
         Document doc = null;
         
         try {
        	 docBuilderFactory = DocumentBuilderFactory.newInstance();
        	 docBuilder = docBuilderFactory.newDocumentBuilder();
        	 doc = docBuilder.parse (new File(filename));
         } catch (Exception exc){
        	 exc.printStackTrace();
         }
         
         if (doc == null) return;
         
         currConfigXml = doc;
         driverConfigs.clear();
		 serviceConfigs.clear();

         Element root = doc.getDocumentElement();
         if (!root.hasAttribute("systemId")){
        	 // Fix it 
         }
         
         this.systemId = root.getAttribute("systemId");
         if (root.hasAttribute("autoSaveConfig")){
        	 this.autoSaveConfig = "true".equalsIgnoreCase(root.getAttribute("autoSaveConfig")) ||  "yes".equalsIgnoreCase(root.getAttribute("autoSaveConfig")) ;
         }
         if (!root.hasChildNodes()) return;
         
         NodeList locations = root.getElementsByTagName("locations");
         if (locations.getLength() == 1){
        	 this.locationConfigs = (Element)locations.item(0);
         }
         
         NodeList driverList = root.getElementsByTagName("driver");
         setDriverConfigs(driverList);

		 NodeList serviceList = root.getElementsByTagName("service");
		 setServiceConfigs(serviceList);
         
         NodeList ruleList = root.getElementsByTagName("ruleengine");
         if (ruleList.getLength() == 1){
        	 this.rules = (Element)ruleList.item(0);
         }
         
         NodeList roleList = root.getElementsByTagName("deviceroles");
         if (roleList.getLength() == 1){
        	 this.roles = (Element)roleList.item(0);
         }
         NodeList dbList = root.getElementsByTagName("database");
         if (dbList.getLength() == 1){
        	 this.databaseConfig = (Element)dbList.item(0);
         }
	}

	protected void setServiceConfigs(NodeList serviceList){
		if (serviceList.getLength()== 0) return;
		Element serviceElem;
		String serviceClass;
		for (int i = 0; i<serviceList.getLength(); i++){
			serviceElem = (Element)serviceList.item(i);
			if (serviceElem.hasAttribute("class")){

				serviceClass = serviceElem.getAttribute("class");
				System.out.println("storing for class : "+serviceClass);
				serviceConfigs.put(serviceClass,serviceElem);
			}
		}
	}
		
	protected void setDriverConfigs(NodeList driverList){
		if (driverList.getLength()== 0) return;
		Element driverElem;
		String driverclass;
		for (int i = 0; i<driverList.getLength(); i++){
			driverElem = (Element)driverList.item(i);
			if (driverElem.hasAttribute("class")){
				driverclass = driverElem.getAttribute("class");
				driverConfigs.put(driverclass,driverElem);
			}
		}
	}
	
	public Element getDataBaseConfiguration(){
		return this.databaseConfig;
	}
	
	public Element getLocationConfigurations(){
		return this.locationConfigs;
	}
	
	public Element getDriverConfiguration(String driverclass){
		return driverConfigs.get(driverclass);
	}

	public Element getServiceConfiguration(String serviceClass) {
		System.out.println("Requesting service for class : "+serviceClass);

		//serviceConfigs.keySet().forEach(str -> System.out.println("  - "+str));
		return serviceConfigs.get(serviceClass);
	}
	
	public Element getRulesConfiguration(){
		return this.rules;
	}
	
	public Element getRolesConfiguration(){
		return this.roles;
	}
	
	public synchronized void writeConfigFile(String filename) throws IOException{		
		
		Document doc = generateConfigXml();		
		if (doc != null){
			try {				
				XmlUtils.saveXml(new File(filename), doc);
			} catch (Exception exc){
				exc.printStackTrace();
				throw new IOException(exc);
				// FIXME: how to handle with that?
			}
		}
	}
	
	protected Document generateConfigXml(){
		Document doc =XmlUtils.createDocument();		
		doc.appendChild(getConfigElement(doc));		
		return doc;
	}
	
	public Element getConfigElement(Document doc){
		DriverManager driverManager = HomeOs.getInstance().getDriverManager();		
		LocationManager locManager = HomeOs.getInstance().getLocationManager();
		DataBaseManager dbManager = HomeOs.getInstance().getDataBaseManager();
		ServiceManager serviceManager = HomeOs.getInstance().getServiceManager();
		
		Element root = doc.createElement("home");	
		root.setAttribute("xmlversion", XMLVERSION);
		root.setAttribute("version", HomeOs.getVersion());
		root.setAttribute("systemId", systemId);
		root.setAttribute("autoSaveConfig", ""+isAutoSaveConfig());
		Element loc = doc.createElement("locations");
		for (Location location : locManager.getLocations()){
			loc.appendChild(generateXmlElement(doc,location));
		}
		
		root.appendChild(loc);
		for (Driver driver : driverManager.getLoadedDriver()){
			root.appendChild(generateXmlElement(doc,driver));
		}

		for (Service service : serviceManager.getServices()){
			root.appendChild(service.toXmlElement(doc));
		}

		root.appendChild(dbManager.getConfigElement(doc));
		
		return root;
	}
	
	private Element generateXmlElement(Document doc, Location loc){
		Element elem = doc.createElement("location");
		elem.setAttribute("id", loc.getId());
		elem.setAttribute("name",loc.getName());
		elem.setAttribute("type",loc.getLocationType().name());
		
		
		if (loc.hasChildLocations()){
			for (Location location : loc.getChildLocations()){
				try {
					elem.appendChild(generateXmlElement(doc,location));
				} catch (Exception exc){
					exc.printStackTrace();
				}
			}
		}
		
		return elem;
	}
	
	private Element generateXmlElement(Document doc, Driver driver){		
		return driver.toXmlElement(doc);
	}

	private Element generateXmlElement(Document doc, Service service) { return service.toXmlElement(doc);}

}
