package de.smahoo.homeos.testing.gui.rules;


import de.smahoo.homeos.automation.Rule;
import de.smahoo.homeos.automation.RuleEvent;
import de.smahoo.homeos.automation.RuleEventListener;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.testing.gui.PanelDetails;

import java.awt.CardLayout;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.BorderLayout;

public class PanelRuleDetails extends PanelDetails implements RuleEventListener{
	
	private Rule currentRule = null;
	
	public void setRule(Rule rule){		
		currentRule = rule;
		panelCommonRuleDetails.setRule(rule);
		panelRuleConditions.setRule(rule);
		panelRuleActions.setRule(rule);
		
	}
	
	public void onRuleEvent(RuleEvent event){
		if (event.getRule() != currentRule) return;
		update();
	}
	
	public PanelRuleDetails() {
		HomeOs.getInstance().getRuleEngine().addEventListener(this);
		initGui();
		update();
	}

	public void update(){
		this.panelCommonRuleDetails.update();
		this.panelRuleConditions.update();
		this.panelRuleActions.update();
	}

	private void initGui(){
		setLayout(new CardLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBorder(null);
		add(splitPane, "name_9351161661049");
		
		JPanel panelLeft = new JPanel();
		panelLeft.setPreferredSize(new Dimension(350, 10));
		splitPane.setLeftComponent(panelLeft);
		panelLeft.setLayout(new BorderLayout(0, 0));
		
		panelCommonRuleDetails = new PanelCommonRuleDetails();
		panelLeft.add(panelCommonRuleDetails);
		
		JPanel panelRight = new JPanel();
		splitPane.setRightComponent(panelRight);
		panelRight.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelRight.add(splitPane_1, BorderLayout.CENTER);
		
		panelRuleConditions = new PanelRuleConditions();
		panelRuleConditions.setPreferredSize(new Dimension(10, 300));
		splitPane_1.setLeftComponent(panelRuleConditions);
		
		panelRuleActions = new PanelRuleActions();
		splitPane_1.setRightComponent(panelRuleActions);
		splitPane_1.setDividerLocation(250);
	}

	private PanelCommonRuleDetails panelCommonRuleDetails;
	private PanelRuleConditions panelRuleConditions;
	private PanelRuleActions panelRuleActions;
	
}
