package de.smahoo.homeos.testing.gui.devices;


import de.smahoo.homeos.devices.Dimmable;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelDimmer extends PanelDetails{

	Dimmable dimmer = null;
	
	public PanelDimmer(){
		
		
		initGui();
	}
	
	private void dimmDown(){
		try {
			//dimmer.dimmDown();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private void dimmUp(){
		try {
			//dimmer.dimmUp();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	public void setDimmer(Dimmable dimmer){
		this.dimmer = dimmer;
	}
	
	public void update(){
		// nothing to refresh!
	}
	
	private void initGui(){
		JButton btnDimmUp = new JButton("Dimm Up");
		btnDimmUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dimmUp();
			}
		});
		JButton btnDimmDown = new JButton("Dimm Down");
		
		btnDimmDown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dimmDown();
			}
		});
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnDimmUp, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDimmDown, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
					.addGap(4))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDimmUp)
						.addComponent(btnDimmDown))
					.addContainerGap(266, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
}
