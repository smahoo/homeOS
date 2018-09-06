package de.smahoo.homeos.kernel.remote.result.cmd;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;


public class SetCmdResultItem extends RemoteResultItem{
		
	List<SetCmdResultItem> items = null;
	
	
	
	public void addChangeCmdResultItem(SetCmdResultItem item){
		if (items == null){
			items = new ArrayList<SetCmdResultItem>();
		}
		items.add(item);
	}
	
	
	public Element generateElement(Document doc){
		Element element = doc.createElement("change");
		if (items != null){
			for (SetCmdResultItem item : items){
				element.appendChild(item.generateElement(doc));
			}
		}
		return element;
	}
}
