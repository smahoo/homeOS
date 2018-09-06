package de.smahoo.homeos.testing.gui.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.smahoo.homeos.common.Event;

public class EventTableModel implements TableModel{

	List<Event> eventList;
	String[] columnNames;
	TableModelListener eventListener;
	
	public EventTableModel(){
		eventList = new ArrayList<Event>();
		setColumnNames();	
	}
		
	protected void setColumnNames(){
		columnNames  = new String[4];
		columnNames[0]="Timestamp";
		columnNames[1]="EventType";
		columnNames[2]="Additional";
		columnNames[3]="Description";	
	}
	
	
	  public void addEvent(Event event){
		  eventList.add(event);
		  if (eventListener != null){
			  eventListener.tableChanged(new TableModelEvent(this));  
		  }
		  
	  }
	
	  public int getRowCount(){
		  return eventList.size();
	  }
	  
	  
	  public int getColumnCount(){
		  return columnNames.length;
	  }
	  
	  
	  public String getColumnName(int arg0){
		  return columnNames[arg0];
	  }

	  public Class<String> getColumnClass(int arg0){
		  return String.class;
	  }
	  
	  
	  public boolean isCellEditable(int arg0, int arg1){
		  return false;
	  }
	  
	  
	  public Object getValueAt(int arg0, int arg1){
		  Event evnt = eventList.get(arg0);
		  switch(arg1){
		  	case 0:	return "["+(new SimpleDateFormat("HH:mm:ss")).format(evnt.getTimeStamp())+"]";
		  	case 1: return evnt.getEventType().name();
		  	case 2: return evnt.toString();
		  	case 3: return evnt.getDescription();
		  }
		  return null;
	  }
	  
	  
	  public void setValueAt(java.lang.Object arg0, int arg1, int arg2){
		  //
	  }
	  
	  
	  public void addTableModelListener(TableModelListener arg0){
		  eventListener = arg0;
	  }
	  
	  
	  public void removeTableModelListener(TableModelListener arg0){
		  
	  }
	
}
