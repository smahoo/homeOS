package de.smahoo.homeos.testing.gui.db;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.BoxLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingConstants;

public class PanelDateInterval extends JPanel{
	private JTextField textDateFrom;
	private JTextField textTimeFrom;
	private JTextField textDateTo;
	private JTextField textTimeTo;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public PanelDateInterval() {
		init();
	}
	
	public Date getFromDate(){
		try {
			return formatter.parse(textDateFrom.getText()+" "+textTimeFrom.getText());
		} catch (Exception exc){
			exc.printStackTrace();
		}
		return null;
	}
	
	public Date getToDate(){
		try {
			return formatter.parse(textDateTo.getText()+" "+textTimeTo.getText());
		} catch (Exception exc){
			exc.printStackTrace();
		}
		return null;
	}
	
	private void initDates(){
		Date now = new Date();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		textDateTo.setText(dateFormat.format(now));
		textTimeTo.setText(timeFormat.format(now));
		
		Date yesterday = new Date(now.getTime()-(60*60*24*1000));
		textDateFrom.setText(dateFormat.format(yesterday));
		textTimeFrom.setText(timeFormat.format(yesterday));
		
		
	}
	
	private void init(){
		setSize(new Dimension(410, 32));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel lbFrom = new JLabel("Von");
		
		textDateFrom = new JTextField();
		textDateFrom.setHorizontalAlignment(SwingConstants.RIGHT);
		textDateFrom.setAlignmentX(Component.LEFT_ALIGNMENT);
		textDateFrom.setColumns(10);
		
		textTimeFrom = new JTextField();
		textTimeFrom.setHorizontalAlignment(SwingConstants.RIGHT);
		textTimeFrom.setAlignmentX(Component.LEFT_ALIGNMENT);
		textTimeFrom.setColumns(10);
		
		JLabel lbTo = new JLabel("Bis");
		
		textDateTo = new JTextField();
		textDateTo.setHorizontalAlignment(SwingConstants.RIGHT);
		textDateTo.setAlignmentX(Component.LEFT_ALIGNMENT);
		textDateTo.setColumns(10);
		
		textTimeTo = new JTextField();
		textTimeTo.setHorizontalAlignment(SwingConstants.RIGHT);
		textTimeTo.setAlignmentX(Component.LEFT_ALIGNMENT);
		textTimeTo.setColumns(10);
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		add(lbFrom);
		add(textDateFrom);
		add(textTimeFrom);
		add(lbTo);
		add(textDateTo);
		add(textTimeTo);
		
		initDates();
	}
}
