package de.smahoo.homeos.testing.gui.devices;




import de.smahoo.homeos.devices.Socket;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelSocket extends PanelDetails{

	private Socket  currSocket  = null;
	
	public PanelSocket(){
		
		
		initGui();
	}
	
	public void update(){
		
	}
	public void setSocket(Socket socket){
		currSocket = socket;
	}
	
	public Socket getSocket(){
		return currSocket;
	}
	
	private void initGui(){
		JButton btnTurnOn = new JButton("Turn On");
		btnTurnOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
					.addComponent(btnTurnOn, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnTurnOff, GroupLayout.PREFERRED_SIZE, 105, Short.MAX_VALUE)
					.addGap(14))
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
	
	private void turnOn(){
		if (currSocket == null) return;		
		try {
			currSocket.turnOn();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private void turnOff(){
		if (currSocket == null) return;		
		try {
			currSocket.turnOff();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
}
