package de.smahoo.homeos.driver.zwave.aeonlabs;

import de.smahoo.homeos.devices.MeterElectricity;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericMeter;
import de.smahoo.jwave.cmd.JWaveCommandParameterType;
import de.smahoo.jwave.node.JWaveNode;

public class AL_HEM extends ZWaveGenericMeter implements MeterElectricity{

	public AL_HEM(String id, JWaveNode node) {
		super(id, node);		
	}

	@Override
	protected void initDevice() {
		// TODO Auto-generated method stub
		associateNode(01,01);
		associateNode(02,01);
		sendConfiguration(5, JWaveCommandParameterType.WORD,1); // send report when consumptions changes at least 1W for clamp 1
		sendConfiguration(8, JWaveCommandParameterType.BYTE,1); // send report when consumptions changes at least 1% for clamp 1

	}
	
	
}
