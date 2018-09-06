package de.smahoo.homeos.automation;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.common.Function;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;
import de.smahoo.homeos.kernel.remote.MissingParameterException;
import de.smahoo.homeos.utils.AttributeValuePair;

public class FunctionExecutionAction extends RuleAction {

	private Function function = null;
	private List<AttributeValuePair> parameter = null;
	
	
	public FunctionExecutionAction(Function function, List<AttributeValuePair> params){
		this(function);
		parameter = params;
	}
	
	public FunctionExecutionAction(Function function){
		this.function = function;
		
	}
	
	protected void setFunctionParameterValues(List<FunctionParameter> lstParameter, List<AttributeValuePair> lstAv) throws MissingParameterException, NumberFormatException{
		
		List<String> missingParameters = new ArrayList<String>();
		AttributeValuePair av = null;
		
		for (FunctionParameter parameter : lstParameter){
			av = getAvPair(parameter.getName(),lstAv);
			if (av == null){
				missingParameters.add(parameter.getName());
			}			
		}
		
		
		
		if (missingParameters.size() != 0){
			throw new MissingParameterException(missingParameters);
		}
		
		for (FunctionParameter parameter : lstParameter){
			av = getAvPair(parameter.getName(),lstAv);
			parameter.setValue(""+av.getValue());	
		}
		
	}
	
	protected AttributeValuePair getAvPair(String parameter, List<AttributeValuePair> lstAv){
		for (AttributeValuePair av : lstAv){
			if (av.getAttribute().toLowerCase().equals(parameter.toLowerCase())){
				return av;
			}
		}
		return null;
	}
	
	public void onAction() throws FunctionExecutionException{
			if (parameter == null){
			   function.execute();
			} else {
				if (function instanceof ParameterizedDeviceFunction){					
					ParameterizedDeviceFunction pf = ((ParameterizedDeviceFunction)function);	
					List<FunctionParameter> lstParameter = pf.getNeededParameter();
					try{ 
						setFunctionParameterValues(lstParameter, parameter);
						pf.execute(lstParameter);
					} catch (Exception exc){
						throw new FunctionExecutionException(function,exc.getMessage());
					}
				}
			}
	}
	
	public String toString(){
		String strFunction = function.getName();
		if (function instanceof SimpleDeviceFunction){
			strFunction = ((SimpleDeviceFunction)function).getDevice().getDeviceId()+"."+strFunction;
		}
		return "Execute: "+strFunction;
	}
	
}
