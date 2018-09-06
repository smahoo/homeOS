package de.smahoo.homeos.testing.gui.rules;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.CardLayout;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;


import de.smahoo.homeos.automation.Rule;
import de.smahoo.homeos.automation.RuleEngine;
import de.smahoo.homeos.automation.RuleEvent;
import de.smahoo.homeos.automation.RuleEventListener;
import de.smahoo.homeos.automation.RuleGroup;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.testing.gui.SelectionListener;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelRulesTree extends JPanel {
	
	private RuleEngine ruleEngine;
	protected SelectionListener listener = null;
	
	
	public PanelRulesTree() {
		initGui();		
		ruleEngine = HomeOs.getInstance().getRuleEngine();
		ruleEngine.addEventListener(new RuleEventListener() {
			
			@Override
			public void onRuleEvent(RuleEvent event) {
				evaluateRuleEvent(event);
				
			}
		});
		buildRuleTree();
	}
	
	private void buildRuleTree(){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Rules");
		
		List<RuleGroup> groups = ruleEngine.getRuleGroups();
		
		for(RuleGroup g : groups){
			root.add(getNode(g));
		}
		
		for (Rule rule : ruleEngine.getRules()){
			if (rule.getRuleGroup() == null){
				root.add(getNode(rule));
			}
		}
		
		DefaultTreeModel model = new DefaultTreeModel(root);
		tree.setModel(model);
	}
	
	private MutableTreeNode getNode(RuleGroup group){
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(group);
		
		for (Rule rule : group.getRules()){
			node.add(getNode(rule));
		}
		
		return node;
	}
	
	private MutableTreeNode getNode(Rule rule){
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(rule);
		
		return node;
	}
	
	private void evaluateRuleEvent(RuleEvent event){
		buildRuleTree();
	}
	
	
	private void evalTreeSelectionEvent(TreeSelectionEvent event){
		if (event.getNewLeadSelectionPath() != null){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)event.getNewLeadSelectionPath().getLastPathComponent();
			Object o = node.getUserObject();
			if (o instanceof RuleGroup){
				if (listener != null){
					listener.onRuleGroupSelected((RuleGroup)o);
				}
				return;
			}
			if (o instanceof Rule){
				if (listener != null){
					listener.onRuleSelected((Rule)o);
				}
				return;
			}
		}
	}
	
	private void initGui(){
		setLayout(new CardLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "name_26966550107625");
		
		tree = new JTree();
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() > 1){
					buildRuleTree();
				}
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				evalTreeSelectionEvent(arg0);
			}
		});
		scrollPane.setViewportView(tree);
	}
	
	private JTree tree;
}
