package de.smahoo.homeos.automation;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;

public class ConditionCompliedEvent extends Event{
	
	public Condition condition;
	
	public ConditionCompliedEvent(Condition condition){
		super(EventType.CONDITION_COMPLIED);
		this.condition = condition;
	}
	
	public Condition getCondition(){
		return condition;
	}
}
