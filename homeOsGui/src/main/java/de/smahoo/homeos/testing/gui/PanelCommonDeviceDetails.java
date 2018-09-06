package de.smahoo.homeos.testing.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.location.LocationManager;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JList;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PanelCommonDeviceDetails extends JPanel{
	
	private Device currentDevice = null;
	LocationManager locationManager;
	
	List<SelectionListener> selectionListener;
	
	
	public PanelCommonDeviceDetails(){	
		selectionListener = new ArrayList<SelectionListener>();
		locationManager = HomeOs.getInstance().getLocationManager();
		initGui();
		initComboBoxLocations();
	}
	
	public void addSelectionListener(SelectionListener listener){
		if (selectionListener.contains(listener)) return;
		selectionListener.add(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener){
		if (selectionListener.isEmpty()) return;
		if (selectionListener.contains(listener)){
			selectionListener.remove(listener);
		}
	}	
	
	public void update(){		
		if (currentDevice == null){
			lbDeviceId.setText("");
			txtDeviceName.setText("");			
			selectDeviceLocation(null);
		} else {			
			lbDeviceId.setText(currentDevice.getDeviceId());
			txtDeviceName.setText(currentDevice.getName());
			selectDeviceLocation(currentDevice.getLocation());
		}
		
		setDeviceTypes(currentDevice);
		txtDeviceName.setEnabled((currentDevice!=null));
		cboxLocation.setEnabled((currentDevice != null));
		lstDeviceTypes.setEnabled((currentDevice!=null));
	}
	
	private void initComboBoxLocations(){
		List<Location> list = locationManager.getAllLocations();
		cboxLocation.removeAllItems();
		cboxLocation.addItem(" ");
		for (Location loc : list){
			cboxLocation.addItem(loc);
		}
	}
	
	private void setDeviceName(){
		if (currentDevice == null) return;
		currentDevice.setName(this.txtDeviceName.getText());
	}
	
	public Device getDevice(){
		return currentDevice;
	}
	
	public void setDevice(Device device){
		if (currentDevice == device) return;
		currentDevice = device;
		update();
	}
	
	private void setDeviceTypes(Device device){
		this.lstDeviceTypes.removeAll();
		
		DefaultListModel listModel = new DefaultListModel();
		
		if (device == null) return;
		List<DeviceType> list = DeviceType.getDeviceTypes(device);
		for (DeviceType type : list){
			listModel.addElement(type);
		}
		lstDeviceTypes.setModel(listModel);
		if (list.size() > 0){
			lstDeviceTypes.setSelectedIndex(0);			
		}
	}
	
	private void selectDeviceLocation(Location location){
		cboxLocation.setSelectedItem(location);
	}
	
	private void setLocation(Location location){
		if (this.currentDevice == null) return;	
		if (currentDevice.getLocation() == location) return;
		currentDevice.assignLocation(location);
		//cboxLocation.setSelectedItem(location);
	}
	
	private void selectDeviceType(DeviceType devType){
		if (selectionListener.isEmpty()) return;
		for (SelectionListener listener : selectionListener){
			listener.onDeviceTypeSelected(devType);
		}
	}
	
	private void initGui(){
		
		JLabel lbDeviceIdCaption = new JLabel("Device ID");
		lbDeviceIdCaption.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbDeviceId = new JLabel("<deviceId>");
		
		JLabel lbDeviceNameCaption = new JLabel("Device Name");
		lbDeviceNameCaption.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtDeviceName = new JTextField();
		txtDeviceName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER){
					setDeviceName();
				}
			}
		});
		txtDeviceName.setEnabled(false);
		txtDeviceName.setColumns(10);
		
		JLabel lbLocationCaption = new JLabel("Location");
		lbLocationCaption.setHorizontalAlignment(SwingConstants.RIGHT);
		
		cboxLocation = new JComboBox();
		cboxLocation.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {				
				if (arg0.getStateChange()==ItemEvent.SELECTED){	
					if (arg0.getItem() instanceof Location){
					    setLocation((Location)arg0.getItem());						
					} else {
						setLocation((Location)null);
					}
				}				
			}
		});
		cboxLocation.setEnabled(false);
		
		JLabel lbDeviceTypesCaption = new JLabel("Device Types");
		lbDeviceTypesCaption.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lbDeviceNameCaption, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lbDeviceIdCaption, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lbDeviceTypesCaption, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lbLocationCaption, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(19)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
						.addComponent(txtDeviceName, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
						.addComponent(lbDeviceId, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
						.addComponent(cboxLocation, 0, 294, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbDeviceIdCaption)
						.addComponent(lbDeviceId))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbDeviceNameCaption)
						.addComponent(txtDeviceName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbLocationCaption)
						.addComponent(cboxLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lbDeviceTypesCaption)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		lstDeviceTypes = new JList();
		lstDeviceTypes.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {				
				if (!arg0.getValueIsAdjusting()){
					selectDeviceType((DeviceType)lstDeviceTypes.getSelectedValue());
				}
			}
		});
		lstDeviceTypes.setEnabled(false);
		scrollPane.setViewportView(lstDeviceTypes);
		setLayout(groupLayout);
	}
	
	private JLabel lbDeviceId;
	private JTextField txtDeviceName;
	private JComboBox cboxLocation;
	private JList lstDeviceTypes;
}
