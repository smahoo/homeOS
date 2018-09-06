package de.smahoo.homeos.testing.gui.devices;


import de.smahoo.homeos.devices.HeatingRtc;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class PanelHeating extends PanelDetails{

	private HeatingRtc heating = null;
	private JTextField txtDesiredTemperature;
	
	
	private void setTemperature(){
		Double newTemp = null;
		try {
			newTemp = Double.parseDouble(txtDesiredTemperature.getText());
		} catch (Exception exc){
			exc.printStackTrace();
		}
		if (newTemp == null) return;
		try {
			heating.setTemperature(newTemp);
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	public PanelHeating(){		
		initGui();
	}
	
	public void update(){
		if (heating != null){
			if (!txtDesiredTemperature.getText().equals(""+heating.getDesiredTemperature())){
				txtDesiredTemperature.setText(""+heating.getDesiredTemperature());
			}
		} else {
			txtDesiredTemperature.setText("");
		}
		txtDesiredTemperature.setEditable(heating != null);
	}

	public void setHeating(HeatingRtc heating){
		this.heating = heating;
		update();
	}
	
	public HeatingRtc getHeating(){
		return heating;
	}
	
	private void initGui(){
		JLabel lblNewLabel = new JLabel("desired temperature");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtDesiredTemperature = new JTextField();
		txtDesiredTemperature.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER){
					setTemperature();
				}
			}
		});
		txtDesiredTemperature.setColumns(10);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtDesiredTemperature, GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(txtDesiredTemperature, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(275, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
}
