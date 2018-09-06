package de.smahoo.homeos.testing.gui.devices;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;


import de.smahoo.homeos.devices.Television;
import de.smahoo.homeos.testing.gui.PanelDetails;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelTelevision extends PanelDetails{

	private Television tv = null;
	
	public PanelTelevision(){	
		initGui();
	}
	
	public void update(){
		
	}
	
	public void setTelevision(Television tv){
		this.tv = tv;
		update();
	}
	
	private void power(){
		if (tv == null) return;
		try {
			tv.power();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private void mute(){
		if (tv == null) return;
		try {
			tv.mute();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}

	private void volumeUp(){
		if (tv == null) return;
		try {
			tv.volumeUp();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private void volumeDown(){
		if (tv == null) return;
		try {
			tv.volumeDown();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private void channelUp(){
		if (tv == null) return;
		try {
			tv.channelUp();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private void channelDown(){
		if (tv == null) return;
		try {
			tv.channelDown();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private void initGui(){
		JButton btnPower = new JButton("Power");
		btnPower.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				power();
			}
		});
		
		JButton btnChUp = new JButton("Ch+");
		btnChUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				channelUp();
			}
		});
		
		JButton btnChDown = new JButton("Ch-");
		btnChDown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				channelDown();
			}
		});
		
		JButton btnMute = new JButton("Mute");
		btnMute.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mute();
			}
		});
		
		JButton btnVolUp = new JButton("Vol+");
		btnVolUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				volumeUp();
			}
		});
		
		JButton btnVolDown = new JButton("Vol-");
		btnVolDown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				volumeDown();
			}
		});
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(btnMute, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
						.addComponent(btnPower, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnVolDown, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnChDown, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnChUp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnVolUp, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(302, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnPower, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnChUp)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnChDown)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnMute, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnVolUp)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnVolDown)))
					.addContainerGap(179, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
}
