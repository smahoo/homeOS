package de.smahoo.homeos.kernel.remote.result.request;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

public class PhysicalDeviceItem extends RemoteResultItem{

	private PhysicalDevice device;
	
	public PhysicalDeviceItem(PhysicalDevice device){
		this.device = device;
		this.setSuccess(true);
	}
	
	
	private Element generateDriverElement(Document doc){
		Element elem = doc.createElement("driver");
		elem.setAttribute("company", device.getDriver().getCompanyName());
		elem.setAttribute("version",device.getDriver().getVersion());
		elem.setAttribute("name",device.getDriver().getName());
		return elem;
	}
	
	private Element generatePropertyElement(Document doc, DeviceProperty property){
		Element elem = doc.createElement("property");
		elem.setAttribute("name",property.getName());
		elem.setAttribute("unit",property.getUnit());		
		elem.setAttribute("valueType",""+property.getPropertyType());
		if (property.isValueSet()){
			elem.setAttribute("value",""+property.getValue());
		}
		return elem;
	}
	
	private Element generatePropertiesElement(Document doc){
		Element elem = doc.createElement("properties");
		
		List<DeviceProperty> pList = device.getPropertyList();
		for (DeviceProperty prop : pList){
			elem.appendChild(generatePropertyElement(doc,prop));
		}				
		return elem;
	}
	
	private Element generateFunctionParameterElement(Document doc, FunctionParameter parameter){
		Element elem = doc.createElement("parameter");
		elem.setAttribute("name",parameter.getName());
		elem.setAttribute("valuetype",""+parameter.getPropertyType());		
		return elem;
	}
	
	private Element generateFunctionElement(Document doc, PhysicalDeviceFunction function){
		Element elem = doc.createElement("function");
		elem.setAttribute("name",function.getName());
		if (function instanceof ParameterizedDeviceFunction){
			ParameterizedDeviceFunction pf = (ParameterizedDeviceFunction)function;
			List<FunctionParameter> fpList = pf.getNeededParameter();
			for (FunctionParameter fp : fpList){
				elem.appendChild(generateFunctionParameterElement(doc,fp));
			}
		}
		return elem;
	}
	
	private Element generateFunctionsElement(Document doc){
		Element elem = doc.createElement("functions");
		
		List<PhysicalDeviceFunction> fList = device.getDeviceFunctions();
		for (PhysicalDeviceFunction function : fList){
			elem.appendChild(generateFunctionElement(doc,function));
		}		
		return elem;
	}
	
	public Element generateElement(Document doc){
		Element elem = doc.createElement("device");
		elem.setAttribute("deviceId",device.getDeviceId());
		elem.setAttribute("isavailable", ""+device.isAvailable());
		elem.setAttribute("ison",""+device.isOn());
		elem.appendChild(generateDriverElement(doc));
		elem.appendChild(generatePropertiesElement(doc));
		elem.appendChild(generateFunctionsElement(doc));
		
		return elem;
	
	}
}
