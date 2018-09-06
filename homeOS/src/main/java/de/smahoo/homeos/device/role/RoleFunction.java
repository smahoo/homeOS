package de.smahoo.homeos.device.role;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.common.Function;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;

public class RoleFunction implements Function{

	protected DeviceRole deviceRole=null;
	protected PhysicalDeviceFunction bindedDeviceFunction=null;
	protected List<ParameterBinding> functionParameterBindings = null;
	protected List<FunctionParameter> roleFunctionParameter;
	protected String name;
	
	public RoleFunction(String name, List<FunctionParameter> parameter){
		this.name = name;
		this.roleFunctionParameter = parameter;
		
	}
	
	public String getName(){
		return name;
	}
	
	protected void bindDeviceFunction(PhysicalDeviceFunction deviceFunction){
		this.bindedDeviceFunction = deviceFunction;
	}
	
	protected void bindDeviceFunction(PhysicalDeviceFunction deviceFunction, List<ParameterBinding> parameterBindings){
		bindDeviceFunction(deviceFunction);
		functionParameterBindings = parameterBindings;		
	}
	
	
	public void execute() throws FunctionExecutionException{
		if (bindedDeviceFunction == null){
			throw new FunctionExecutionException(this, "RoleFunction \'"+this.getName()+"\' has no device function binded");			
		}
		
		if (bindedDeviceFunction instanceof SimpleDeviceFunction){
			bindedDeviceFunction.execute();
			return;
		}
		if (bindedDeviceFunction instanceof ParameterizedDeviceFunction){
			ParameterizedDeviceFunction pdf = (ParameterizedDeviceFunction)bindedDeviceFunction;
			List<FunctionParameter> parameter = pdf.getNeededParameter();			
			for (FunctionParameter fp : parameter){
				ParameterBinding paramBinding = getParameterBinding(fp.getName());
				if (paramBinding == null){
					throw new FunctionExecutionException(this, "no value for needed parameter \'"+fp.getName()+"\' given");
				}
				if (!paramBinding.isRoleParameterFix()){
					throw new FunctionExecutionException(this,"fixed value for needed parameter \'"+fp.getName()+"\' needed");
				}				
				fp.setValue(paramBinding.getFixedRoleParameterValue());
			}		
			pdf.execute(parameter);
		}		
	}
		
	private ParameterBinding getParameterBinding(String devFunctionParameter){
		for (ParameterBinding pb : this.functionParameterBindings){
			if (pb.getDeviceParameterName().equalsIgnoreCase(devFunctionParameter)){
				return pb;
			}
		}
		return null;
	}
	
	public void execute(List<FunctionParameter> parameter) throws FunctionExecutionException{
		if (parameter == null){
			execute();
			return;
		}
		if (bindedDeviceFunction == null){
			throw new FunctionExecutionException(this, "RoleFunction \'"+this.getName()+"\' has no device function binded");			
		}
		if (bindedDeviceFunction instanceof SimpleDeviceFunction){
			bindedDeviceFunction.execute();
			return;
		}
		if (bindedDeviceFunction instanceof ParameterizedDeviceFunction){
			ParameterizedDeviceFunction pdf = (ParameterizedDeviceFunction)bindedDeviceFunction;
			List<FunctionParameter> funcParams = pdf.getNeededParameter();			
			for (FunctionParameter fp : funcParams){
				ParameterBinding paramBinding = getParameterBinding(fp.getName());
				if (paramBinding == null){
					throw new FunctionExecutionException(this, "no value for needed parameter \'"+fp.getName()+"\' given");
				}
				
				FunctionParameter roleFP = getFunctionParameter(paramBinding.getRoleParameterName(), parameter);
				if (roleFP == null){
					throw new FunctionExecutionException(this,"parameter for \'"+paramBinding.getRoleParameterName()+"\' not found");
				}
				fp.setValue(roleFP.getValue());
			}		
			pdf.execute(funcParams);
		}	
	}
	
	private FunctionParameter getFunctionParameter(String name, List<FunctionParameter> paramList){
		for (FunctionParameter fp : paramList){
			if (fp.getName().equalsIgnoreCase(name)) return fp;
		}
		return null;
	}
	
	protected void executeBindedDeviceFunction(List<FunctionParameter> parameter) throws FunctionExecutionException{
		((ParameterizedDeviceFunction)bindedDeviceFunction).execute(parameter);
	}
	
	public Function getBindedFunction(){
		return this.bindedDeviceFunction;
	}
	
	public List<FunctionParameter> getNeededFunctionParameters(){
		List<FunctionParameter> paramList = new ArrayList<FunctionParameter>();		
		if (this.roleFunctionParameter != null){
			for (FunctionParameter fp : roleFunctionParameter){
				paramList.add(new FunctionParameter(fp.getPropertyType(),fp.getName()));				
			}
		}		
		return paramList;
	}
}
