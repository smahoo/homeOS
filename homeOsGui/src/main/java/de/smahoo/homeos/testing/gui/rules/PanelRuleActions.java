package de.smahoo.homeos.testing.gui.rules;


import de.smahoo.homeos.automation.Rule;
import de.smahoo.homeos.automation.RuleAction;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.DefaultListModel;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JList;

public class PanelRuleActions extends PanelDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3733690122274021580L;
	private Rule rule = null;
	
	public PanelRuleActions(){
		
		initGui();
		update();
	}
	
	
	public void setRule(Rule rule){
		this.rule = rule;
		update();
	}
	
	public void update(){
		buildList();
	}
	
	private void buildList(){
		DefaultListModel<RuleAction> model = new DefaultListModel<RuleAction>();
		
		if (rule != null){
			for (RuleAction action : rule.getActions()){
				model.addElement(action);
			}
		}
		
		list.setModel(model);
	}
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		JPanel pnlTop = new JPanel();
		pnlTop.setPreferredSize(new Dimension(10, 30));
		add(pnlTop, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Actions");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		pnlTop.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		list = new JList<RuleAction>();
		scrollPane.setViewportView(list);
	}
	
	private JList<RuleAction> list;
	
}
