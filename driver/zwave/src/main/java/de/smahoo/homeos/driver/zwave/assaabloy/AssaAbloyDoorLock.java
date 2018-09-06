package de.smahoo.homeos.driver.zwave.assaabloy;

import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.jwave.JWaveController;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.node.JWaveNode;
import de.smahoo.jwave.security.JWaveSecurityNonce;
import de.smahoo.jwave.utils.logger.LogTag;

public class AssaAbloyDoorLock extends ZWaveDevice{
	byte[] networkKey = {0x01, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};
	JWaveSecurityNonce currentNonce;
	boolean keysent = false;
	public AssaAbloyDoorLock(String id, JWaveNode node) {
		super(id, node);
		// TODO Auto-generated constructor stub
	}


	
	@Override
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd) {
		JWaveController.log(LogTag.DEBUG,"received cmd "+cmd.getCommand().getName());
		switch (cmd.getCommandClassKey()){
		case 0x98:
			try {
				JWaveController.log(LogTag.DEBUG,"received Security Message, but this should'nt be the case.!");
			} catch (Exception exc){
				exc.printStackTrace();
			}
			break;
		}
	}

	@Override
	protected void initDevice() {
	
	//	this.getNode().sendData(nodeCmd);
		// scheme get
	
		// on scheme report -> nonce get
		// on nonce report -> KeySet
		// security 
		try {
			Thread.sleep(7000);
		} catch (Exception exc){
			exc.printStackTrace();
		}
		//this.getNode().sendData(cmdFactory.generateCmd_SecuritySchemeGet());
	}

	@Override
	protected void executeDeviceFunction(PhysicalDeviceFunction function,
			List<FunctionParameter> params) throws FunctionExecutionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPropertyChanged(DeviceProperty property) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void generateDeviceFunctions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void generateProperties() {
		// TODO Auto-generated method stub
		
	}

}
