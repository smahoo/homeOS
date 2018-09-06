package de.smahoo.homeos.common;

import java.util.List;

public interface ParameterizedFunction extends Function{
	public void setParameters(List<FunctionParameter> params);
	void execute(List<FunctionParameter> params)  throws FunctionExecutionException;
}
