package de.smahoo.homeos.kernel.remote.result.request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.kernel.remote.result.RemoteResultItem;


public class HistoryResultListItem extends RemoteResultItem{

	private List<RemoteResultItem> items;
	private List<String> messages;
	private Date start = null;
	private Date stop = null;
	
	
	public HistoryResultListItem(){
		items = new ArrayList<RemoteResultItem>();
		messages = new ArrayList<String>();
	}
	
	public void addResultItem(RemoteResultItem item){
		items.add(item);	
		setSuccess(this.isSuccess()&&item.isSuccess());
		if (item.hasMessage()){
			messages.add(item.getMessage());
		}
	}
	
	public void setStartTime(Date start){
		this.start = start;
	}
	
	public void setEndTime(Date end){
		this.stop = end;
	}
	
	@Override
	protected Element generateErrorElement(Document doc){
		if (doc == null) return null;
		Element tmp = doc.createElement("error");
		for (String msg : messages){
			Element message = doc.createElement("message");
			message.setTextContent(msg);
			tmp.appendChild(message);
		}
		return tmp;
	}
	
	@Override
	public void setMessage(String message){
		messages.add(message);
	}
	
	@Override
	public boolean hasMessage(){
		return !messages.isEmpty();
	}
	
	@Override
	public String getMessage(){
		String res = "";
		for (String msg : messages){
			res = res+" "+msg;
		}
		return res;
	}
	
	@Override
	public Element generateElement(Document doc){
		if (!isSuccess()){
			return generateErrorElement(doc);
		}
		Element elem = doc.createElement("historydata");
		DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");		    
		if (start != null){
			elem.setAttribute("start", formatter.format(start));
		}
		if (stop != null){
			elem.setAttribute("end",formatter.format(stop));
		}
		
		
		for (RemoteResultItem item : items){
			elem.appendChild(item.generateElement(doc));		
		}
		
		return elem;
	}
	
	
	
}
