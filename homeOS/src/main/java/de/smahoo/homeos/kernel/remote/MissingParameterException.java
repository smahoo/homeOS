package de.smahoo.homeos.kernel.remote;

import java.util.ArrayList;
import java.util.List;

public class MissingParameterException extends Exception{

	protected List<String> missingParameters;
	
	private MissingParameterException(){		
		missingParameters = new ArrayList<String>();
	}
	
	public MissingParameterException(String parameter){
		this();
		missingParameters.add(parameter);
	}
	
	public MissingParameterException(List<String> missingParameters){
		this();
		
		this.missingParameters = missingParameters;
	}
	
	@Override
	public String getMessage(){
		return toString();
	}
	
	@Override
	public String toString(){		
		String res;
		if (missingParameters.size() == 1){
			res = "Parameter \'"+missingParameters.get(0)+"\' is missing.";
		} else {
			res =  ""+missingParameters.size()+" parameters are missing.";
		}
		
		return res;
	}
	
	public List<String> getMissingParameterList(){
		return missingParameters;
	}
}
