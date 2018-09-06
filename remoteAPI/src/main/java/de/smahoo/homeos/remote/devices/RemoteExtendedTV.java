package de.smahoo.homeos.remote.devices;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.devices.ExtendedTelevision;
import de.smahoo.homeos.utils.AttributeValuePair;

public class RemoteExtendedTV extends RemoteTV implements ExtendedTelevision{
	public void setChannel(int channel){		
		AttributeValuePair avp = new AttributeValuePair("channel",""+channel);
		List<AttributeValuePair> avpList = new ArrayList<AttributeValuePair>();
		avpList.add(avp);
		executeFunction("setChannel",avpList);
	}
}
