package de.smahoo.homeos.testing.gui.db;

import javax.swing.JFrame;

import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.PropertyHistoryData;

import sun.security.krb5.internal.PAEncTSEnc;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class FrameHistoryData extends JFrame{

	
	private Device currentDevice = null;
	
	public FrameHistoryData(){
		super();
		
		init();
	}
	
	public void setDevice(Device device){
		currentDevice = device;
		if (device == null){
			
			this.setTitle("History");			
		} else {
			
			this.setTitle("History - "+device.getName());			
		}
	}
	
		
	private void executeHistoryRequest(){
		if (currentDevice == null){
			return;
		}		
		try {
			Date start = panelDateInterval.getFromDate();
			Date end = panelDateInterval.getToDate();		
			panelHistoryData.setData(currentDevice.getHistoryData(start, end));
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	private void init(){
		
		setSize(800, 500);
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panelTimeIntervall = new JPanel();
		panelTimeIntervall.setRequestFocusEnabled(false);
		panelTimeIntervall.setPreferredSize(new Dimension(10, 30));
		getContentPane().add(panelTimeIntervall, BorderLayout.NORTH);
		
		JButton btnExecute = new JButton("get data");
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeHistoryRequest();
			}
		});
		
		panelDateInterval = new PanelDateInterval();
		GroupLayout gl_panelTimeIntervall = new GroupLayout(panelTimeIntervall);
		gl_panelTimeIntervall.setHorizontalGroup(
			gl_panelTimeIntervall.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panelTimeIntervall.createSequentialGroup()
					.addComponent(panelDateInterval, GroupLayout.PREFERRED_SIZE, 412, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnExecute, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(43, Short.MAX_VALUE))
		);
		gl_panelTimeIntervall.setVerticalGroup(
			gl_panelTimeIntervall.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelTimeIntervall.createSequentialGroup()
					.addGroup(gl_panelTimeIntervall.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnExecute, GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
						.addComponent(panelDateInterval, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addGap(0))
		);
		panelTimeIntervall.setLayout(gl_panelTimeIntervall);
		
		panelHistoryData = new PanelHistoryData();
		getContentPane().add(panelHistoryData, BorderLayout.CENTER);
	}
	
	private PanelHistoryData panelHistoryData;
	private PanelDateInterval panelDateInterval;
}
