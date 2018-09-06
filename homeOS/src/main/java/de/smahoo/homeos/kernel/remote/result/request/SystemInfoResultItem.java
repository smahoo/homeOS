package de.smahoo.homeos.kernel.remote.result.request;

import java.text.NumberFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

public class SystemInfoResultItem extends RemoteResultItem {
	
	public SystemInfoResultItem(){
		this.setSuccess(true);
	}
	
	
	 public Element generateElement(Document doc){
		 Element result = doc.createElement("homeos");
		 result.setAttribute("version",HomeOs.getVersion());
		 
		 NumberFormat format = NumberFormat.getInstance();
		 
		 Element tmp = doc.createElement("freediskspace");
		 tmp.setTextContent(format.format(HomeOs.getFreeDiskSpace()));		 
		 result.appendChild(tmp);
		 		
		 tmp = doc.createElement("freediskspace");
		 tmp.setTextContent(format.format(Runtime.getRuntime().freeMemory()));		 
		 result.appendChild(tmp);
				 		 		 
		 return result;
	 }
}
