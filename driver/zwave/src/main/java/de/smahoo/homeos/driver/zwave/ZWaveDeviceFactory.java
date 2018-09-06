package de.smahoo.homeos.driver.zwave;

import java.lang.reflect.Constructor;

import de.smahoo.homeos.driver.zwave.aeonlabs.AL_DSB05_ZWEU;
import de.smahoo.homeos.driver.zwave.aeonlabs.AL_HEM;
import de.smahoo.homeos.driver.zwave.aeonlabs.AL_Multisensor_6;
import de.smahoo.homeos.driver.zwave.assaabloy.AssaAbloyDoorLock;
import de.smahoo.homeos.driver.zwave.danfoss.Dan_LivingConnect;
import de.smahoo.homeos.driver.zwave.danfoss.Dan_LivingConnectV2;
import de.smahoo.homeos.driver.zwave.dlink.DLinkPowerClamp;
import de.smahoo.homeos.driver.zwave.everspring.ES_ST814;
import de.smahoo.homeos.driver.zwave.fibaro.Fibaro_MotionSensor;
import de.smahoo.homeos.driver.zwave.fibaro.Fibaro_RGBController;
import de.smahoo.homeos.driver.zwave.fibaro.Fibaro_SmokeDetector;
import de.smahoo.homeos.driver.zwave.fibaro.Fibaro_WallPlug;
import de.smahoo.homeos.driver.zwave.fibaro.Fibaro_DoorWindowSensor;
import de.smahoo.homeos.driver.zwave.fibaro.Fibaro_FloodSensor;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericMeter;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSensorBinary;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSwitchBinary;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSwitchMultiLevel;
import de.smahoo.homeos.driver.zwave.siegenia.Siegenia_AeropacWave;
import de.smahoo.homeos.driver.zwave.vision.Vis_ZM1602;
import de.smahoo.jwave.JWaveException;
import de.smahoo.jwave.cmd.JWaveNodeCommandFactory;
import de.smahoo.jwave.node.JWaveNode;

public class ZWaveDeviceFactory {
	
	protected JWaveNodeCommandFactory nodeCmdFactory= null;
	
	public ZWaveDeviceFactory(JWaveNodeCommandFactory cmdFactory){
		this.nodeCmdFactory = cmdFactory;
	}
	
	protected String generateDeviceId(JWaveNode node){
		String id = ""+node.getNodeId();
		while (id.length() < 3){
			id = "0"+id;
		}
		return id;
	}
	
	
	protected ZWaveDevice generateZWaveDevice(String id, JWaveNode node, String className){
		ZWaveDevice device = null;
		
		try {
			Class<?> cl = Class.forName(className);
			
			Constructor<?> cons = cl.getConstructor(String.class, JWaveNode.class);			
		
			device =  (ZWaveDevice)cons.newInstance(id, node);
			
		} catch (Exception exc){
			exc.printStackTrace();
		}		
		return device;
	}
	
	protected ZWaveDevice generateZWaveDevice(JWaveNode node) throws JWaveException{
		
		ZWaveDevice device = null;
		System.out.println(node.getGenericDeviceType().getName()+"_"+node.getGenericDeviceType().getKey());
		// FIXME: Node was added but does not contain manufacture details
		
		if (node.getManufactureId() > 0) {
			switch (node.getManufactureId()){
			case 0x86:	// AEON Labs
				device = generateAeonLabsDevice(node);
				break;
			case 0x02: // Danfoss
				device = generateDanfossDevice(node);
				break;
			case 0x60: // Everspring
				device = generateEverspringDevice(node);
				break;
			case 0x81: // Siegenia
				device = generateSiegeniaDevice(node);
				break;
			case 0x108: // D-LINK
				device = generateDLinkDevice(node);
				break;
			case 0x109: // Vision Security
				device = generateVisionDevice(node);
				break;
			case 0x10F: // Fibaro
				device = generateFibaroDevice(node);
				break;
			case 0x129: // Assa Abloy
				device = generateAssaAbloyDevice(node);
				break;
			default:
					break;
			}
		}
		
		if (device != null){
			device.cmdFactory = this.nodeCmdFactory;
			return device;
		}
		
		switch (node.getGenericDeviceType().getKey()){
			case 0x10: // GENERIC_TYPE_SWITCH_BINARY
				device = generateGenericSwitchBinary(node);
				break;
			case 0x11: // GENERIC_TYPE_SWITCH_MULTILEVEL
				device = generateGenericSwitchMultiLevel(node);
				break;
			case 0x20: // GENERIC_TYPE_SENSOR_BINARY			
					 device = generateGenericSensorBinary(node);
				
				break;
			case 0x21: // GENERIC_TYPE_SENSOR_MULTILEVEL
				//device = generateMultiSensor(node);
				break;			
			case 0x31: // GENERIC_TYPE_METER
					device = generateGenericMeter(node);
				break;
			default:
				break;
		}
		
		if (device != null){
			device.cmdFactory = this.nodeCmdFactory;
		}
		
		return device;
	}
	
	
	protected ZWaveDevice generateAssaAbloyDevice(JWaveNode node){
		ZWaveDevice dev = null;
		switch (node.getProductTypeId()){
		case 0x06:
			if (node.getProductId() == 0){
				dev = generateAssaAbloyDoorLock(node);
			}
		}
		
		return dev;
	}
	
	protected ZWaveDevice generateAssaAbloyDoorLock(JWaveNode node){
		String deviceId = "AssaAbloy_DoorLock_"+generateDeviceId(node);
		return new AssaAbloyDoorLock(deviceId, node);
	}
	
	protected ZWaveDevice generateSiegeniaDevice(JWaveNode node){
		ZWaveDevice dev = null;
		
		switch(node.getProductTypeId()){
		case 0x14:
				if (node.getProductId()==0x01){	// SIEGENIA AEROPAC wave
					dev = generateSiegenia_AeropacWave(node);
				}
				
			break;
		}
		
		
		return dev;
	}
	
	
	
	
	protected ZWaveDevice generateDanfossDevice(JWaveNode node){
		ZWaveDevice dev = null;
		
		switch(node.getProductTypeId()){
		case 0x05:
				if (node.getProductId()==0x03){
					dev = generateDanfoss_LivingConnect(node);
				}
				if (node.getProductId() == 0x04){
					dev = generateDanfoss_LivingConnectV2(node);
				}
			break;
		}
		
		
		return dev;
	}
	
	
	protected ZWaveDevice generateFibaroDevice(JWaveNode node){
		ZWaveDevice dev = null;
		
		switch(node.getProductTypeId()){
		case 0xC00:
			if (node.getProductId() == 0x1000){
				dev = generateFib_SmokeDetector(node);
			}
			break;
		case 0x900:
			if (node.getProductId() == 0x1000){
				dev = generateFib_RGBController(node);
			}
			break;
		case 0x800:
			if (node.getProductId() == 0x1001){
				dev = generateFibaro_MotionSensor(node);
			}
		case 0x700:
			if (node.getProductId() == 0x1000){
				dev = generateFib_WindowContact(node);
			}		
			break;
		case 0x600:
			if (node.getProductId() == 0x1000){
				dev = generateFib_WallPlug(node);
			}
			break;
		case 0xb00:
			if (node.getProductId() == 0x1001){
				dev = generateFibaro_FloodSensor(node);
			}
			
			break;
		}
		return dev;
	}
	
	

	protected ZWaveDevice generateDLinkDevice(JWaveNode node){
		ZWaveDevice dev = null;
		
		switch(node.getProductTypeId()){
		case 0x06:
			if (node.getProductId() == 0x1A){
				dev = generateDlinkPowerClamp(node);
			}
			break;
		}
		
		return dev;
	}
	
	
	protected ZWaveDevice generateVisionDevice(JWaveNode node){
		ZWaveDevice dev = null;
		
		switch(node.getProductTypeId()){
		case 0x2005:
			if (node.getProductId() == 0x01){
				dev = generateVis_ZM1602(node);
			}
			break;
		}
		
		return dev;
	}
	
	protected ZWaveDevice generateEverspringDevice(JWaveNode node){
		ZWaveDevice dev = null;
		
		switch(node.getProductTypeId()){
		case 0x06:
			if (node.getProductId() == 0x01){  // ATTENTION: seems the id is changing, not sure but beware of it
				dev = generateES_ST814(node);
			}
			break;
		}
		
		return dev;
	}
	
	protected ZWaveDevice generateAeonLabsDevice(JWaveNode node){
		ZWaveDevice dev = null;
		
		switch(node.getProductTypeId()){
		case 0x02:
				if (node.getProductId() == 0x05){
					dev = generateAL_DSB05_ZWEU(node);
				}
				if (node.getProductId() == 0x1C){
					dev = generateAl_HEM(node);
				}
				if (node.getProductId() == 0x64){
					dev = generateAl_Multisensor_6(node);
				}
			break;
		}
		
		
		return dev;
	}
	
	protected DLinkPowerClamp generateDlinkPowerClamp(JWaveNode node){
		String deviceId = "Dlink_PowerClamp_"+generateDeviceId(node);
		return new DLinkPowerClamp(deviceId, node);
	}
	
	protected Vis_ZM1602 generateVis_ZM1602(JWaveNode node){
		String deviceId = "Vision_Siren_"+generateDeviceId(node);
		return new Vis_ZM1602(deviceId,node);
	}
	
	protected ZWaveDevice generateFib_RGBController(JWaveNode node){
		String deviceId = "Fibaro_RGBController_"+generateDeviceId(node);
		return new Fibaro_RGBController(deviceId,node);
	}
	
	protected Fibaro_WallPlug generateFib_WallPlug(JWaveNode node){
		String deviceId = "Fibaro_WallPlug_"+generateDeviceId(node);
		return new Fibaro_WallPlug(deviceId, node);
	}
	
	protected Fibaro_FloodSensor generateFibaro_FloodSensor(JWaveNode node){
		String deviceId = "Fibaro_FloodSensor_"+generateDeviceId(node);
		return new Fibaro_FloodSensor(deviceId, node);
	}
	
	protected Fibaro_MotionSensor generateFibaro_MotionSensor(JWaveNode node){
		String deviceId = "Fibaro_MotionSensor_"+generateDeviceId(node);
		return new Fibaro_MotionSensor(deviceId, node);
	}
	
	protected Fibaro_DoorWindowSensor generateFib_WindowContact(JWaveNode node){
		String deviceId = "Fibaro_DoorWindowSensor_"+generateDeviceId(node);
		return new Fibaro_DoorWindowSensor(deviceId, node);
	}
	
	protected Fibaro_SmokeDetector  generateFib_SmokeDetector(JWaveNode node){
		String deviceId = "Fibaro_SmokeDetector_"+generateDeviceId(node);
		return new Fibaro_SmokeDetector(deviceId,node);
	}
		
	protected Siegenia_AeropacWave generateSiegenia_AeropacWave(JWaveNode node){
		String deviceId = "Siegenia_AeropacWave_"+generateDeviceId(node);
		return new Siegenia_AeropacWave(deviceId, node);
	}
		
	protected Dan_LivingConnect generateDanfoss_LivingConnect(JWaveNode node){
		String deviceId = "Danfoss_LivingConnect_"+generateDeviceId(node);
		return new Dan_LivingConnect(deviceId, node);
	}
	
	protected Dan_LivingConnectV2 generateDanfoss_LivingConnectV2(JWaveNode node){
		String deviceId = "Danfoss_LivingConnect_V2_"+generateDeviceId(node);
		return new Dan_LivingConnectV2(deviceId, node);
	} 

	protected AL_Multisensor_6 generateAl_Multisensor_6(JWaveNode node){
		String deviceid = "AL_Multisensor_6_"+generateDeviceId(node);
		return new AL_Multisensor_6(deviceid,node);
	}

	protected AL_DSB05_ZWEU generateAL_DSB05_ZWEU(JWaveNode node){
		String deviceId = "AL_DSB05_ZWEU_"+generateDeviceId(node);
		return new AL_DSB05_ZWEU(deviceId, node);
	}
	
	protected AL_HEM generateAl_HEM(JWaveNode node){
		String deviceID = "AL_HEM"+generateDeviceId(node);
		return new AL_HEM(deviceID, node);
	}
	
	protected ES_ST814 generateES_ST814(JWaveNode node){
		String deviceId = "ES_ST814_"+generateDeviceId(node);
		return new ES_ST814(deviceId, node);
	}
	
	protected ZWaveGenericSensorBinary generateGenericSensorBinary(JWaveNode node){
		String deviceId = "Generic_Sensor_Binary_"+generateDeviceId(node);
		return new ZWaveGenericSensorBinary(deviceId, node);
	}
	
	protected ZWaveGenericSwitchBinary generateGenericSwitchBinary(JWaveNode node){
		String deviceId = "GEN_SWI_BIN_"+generateDeviceId(node);
		return new ZWaveGenericSwitchBinary(deviceId, node);
	}
		
	protected ZWaveGenericSwitchMultiLevel generateGenericSwitchMultiLevel(JWaveNode node){
		String deviceId = "GEN_SWI_MUL_"+generateDeviceId(node);
		return new ZWaveGenericSwitchMultiLevel(deviceId, node);
	}
	
	protected ZWaveGenericMeter generateGenericMeter(JWaveNode node){
		String deviceId = "Generic_Meter_"+generateDeviceId(node);
		return new ZWaveGenericMeter(deviceId, node);
	}
	
	protected boolean isAL_DSB05_ZWEU(JWaveNode node){
		
		if (node.getGenericDeviceType().getKey() != 0x20){
			return false;
		}
		
		if (node.getSpecificDeviceType().getKey() != 0x01){
			return false;
		}
		
		if (node.hasManufactureDetails()){
			if (node.getManufactureId() != 0x86){
				return false;
			}
		}
		
		return true;
	}
	
}
