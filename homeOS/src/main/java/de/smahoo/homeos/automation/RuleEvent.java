package de.smahoo.homeos.automation;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;

public class RuleEvent extends Event{

	private Rule rule;
	
	public RuleEvent(EventType type, Rule rule){
		super(type);
		this.rule = rule;
	}
	public RuleEvent(EventType type, Rule rule, String description){
		super(type,description);
		this.rule = rule;
	}
	
	public Rule getRule(){
		return rule;
	}
}
