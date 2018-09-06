package de.smahoo.homeos.testing.gui.devices;


import de.smahoo.homeos.devices.MeterElectricity;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;

public class PanelMeterElectricity extends PanelDetails{

	MeterElectricity meter = null;
	
	
	public PanelMeterElectricity(){
		super();	
		init();
	}
	
	public void setMeter(MeterElectricity meter){
		this.meter = meter;
		update();
	}
	
	public void update(){
		if (meter == null){
			lbConsumptionCurrent.setText("");
			lbConsumptionTotal.setText("");
		} else {
			lbConsumptionCurrent.setText(""+meter.getCurrentConsumption()+" W");
			lbConsumptionTotal.setText(""+meter.getTotalConsumption()+" kWh");
		}
	}
	
	
	private void init(){
		JLabel lblNewLabel = new JLabel("aktueller Verbruach");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblNewLabel_1 = new JLabel("kommulierter Verbrach");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbConsumptionCurrent = new JLabel("");
		
		lbConsumptionTotal = new JLabel("");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(lblNewLabel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lbConsumptionTotal, GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
						.addComponent(lbConsumptionCurrent, GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lbConsumptionCurrent))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(lbConsumptionTotal))
					.addContainerGap(255, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	private JLabel lbConsumptionCurrent;
	private JLabel lbConsumptionTotal;
	
}
