package de.smahoo.homeos.testing.simulation.gui;


import de.smahoo.homeos.devices.MeterElectricity;
import de.smahoo.homeos.testing.gui.PanelDetails;
import de.smahoo.homeos.simulation.devices.SimMeterElectricity;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelSimMeterElectricity extends PanelDetails{

	protected SimMeterElectricity meter;
	private double currentConsumption = 0.0;
	
	public PanelSimMeterElectricity(SimMeterElectricity meter){
		this.meter = meter;
		currentConsumption = Math.round(meter.getCurrentConsumption()*1000);
		initGui();
		update();
	}
	
	protected void setConsumption(){
		meter.setCurrentConsumption(currentConsumption / 1000.0);
	}
	
	public void update(){
		currentConsumption = Math.round(meter.getCurrentConsumption() * 1000);
		slider.setValue((int)currentConsumption);
		lbConsumption.setText(""+currentConsumption+" W");
	}
	
	private void initGui(){
		JLabel lblNewLabel = new JLabel("consumption");
		
		slider = new JSlider();
		slider.setMaximum(2000);		
		slider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				setConsumption();								
			}
		});
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				currentConsumption = slider.getValue();
				lbConsumption.setText(""+currentConsumption+" W");
				
			}
		});
		
		lbConsumption = new JLabel("");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(slider, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lbConsumption, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lbConsumption)
						.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addContainerGap(264, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	private JLabel lbConsumption;
	private JSlider slider;
}
