package de.smahoo.homeos.testing.gui.remote;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.CardLayout;
import java.text.SimpleDateFormat;

import javax.swing.JTextArea;

import de.smahoo.homeos.common.Event;

public class PanelRemoteLogger extends JPanel{
	public PanelRemoteLogger() {		
		initGui();
	}

	private void initGui(){
		setLayout(new CardLayout(0, 0));		
		scrollPane = new JScrollPane();
		
		scrollPane.setBorder(null);
		
		add(scrollPane, "name_2963424391647");		
		txtEventLogger = new JTextArea();
		
		scrollPane.setViewportView(txtEventLogger);
	}
	
	
	public void log(Event event){		
		String line = "["+ (new SimpleDateFormat("HH:mm:ss")).format(event.getTimeStamp())+"] "+event.toString()+"\r\n";
		txtEventLogger.append(line);		
	}
	
	
		
	
	
	private JScrollPane scrollPane;
	private JTextArea txtEventLogger;
}
