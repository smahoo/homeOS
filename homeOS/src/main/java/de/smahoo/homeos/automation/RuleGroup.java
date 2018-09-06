package de.smahoo.homeos.automation;

import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.kernel.HomeOs;

public class RuleGroup {
	private String name = null;
	private String description = null;
	private List<Rule> rules;
	
	private List<RuleEventListener> eventListeners;
	
	public RuleGroup(){
		rules = new ArrayList<Rule>();
		eventListeners = new ArrayList<RuleEventListener>();
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
	
	public boolean hasDescription(){
		return (description != null);
	}
	
	public List<Rule> getRules(){
		return rules;
	}
	
	public void addRule(Rule rule){
		if (rules.contains(rule)) return;
		if (rule.group != null){
			rule.group.removeRule(rule);
		}
		rule.group = this;
		rules.add(rule);
		HomeOs.getInstance().getRuleEngine().addRule(rule);
	}
	
	public void removeRule(Rule rule){
		rules.remove(rule);
		rule.group = null;
		HomeOs.getInstance().getRuleEngine().removeRule(rule);
	}
	
	public String toString(){
		if (this.getName() != null){
			return this.getName();
		}
		return super.toString();
	}
	
	
}
