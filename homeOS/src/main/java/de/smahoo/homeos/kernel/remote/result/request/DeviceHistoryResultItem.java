package de.smahoo.homeos.kernel.remote.result.request;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.PropertyHistoryData;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;
import de.smahoo.homeos.utils.AttributeValuePair;

public class DeviceHistoryResultItem extends RemoteResultItem{
	
	private Device device;
	
	private  List<PropertyHistoryData> historyData;
	
	public DeviceHistoryResultItem(){
		
	}
	
	public DeviceHistoryResultItem(Device device, List<PropertyHistoryData> historyData){
		this.device = device;
		this.historyData = historyData;
	}	
	
	
	@Override
	public Element generateElement(Document doc){	
		if (!isSuccess()){
			return generateErrorElement(doc);
		}
		Element elem = doc.createElement("device");
		elem.setAttribute("deviceId",device.getDeviceId());		
		if (historyData != null){
			appendHistoryDataEntries(doc, elem, historyData);
		}
		return elem;
	}
	
	private void appendHistoryDataEntries(Document doc, Element elem, List<PropertyHistoryData> data){		
		Element tmp;		
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
		for (PropertyHistoryData hd : data){
			tmp = doc.createElement("entry");			
		//	tmp.setAttribute("value",""+hd.getValue());
			tmp.setAttribute("timestamp",formatter.format(hd.getTimeStamp()));
			tmp.setAttribute("isOn",""+hd.isOn());
			tmp.setAttribute("isAvailable",""+hd.isAvailabe());
			for (AttributeValuePair avp : hd.getValues()){
				tmp.setAttribute(avp.getAttribute(),avp.getValue());
			}
			elem.appendChild(tmp);
		}
		
	}
}
