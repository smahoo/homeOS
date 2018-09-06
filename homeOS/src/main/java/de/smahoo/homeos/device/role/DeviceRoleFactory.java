package de.smahoo.homeos.device.role;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceManager;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.roles.LampRole;
import de.smahoo.homeos.device.roles.SensorHumidityRole;
import de.smahoo.homeos.device.roles.SensorTemperatureRole;
import de.smahoo.homeos.device.roles.TelevisionRole;
import de.smahoo.homeos.device.roles.VentilationRole;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.property.Property;

public class DeviceRoleFactory {

	
	private HashMap<String,Integer> ids;
	private static DeviceRoleFactory instance = null;
	
	protected DeviceRoleFactory(){
		ids = new HashMap<String,Integer>();
	}
	
	public static DeviceRoleFactory getInstance(){
		if (instance == null){
			instance = new DeviceRoleFactory();
		}
		return instance;
	}
	
	public void generateRolesFromXml(final String filename){
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
         
         generateRolesFromXml(doc.getDocumentElement());
	}
	
	
	public void generateRolesFromXml(final Element e){
		if (e == null) return;
		NodeList nodes = e.getElementsByTagName("devicerole");
		if (nodes.getLength() <= 0) return;
		
		int len = nodes.getLength();
		Node tmpNode;	
		DeviceRole tmpRole;
		DeviceManager devManager = HomeOs.getInstance().getDeviceManager();		
		for (int i=0; i<len; i++){
			tmpNode = nodes.item(i);
			if (tmpNode instanceof Element){
				tmpRole = generateDeviceRoleFromXml((Element)tmpNode);
				if (tmpRole != null) {
					devManager.addDeviceRole(tmpRole);
				}
			}
		}
	}
	
	public DeviceRole generateDeviceRoleFromXml(Element e){
		
		if (!e.hasAttribute("type")) return null;	// FIX: throw relative parsing exception		
		
		String roleType = e.getAttribute("type");
		String deviceId = e.getAttribute("deviceId");
		
		DeviceType type = getDeviceType(roleType);// FIX: throw relative parsing exception		
		if (type == null) return null; 
		
		DeviceManager devManager = HomeOs.getInstance().getDeviceManager();		
		PhysicalDevice device = devManager.getPhysicalDevice(deviceId);
		
		/*
		 * when device is null at this point, each function and property binding needs an attribute "deviceId" to bind relative object successfully 
		 */
		
				
		// generatePropertyBindings
		List<PropertyBinding> propertyBindings = getPropertyBindings(device,e.getElementsByTagName("propertybinding"));
		List<FunctionBinding> functionBindings = generateFunctionBindings(device,e.getElementsByTagName("functionbinding"));
		
		DeviceRole devRole = null;
		
		switch(type) {
			case LAMP: devRole = generateLamp(propertyBindings, functionBindings); break;
			case VENTILATION: devRole = generateFan(propertyBindings, functionBindings); break;
			case SENSOR_HUMIDITY: devRole = generateSensorHumidity(propertyBindings); break;
			case SENSOR_TEMPERATURE : devRole = generateSensorTemperature(propertyBindings); break;
			case TELEVISION: devRole = generateTv(propertyBindings, functionBindings); break;
		}
			
		
		if (devRole != null){			
			if (e.hasAttribute("name")){
				devRole.name = e.getAttribute("name");
			}
			if (e.hasAttribute("location")){
				devRole.assignLocation(e.getAttribute("location"));
			}
		}
		
		return devRole;
	}
	
	
	protected String generateId(String id){
		String result = id;
		int tmp = 0;
		if (ids.containsKey(id)){
			tmp = ids.get(id);
			tmp++;
			ids.put(id,tmp);
		} else {
			ids.put(id,tmp);			
		}
		result = id+tmp;		
		return result;
	}
	
	private List<PropertyBinding> getPropertyBindings(PhysicalDevice device, NodeList nodelist){
		List<PropertyBinding> properties= new ArrayList<PropertyBinding>();
		
		Node tmpNode;
		Element tmpElem;
		PropertyBinding pb;
		PhysicalDevice dev;
		
		for (int i=0; i< nodelist.getLength(); i++){
			tmpNode = nodelist.item(i);
			if (tmpNode instanceof Element){
				tmpElem = (Element)tmpNode;
				if (tmpElem.hasAttribute("deviceId")){
					dev = HomeOs.getInstance().getDeviceManager().getPhysicalDevice(tmpElem.getAttribute("deviceId"));
				} else {
					dev = device;
				}
				properties.add(new PropertyBinding(dev,tmpElem.getAttribute("roleProperty"), tmpElem.getAttribute("deviceProperty")));				
			}
		}		
		return properties;
	}
	
	private List<FunctionBinding> generateFunctionBindings(PhysicalDevice device, NodeList nodelist){
		List<FunctionBinding> functionBindings = new ArrayList<FunctionBinding>();		
		Node tmpNode;
		Element tmpElem;
		FunctionBinding tmpBinding;
		PhysicalDevice dev = null;
		//<functionbinding roleFunction="turnOn" deviceFunction="turnOn"/>
		for (int i=0; i< nodelist.getLength(); i++){
			tmpNode = nodelist.item(i);
			if (tmpNode instanceof Element){
				tmpElem = (Element)tmpNode;
				if (tmpElem.hasAttribute("deviceId")){
					dev = HomeOs.getInstance().getDeviceManager().getPhysicalDevice(tmpElem.getAttribute("deviceId"));
					if (dev == null){
						// Device with id not found
					}
				} else {
					dev = device;
				}
				if (dev == null){
					// au backe
					// Exception schmeissen oder Feler loggen! -> Kein Geraet zum binden gefunden
					
				}
				tmpBinding = new FunctionBinding(dev,tmpElem.getAttribute("roleFunction"),tmpElem.getAttribute("deviceFunction"));	
				if (tmpElem.hasChildNodes()){
					tmpBinding.addParameterBindings(getFunctionParameterBindings(tmpElem.getChildNodes()));
				}				
				functionBindings.add(tmpBinding);
			}
		}		
		return functionBindings;				
	}
	
	private List<ParameterBinding> getFunctionParameterBindings(NodeList list){
		List<ParameterBinding> parameterBindings = new ArrayList<ParameterBinding>();
		
		Node tmpNode;
		Element tmpElem;
		
		String roleParam;
		String devParam;
		
		for (int i = 0; i<list.getLength(); i++){
			tmpNode = list.item(i);
			if (tmpNode instanceof Element){
				tmpElem = (Element)tmpNode;
				if (tmpElem.hasAttribute("fixedParameter")){
					roleParam = "#"+tmpElem.getAttribute("fixedParameter");
				} else {
					roleParam = tmpElem.getAttribute("roleFunctionParameter");
				}
				devParam = tmpElem.getAttribute("deviceFunctionParameter");
				parameterBindings.add(new ParameterBinding(roleParam,devParam));
			}
		}
		
		
		return parameterBindings;
	}
	
	private DeviceType getDeviceType(String type){
		
		if (type.equalsIgnoreCase("LAMP")) return DeviceType.LAMP;
		if (type.equalsIgnoreCase("VENTILATOR")) return DeviceType.VENTILATION;
		if (type.equalsIgnoreCase("SENSOR_HUMIDITY")) return DeviceType.SENSOR_HUMIDITY;
		if (type.equalsIgnoreCase("SENSOR_TEMPERATURE")) return DeviceType.SENSOR_TEMPERATURE;	
		if (type.equalsIgnoreCase("TELEVISION")) return DeviceType.TELEVISION;
				
		return null;
	}
	
	private DeviceRole generateFan(List<PropertyBinding> propertyBindings, List<FunctionBinding> functionBindings){
		VentilationRole fan = new VentilationRole();
		PhysicalDeviceFunction df;
		RoleFunction rf;
		PhysicalDevice dev;
		for (FunctionBinding functionBinding : functionBindings){
			dev = functionBinding.getPhysicalDevice();
			df = dev.getFunction(functionBinding.getDeviceFunctionName());
			if (df != null){
				rf = fan.getRoleFunction(functionBinding.getRoleFunctionName());				
				rf.bindDeviceFunction(df);
				fan.addRoleFunction(rf);
			}		
		}		
		
		return fan;
	}
	
	private DeviceRole generateTv(List<PropertyBinding> propertyBindings, List<FunctionBinding> functionBindings){
		TelevisionRole tv = new TelevisionRole();
		PhysicalDeviceFunction df;
		RoleFunction rf;
		PhysicalDevice device = null;
		for (FunctionBinding functionBinding : functionBindings){
			device = functionBinding.getPhysicalDevice();
			df = device.getFunction(functionBinding.getDeviceFunctionName());
			if (df != null) {
				rf = tv.getRoleFunction(functionBinding.getRoleFunctionName());
				rf.bindDeviceFunction(df,functionBinding.getParameterBindings());
				((DeviceRole) tv).addDevice(device);
			}
		}
		
		return tv;
	}
	
	private DeviceRole generateLamp(List<PropertyBinding> propertyBindings, List<FunctionBinding> functionBindings){
		
		LampRole lamp = new LampRole();
		
		PhysicalDeviceFunction df;
		RoleFunction rf;
		PhysicalDevice device;
		for (FunctionBinding functionBinding : functionBindings){
			device = functionBinding.getPhysicalDevice();
			if (device != null){
				df = device.getFunction(functionBinding.getDeviceFunctionName());
				if (df != null){
					rf = lamp.getRoleFunction(functionBinding.getRoleFunctionName());				
					rf.bindDeviceFunction(df);		
					((DeviceRole) lamp).addDevice(device);
				}
			}
		}	
		return lamp;
	}
	
	private DeviceRole generateSensorHumidity(List<PropertyBinding> propertyBindings){		
		SensorHumidityRole sensor = new SensorHumidityRole();
		
		RoleProperty roleProperty;
		PhysicalDevice device = null;
		Property deviceProperty;
		for (PropertyBinding pb : propertyBindings){
			device = pb.getPhysicalDevice();
			roleProperty = sensor.getRoleProperty(pb.getRolePropertyName());
			deviceProperty = device.getProperty(pb.getDevicePropertyName());
			((DeviceRole) sensor).addDevice(device);
			roleProperty.bindProperty(deviceProperty);
		}		
		if (device != null){
			sensor.setName("Sensor Humdidity ("+device.getName()+")");
		} 
		return sensor;
	}
	
	private DeviceRole generateSensorTemperature(List<PropertyBinding> propertyBindings){		
		SensorTemperatureRole sensor = new SensorTemperatureRole();
		RoleProperty roleProperty;
		PhysicalDevice device = null;
		Property deviceProperty;
		for (PropertyBinding pb : propertyBindings){
			device = pb.getPhysicalDevice();
			roleProperty = sensor.getRoleProperty(pb.getRolePropertyName());
			deviceProperty = device.getProperty(pb.getDevicePropertyName());
			((DeviceRole) sensor).addDevice(device);
			roleProperty.bindProperty(deviceProperty);
		}		
		if (device != null){
			sensor.setName("Sensor Temperature ("+device.getName()+")");
		} 
		return sensor;
	}
	
	
	
	
}
