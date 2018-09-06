package de.smahoo.homeos.testing.gui.remote;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JSplitPane;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;



import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;



import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventListener;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.remote.RemoteAPI;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.BoxLayout;


public class PanelRemoteDetails extends JPanel{
	
	private RemoteAPI remoteAPI;
	private PanelDetails currentDetailPanel = null;
	private PanelRemoteDeviceDetails panelRemoteDeviceDetails;
	//private PanelRemoteLocationDetails panelRemoteLocationDetails;
	
	public PanelRemoteDetails(){		
		initGui();
		update();
	}
	
	private void connect() throws IOException{
		if (remoteAPI == null){
			remoteAPI = new RemoteAPI(new EventListener() {
				
				@Override
				public void onEvent(Event event) {
					evaluateEvent(event);
					
				}
			});
		}
		int port;
		try {
			port = Integer.parseInt(txtServerPort.getText());
		} catch (Exception exc){
			return;
		}
		
		remoteAPI.connect(txtServerAddress.getText(), port);	
		update();
		
	}
	
	private void saveRemoteCache(){
		File f = new File("D:\\tmp\\cache.xml");
		remoteAPI.saveHistoryCache(f);
	}
	
	private void loadRemoteCache(){
		File f = new File("D:\\tmp\\cache.xml");
		try {
			remoteAPI.loadHistoryCache(f);
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private void update(){
		buildTree();
	}
	
	private void buildTree(){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Location List");
		DefaultTreeModel model = new DefaultTreeModel(root);
		if (remoteAPI != null){
			DefaultMutableTreeNode tmp;
			List<Location> locList = remoteAPI.getLocationTree();
			for (Location loc : locList){
				tmp = new DefaultMutableTreeNode();
				addLocation(loc,tmp);
				root.add(tmp);
			}
			List<Device> devList = remoteAPI.getDevices();
			for (Device dev : devList){
				if (dev.getLocation() == null){
					tmp = new DefaultMutableTreeNode();
					tmp.setUserObject(dev);
					root.add(tmp);
				}
			}
		}		
		treeDevices.setModel(model);
	}
	
	private void addLocation(Location location, DefaultMutableTreeNode node){
		node.setUserObject(location);
		if (location.hasChildLocations()){
			List<Location> childs = location.getChildLocations();
			DefaultMutableTreeNode tmp;
			for (Location loc : childs){
				tmp = new DefaultMutableTreeNode();
				addLocation(loc,tmp);
				node.add(tmp);
				
			}
		}
		addDevices(location,node);
	}	
	
	private void addDevices(Location location, DefaultMutableTreeNode node){
		if (location.hasAssignedDevices()){
			List<Device> devList = location.getAssignedDevices();
			DefaultMutableTreeNode tmp;
			for (Device device : devList){
				tmp = new DefaultMutableTreeNode();
				tmp.setUserObject(device);
				node.add(tmp);
			}
		}
	}
	
	private void evaluateTreeSelection(TreeSelectionEvent event){
		DefaultMutableTreeNode node;
		try {
			node = (DefaultMutableTreeNode)treeDevices.getSelectionPath().getLastPathComponent();
		} catch (Exception exc){
			return;
		}
		if (node.getUserObject() instanceof Location){
			
		}
		if (node.getUserObject() instanceof Device){
			setDeviceDetailsPanel((Device)node.getUserObject());
		}
	}
	
	private void setLocationDetailsPanel(Location location){
		if (this.currentDetailPanel != null){
			
		}
		
	}
	
	private void setDeviceDetailsPanel(Device device){
		if (this.currentDetailPanel != null){
			panelDetails.remove(currentDetailPanel);
		}
		if (this.panelRemoteDeviceDetails == null){
			panelRemoteDeviceDetails = new PanelRemoteDeviceDetails(remoteAPI);			
		}
		panelRemoteDeviceDetails.setDevice(device);
		currentDetailPanel = panelRemoteDeviceDetails;
		panelDetails.add(currentDetailPanel);
		panelDetails.validate();
		panelDetails.repaint();
	}
	
	private void evaluateEvent(Event event){
		if (panelRemoteDeviceDetails != null) {
			panelRemoteDeviceDetails.update();
		}
		buildTree();
		panelRemoteLogger.log(event);
	}
	
	
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBorder(null);
		add(splitPane, BorderLayout.CENTER);
		
		pnlDeviceDetails = new JPanel();
		splitPane.setRightComponent(pnlDeviceDetails);
		pnlDeviceDetails.setLayout(new BorderLayout(0, 0));
		
		splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setBorder(null);
		pnlDeviceDetails.add(splitPane_1);
		
		panelRemoteLogger = new PanelRemoteLogger();
		splitPane_1.setRightComponent(panelRemoteLogger);
		
		panelDetails = new JPanel();
		panelDetails.setPreferredSize(new Dimension(10, 500));
		splitPane_1.setLeftComponent(panelDetails);
		panelDetails.setLayout(new GridLayout(1, 0, 0, 0));
		
		
		
		
		splitPane_1.setDividerLocation(250);
		
		pnlRemote = new JPanel();
		pnlRemote.setPreferredSize(new Dimension(250, 10));
		splitPane.setLeftComponent(pnlRemote);
		pnlRemote.setLayout(new BorderLayout(0, 0));
		
		panelConnection = new JPanel();
		panelConnection.setPreferredSize(new Dimension(10, 90));
		pnlRemote.add(panelConnection, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Server Address");
		
		txtServerAddress = new JTextField();
		txtServerAddress.setText("127.0.0.1");
		txtServerAddress.setColumns(10);
		
		txtServerPort = new JTextField();
		txtServerPort.setText("2020");
		txtServerPort.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Port");
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					connect();
				} catch (Exception exc){
					exc.printStackTrace();
				}
			}
		});
		GroupLayout gl_panelConnection = new GroupLayout(panelConnection);
		gl_panelConnection.setHorizontalGroup(
			gl_panelConnection.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelConnection.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelConnection.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelConnection.createSequentialGroup()
							.addGroup(gl_panelConnection.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelConnection.createSequentialGroup()
									.addComponent(txtServerAddress, GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED))
								.addGroup(gl_panelConnection.createSequentialGroup()
									.addComponent(lblNewLabel)
									.addGap(134)))
							.addGroup(gl_panelConnection.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel_1)
								.addComponent(txtServerPort, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)))
						.addComponent(btnConnect, Alignment.TRAILING))
					.addContainerGap())
		);
		gl_panelConnection.setVerticalGroup(
			gl_panelConnection.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelConnection.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelConnection.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lblNewLabel_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelConnection.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtServerAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtServerPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnConnect)
					.addContainerGap(70, Short.MAX_VALUE))
		);
		panelConnection.setLayout(gl_panelConnection);
		
		JScrollPane scrollPaneRemoteDevices = new JScrollPane();
		pnlRemote.add(scrollPaneRemoteDevices, BorderLayout.CENTER);
		
		treeDevices = new JTree();
		treeDevices.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				evaluateTreeSelection(arg0);
			}
		});
		scrollPaneRemoteDevices.setViewportView(treeDevices);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 50));
		add(panel, BorderLayout.NORTH);
		
		JButton btnNewButton = new JButton("Save History");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveRemoteCache();
			}
		});
		
		JButton btnNewButton_1 = new JButton("Load History");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadRemoteCache();
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewButton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_1)
					.addContainerGap(252, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewButton)
						.addComponent(btnNewButton_1))
					.addContainerGap(16, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
	}
	
	private JPanel pnlDeviceDetails;
	private JPanel pnlRemote;
	private JPanel panelConnection;
	private JTree  treeDevices;
	private JTextField txtServerAddress;
	private JTextField txtServerPort;
	private JButton btnConnect;
	private JSplitPane splitPane_1;
	private PanelRemoteLogger panelRemoteLogger;
	private JPanel panelDetails;
}
