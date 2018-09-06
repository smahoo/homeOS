package de.smahoo.homeos.testing.gui.devices;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.kernel.HomeOs;

public class PopupMenuDevice extends JPopupMenu{

	protected Device device = null;
			
	
	public PopupMenuDevice(){
		this(null);
	}
	
	public PopupMenuDevice(Device device){	
		setDevice(device);
		init();
	}
	
	public void setDevice(Device device){
		this.device = device;
	}
	
	private void deleteDevice(){
		HomeOs.getInstance().getDeviceManager().deleteDevice(device);
		setVisible(false);
	}
	
	private void init(){
		 JMenuItem menuItem = new JMenuItem("Delete");
		    menuItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					deleteDevice();
					
				}
			});
		    add(menuItem);
	}
	
	
	
}
