package de.smahoo.homeos.remote;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.PropertyHistoryData;
import de.smahoo.homeos.utils.AttributeValuePair;
import de.smahoo.homeos.utils.xml.XmlUtils;

public class RemoteHistroyCacheLoader {
	
	private RemoteDeviceManager devManager = null;
	
	public RemoteHistroyCacheLoader(RemoteDeviceManager devManager){
		this.devManager = devManager;
	}
	
	public void loadHistoryCache(File file) throws IOException{	
    	if (!file.exists()) {
    		throw new IOException("File '"+file.getAbsolutePath()+"' doesn't exist.");
    	}
    	
    	Document doc = XmlUtils.loadXml(file);
        Element root = doc.getDocumentElement();
        
        if (!root.getTagName().equals("persistence")){
            	throw new IOException("File doesn't contain persistence data.");
        }
            
        NodeList nodeList = root.getChildNodes();
        Element tmp;
        String deviceId;
        for (int i = 0; i<nodeList.getLength(); i++){
           	if (nodeList.item(i) instanceof Element){           		
            		tmp = (Element)nodeList.item(i);
            		if (!tmp.getTagName().equalsIgnoreCase("device")){
            			throw new IOException("Xml file contains unexpected xml nodes. Expected node with tagname 'device' but found node with tagname '"+tmp.getTagName()+"'!");
            		}
            		if (!tmp.hasAttribute("deviceId")){
            			throw new IOException("Xml file contains unexpected xml nodes. Missing attribute 'deviceId' for xml node 'device'");
            		}
            		deviceId = tmp.getAttribute("deviceId");
            	
            		Element historyDataElem = getHistoryDataElement(tmp);
            		if (historyDataElem != null){
            			setHistoryCache(deviceId, historyDataElem);
            			
            		}
            	}
            }
	}
	
	protected Element getHistoryDataElement(Element elem){
		if (elem == null) return null;
		if (!elem.hasChildNodes()){
			return null;
		}
		
		Element historyDataElem = null;
		NodeList nodeList = elem.getChildNodes();	
		
		for(int i=0; i<nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				historyDataElem = (Element)nodeList.item(i);
				if (historyDataElem.getTagName().equalsIgnoreCase("historydata")){
					return historyDataElem;
				}
			}
		}
		
		return null;
		
	}
	
	protected void setHistoryCache(String deviceId, Element elem) throws IOException{
		Device dev = devManager.getDevice(deviceId);
		if (dev == null){
			return;
		}
		
		RemoteDevice rdev = (RemoteDevice)dev;
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		
		try {
			rdev.historyProcessor.getCache().setStart(formatter.parse(elem.getAttribute("start")));
			rdev.historyProcessor.getCache().setEnd(formatter.parse(elem.getAttribute("end")));
		} catch (Exception exc){
			IOException ioExc = new IOException("Unable to read start/end values for history data");
			ioExc.setStackTrace(exc.getStackTrace());
			throw ioExc;
		}
		
		Element tmp;		
		
		NodeList nodeList = elem.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			if (nodeList.item(i) instanceof Element){
				tmp = (Element)nodeList.item(i);
				if (tmp.getTagName().equalsIgnoreCase("entry")){ 
					rdev.historyProcessor.getCache().addData(generatePropertyHistoryData(tmp,dev));
				}
			}
		}
	}
	
	protected PropertyHistoryData generatePropertyHistoryData(Element elem, Device device) throws IOException{
		Date timestamp = null;
		boolean isOn = false;
		boolean isAvailable = false;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		try {
			timestamp = formatter.parse(elem.getAttribute("timestamp"));
			isOn = Boolean.parseBoolean(elem.getAttribute("isOn"));
			isAvailable = Boolean.parseBoolean(elem.getAttribute("isAvailable"));
		} catch (Exception exc){
			IOException ioExc = new IOException("Unable to read default values for property history entry");
			ioExc.setStackTrace(exc.getStackTrace());
			throw ioExc;
		}
		
		PropertyHistoryData phd = new PropertyHistoryData(timestamp, device, isOn, isAvailable);
		
		NamedNodeMap map = elem.getAttributes();
		for (int i = 0; i<map.getLength(); i++){
			if (   (!(map.item(i).getNodeName().equalsIgnoreCase("timestamp"))) &&
				   (!(map.item(i).getNodeName().equalsIgnoreCase("isOn"))) &&
				   (!(map.item(i).getNodeName().equalsIgnoreCase("isAvailable")))){
				
				phd.addAttributeValuePair(new AttributeValuePair(map.item(i).getNodeName(),map.item(i).getNodeValue()));				
			}
				
		}
		
		return phd;
	}
}
