package de.smahoo.homeos.remote.physical;

import de.smahoo.homeos.common.Function;
import de.smahoo.homeos.common.FunctionExecutionException;

public class RemotePhysicalFunction implements Function{

	String name = null;
	
	public String getName(){
		return name;
	}
	
	public void execute() throws FunctionExecutionException{
		//
	}
	
}
