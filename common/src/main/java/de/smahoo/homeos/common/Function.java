package de.smahoo.homeos.common;


public interface Function {	
	public String getName();
	public void execute() throws FunctionExecutionException;
}
