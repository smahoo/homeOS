package de.smahoo.homeos.automation;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import de.smahoo.homeos.common.EventType;

public class Rule {
	
	private List<Condition> conditionList;
	private List<RuleAction> actionList;
	private List<RuleEventListener> eventListener;
	private boolean active = true;
	private String name = null;
	private String description = null; 
	protected RuleGroup group = null;
	protected Element xmlSource=null;
	protected boolean applicable = true;
	
	public Rule(){
		conditionList = new ArrayList<Condition>();
		eventListener = new ArrayList<RuleEventListener>();
		actionList = new ArrayList<RuleAction>();
	}
	
	protected void removeConditions(){
		for (Condition cond : conditionList){
			cond.prepareDeletion();
		}
		while(!conditionList.isEmpty()){
			conditionList.remove(0);
		}
	}
	
	protected void removeActions(){
		while(!actionList.isEmpty()){
			actionList.remove(0);
		}
	}
	
	public void setName(String name){
		String oldname = this.name;
		if (this.name != null){
			if (this.name.equalsIgnoreCase(name)) return;
		}
		this.name = name;
		dispatchRuleEvent(new RuleEvent(EventType.RULE_RENAMED,this,"Changed name from \""+oldname+"\" to \""+name+"\""));
	}
	
	protected void setApplicable(boolean applicable){
		if (applicable != this.applicable){
			this.applicable = applicable;
			if (applicable){
				dispatchRuleEvent(new RuleEvent(EventType.RULE_APPLICABLE, this));
			} else {
				dispatchRuleEvent(new RuleEvent(EventType.RULE_NOT_APPLICABLE, this));
			}
		}
	}
	
	public boolean isApplicable(){
		return applicable;
	}
	
	public Element getXmlSource(){
		return xmlSource;
	}
	
	public void setXmlSource(Element xmlSource){
		this.xmlSource = xmlSource;
	}
	
	public String getName(){
		return name;
	}
	
	public void setDescription(String description){		
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
		
	public RuleGroup getRuleGroup(){
		return group;
	}
	
	public void addRuleAction(RuleAction action){
		if (action == null){
			setApplicable(false);
		}
		if (actionList.contains(action)) return;
		actionList.add(action);
	}
	
	public void removeRuleAction(RuleAction action){
		actionList.remove(action);
	}
	
	public void addCondition(Condition condition){
		if (conditionList.contains(condition)) return;
		condition.listener = new ConditionCompliedEventListener() {
			
			@Override
			public void onConditionCompliedEvent(ConditionCompliedEvent event) {
				evaluateConditionEvent(event);
				
			}
		};
		conditionList.add(condition);
	}
	
	private void evaluateConditionEvent(ConditionCompliedEvent event){
		if (!this.isApplicable()) return;		
		checkConditions();
	}
	
	protected void doAction(final RuleAction action) throws Exception{		
		action.onAction();				
	}
	
	protected void checkConditions(){
		if (!active) return;
		boolean check = true;
		for (Condition cond : conditionList){
			check = check && cond.isComplied();
		}
		if (check){
			fire();
		}
	}
	
	protected void fire(){
		dispatchRuleEvent(new RuleEvent(EventType.RULE_FIRING,this,this.description));	
		
		for (RuleAction action : actionList){
			try {
				
			  doAction(action);
			} catch (Exception exc){
				dispatchRuleEvent(new RuleEvent(EventType.RULE_EXECUTION_FAILED,this,exc.getMessage()));
			}
		}				
		dispatchRuleEvent(new RuleEvent(EventType.RULE_FIRED,this,this.description));		
	}
	
	protected void addEventListener(RuleEventListener listener){
		if (eventListener.contains(listener)) return;
		eventListener.add(listener);
	}
	
	protected void removeEventListener(RuleEventListener listener){
		if (!eventListener.contains(listener)) return;
		eventListener.remove(listener);
	}
	
	protected void dispatchRuleEvent(RuleEvent event){
		for (RuleEventListener listener :eventListener){		
			listener.onRuleEvent(event);
		}
	}
	
	public boolean isEnabled(){
		return this.active;
	}
	
	public void setEnabled(boolean enabled){
		if (active == enabled) return;
		this.active = enabled;
		if (enabled) {
			dispatchRuleEvent(new RuleEvent(EventType.RULE_ENABLED, this));
		} else {
			dispatchRuleEvent(new RuleEvent(EventType.RULE_DISABLED, this));
		}
	}
	public List<Condition> getConditions(){
		return this.conditionList;
	}
	public List<RuleAction> getActions(){
		return this.actionList;
	}
	
	public String toString(){
		if (this.getName() != null) return this.getName();
		return super.toString();
	}
}
