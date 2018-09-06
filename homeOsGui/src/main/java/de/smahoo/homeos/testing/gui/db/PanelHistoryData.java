package de.smahoo.homeos.testing.gui.db;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JPanel;


import de.smahoo.homeos.device.PropertyHistoryData;
import de.smahoo.homeos.utils.AttributeValuePair;

import javax.swing.JScrollPane;
import java.awt.CardLayout;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PanelHistoryData extends JPanel{

	List<PropertyHistoryData> data = null;
	private JTable table;
	
	public PanelHistoryData(){
		
		init();
	}
	
	public void setData(List<PropertyHistoryData> data){
		this.data = data;
		
		if (data == null){

			return;
		}
		if (data.isEmpty()){

			return;
		}
		buildTableModel();
		
		String dataStr = "";
		String line = "";
		SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<AttributeValuePair> values;
		for (PropertyHistoryData d : data){
			line = formatter.format(d.getTimeStamp())+"  isOn="+d.isOn()+" isAvailable="+d.isAvailabe();
			for (AttributeValuePair avp : d.getValues()){
				line = line + " "+avp.getAttribute()+"="+avp.getValue();
			}
			dataStr = dataStr + "\r\n"+line;
		}
		
		
	}
	
	private void buildTableModel(){
		DefaultTableModel model = new DefaultTableModel();
		SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String[] columns = {"timestamp","isOn","isAvailable"};
		if (data != null){
			if (!data.isEmpty()){
				List<String> valueNames = data.get(0).getPropertyNames();
				if (!valueNames.isEmpty()){
					int columCnt = 3+valueNames.size();
					columns = new String[columCnt];
					columns[0] = "timestamp";
					columns[1] = "isOn";
					columns[2] = "isAvailable";
					int i = 2;
					for (String name : valueNames){
						i++;
						columns[i] = name;
					}
					model.setColumnIdentifiers(columns);
					String[] row;
					for (PropertyHistoryData d : data){						
						row =  new String[columCnt];
						row[0] = formatter.format(d.getTimeStamp());
						row[1] = "" + d.isOn();
						row[2] = "" + d.isAvailabe();
						i=2;
						for (String name : valueNames){
							i++;
							row[i]=""+d.getValue(name);
						}
						model.addRow(row);
					}
					
				}							
				table.setModel(model);				
				return;
			}
		}
		model.setColumnIdentifiers(columns);
		table.setModel(model);
	}
	
	private void init(){
		setLayout(new CardLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "name_9390281550588");
		
		table = new JTable();
		scrollPane.setViewportView(table);
	}
	
}
