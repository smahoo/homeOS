package de.smahoo.homeos.automation;

import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.common.EventType;


public class RuleEngine implements RuleEventListener{

	static RuleEngine instance = null;
	
	List<Rule> rules;
	List<RuleGroup> ruleGroups;
	List<RuleEventListener> eventListener;
	
	public RuleEngine(){
		eventListener = new ArrayList<RuleEventListener>();
		rules = new ArrayList<Rule>();	
		ruleGroups = new ArrayList<RuleGroup>();
		instance = this;
	}
	
	static public RuleEngine getInstance(){
		return instance;
	}

	public void checkRuleApplicability(){
		for (Rule rule : rules){
			if (!rule.isApplicable()){
				RuleFactory.getInstance().checkForApplicability(rule);
			}
		}
	}
	
	public List<Rule> getRules(){
		return rules;
	}
	
	public List<RuleGroup> getRuleGroups(){
		return ruleGroups;
	}
	
	protected void addRule(Rule rule){
		if (rules.contains(rule)) return;
		rules.add(rule);
		rule.addEventListener(this);
		dispatchRuleEvent(new RuleEvent(EventType.RULE_ADDED, rule));
	}
	
	@Override
	public void onRuleEvent(RuleEvent event) {
		dispatchRuleEvent(event);
		
	}
	
	protected void removeRule(Rule rule){
		if (!rules.contains(rule)) return;
		if (rule.getRuleGroup() != null){
			rule.getRuleGroup().removeRule(rule);			
		}
		rule.removeEventListener(this);
		dispatchRuleEvent(new RuleEvent(EventType.RULE_REMOVED, rule));
	}
	
	public void addEventListener(RuleEventListener listener){
		if (eventListener.contains(listener)) return;
		eventListener.add(listener);
	}
	
	public void removeEventListener(RuleEventListener listener){
		if (!eventListener.contains(listener)) return;
		eventListener.remove(listener);
	}
	
	protected void dispatchRuleEvent(RuleEvent event){
		for (RuleEventListener listener :eventListener){
			listener.onRuleEvent(event);
		}
	}
}
