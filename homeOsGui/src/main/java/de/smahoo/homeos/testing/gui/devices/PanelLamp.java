package de.smahoo.homeos.testing.gui.devices;


import de.smahoo.homeos.devices.Lamp;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelLamp extends PanelDetails{

	private Lamp lamp;
	
	public void update(){
		
	}
	
	public PanelLamp(){
		
		
		initGui();
	}
	
	private void turnOn(){
		if (lamp == null) return;
		try {
			lamp.turnOn();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private void turnOff(){
		if (lamp == null) return;
		try {
			lamp.turnOff();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	public void setLamp(Lamp lamp){
		this.lamp = lamp;
	}
	
	public Lamp getLamp(){
		return lamp;
	}
	
	private void initGui(){
		JButton btnTurnOn = new JButton("Turn On");
		btnTurnOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				turnOn();
			}
		});
		
		JButton btnTurnOff = new JButton("Turn Off");
		btnTurnOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				turnOff();
			}
		});
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnTurnOn, GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnTurnOff, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnTurnOn)
						.addComponent(btnTurnOff))
					.addContainerGap(266, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
}
