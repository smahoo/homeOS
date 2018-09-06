package de.smahoo.homeos.testing.gui.remote;

import javax.swing.JFrame;

import de.smahoo.homeos.remote.physical.RemotePhysicalDevice;

import java.awt.BorderLayout;

public class FramePhysicalDeviceDetails extends JFrame{
	private RemotePhysicalDevice device = null;
	
	
	public FramePhysicalDeviceDetails(RemotePhysicalDevice device) {
		this.device = device;
		this.setSize(400, 800);
		PanelPhysicalDeviceDetails panelPhysicalDeviceDetails = new PanelPhysicalDeviceDetails(device);
		getContentPane().add(panelPhysicalDeviceDetails, BorderLayout.CENTER);
	}
}



