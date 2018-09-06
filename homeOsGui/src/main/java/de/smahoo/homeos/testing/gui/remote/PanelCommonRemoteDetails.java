package de.smahoo.homeos.testing.gui.remote;



import java.text.SimpleDateFormat;
import java.util.List;


import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.remote.RemoteAPI;
import de.smahoo.homeos.remote.RemoteDevice;
import de.smahoo.homeos.remote.physical.RemotePhysicalDevice;
import de.smahoo.homeos.testing.gui.PanelDetails;
import de.smahoo.homeos.testing.gui.db.FrameHistoryData;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class PanelCommonRemoteDetails extends PanelDetails{
	
	private Device device = null;
	private RemoteAPI remoteApi = null;
	
	
	public PanelCommonRemoteDetails(RemoteAPI remoteApi){
		this.remoteApi = remoteApi;	
		initGui();
		updateLocationComboBox();
	}
	
	public void setDevice(Device device){
		this.device = device;
		update();
	}
	
	private void updateLocationComboBox(){
		List<Location> list = remoteApi.getLocationList();
		this.cbLocation.removeAllItems();
		this.cbLocation.addItem("  ");
		for (Location location : list){
			this.cbLocation.addItem(location);
		}
		
	}
	
	public void update(){
		if (device == null){
			txtDeviceId.setText("");
			txtDeviceName.setText("");
			lbIsOnState.setText("");
			lbLastActivity.setText("");
		} else {
			txtDeviceId.setText(device.getDeviceId());
			txtDeviceName.setText(device.getName());
			lbIsOnState.setText(""+device.isOn());
			if (device.getLocation() == null){
				cbLocation.setSelectedIndex(0);
			} else {
				cbLocation.setSelectedItem(device.getLocation());
			}
			if (device.getLastActivityTimeStamp() == null){
				lbLastActivity.setText("---");
			} else {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				lbLastActivity.setText(formatter.format(device.getLastActivityTimeStamp()));
			}
		}
		txtDeviceId.setEnabled(device != null);
		txtDeviceName.setEnabled(device != null);
		cbLocation.setEditable(device != null);
		this.validate();
		this.repaint();
	}
	
	private void showPhysicalDeviceDetails(){
		RemotePhysicalDevice dev = remoteApi.getPhysicalDeviceDetails((RemoteDevice)this.device);
		if (dev == null){
			JOptionPane.showMessageDialog(null,"No physical details received","ERROR", JOptionPane.ERROR_MESSAGE,null);
			return;
		}
		FramePhysicalDeviceDetails frame = new FramePhysicalDeviceDetails(dev);
		frame.setVisible(true);
	}
	
	private void setDeviceName(){
		this.device.setName(txtDeviceName.getText());
	}
	
	private void getHistoryData(){
		FrameHistoryData frame = new FrameHistoryData();
		frame.setDevice(device);
		frame.setVisible(true);
	}
	
	private void initGui(){
		JLabel lblEviceId = new JLabel("Device ID");
		lblEviceId.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtDeviceId = new JTextField();
		txtDeviceId.setEditable(false);
		txtDeviceId.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Device Name");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtDeviceName = new JTextField();
		txtDeviceName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER){
					setDeviceName();
				}
			}
		});
		txtDeviceName.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Location");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		
		cbLocation = new JComboBox();
		
		JLabel lblNewLabel_2 = new JLabel("Is On?");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbIsOnState = new JLabel("");
		
		JButton btnPhysicalDeviceDetails = new JButton("Show physical device details...");
		btnPhysicalDeviceDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showPhysicalDeviceDetails();
			}
		});
		
		JButton btnDeviceHistory = new JButton("getDeviceHistory");
		btnDeviceHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getHistoryData();
			}
		});
		
		JLabel lblNewLabel_3 = new JLabel("Last Activity");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbLastActivity = new JLabel("");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblNewLabel_2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblNewLabel_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblNewLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblEviceId, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
								.addComponent(lblNewLabel_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(txtDeviceName, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
								.addComponent(txtDeviceId, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
								.addComponent(cbLocation, 0, 332, Short.MAX_VALUE)
								.addComponent(lbIsOnState, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
								.addComponent(lbLastActivity, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)))
						.addComponent(btnDeviceHistory, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(btnPhysicalDeviceDetails, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEviceId)
						.addComponent(txtDeviceId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(txtDeviceName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(cbLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_2)
						.addComponent(lbIsOnState))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_3)
						.addComponent(lbLastActivity))
					.addGap(25)
					.addComponent(btnPhysicalDeviceDetails)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDeviceHistory)
					.addContainerGap(85, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	private JTextField txtDeviceId;
	private JTextField txtDeviceName;
	private JComboBox cbLocation;
	private JLabel lbIsOnState;
	private JLabel lbLastActivity;
}
