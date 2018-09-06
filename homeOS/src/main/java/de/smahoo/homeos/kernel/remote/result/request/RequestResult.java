package de.smahoo.homeos.kernel.remote.result.request;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.kernel.remote.result.RemoteResult;

public class RequestResult extends RemoteResult{
	
	
	public Document toXmlDocument(){
		Document result = null;
		DocumentBuilder docBuilder;
		DocumentBuilderFactory docBFac;

		try {
			docBFac = DocumentBuilderFactory.newInstance();
			docBuilder = docBFac.newDocumentBuilder();			
			result = docBuilder.newDocument();
			
		} catch (Exception exc){
			exc.printStackTrace();
		}
		result.setXmlVersion("1.1");
		Element root = result.createElement("requestResult");
		root.setAttribute("systemId", HomeOs.getInstance().getSystemid());
		if (isSuccess()){
			root.setAttribute("success","true");	
		} else {
			root.setAttribute("success","false");
		}
		
		if (hasMessage()){
			root.setAttribute("message",getMessage());
		}
		
		appendRemoteResultItems(result, root);
		result.appendChild(root);
		return result;
	}
}
