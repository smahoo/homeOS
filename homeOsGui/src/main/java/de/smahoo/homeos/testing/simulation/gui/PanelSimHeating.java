package de.smahoo.homeos.testing.simulation.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import de.smahoo.homeos.testing.gui.PanelDetails;
import de.smahoo.homeos.simulation.devices.SimHeating;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PanelSimHeating extends PanelDetails{

	SimHeating heating;
	
	public PanelSimHeating(SimHeating heating){
		this.heating = heating;		
		initGui();
		update();
	}
	
	public void update(){
		if (heating != null){
			slider.setValue((int)(heating.getDesiredTemperature()*2));
			lbDesiredTemperature.setText(""+heating.getDesiredTemperature()+"°C");
		} else {
			lbDesiredTemperature.setText("");
		}
	}
	
	private void setTemperature(){
		double temp = ((double)slider.getValue())/2.0;
		heating.setTemperature(temp);
	}
	
	private void evaluateSliderPos(){
		lbDesiredTemperature.setText(""+((double)slider.getValue())/2.0+"°C");
	}
	
	private void initGui(){
		
		JLabel lbDesiredTemperatured = new JLabel("Desired Temperature");
		lbDesiredTemperatured.setHorizontalAlignment(SwingConstants.RIGHT);
		
		slider = new JSlider();
		slider.setValue(25);
		slider.setMaximum(50);
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				evaluateSliderPos();				
			}
		});
		slider.addMouseListener(new MouseAdapter() {		
			@Override
			public void mouseReleased(MouseEvent e) {
				setTemperature();
			}
		});
		lbDesiredTemperature = new JLabel("-30\u00B0C");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbDesiredTemperatured, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(slider, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lbDesiredTemperature, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lbDesiredTemperature, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
						.addComponent(slider, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lbDesiredTemperatured, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
					.addGap(264))
		);
		setLayout(groupLayout);
	}
	
	private JLabel lbDesiredTemperature;
	private JSlider slider;
}
