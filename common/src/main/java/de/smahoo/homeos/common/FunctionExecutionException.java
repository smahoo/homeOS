package de.smahoo.homeos.common;


public class FunctionExecutionException extends Exception{

	public FunctionExecutionException(Function function, String msg){		
		super(msg);
	}
	
}
