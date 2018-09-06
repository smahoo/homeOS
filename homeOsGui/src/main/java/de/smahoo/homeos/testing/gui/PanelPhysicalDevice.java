package de.smahoo.homeos.testing.gui;

import javax.swing.JPanel;


import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.testing.gui.devices.PanelDevice;

import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSplitPane;
import java.awt.CardLayout;
import java.util.List;

import javax.swing.border.EtchedBorder;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class PanelPhysicalDevice extends PanelDevice{
	private PhysicalDevice currDevice = null;
	
	public PanelPhysicalDevice(){
				
		initGui();
	}
	
	public void update(){
		if (currDevice == null){
			lbDriver.setText("");
			lbAddress.setText("");
			lbAvailable.setText("");
		} else {		
			lbDriver.setText(currDevice.getDriver().getName()+" "+currDevice.getDriver().getVersion());
			lbAddress.setText(currDevice.getAddress());
			lbAvailable.setText(""+currDevice.isAvailable());
		}
		setFunctions();
		setProperties();
	}
	
	public void setPhysicalDevice(PhysicalDevice device){
		
		currDevice = device;
		update();
	}
	
	public PhysicalDevice getPhysicalDevice(){
		return currDevice;
	}
	
	public void onDeviceEvent(DeviceEvent evt){
		switch(evt.getEventType()){
		case PROPERTY_VALUE_CHANGED:
		case DEVICE_PROPERTY_CHANGED: setProperties(); break;
		}
	}
	
	private void setFunctions(){
		if (!needRebuild()) return;
		DefaultListModel listModel = new DefaultListModel();
		
		if (currDevice != null){
			List<PhysicalDeviceFunction> list = currDevice.getDeviceFunctions();
			for (PhysicalDeviceFunction f : list){
				listModel.addElement(f);
			}
		}		
		
		listFunctions.setModel(listModel);
	}
	
	private boolean needRebuild(){
		boolean res = true;
		if (!(listFunctions.getModel() instanceof DefaultListModel)) return true;
		DefaultListModel m = (DefaultListModel)listFunctions.getModel();
		if (currDevice != null){
			List<PhysicalDeviceFunction> list = currDevice.getDeviceFunctions();
			for (PhysicalDeviceFunction f : list){
				res = res && m.contains(f);
			}
		}		
		return !res;
	}
	
	private void setProperties(){
		DefaultListModel listModel = new DefaultListModel();		
		if (currDevice != null){			
			List<DeviceProperty> list = currDevice.getPropertyList();
			for (DeviceProperty p : list){
				listModel.addElement(p);
			}
		}		
		listProperties.setModel(listModel);
	}

	private void executeFunction(PhysicalDeviceFunction function){
		if (function instanceof ParameterizedDeviceFunction){
			FrameFunctionExecution frame = new FrameFunctionExecution();			
			frame.executeFunction((ParameterizedDeviceFunction)function);			
		} else {
			try {
		     function.execute();
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}
	}
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		JPanel pnlDetails = new JPanel();
		pnlDetails.setBorder(null);
		pnlDetails.setPreferredSize(new Dimension(10, 95));
		add(pnlDetails, BorderLayout.NORTH);
		
		JLabel lblPhysicalDevice = new JLabel("Physical Device");
		lblPhysicalDevice.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblPhysicalDevice.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblNewLabel = new JLabel("Driver");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbDriver = new JLabel("<driver>");
		
		JLabel lblNewLabel_1 = new JLabel("Adress");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbAddress = new JLabel("<address>");
		
		JLabel lblNewLabel_2 = new JLabel("Available");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbAvailable = new JLabel("<available>");
		GroupLayout gl_pnlDetails = new GroupLayout(pnlDetails);
		gl_pnlDetails.setHorizontalGroup(
			gl_pnlDetails.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlDetails.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlDetails.createParallelGroup(Alignment.LEADING)
						.addComponent(lblPhysicalDevice, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addGroup(gl_pnlDetails.createSequentialGroup()
							.addGroup(gl_pnlDetails.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
								.addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
								.addComponent(lblNewLabel_2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_pnlDetails.createParallelGroup(Alignment.LEADING)
								.addComponent(lbAddress, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
								.addComponent(lbDriver, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
								.addComponent(lbAvailable, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_pnlDetails.setVerticalGroup(
			gl_pnlDetails.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlDetails.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPhysicalDevice)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlDetails.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lbDriver))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlDetails.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(lbAddress))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pnlDetails.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2)
						.addComponent(lbAvailable))
					.addGap(7))
		);
		pnlDetails.setLayout(gl_pnlDetails);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, BorderLayout.CENTER);
		
		JPanel pnlProperties = new JPanel();
		pnlProperties.setSize(new Dimension(0, 150));
		pnlProperties.setPreferredSize(new Dimension(10, 150));
		splitPane.setLeftComponent(pnlProperties);
		pnlProperties.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 20));
		pnlProperties.add(panel, BorderLayout.NORTH);
		panel.setLayout(new CardLayout(0, 0));
		
		JLabel lblProperties = new JLabel("Properties");
		lblProperties.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lblProperties.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblProperties, "name_33161572881748");
		
		JScrollPane scrollPaneProperties = new JScrollPane();
		pnlProperties.add(scrollPaneProperties, BorderLayout.CENTER);
		
		listProperties = new JList();
		scrollPaneProperties.setViewportView(listProperties);
		
		JPanel pnlFunctions = new JPanel();
		splitPane.setRightComponent(pnlFunctions);
		pnlFunctions.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(10, 20));
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlFunctions.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new CardLayout(0, 0));
		
		JLabel lbFunctions = new JLabel("Functions");
		lbFunctions.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lbFunctions, "name_33310732305642");
		
		JScrollPane scrollPaneFunctions = new JScrollPane();
		pnlFunctions.add(scrollPaneFunctions, BorderLayout.CENTER);
		
		listFunctions = new JList();
		listFunctions.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()){
					executeFunction((PhysicalDeviceFunction)listFunctions.getSelectedValue());
				}
			}
		});
		
		scrollPaneFunctions.setViewportView(listFunctions);
		splitPane.setDividerLocation(120);
	}
	
	private JLabel lbDriver;
	private JList listFunctions;
	private JList listProperties;
	private JLabel lbAddress;
	private JLabel lbAvailable;
}
