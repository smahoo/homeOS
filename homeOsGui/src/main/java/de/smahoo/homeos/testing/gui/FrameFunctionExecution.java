package de.smahoo.homeos.testing.gui;

import javax.swing.JFrame;

import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;

import javax.swing.JTable;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.awt.Dimension;

public class FrameFunctionExecution extends JFrame{
	private JTable table;
	protected ParameterizedDeviceFunction function;
	
	
	public FrameFunctionExecution(){
		getContentPane().setPreferredSize(new Dimension(400, 400));
		setPreferredSize(new Dimension(400, 400));
		
		table = new JTable();
		getContentPane().add(table, BorderLayout.CENTER);
		
		JPanel pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		JButton btnExecute = new JButton("Execute");
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		pnlBottom.add(btnExecute);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		pnlBottom.add(btnCancel);
		
		JPanel pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		this.setSize(400, 400);
	}
	
	protected void execute(){
		List<FunctionParameter> pList = ((ParameterTableModel)table.getModel()).getParameterList();
		try {
		   function.execute(pList);
		} catch (Exception exc){
			exc.printStackTrace();
		}
		this.setVisible(false);
	}
	
	public void executeFunction(ParameterizedDeviceFunction function){
		this.function = function;
		table.setModel(new ParameterTableModel(function.getNeededParameter()));
		this.setVisible(true);
	}	
	
	private class ParameterTableModel extends AbstractTableModel {
	    private String[] columnNames =  {"Name","Value"};
	    private Object[][] data;
	    private List<FunctionParameter> parameterList;
	    
	    public ParameterTableModel(List<FunctionParameter> parameterList){
	    	super();
	    	this.parameterList = parameterList;
	    	int dataCnt = parameterList.size();
	    	data = new Object[dataCnt][2];
			
			for (int i = 0; i<dataCnt; i++){
				data[i][0] = parameterList.get(i).getName();				
				//if (parameterList.get(i).getValue()==null){
					data[i][1] = "";
		//		} else data[i][1] = parameterList.get(i).getValue();
			}
	    }
	    
	    public List<FunctionParameter> getParameterList(){
	    	return parameterList;
	    }
	    
	    public int getColumnCount() {
	       return columnNames.length;
	    }

	    public int getRowCount() {
	        return data.length;
   	    }

   	    public String getColumnName(int col) {
   	        return columnNames[col];
   	    }

   	    public Object getValueAt(int row, int col) {
   	        return data[row][col];
   	    }

   	    public Class<? extends Object> getColumnClass(int c) {
   	    	return getValueAt(0, c).getClass();
	    }


   	    public boolean isCellEditable(int row, int col) {
   	    	return (col == 1);
	    }

	    	    /*
	    	     * Don't need to implement this method unless your table's
	    	     * data can change.
	    	     */
	    public void setValueAt(Object value, int row, int col) {
	    	        data[row][col] = value;
	    	        fireTableCellUpdated(row, col);
	    	        this.parameterList.get(row).setValue(""+value.toString());
	    }
	    	
  	} // TableModel
	
	
}
