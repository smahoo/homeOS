package de.smahoo.homeos.remote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.HistoryDataCache;
import de.smahoo.homeos.device.PropertyHistoryData;
import de.smahoo.homeos.remote.connection.RemoteConnection;
import de.smahoo.homeos.utils.AttributeValuePair;
import de.smahoo.homeos.utils.xml.XmlUtils;

public class RemoteHistoryProcessor {
	
	private RemoteConnection connection; 
	private HistoryDataCache cache;
	
	
	public RemoteHistoryProcessor(RemoteConnection connection){
		setConnection(connection);
		cache = new HistoryDataCache();
	}
	
	public void setConnection(RemoteConnection connection){
		this.connection = connection;
	}
	
	public HistoryDataCache getCache(){
		return cache;
	}
	
	protected boolean isCacheUp2Date(RemoteDevice device){
		boolean result = false;
		
		if (!cache.isEmpty()){
			result = cache.getEnd().after(device.getConnectionDate());
		}
		
		return result;
	}
	
	public void updateCache(PropertyHistoryData phd){
		if (cache == null) return;
		if (phd == null) return;
		cache.addData(phd);
		if (phd.getTimeStamp() == null) return;
		if (cache.getEnd() == null){
			cache.setEnd(phd.getTimeStamp());
			if (cache.getStart() == null){
				cache.setStart(phd.getTimeStamp());
			}
			return;
		}
		if (phd.getTimeStamp().after(cache.getEnd())){
			cache.setEnd(phd.getTimeStamp());
		}
	}
	
	public List<PropertyHistoryData> getHistoryData(Device device, Date start, Date end){
		// FIXME -> Im Cache werden sich nur start und endzeiten gemerkt. Lücken 
		//			wenn zum Beispiel einmal 12-14 Uhr gecached und dann 15-16 uhr, entsteht eine Lücke von 14-15 uhr. Diese muss bei einem 
		// 			Abruf von 12-16 uhr gefüllt werden. 
		List<PropertyHistoryData> result = new ArrayList<PropertyHistoryData>();
		
		if (!cache.isEmpty()){	
			
			if ((start.compareTo(cache.getStart()) < 0) || (end.compareTo(cache.getEnd())> 0)){			
				if (start.compareTo(cache.getStart())<0){
					List<PropertyHistoryData> tmp =  loadHistoryData(device, start, cache.getStart());
					cache.addData(tmp);		
					cache.setStart(start);
				}
				
				
				// Wenn Enddatum vom Cache kleiner als Abfrage-Enddatum
				if (end.compareTo(cache.getEnd()) > 0){
					RemoteDevice rd = (RemoteDevice)device;
					if (!isCacheUp2Date(rd)){					
						List<PropertyHistoryData> tmp =  loadHistoryData(device, cache.getEnd(),end);
						cache.addData(tmp);
						cache.setEnd(end);					
					}
					
				}
			}
			// if cache.hasGaps(start,end){
			// 		for (Gap gap : cache.getGaps()) {
			//			fill gap;
			// }
			
			return cache.getData(start,end);
			
		} else {
			List<PropertyHistoryData> tmp =  loadHistoryData(device, start, end);
			if (tmp != null){
				cache.addData(tmp);
				cache.setStart(start);
				cache.setEnd(end);
				result.addAll(tmp);
			}
			
			
		}
		return result;
	}
	
	protected List<PropertyHistoryData> loadHistoryData(Device device, Date start, Date end){
		String cmd = RemoteCommunicationProcessor.generateHistoryRequestCommand(device, start, end);
		String response = null;
		try {
		 response = connection.sendCommand(cmd);
		} catch (Exception exc){
			exc.printStackTrace();
			// FIXME throw exception
			return null;
		}
		
		Document doc = XmlUtils.parseDoc(response);
		Element root;
		if (doc != null) {			
			root = doc.getDocumentElement();
			
			if (root.getTagName().equalsIgnoreCase("requestResult")){
				// FIXME: current version of homeos (0.2.25) responses with success=false even the request was successfull
				//if (!root.getAttribute("success").equalsIgnoreCase("true")){
					// something went wrong			
				//	return null;
			//	}	
				
				NodeList nodelist = root.getChildNodes();
				Element tmp = null;
				for (int i=0; i<nodelist.getLength(); i++){
					if (nodelist.item(i) instanceof Element){
						tmp = (Element)nodelist.item(i);
						if (tmp.getTagName().equalsIgnoreCase("historydata")){
							if (tmp.hasChildNodes()){
								NodeList devicelist = tmp.getChildNodes();
								for (int n=0; n < devicelist.getLength(); n++){
									if (devicelist.item(n) instanceof Element){
										Element deviceElement = (Element)devicelist.item(n);
										return transformData(device, deviceElement.getChildNodes());		
									}					
								
								}
							}	
						}
					}
				}				
			}
		}		
		return null;
	}
	
	protected PropertyHistoryData getPropertyHistoryData(Device device, Element elem){
		Date timestamp;
		PropertyHistoryData  phd;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
		try {			
			timestamp = formatter.parse(elem.getAttribute("timestamp"));				
			phd = new PropertyHistoryData(timestamp, device, Boolean.parseBoolean(elem.getAttribute("isOn")),Boolean.parseBoolean(elem.getAttribute("isAvailable")));
			NamedNodeMap attr = elem.getAttributes();
			for (int i = 0; i<attr.getLength(); i++){
				if (!(attr.item(i).getNodeName().equalsIgnoreCase("isOn"))
						&&(!attr.item(i).getNodeName().equalsIgnoreCase("isAvailable"))
						&&(!attr.item(i).getNodeName().equalsIgnoreCase("timestamp"))){
					phd.addAttributeValuePair(new AttributeValuePair(attr.item(i).getNodeName(), attr.item(i).getNodeValue()));
				}
			}			
			return phd;
		} catch (Exception exc){
			exc.printStackTrace();
		}
		return null;
	}
	
	protected List<PropertyHistoryData> transformData(Device device, NodeList nodelist){
		List<PropertyHistoryData> result = new ArrayList<PropertyHistoryData>();
		Element tmp;		
		for (int i = 0; i<nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				tmp = (Element)nodelist.item(i);
				PropertyHistoryData phd = getPropertyHistoryData(device, tmp);
				if (phd != null){
					result.add(phd);
				}
			}
		}		
		return result;
	}
	
	
}
