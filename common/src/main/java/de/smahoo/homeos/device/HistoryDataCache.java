package de.smahoo.homeos.device;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class HistoryDataCache {
	
	protected List<PropertyHistoryData> historyCache = null;

	
	protected Date start = null;
	protected Date end = null;	
	
	public HistoryDataCache(){
		historyCache = new ArrayList<PropertyHistoryData>();
	}

	
	
	public void addData(PropertyHistoryData data){
		if (data == null){
			return;
		}
		
		// FIXME: check whether argument is complete or not
		
		if (historyCache.isEmpty()){
			historyCache.add(data);
			return;
		}
		
		
		PropertyHistoryData tmp;
		for (int i = 0; i < historyCache.size(); i++){
			tmp = historyCache.get(i);
			if (tmp.getTimeStamp().compareTo(data.getTimeStamp())>0){
				historyCache.add(i,data);
				return;
			}
		}
		
		historyCache.add(data);
	}
	
	public void addData(List<PropertyHistoryData> dataList){		
		if (dataList == null){
			return;
		}		
		
		for (PropertyHistoryData phd : dataList){
			addData(phd);
		}
	}
	
	public boolean isEmpty(){
		return historyCache.isEmpty();
	}
	
	public Date getStart(){
		return start;
	}
	
	public void setStart(Date start){
		this.start = start;
	}
	
	public Date getEnd(){
		return end;
	}
	
	public void setEnd(Date end){
		this.end = end;
	}
	
	public Date getFirstTimeStamp(){
		if (isEmpty()){
			return null;
		}
		return historyCache.get(0).getTimeStamp();
	}
	
	public Date getLastTimeStamp(){
		if (isEmpty()){
			return null;
		}
		return historyCache.get(historyCache.size()-1).getTimeStamp();
	}
	
	public List<PropertyHistoryData> getData(Date start, Date end){
		List<PropertyHistoryData> list = new ArrayList<PropertyHistoryData>();
		for (PropertyHistoryData phd : historyCache){
			if (phd.getTimeStamp().compareTo(end)> 0){
				return list;
			}
			if (phd.getTimeStamp().compareTo(start)>=0){
				list.add(phd);
			}
		}
		
		return list;
	}
	
	public List<PropertyHistoryData> getData(){
		List<PropertyHistoryData> list = new ArrayList<PropertyHistoryData>();
		for (PropertyHistoryData phd : historyCache){
			list.add(phd);
		}
		return list;
	}
}
