package de.smahoo.homeos.testing.gui.rules;


import de.smahoo.homeos.automation.Condition;
import de.smahoo.homeos.automation.Rule;
import de.smahoo.homeos.testing.gui.PanelDetails;


import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Font;

public class PanelRuleConditions extends PanelDetails{
	
	private Rule rule;
	
	public PanelRuleConditions() {
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
		DefaultListModel model = new DefaultListModel();
		if (rule != null){
			for (Condition condition : rule.getConditions()){
				model.addElement(condition);
			}
		}
		list.setModel(model);
	}
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 30));
		add(panel, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Conditions");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		list = new JList();
		scrollPane.setViewportView(list);
	}
	
	private JList list;
}
