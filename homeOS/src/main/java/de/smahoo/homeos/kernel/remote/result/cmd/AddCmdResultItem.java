package de.smahoo.homeos.kernel.remote.result.cmd;



import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;


public class AddCmdResultItem extends RemoteResultItem{
	
	List<RemoteResultItem> itemList;
	
	
	@Override
	public boolean isSuccess(){
		boolean success = true;
		
		if (itemList != null){
			for (RemoteResultItem item : itemList){
				success = success && item.isSuccess();
			}
		}
		return success;
	}
	
	
	public void addResultItem(RemoteResultItem item){
	
		if (itemList == null){
			itemList = new ArrayList<RemoteResultItem>();
		}
		
		itemList.add(item);
	}

	public Element generateElement(Document doc){
		Element result = doc.createElement("add");
		if (isSuccess()){
			result.setAttribute("success","true");
		} else {
			result.setAttribute("success", "false");
			result.appendChild( generateErrorElement(doc));
		}
		
		if (itemList != null){
			for (RemoteResultItem item : itemList){
				result.appendChild(item.generateElement(doc));
			}
		}
		
		return result;
	}
}
