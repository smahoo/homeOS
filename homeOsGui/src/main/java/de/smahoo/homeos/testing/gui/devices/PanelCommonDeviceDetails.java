package de.smahoo.homeos.testing.gui.devices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceImpl;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.location.LocationManager;
import de.smahoo.homeos.testing.gui.SelectionListener;

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
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
			chkHidden.setSelected(false);
			lbIsOnState.setText("");
		} else {			
			lbDeviceId.setText(currentDevice.getDeviceId());
			txtDeviceName.setText(currentDevice.getName());
			selectDeviceLocation(currentDevice.getLocation());
			chkHidden.setSelected(currentDevice.isHidden());
			lbIsOnState.setText(""+currentDevice.isOn());
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (currentDevice.getLastActivityTimeStamp() == null){
				lbLastActivity.setText("---");
			} else {
				lbLastActivity.setText(formatter.format(currentDevice.getLastActivityTimeStamp()));
			}
		}		
		setDeviceTypes(currentDevice);
		txtDeviceName.setEnabled((currentDevice!=null));
		cboxLocation.setEnabled((currentDevice != null));
		lstDeviceTypes.setEnabled((currentDevice!=null));
		chkHidden.setEnabled(currentDevice!=null);
	}
	
	protected void initComboBoxLocations(){
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
	
	private void setHidden(){
		if (currentDevice != null){
			if (currentDevice instanceof DeviceImpl){
				((DeviceImpl)currentDevice).setHidden(this.chkHidden.isSelected());
			}
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
		
		JLabel lbLastActivityStr = new JLabel("Last Activity");
		lbLastActivityStr.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JScrollPane scrollPane = new JScrollPane();
		
		chkHidden = new JCheckBox("");
		chkHidden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setHidden();
			}
		});
		
		lbIsOnState = new JLabel("");
		
		lblNewLabel = new JLabel("Hidden");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lblNewLabel_1 = new JLabel("Is On?");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lbDeviceTypes = new JLabel("Device Types");
		lbDeviceTypes.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbLastActivity = new JLabel("");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lbDeviceNameCaption, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lbDeviceIdCaption, GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
								.addComponent(lbLocationCaption, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lbLastActivityStr, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
								.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lbIsOnState, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
								.addComponent(chkHidden, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
								.addComponent(txtDeviceName, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
								.addComponent(lbDeviceId, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
								.addComponent(cboxLocation, 0, 287, Short.MAX_VALUE)
								.addComponent(lbLastActivity, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lbDeviceTypes, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)))
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
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(chkHidden)
						.addComponent(lblNewLabel))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbIsOnState, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(12)
							.addComponent(lblNewLabel_1)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbLastActivityStr)
						.addComponent(lbLastActivity))
					.addGap(13)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
						.addComponent(lbDeviceTypes))
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
	private JCheckBox chkHidden;
	private JLabel lbIsOnState;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lbLastActivity;
}
