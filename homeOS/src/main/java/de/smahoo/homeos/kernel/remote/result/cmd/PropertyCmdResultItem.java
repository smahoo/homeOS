package de.smahoo.homeos.kernel.remote.result.cmd;

import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.kernel.remote.SetPropertyResultItem;

public class PropertyCmdResultItem extends SetCmdResultItem {

	protected List<SetPropertyResultItem> props = null;
	
	public void addProperty(String name, String value, boolean success){
		addProperty(name,value,success, null);
	}
	
	public void addProperty(String name, String value, boolean success, String message){
		if (props == null){
			props = new ArrayList<SetPropertyResultItem>();
		}
		props.add(new SetPropertyResultItem(name, value, success, message));
	}
	
}
