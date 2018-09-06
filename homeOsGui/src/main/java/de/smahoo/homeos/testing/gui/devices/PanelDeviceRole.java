package de.smahoo.homeos.testing.gui.devices;


import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.device.role.RoleFunction;
import de.smahoo.homeos.device.role.RoleProperty;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;
import java.util.List;

import javax.swing.SwingConstants;

public class PanelDeviceRole extends PanelDetails{

	private DeviceRole role = null;

	
	public PanelDeviceRole(){

		initGui();
	}
	
	public void setDeviceRole(DeviceRole role){
		this.role = role;
		update();
	}
	
	public void update(){
		buildTableProperties();
		buildTableFunctions();
	}
	
	private void buildTableProperties(){
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Name");
		model.addColumn("Device");
		model.addColumn("Device Property");
		
		if (role != null){
			List<RoleProperty> list = role.getRoleProperties();
			Object[] row;
			
			for (RoleProperty p : list){
				row = new Object[3];
				row[0] = p;
				if (p.getBindedProperty() instanceof DeviceProperty){
					row[1] = ((DeviceProperty)p.getBindedProperty()).getDevice();				
				}
				row[2] = p.getBindedProperty();
				model.addRow(row);
			}
		}
		
		tblProperties.setModel(model);		
	}
	
	private void buildTableFunctions(){		
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Name");
		model.addColumn("Device");
		model.addColumn("Device Function");
		
		if (role != null){
			List<RoleFunction> list = role.getRoleFunctions();		
			Object[] row;
		
			for (RoleFunction func : list){
				row = new Object[3];
				row[0] = func;
				if (func.getBindedFunction() instanceof PhysicalDeviceFunction){
					row[1]=((PhysicalDeviceFunction)func.getBindedFunction()).getDevice();
				}			
				row[2] = func.getBindedFunction();
				model.addRow(row);
			}
		}
		this.tblFunctions.setModel(model);
		
	}
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setPreferredSize(new Dimension(10, 50));
		add(panel, BorderLayout.NORTH);
		
		JLabel lbDeviceTypeCaption = new JLabel("Device Role");
		lbDeviceTypeCaption.setHorizontalAlignment(SwingConstants.CENTER);
		lbDeviceTypeCaption.setFont(new Font("Tahoma", Font.BOLD, 14));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbDeviceTypeCaption, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbDeviceTypeCaption)
					.addContainerGap(121, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBorder(null);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, BorderLayout.CENTER);
		
		JPanel pnlProperties = new JPanel();
		pnlProperties.setPreferredSize(new Dimension(10, 150));
		splitPane.setLeftComponent(pnlProperties);
		pnlProperties.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlPropertiesCaption = new JPanel();
		pnlPropertiesCaption.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlProperties.add(pnlPropertiesCaption, BorderLayout.NORTH);
		
		JLabel lbPropertiesCaption = new JLabel("Properties");
		pnlPropertiesCaption.add(lbPropertiesCaption);
		
		JScrollPane scrollPaneProperties = new JScrollPane();
		scrollPaneProperties.setBorder(null);
		pnlProperties.add(scrollPaneProperties, BorderLayout.CENTER);
		
		tblProperties = new JTable();
		scrollPaneProperties.setViewportView(tblProperties);
		
		JPanel pnlFunctions = new JPanel();
		splitPane.setRightComponent(pnlFunctions);
		pnlFunctions.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlFunctionCaption = new JPanel();
		pnlFunctionCaption.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlFunctions.add(pnlFunctionCaption, BorderLayout.NORTH);
		
		JLabel lbFunctionCaption = new JLabel("Functions");
		pnlFunctionCaption.add(lbFunctionCaption);
		
		JScrollPane scrollPaneFunctions = new JScrollPane();
		scrollPaneFunctions.setBorder(null);
		pnlFunctions.add(scrollPaneFunctions, BorderLayout.CENTER);
		
		tblFunctions = new JTable();
		scrollPaneFunctions.setViewportView(tblFunctions);
		splitPane.setDividerLocation(150);
	}
	
	private JTable tblProperties;
	private JTable tblFunctions;
}
