package de.smahoo.homeos.kernel.remote.result;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.request.DeviceListRequestResultItem;

public abstract class RemoteResult {
	private List<RemoteResultItem> resultItems;
	List<String> errors;
	private String msg = null;
	private boolean success;
	
	public RemoteResult(){
		resultItems = new ArrayList<RemoteResultItem>();
		errors= new ArrayList<String>();
	}
	
	public void addResultItem(RemoteResultItem resultItem){
		resultItems.add(resultItem);
		success = success && resultItem.isSuccess();
		if (!resultItem.isSuccess()){
			errors.add(resultItem.getMessage());		
		}
	}
	
	public boolean hasMessage(){
		return msg != null;
	}
	
	public String getMessage(){
		return msg;
	}
	public void setMessage(String msg){
		this.msg = msg;
	}
	
	public String toString(){
		String result="";
		
		if (! isSuccess()){
			result = result + "Failure";		
			if (this.msg != null) {
				result = result +" ("+ this.msg +")";
			} else {
					if (!resultItems.isEmpty()){
						RemoteResultItem res = resultItems.get(0);
						result = result + " ("+res.getMessage()+")";
					}
			}			
		} else result = result + "Succes";
		
		return result;
	}
	
	public boolean isSuccess(){
		return success;
	}
	
	public void setSuccess(boolean success){
		this.success = success;
	}
	
	protected void appendRemoteResultItems(Document doc, Element root){
		if (!resultItems.isEmpty()){
			for (RemoteResultItem item : resultItems){
				//if (item.isSuccess()){
				try {
					root.appendChild(item.generateElement(doc));
				} catch (Exception exc){				
					exc.printStackTrace();
				}
				//} else {
				//	root.appendChild(item.generateErrorElement(doc));
				//}
			}
		}
	}
	
	abstract public Document toXmlDocument();
}
