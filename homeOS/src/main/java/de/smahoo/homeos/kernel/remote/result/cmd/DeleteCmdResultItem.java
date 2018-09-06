package de.smahoo.homeos.kernel.remote.result.cmd;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.DeleteDeviceResultItem;
import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

public class DeleteCmdResultItem extends RemoteResultItem{
	
	private List<DeleteDeviceResultItem> delListItems;
	
	public DeleteCmdResultItem(){
		delListItems = new ArrayList<DeleteDeviceResultItem>();
	}
	
	public void addResultItem(DeleteDeviceResultItem item){
		delListItems.add(item);
	}

	public Element generateElement(Document doc){
		Element elem = doc.createElement("delete");
		for (DeleteCmdResultItem item : delListItems){
			elem.appendChild(item.generateElement(doc));
		}
		return elem;
	}
	
}
