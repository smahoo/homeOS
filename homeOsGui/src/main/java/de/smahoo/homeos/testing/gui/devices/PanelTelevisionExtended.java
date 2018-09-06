package de.smahoo.homeos.testing.gui.devices;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;
import java.awt.GridLayout;


import de.smahoo.homeos.devices.ExtendedTelevision;
import de.smahoo.homeos.testing.gui.PanelDetails;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelTelevisionExtended extends PanelDetails{

	private ExtendedTelevision tv = null;
	
	public PanelTelevisionExtended(){	
		initGui();
	}
	
	public void update(){
		
	}
	
	public void setTelevision(ExtendedTelevision tv){
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
	
	private void setChannel(int channel){
		if (tv == null) return;
		try {
			tv.setChannel(channel);
			
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
		
		pnlChannels = new JPanel();
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
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pnlChannels, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
					.addGap(11))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(pnlChannels, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(btnPower, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
									.addComponent(btnChUp)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnChDown)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(btnMute, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnVolUp)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnVolDown)))))
					.addContainerGap(112, Short.MAX_VALUE))
		);
		
		btnCh1 = new JButton("1");
		btnCh1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setChannel(1);
			}
		});
		
		btnCh2 = new JButton("2");
		btnCh2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setChannel(2);
			}
		});
		btnCh3 = new JButton("3");
		btnCh3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setChannel(3);
			}
		});
		
		btnCh4 = new JButton("4");
		btnCh4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setChannel(4);
			}
		});
		
		btnCh5 = new JButton("5");
		btnCh5.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setChannel(5);
			}
		});
		pnlChannels.setLayout(new GridLayout(0, 3, 0, 0));
		pnlChannels.add(btnCh1);
		pnlChannels.add(btnCh2);
		pnlChannels.add(btnCh3);
		pnlChannels.add(btnCh4);
		pnlChannels.add(btnCh5);
		
		btnCh6 = new JButton("6");
		btnCh6.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setChannel(6);
			}
		});
		pnlChannels.add(btnCh6);
		
		btnCh7 = new JButton("7");
		btnCh7.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setChannel(7);
			}
		});
		pnlChannels.add(btnCh7);
		
		btnCh8 = new JButton("8");
		btnCh8.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setChannel(2);
			}
		});
		pnlChannels.add(btnCh8);
		
		btnCh9 = new JButton("9");
		btnCh9.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setChannel(9);
			}
		});
		pnlChannels.add(btnCh9);
		setLayout(groupLayout);
	}
	
	
	private JPanel pnlChannels;
	private JButton btnCh1;
	private JButton btnCh2;
	private JButton btnCh3;
	private JButton btnCh4;
	private JButton btnCh5;
	private JButton btnCh6;
	private JButton btnCh7;
	private JButton btnCh8;
	private JButton btnCh9;
	
}
