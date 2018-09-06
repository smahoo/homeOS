package de.smahoo.homeos.testing.gui;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;


import javax.swing.border.EtchedBorder;
import java.awt.CardLayout;
import javax.swing.JToggleButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.smahoo.homeos.automation.Rule;
import de.smahoo.homeos.automation.RuleGroup;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.testing.gui.cmd.PanelCommandDetails;
import de.smahoo.homeos.testing.gui.devices.PanelDeviceDetails;
import de.smahoo.homeos.testing.gui.devices.PanelDevices;
import de.smahoo.homeos.testing.gui.event.PanelEventBus;
import de.smahoo.homeos.testing.gui.remote.PanelRemoteDetails;
import de.smahoo.homeos.testing.gui.rules.PanelRules;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FrameTestHomeOs extends JFrame implements SelectionListener {
	
	protected JPanel currPanelContent = null;
	
	public FrameTestHomeOs() {
		setSize(new Dimension(1200, 768));
		setPreferredSize(new Dimension(800, 600));
		setTitle("HomeOs Test Environment");		
		initGui();
	}
	
	public void init(){
		
	}
	
	
	public void onDeviceSelected(Device device){
		pnlDetails.setDevice(device);
	}
	
	public void onDriverSelected(Driver driver){
		
	}
	
	public void onLocationSelected(Location location){
		
	}
	
	public void onDeviceTypeSelected(DeviceType deviceType){
		
	}
	
	public void onRuleSelected(Rule rule){
		
	}
	public void onRuleGroupSelected(RuleGroup group){
		
	}
	
	private void setLookAndFeel(){
		try {            
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception exc) {
			// unable to set Look & Feel
		}

	}
	
	private void switchDevices(){
		if (!btnDevices.isSelected()){
			btnDevices.setSelected(true);			
		}
		
		this.btnRules.setSelected(false);
		btnCommand.setSelected(false);
		btnRemote.setSelected(false);
		btnRules.repaint();
		if (currPanelContent != null){
			panelContent.remove(currPanelContent);	
		}
		currPanelContent = panelDeviceDetails;
		panelContent.add(currPanelContent);
		panelContent.repaint();
		panelContent.revalidate();
	}
	
	private void switchRemote(){
		if (!btnRemote.isSelected()){
			btnRemote.setSelected(true);			
		}
		btnCommand.setSelected(false);
		btnRules.setSelected(false);
		btnDevices.setSelected(false);
		btnRemote.repaint();		
		if (currPanelContent != null){
			panelContent.remove(currPanelContent);	
		}
		currPanelContent = panelRemoteDetails;
		panelContent.add(currPanelContent);	
		panelContent.repaint();
		panelContent.revalidate();
	}
	
	private void switchCommandAPI(){
		if (!btnCommand.isSelected()){
			btnCommand.setSelected(true);			
		}
		btnRules.setSelected(false);
		btnDevices.setSelected(false);
		btnRemote.setSelected(false);
		btnCommand.repaint();		
		if (currPanelContent != null){
			panelContent.remove(currPanelContent);	
		}
		currPanelContent = panelCommandDetails;
		panelContent.add(currPanelContent);
		panelContent.revalidate();
		panelContent.repaint();
	}
	
	private void switchRules(){
		if (!btnRules.isSelected()){
			btnRules.setSelected(true);			
		}
		btnDevices.setSelected(false);
		btnCommand.setSelected(false);
		btnRemote.setSelected(false);
		btnDevices.repaint();
		if (currPanelContent != null){
			panelContent.remove(currPanelContent);	
		}
		currPanelContent = panelRuleDetails;
		panelContent.add(currPanelContent);
		panelRuleDetails.revalidate();		
		panelContent.revalidate();
		panelContent.repaint();
	}
	
	private void initGui(){
		
		setLookAndFeel();
		
		pnlTop = new JPanel();
		pnlTop.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlTop.setPreferredSize(new Dimension(150, 50));
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		
		btnDevices = new JToggleButton("Devices");
		btnDevices.setSelected(true);
		btnDevices.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				switchDevices();
			}
		});
		
		btnRules = new JToggleButton("Rules");
		btnRules.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				switchRules();
			}
		});
		
		btnCommand = new JToggleButton("Command API");
		btnCommand.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				switchCommandAPI();
			}
		});
		
		btnRemote = new JButton("Remote");
		btnRemote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switchRemote();
			}
		});
		
		GroupLayout gl_pnlTop = new GroupLayout(pnlTop);
		gl_pnlTop.setHorizontalGroup(
			gl_pnlTop.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlTop.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnDevices)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnRules)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCommand)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnRemote)
					.addContainerGap(844, Short.MAX_VALUE))
		);
		gl_pnlTop.setVerticalGroup(
			gl_pnlTop.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlTop.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_pnlTop.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDevices)
						.addComponent(btnRules)
						.addComponent(btnCommand)
						.addComponent(btnRemote))
					.addContainerGap(12, Short.MAX_VALUE))
		);
		pnlTop.setLayout(gl_pnlTop);
		
		pnlBottom = new JPanel();
		pnlBottom.setPreferredSize(new Dimension(30, 30));
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		panelContent = new JPanel();
		panelContent.setPreferredSize(new Dimension(10, 400));
		panelContent.setLayout(new BorderLayout(0, 0));
		//tabbedPaneContent = new JTabbedPane(JTabbedPane.TOP);
		//pnlBottom.add(tabbedPaneContent);
		splitMain = new JSplitPane();
		splitMain.setBorder(null);	
		panelDeviceDetails = new JPanel();
	//	tabbedPaneContent.addTab("New tab", null, panelDeviceDetails, null);
		panelContent.add(panelDeviceDetails);
		panelDeviceDetails.setLayout(new CardLayout(0, 0));
		panelDeviceDetails.add(splitMain, "name_25168138187115");
		this.currPanelContent = panelDeviceDetails;
		//splitRight = new JSplitPane();
		//splitRight.setBorder(null);
		//splitRight.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		
		pnlDetails = new PanelDeviceDetails();
		pnlDetails.setBorder(null);
		pnlDetails.setPreferredSize(new Dimension(10, 400));
		//splitRight.setLeftComponent(pnlDetails);
		
		pnlDevices = new PanelDevices();
		pnlDevices.setBorder(null);
		pnlDevices.setPreferredSize(new Dimension(400, 10));
		pnlDevices.addSelectionListener(this);
		splitMain.setRightComponent(pnlDetails);
		splitMain.setLeftComponent(pnlDevices);
		
		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(splitPane, BorderLayout.WEST);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		splitPane.setLeftComponent(panelContent);
	
		panelEventBus = new PanelEventBus();
		panelEventBus.setPreferredSize(new Dimension(457, 250));
		splitPane.setRightComponent(panelEventBus);
		splitPane.setDividerLocation(450);
		
		panelRuleDetails = new PanelRules();
		panelCommandDetails = new PanelCommandDetails();
		panelRemoteDetails = new PanelRemoteDetails();
	}

	
	private JSplitPane splitMain;	
	private PanelDeviceDetails pnlDetails;
	private PanelDevices pnlDevices;
	private JPanel pnlTop;
	private JPanel pnlBottom;
	private JSplitPane splitPane;	
	private JToggleButton btnDevices;
	private JToggleButton btnRules;
	private JPanel panelContent;
	private JPanel panelDeviceDetails;
	private PanelRules panelRuleDetails;
	private PanelEventBus panelEventBus;
	private JToggleButton btnCommand;
	private PanelCommandDetails panelCommandDetails;
	private JButton btnRemote;
	private PanelRemoteDetails panelRemoteDetails;
}
