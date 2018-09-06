package de.smahoo.homeos.driver.zwave.siegenia;

import de.smahoo.homeos.devices.Ventilator;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSwitchMultiLevel;
import de.smahoo.jwave.node.JWaveNode;

public class Siegenia_AeropacWave extends ZWaveGenericSwitchMultiLevel implements Ventilator{

	public Siegenia_AeropacWave(String id, JWaveNode node){
		super(id, node);
	}
	
}
