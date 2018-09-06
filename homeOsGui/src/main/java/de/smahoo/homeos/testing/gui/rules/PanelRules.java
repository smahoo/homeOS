package de.smahoo.homeos.testing.gui.rules;

import javax.swing.JPanel;
import javax.swing.JSplitPane;


import de.smahoo.homeos.automation.Rule;
import de.smahoo.homeos.automation.RuleGroup;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.testing.gui.PanelDetails;
import de.smahoo.homeos.testing.gui.SelectionListener;


import java.awt.BorderLayout;
import java.awt.Dimension;

public class PanelRules extends JPanel{

	private PanelDetails currentPanel = null;
	private Rule currentRule = null;
	private RuleGroup currentGroup = null;
	
	public PanelRules() {
		initGui();
	}
	
	public void setObject(Object o){
		if (o instanceof RuleGroup){
			currentGroup = (RuleGroup)o;
			currentRule = null;
			setPanel(currentGroup);
			return;
		}
		if (o instanceof Rule){
			currentGroup = null;
			currentRule = (Rule)o;
			setPanel(currentRule);
			return;
		}
	}
	
	private void setPanel(RuleGroup group){
		PanelRuleGroup panel = new PanelRuleGroup();
		panel.setRuleGroup(group);
		setPanel(panel);
	}
	
	private void setPanel(Rule rule){
		PanelRuleDetails panel = new PanelRuleDetails();
		panel.setRule(rule);
		setPanel(panel);
	}
	
	private void setPanel(PanelDetails panel){	
		splitPane.setRightComponent(panel);  
		currentPanel = panel;
	}
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);		
		PanelRulesTree panelRulesTree = new PanelRulesTree();
		panelRulesTree.setPreferredSize(new Dimension(300, 2));
		panelRulesTree.listener = new SelectionListener() {
			
			@Override
			public void onRuleSelected(Rule rule) {
				setPanel(rule);
				
			}
			
			@Override
			public void onRuleGroupSelected(RuleGroup group) {
				setPanel(group);
				
			}
			
			@Override
			public void onLocationSelected(Location location) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDriverSelected(Driver driver) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDeviceTypeSelected(DeviceType deviceType) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDeviceSelected(Device device) {
				// TODO Auto-generated method stub
				
			}
		};
		splitPane.setLeftComponent(panelRulesTree);
		setPanel((Rule)null);
	}
	
	private JSplitPane splitPane;
}
