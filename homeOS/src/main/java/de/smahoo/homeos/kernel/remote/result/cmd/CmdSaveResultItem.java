package de.smahoo.homeos.kernel.remote.result.cmd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;

public class CmdSaveResultItem extends RemoteResultItem{

	protected String name;
	protected String filename;
	
	
	public CmdSaveResultItem(boolean success, String message){
		setSuccess(success);
		setMessage(message);
	}
	
	public CmdSaveResultItem(String name, String filename){
		this.name = name;
		this.filename = filename;
		setSuccess(true);
	}	
	
	@Override
	public Element generateElement(Document doc) {	
		Element result = doc.createElement("save");		
		if (!isSuccess()){
			result.appendChild(this.generateErrorElement(doc));
			return result;
		}
		result.setAttribute("name",name);
		result.setAttribute("filename",filename);
		return result;
	}
	
}
