package de.smahoo.homeos.remote.devices;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.devices.RGBController;
import de.smahoo.homeos.remote.RemoteDevice;
import de.smahoo.homeos.utils.AttributeValuePair;

public class RemoteRgbController extends RemoteDevice implements RGBController{

	private int red = 0;
	private int green = 0;
	private int blue = 0;
	
	
	@Override
	public void setRGB(int red, int green, int blue) {
		List<AttributeValuePair> values = new ArrayList<AttributeValuePair>();
		values.add(new AttributeValuePair("red",""+red));
		values.add(new AttributeValuePair("green",""+green));
		values.add(new AttributeValuePair("blue",""+blue));
		executeFunction("setColor", values);
		
	}

	@Override
	public long getRGB() {
		return  (red << 16L) + (green << 8L) + blue ;
	}

	@Override
	public int getRed() {
		return red;
	}

	@Override
	public int getGreen() {
		return green;
	}

	@Override
	public int getBlue() {
		return blue;
	}

	@Override
	protected void updateProperties(NodeList properties) {		
		if (properties.getLength() > 0){
			Element tmp;
			try {
				for (int i = 0; i<properties.getLength(); i++){
					if (properties.item(i) instanceof Element){
						tmp = (Element)properties.item(i);
					
						if ("property".equalsIgnoreCase(tmp.getTagName())){
							if ("green".equalsIgnoreCase(tmp.getAttribute("green"))){
								green = Integer.parseInt(tmp.getAttribute("green"));
							}
							if ("red".equalsIgnoreCase(tmp.getAttribute("red"))){
								red = Integer.parseInt(tmp.getAttribute("red"));
							}
							if ("blue".equalsIgnoreCase(tmp.getAttribute("blue"))){
								blue = Integer.parseInt(tmp.getAttribute("blue"));
							}
						}
					}
				}
			} catch (Exception exc){
				//
			}
		}
	}

}
