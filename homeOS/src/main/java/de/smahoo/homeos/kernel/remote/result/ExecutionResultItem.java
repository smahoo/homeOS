package de.smahoo.homeos.kernel.remote.result;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class ExecutionResultItem extends RemoteResultItem{
	private String function	= null;	
	private String device = null;	
	
	
	public ExecutionResultItem(String msg){
		setMessage(msg);
	}
	
	public ExecutionResultItem(String device, String function){
		this.setSuccess(true);
		this.device = device;
		this.function = function;
	}
	
	public ExecutionResultItem(String device, String function,boolean success, String msg){		
		this(device, function);		
		setMessage(msg);
		setSuccess(success);
	}	
		
	public String getFunction(){
		return function;
	}
	
	public String getDevice(){
		return device;
	}
	
	public Element generateElement(Document doc){
		if (doc == null) return null;
		Element tmp = doc.createElement("executed");
		tmp.setAttribute("function",getFunction().toString());	
		if (device != null){
			tmp.setAttribute("deviceId",getDevice());
		}
		if (isSuccess()){
			tmp.setAttribute("success","true");
		} else {
			tmp.setAttribute("success", "false");
			tmp.appendChild( generateErrorElement(doc));
		}
		
		return tmp;
	}
	
}
