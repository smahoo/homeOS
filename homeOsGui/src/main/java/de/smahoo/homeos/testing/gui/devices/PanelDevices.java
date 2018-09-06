package de.smahoo.homeos.testing.gui.devices;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.CardLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;



import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceEventListener;
import de.smahoo.homeos.device.DeviceManager;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.driver.DriverEvent;
import de.smahoo.homeos.driver.DriverEventListener;
import de.smahoo.homeos.driver.DriverManager;
import de.smahoo.homeos.driver.DriverMode;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.location.LocationEvent;
import de.smahoo.homeos.location.LocationEventListener;
import de.smahoo.homeos.location.LocationManager;
import de.smahoo.homeos.testing.gui.SelectionListener;
import de.smahoo.homeos.simulation.devices.SimDevice;
import de.smahoo.homeos.testing.simulation.gui.FrameSimulationDevice;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelDevices extends JPanel {
	
	private List<SelectionListener> selectionListeners;
	private DeviceManager deviceManager;
	private DriverManager driverManager;
	private LocationManager locationManager;
	private Device currDevice = null;
	
	private PopupMenuDevice popDevice = null;
	
	public PanelDevices() {
		selectionListeners = new ArrayList<SelectionListener>();
		deviceManager = HomeOs.getInstance().getDeviceManager();		
		deviceManager.addDeviceEventListener(new DeviceEventListener() {
			
			@Override
			public void onDeviceEvent(DeviceEvent event) {
				evaluateEvent(event);
				
			}
		});
		driverManager = HomeOs.getInstance().getDriverManager();
		driverManager.addDriverEventListener(new DriverEventListener() {
			
			@Override
			public void onDriverEvent(DriverEvent evnt) {
				evaluateEvent(evnt);
				
			}
		});		
		locationManager = HomeOs.getInstance().getLocationManager();
		locationManager.addEventListener(new LocationEventListener() {
			
			@Override
			public void onLocationEvent(LocationEvent evnt) {
				evaluateEvent(evnt);
				
			}
		});
		initGui();
		buildPhysicalDeviceTree();
		buildDeviceTypesTree();
		buildAllDevicesTree();
		buildDeviceRolesTree();
		buildLocationTree();
	}
	
	public synchronized void addSelectionListener(SelectionListener listener){
		if (selectionListeners.contains(listener)) return;
		selectionListeners.add(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener){
		if (selectionListeners.isEmpty()) return;
		if (selectionListeners.contains(listener)){
			selectionListeners.remove(listener);
		}
	}
	
	private void onDeviceSelection(Device device){
		if (selectionListeners.isEmpty()) return;
		for (SelectionListener listener : selectionListeners){
			listener.onDeviceSelected(device);
		}
	}
	
	private synchronized void evaluateEvent(Event event){
		switch(event.getEventType()){
		case DEVICE_ADDED: 
		case DEVICE_RENAMED:
		case DEVICE_REMOVED: buildDeviceRolesTree();buildAllDevicesTree(); buildPhysicalDeviceTree(); buildDeviceTypesTree(); buildLocationTree();break;	
		case DRIVER_LOADED: buildAllDevicesTree(); buildPhysicalDeviceTree(); buildLocationTree();break;
		case ROLE_ADDED:
		case ROLE_REMOVED: buildDeviceRolesTree();buildAllDevicesTree();buildDeviceTypesTree(); buildLocationTree(); break;
		case LOCATION_ADDED:
		case LOCATION_REMOVED:
		case LOCATION_ASSIGNED:
		case LOCATION_RENAMED: buildLocationTree();break;
		}
	}
	
	private synchronized void buildDeviceTypesTree(){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Device Types");
		DefaultMutableTreeNode tmp;
		DefaultMutableTreeNode elem;
		for (DeviceType t : DeviceType.values()){
			tmp = new DefaultMutableTreeNode(t.name());
			
			List<Device> list = deviceManager.getDevices(t);
			for (Device d : list){
				elem = new DefaultMutableTreeNode(d);
				tmp.add(elem);
			}			
			root.add(tmp);
		}
		DefaultTreeModel treemodel = new DefaultTreeModel(root);
		this.treeDeviceTypes.setModel(treemodel);
	}
	
	private synchronized void buildPhysicalDeviceTree(){
		List<Driver> driverList = HomeOs.getInstance().getDriverManager().getLoadedDriver();
		List<Device> devList;
		
		  DefaultMutableTreeNode root = new DefaultMutableTreeNode("Driver");
		  DefaultMutableTreeNode deviceNode;
		  DefaultMutableTreeNode driverNode;
		  for (Driver d : driverList){
			  if (d != null){
				  driverNode = new DefaultMutableTreeNode(d);
				  root.add(driverNode);
				  devList = deviceManager.getDevices(d);
				  for (Device dev: devList){
					  if (dev != null){
						  deviceNode = new DefaultMutableTreeNode(dev);
						  driverNode.add(deviceNode);
					  }		 
				  }
			  }
		  }	  
		DefaultTreeModel treemodel = new DefaultTreeModel(root);		
		treePhysicalDevices.setModel(treemodel);		
	}
	
	private synchronized void buildAllDevicesTree(){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("All Devices");
		
		DefaultMutableTreeNode elem;			
		List<Device> list = deviceManager.getDevices();
		for (Device d : list){
			elem = new DefaultMutableTreeNode(d);
			root.add(elem);
		}		
		DefaultTreeModel treemodel = new DefaultTreeModel(root);
		this.treeAllDevices.setModel(treemodel);
	}
	
	protected synchronized void buildLocationTree(){
		 DefaultMutableTreeNode root = new DefaultMutableTreeNode("Locations");
		  DefaultMutableTreeNode locationNode;
		  for (Location location : locationManager.getLocations()){
				  if (location != null){
					  locationNode = getLocationNode(location);					 
					  root.add(locationNode);
				  }			 
		    }		
		  List<Device> devList = deviceManager.getDevices();
		  for (Device dev : devList){
			  if (dev.getLocation() == null){
				  root.add(new DefaultMutableTreeNode(dev));
			  }
		  }
		DefaultTreeModel treemodel = new DefaultTreeModel(root);
		
		treeLocations.setModel(treemodel);	
	}
	
	protected synchronized DefaultMutableTreeNode getLocationNode(Location location){
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(location);		
		if (location.hasChildLocations()){
			for (Location loc: location.getChildLocations()){
				node.add(getLocationNode(loc));				
			}
		}
		if (location.hasAssignedDevices()){
			  for (Device device : location.getAssignedDevices()){
				  node.add(new DefaultMutableTreeNode(device));
			  }
		  }
		  
		return node;
	}
	
	private synchronized void buildDeviceRolesTree(){
		 DefaultMutableTreeNode root = new DefaultMutableTreeNode("Device Roles");
		 DefaultMutableTreeNode roleNode;
		 for (DeviceRole role : deviceManager.getDeviceRoles()){
			 if (role != null){
				 roleNode = new DefaultMutableTreeNode(role);
				 root.add(roleNode);
			 }
		 }		 
		DefaultTreeModel treemodel = new DefaultTreeModel(root);		
		this.treeDeviceRoles.setModel(treemodel);	
	}
	
	private synchronized void evaluateSelectionEvent(DefaultMutableTreeNode node){
		
		if (node.getUserObject() instanceof Device){
			currDevice = (Device)node.getUserObject();
			this.onDeviceSelection((Device)node.getUserObject());
		} else {
			currDevice = null;
		}
	}
	
	private void showFrameSimDevice(){
		Object o = this.treePhysicalDevices.getSelectionPath().getLastPathComponent();
		if (o == null) return;
		
		if (!(o instanceof DefaultMutableTreeNode)) return;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
		if (!(node.getUserObject() instanceof SimDevice)) return;
		SimDevice dev = (SimDevice)node.getUserObject();
		
		FrameSimulationDevice frame = new FrameSimulationDevice(dev);
		frame.setVisible(true);
	}
	
	private void showDevicePopup(Point p){
		if (popDevice == null){
			popDevice = new PopupMenuDevice();
		}
		popDevice.setDevice(currDevice);
		popDevice.setLocation(p);
		popDevice.setVisible(true);
	}
	
	private void setLearnMode(){
		HomeOs.getInstance().getDriverManager().setMode(DriverMode.DRIVER_MODE_ADD_DEVICE);
	}
	
	private void setRemoveMode(){
		HomeOs.getInstance().getDriverManager().setMode(DriverMode.DRIVER_MODE_REMOVE_DEVICE);
	}
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		pnlTop = new JPanel();
		pnlTop.setPreferredSize(new Dimension(10, 50));
		add(pnlTop, BorderLayout.NORTH);
		
		btnAddDevice = new JButton("Add Device");
		btnAddDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setLearnMode();
			}
		});
		
		btnRemoveDevice = new JButton("Remove Device");
		btnRemoveDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRemoveMode();
			}
		});
		GroupLayout gl_pnlTop = new GroupLayout(pnlTop);
		gl_pnlTop.setHorizontalGroup(
			gl_pnlTop.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlTop.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnAddDevice, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnRemoveDevice, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(172, Short.MAX_VALUE))
		);
		gl_pnlTop.setVerticalGroup(
			gl_pnlTop.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_pnlTop.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlTop.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAddDevice, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
						.addComponent(btnRemoveDevice, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
					.addContainerGap())
		);
		pnlTop.setLayout(gl_pnlTop);
		
		JTabbedPane tabbedPaneDevices = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPaneDevices);
		
		pnlAllDevices = new JPanel();
		tabbedPaneDevices.addTab("All Devices", null, pnlAllDevices, null);
		pnlAllDevices.setLayout(new CardLayout(0, 0));
		
		scrollPaneAllDevices = new JScrollPane();
		pnlAllDevices.add(scrollPaneAllDevices, "name_17025045197364");
		
		treeAllDevices = new JTree();
		treeAllDevices.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (currDevice != null){
					if (arg0.getButton() != MouseEvent.BUTTON1){					
						showDevicePopup(new Point(arg0.getXOnScreen(),arg0.getYOnScreen()));
					} else {
						if (popDevice != null){
							popDevice.setVisible(false);
						}
					}
				}
			}
		});
		treeAllDevices.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				if (e.getPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
					evaluateSelectionEvent((DefaultMutableTreeNode)e.getPath().getLastPathComponent());
				}
			}
		});
		scrollPaneAllDevices.setViewportView(treeAllDevices);
		
		pnlDeviceTypes = new JPanel();
		tabbedPaneDevices.addTab("Device Types", null, pnlDeviceTypes, null);
		pnlDeviceTypes.setLayout(new CardLayout(0, 0));
		
		scrollPaneTypes = new JScrollPane();
		pnlDeviceTypes.add(scrollPaneTypes, "name_16569889146804");
		
		treeDeviceTypes = new JTree();
		treeDeviceTypes.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				if (arg0.getPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
					evaluateSelectionEvent((DefaultMutableTreeNode)arg0.getPath().getLastPathComponent());
				}
			}
		});
		scrollPaneTypes.setViewportView(treeDeviceTypes);
		
		pnlLocations = new JPanel();
		tabbedPaneDevices.addTab("Locations", null, pnlLocations, null);
		pnlLocations.setLayout(new CardLayout(0, 0));
		
		scrollPaneLocations = new JScrollPane();
		pnlLocations.add(scrollPaneLocations, "name_17404066276859");
		
		treeLocations = new JTree();
		treeLocations.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				if (e.getPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
					evaluateSelectionEvent((DefaultMutableTreeNode)e.getPath().getLastPathComponent());
				}
			}
		});
		scrollPaneLocations.setViewportView(treeLocations);
		
		pnlDeviceRoles = new JPanel();
		tabbedPaneDevices.addTab("Device Roles", null, pnlDeviceRoles, null);
		pnlDeviceRoles.setLayout(new CardLayout(0, 0));
		
		scrollPaneDeviceRoles = new JScrollPane();
		pnlDeviceRoles.add(scrollPaneDeviceRoles, "name_17451439975192");
		
		treeDeviceRoles = new JTree();
		treeDeviceRoles.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				if (e.getPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
					evaluateSelectionEvent((DefaultMutableTreeNode)e.getPath().getLastPathComponent());
				}
			}
		});
		scrollPaneDeviceRoles.setViewportView(treeDeviceRoles);
		
		pnlDevicesDriver = new JPanel();
		tabbedPaneDevices.addTab("Physical Devices", null, pnlDevicesDriver, null);
		pnlDevicesDriver.setLayout(new CardLayout(0, 0));
		
		scrollPaneDriver = new JScrollPane();
		pnlDevicesDriver.add(scrollPaneDriver, "name_9421137662747");
		
		treePhysicalDevices = new JTree();
		treePhysicalDevices.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() >= 2){
					showFrameSimDevice();
				}
			}
		});
		treePhysicalDevices.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				if (arg0.getPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
					evaluateSelectionEvent((DefaultMutableTreeNode)arg0.getPath().getLastPathComponent());
				}
			}
		});
		scrollPaneDriver.setViewportView(treePhysicalDevices);
	}

	
	private JPanel pnlDevicesDriver;
	private JScrollPane scrollPaneDriver;
	private JTree treePhysicalDevices;
	private JPanel pnlDeviceTypes;
	private JScrollPane scrollPaneTypes;
	private JTree treeDeviceTypes;
	private JPanel pnlAllDevices;
	private JScrollPane scrollPaneAllDevices;
	private JTree treeAllDevices;
	private JPanel pnlLocations;
	private JScrollPane scrollPaneLocations;
	private JTree treeLocations;
	private JPanel pnlDeviceRoles;
	private JScrollPane scrollPaneDeviceRoles;
	private JTree treeDeviceRoles;
	private JPanel pnlTop;
	private JButton btnAddDevice;
	private JButton btnRemoveDevice;
}
