package de.smahoo.homeos.remote.physical;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.Function;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.homeos.remote.RemoteDevice;

public class RemotePhysicalFactory {

	

	public RemotePhysicalDevice generatePhysicalDevice(RemoteDevice device, Element element){
		
		if (element == null) return null;
		if (!element.getTagName().equalsIgnoreCase("device")) return null;
		
		RemotePhysicalDevice dev = new RemotePhysicalDevice();
		dev.device = device;
		
		
		
		Element tmp;
		NodeList nodelist = element.getElementsByTagName("functions");	
		if (nodelist.getLength() == 1){
			tmp = (Element)nodelist.item(0);
			dev.functions = generateFunctions(tmp.getChildNodes());
		}
		
		
		nodelist = element.getElementsByTagName("properties");	
		if (nodelist.getLength() == 1){
			tmp = (Element)nodelist.item(0);
			dev.properties = generateProperties(tmp.getChildNodes());
		}
		
		
		nodelist = element.getElementsByTagName("driver");
		if (nodelist.getLength() == 1){
			tmp = (Element)nodelist.item(0);
			setDriverInformation(dev,tmp);
		}
		
		return dev;
	}
	
	private void setDriverInformation(RemotePhysicalDevice device, Element elem){
		
		if (!elem.getTagName().equalsIgnoreCase("driver")){
			return;
		}
		device.driverCompany = elem.getAttribute("company");
		device.driverName = elem.getAttribute("name");
		device.driverVersion = elem.getAttribute("version");
	}
	
	private List<Property> generateProperties(NodeList nodeList){
		List<Property> properties = new ArrayList<Property>();
		//<property name="actuator" unit="%" valueType="java.lang.Double"/>
		Property tmp;
		Element elem;
		for (int i= 0; i<nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				elem = (Element)nodeList.item(i);			
				tmp = new Property(PropertyType.valueOf(elem.getAttribute("valueType")), elem.getAttribute("name"));
				if (elem.hasAttribute("unit")){
					tmp.setUnit(elem.getAttribute("unit"));
				}
				if (elem.hasAttribute("value")){
					tmp.setValue(elem.getAttribute("value"));
				}
				properties.add(tmp);
			}
			
		}
				
		return properties;
	}
	
	private List<Function> generateFunctions(NodeList nodeList){
		List<Function> list = new ArrayList<Function>();
		
		for (int i=0; i<nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				list.add(generateFunction((Element)nodeList.item(i)));
			}
		}
		
		return list;
	}
	
	private Function generateFunction(Element elem){
		RemotePhysicalFunction function = new RemotePhysicalFunction();
		
		
		
		function.name = elem.getAttribute("name");
		
		return function;
	}
	
}
