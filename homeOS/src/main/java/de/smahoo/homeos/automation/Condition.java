package de.smahoo.homeos.automation;

public abstract class Condition {
	
	protected ConditionCompliedEventListener listener = null;
	protected String description = null;
	protected boolean active = true;
	
	abstract public boolean isComplied();
	abstract protected void prepareDeletion();
	
	protected void dispatchConditionCompliedEvent(ConditionCompliedEvent event){
		if (listener == null) return;
		listener.onConditionCompliedEvent(event);
	}
	
	public boolean isActive(){
		return active;
	}
	
	public void setActive(boolean active){
		this.active = active;
	}
	
	public boolean hasDescription(){
		return (description != null);
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	
}
