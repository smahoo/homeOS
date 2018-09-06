package de.smahoo.homeos.kernel.remote.result;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class RemoteResultItem {
	private String msg = null;	
	private boolean success = false;
	
	public void setMessage(String message){
		this.msg = message;
	}
	
	public boolean hasMessage(){
		return msg!=null;
	}
	
	public String getMessage(){
		return msg;
	}
	
	public void setSuccess(boolean success){
		this.success = success;
	}
	
	public boolean isSuccess(){
		return success;
	}
	
	protected Element generateErrorElement(Document doc){
		if (doc == null) return null;
		Element tmp = doc.createElement("error");
		Element message = doc.createElement("message");
		message.setTextContent(msg);
		tmp.appendChild(message);
		return tmp;
	}
	
	abstract public Element generateElement(Document doc);
}
